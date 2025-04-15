cmake_minimum_required(VERSION 3.10)
project(fp8_dot_product CUDA)

# 设置CUDA架构
set(CMAKE_CUDA_ARCHITECTURES 89)

# 设置CUTLASS路径
set(CUTLASS_PATH "/nfs/home/yangguolin/code/cutlass")

# 添加包含目录
include_directories(
    ${CUTLASS_PATH}/include
    ${CUTLASS_PATH}/tools/util/include
)

# 添加源文件
add_executable(fp8_dot_product fp8_dot_product.cu)

# 设置CUDA编译选项
set_target_properties(fp8_dot_product PROPERTIES
    CUDA_SEPARABLE_COMPILATION ON
    CUDA_ARCHITECTURES 89
)

# 设置编译选项
target_compile_options(fp8_dot_product PRIVATE
    -O3
    -DNDEBUG
) 