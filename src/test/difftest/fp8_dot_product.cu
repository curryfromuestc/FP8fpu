#include <iostream>                                           // 标准输入输出流
#include <cutlass/cutlass.h>                       // 引入cutlass头文件
#include <cutlass/numeric_types.h>                 // For FP8 types
#include <cutlass/gemm/device/gemm.h>              // For GEMM operation
#include <cutlass/gemm/device/gemm_universal.h>    // For universal GEMM
#include <cutlass/gemm/device/gemm_universal_adapter.h>
#include <cuda_runtime.h>                          // For CUDA runtime API

using ElementA = cutlass::float_e4m3_t;
using ElementB = cutlass::float_e4m3_t;
using ElementOutput = cutlass::float_e4m3_t;
using ElementAccumulator = float;

// Define the GEMM operation using Tensor Core
using Gemm = cutlass::gemm::device::GemmUniversalAdapter<
    cutlass::gemm::device::GemmUniversal<
        ElementA, cutlass::layout::RowMajor,
        ElementB, cutlass::layout::RowMajor,
        ElementOutput, cutlass::layout::RowMajor,
        ElementAccumulator,
        cutlass::arch::OpClassTensorOp,  // Use Tensor Core
        cutlass::arch::Sm89,            // Ampere architecture
        cutlass::gemm::GemmShape<128, 128, 32>,  // Tile size optimized for FP8
        cutlass::gemm::GemmShape<64, 64, 32>,    // Threadblock tile size
        cutlass::gemm::GemmShape<16, 8, 32>,     // Warp tile size
        cutlass::epilogue::thread::LinearCombination<
            ElementOutput,
            1,
            ElementAccumulator,
            ElementAccumulator
        >,
        cutlass::gemm::threadblock::GemmIdentityThreadblockSwizzle<>,
        2,
        cutlass::arch::OpMultiplyAdd
    >
>;

void generate_tensor_1D(ElementA *A, int M) {
    for (int i = 0; i < M; i++) {
        // Convert random integer to FP8 by creating a proper FP8 value
        float value = static_cast<float>(rand() % 100) / 100.0f;
        A[i] = cutlass::float_e4m3_t(value);
    }
}

int main() {
    int M = 128;
    float scale = 0.25*0.25;

    // Allocate device memory for matrices
    ElementA *d_A = nullptr;
    ElementB *d_B = nullptr;
    ElementOutput *d_C = nullptr;
    
    cudaError_t cuda_status;
    
    // Allocate device memory
    cuda_status = cudaMalloc(&d_A, M * M * sizeof(ElementA));
    if (cuda_status != cudaSuccess) {
        std::cerr << "Failed to allocate device memory for A: " << cudaGetErrorString(cuda_status) << std::endl;
        return -1;
    }
    
    cuda_status = cudaMalloc(&d_B, M * M * sizeof(ElementB));
    if (cuda_status != cudaSuccess) {
        std::cerr << "Failed to allocate device memory for B: " << cudaGetErrorString(cuda_status) << std::endl;
        cudaFree(d_A);
        return -1;
    }
    
    cuda_status = cudaMalloc(&d_C, M * M * sizeof(ElementOutput));
    if (cuda_status != cudaSuccess) {
        std::cerr << "Failed to allocate device memory for C: " << cudaGetErrorString(cuda_status) << std::endl;
        cudaFree(d_A);
        cudaFree(d_B);
        return -1;
    }

    // Allocate and initialize host memory
    ElementA *h_A = (ElementA *)malloc(M * M * sizeof(ElementA));
    ElementB *h_B = (ElementB *)malloc(M * M * sizeof(ElementB));
    ElementOutput *h_C = (ElementOutput *)malloc(M * M * sizeof(ElementOutput));
    
    if (!h_A || !h_B || !h_C) {
        std::cerr << "Failed to allocate host memory" << std::endl;
        cudaFree(d_A);
        cudaFree(d_B);
        cudaFree(d_C);
        free(h_A);
        free(h_B);
        free(h_C);
        return -1;
    }

    // Initialize host matrices
    generate_tensor_1D(h_A, M * M);
    generate_tensor_1D(h_B, M * M);

    // Copy data to device
    cuda_status = cudaMemcpy(d_A, h_A, M * M * sizeof(ElementA), cudaMemcpyHostToDevice);
    if (cuda_status != cudaSuccess) {
        std::cerr << "Failed to copy A to device: " << cudaGetErrorString(cuda_status) << std::endl;
        goto cleanup;
    }

    cuda_status = cudaMemcpy(d_B, h_B, M * M * sizeof(ElementB), cudaMemcpyHostToDevice);
    if (cuda_status != cudaSuccess) {
        std::cerr << "Failed to copy B to device: " << cudaGetErrorString(cuda_status) << std::endl;
        goto cleanup;
    }

    // Initialize the GEMM operation
    Gemm gemm_op;
    
    // Set up the GEMM arguments
    typename Gemm::Arguments args(
        {M, M, M},  // problem size
        {d_A, M},   // A matrix
        {d_B, M},   // B matrix
        {d_C, M},   // C matrix
        {d_C, M},   // D matrix
        {scale},    // alpha
        {scale}     // beta
    );

    // Initialize the GEMM operation
    cutlass::Status status = gemm_op.initialize(args);
    if (status != cutlass::Status::kSuccess) {
        std::cerr << "Failed to initialize GEMM operation" << std::endl;
        goto cleanup;
    }

    // Run the GEMM operation
    status = gemm_op();
    if (status != cutlass::Status::kSuccess) {
        std::cerr << "Failed to run GEMM operation" << std::endl;
        goto cleanup;
    }

    // Copy result back to host
    cuda_status = cudaMemcpy(h_C, d_C, M * M * sizeof(ElementOutput), cudaMemcpyDeviceToHost);
    if (cuda_status != cudaSuccess) {
        std::cerr << "Failed to copy result from device: " << cudaGetErrorString(cuda_status) << std::endl;
        goto cleanup;
    }

    // Print some results for verification
    std::cout << "Matrix multiplication completed successfully" << std::endl;
    std::cout << "First few elements of result matrix:" << std::endl;
    for (int i = 0; i < 5; i++) {
        std::cout << static_cast<float>(h_C[i]) << " ";
    }
    std::cout << std::endl;

cleanup:
    // Free device memory
    cudaFree(d_A);
    cudaFree(d_B);
    cudaFree(d_C);
    
    // Free host memory
    free(h_A);
    free(h_B);
    free(h_C);

    return 0;
}
    
