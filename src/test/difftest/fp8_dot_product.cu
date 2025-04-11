#include <cuda_fp8.h>
#include <cuda_bf16.h>
#include <cuda_runtime.h>
#include <iostream>

#define CHECK_CUDA_ERROR(val) check((val), #val, __FILE__, __LINE__)
template <typename T>
void check(T result, char const *const func, const char *const file, int const line) {
    if (result) {
        fprintf(stderr, "CUDA error at %s:%d code=%d \"%s\" \n", file, line,
                static_cast<unsigned int>(result), func);
        exit(EXIT_FAILURE);
    }
}

// 使用CUDA 12.8的FP8 Tensor Core API
__global__ void fp8_tensorcore_matmul(const __nv_fp8_e4m3* A, const __nv_fp8_e4m3* B, __nv_fp8_e4m3* C) {
    // 每个线程块处理整个8x8矩阵
    int row = threadIdx.y;
    int col = threadIdx.x;
    
    float sum = 0.0f;
    
    for (int k = 0; k < 8; ++k) {
        // 使用CUDA 12.8的FP8转换函数
        float a = __half2float(__nv_cvt_fp8_to_halfraw(*reinterpret_cast<const __nv_fp8_storage_t*>(&A[row * 8 + k]), __NV_E4M3));
        float b = __half2float(__nv_cvt_fp8_to_halfraw(*reinterpret_cast<const __nv_fp8_storage_t*>(&B[k * 8 + col]), __NV_E4M3));
        sum += a * b;
    }
    
    // 将结果转换回FP8
    __nv_fp8_storage_t fp8_result = __nv_cvt_halfraw_to_fp8(__float2half(sum), __NV_E4M3, __NV_SATFINITE);
    C[row * 8 + col] = *reinterpret_cast<__nv_fp8_e4m3*>(&fp8_result);
}

int main() {
    const int M = 8, N = 8, K = 8;
    
    // 初始化主机端数据
    __nv_fp8_e4m3 A_host[M*K], B_host[K*N], C_host[M*N];
    
    // 填充测试数据
    for (int i = 0; i < M*K; ++i) {
        float val = (i % 3) * 0.5f;
        __nv_fp8_storage_t fp8_val = __nv_cvt_halfraw_to_fp8(__float2half(val), __NV_E4M3, __NV_SATFINITE);
        A_host[i] = *reinterpret_cast<__nv_fp8_e4m3*>(&fp8_val);
    }
    
    for (int i = 0; i < K*N; ++i) {
        float val = (i % 5) * 0.3f;
        __nv_fp8_storage_t fp8_val = __nv_cvt_halfraw_to_fp8(__float2half(val), __NV_E4M3, __NV_SATFINITE);
        B_host[i] = *reinterpret_cast<__nv_fp8_e4m3*>(&fp8_val);
    }
    
    // 分配设备内存
    __nv_fp8_e4m3 *A_dev, *B_dev, *C_dev;
    CHECK_CUDA_ERROR(cudaMalloc(&A_dev, M*K*sizeof(__nv_fp8_e4m3)));
    CHECK_CUDA_ERROR(cudaMalloc(&B_dev, K*N*sizeof(__nv_fp8_e4m3)));
    CHECK_CUDA_ERROR(cudaMalloc(&C_dev, M*N*sizeof(__nv_fp8_e4m3)));
    
    // 拷贝数据到设备
    CHECK_CUDA_ERROR(cudaMemcpy(A_dev, A_host, M*K*sizeof(__nv_fp8_e4m3), cudaMemcpyHostToDevice));
    CHECK_CUDA_ERROR(cudaMemcpy(B_dev, B_host, K*N*sizeof(__nv_fp8_e4m3), cudaMemcpyHostToDevice));
    
    // 启动核函数 - 使用8x8线程块
    dim3 block(8, 8);
    fp8_tensorcore_matmul<<<1, block>>>(A_dev, B_dev, C_dev);
    
    // 拷贝结果回主机
    CHECK_CUDA_ERROR(cudaMemcpy(C_host, C_dev, M*N*sizeof(__nv_fp8_e4m3), cudaMemcpyDeviceToHost));
    
    // 打印结果
    std::cout << "FP8 8x8 Matrix Multiplication Result:\n";
    for (int i = 0; i < M; ++i) {
        for (int j = 0; j < N; ++j) {
            float val = __half2float(__nv_cvt_fp8_to_halfraw(*reinterpret_cast<const __nv_fp8_storage_t*>(&C_host[i*N + j]), __NV_E4M3));
            printf("%.2f ", val);
        }
        printf("\n");
    }
    
    // 释放设备内存
    CHECK_CUDA_ERROR(cudaFree(A_dev));
    CHECK_CUDA_ERROR(cudaFree(B_dev));
    CHECK_CUDA_ERROR(cudaFree(C_dev));
    
    return 0;
}