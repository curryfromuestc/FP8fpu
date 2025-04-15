import torch
import struct

e4m3 = torch.float8_e4m3fn

device = torch.device("cuda" if torch.cuda.is_available() else "cpu")

# 生成范围在[-400, 400]之间的随机数
a = (torch.rand(256, device=device) * 800 - 400).to(e4m3)
b = (torch.rand(256, device=device) * 800 - 400).to(e4m3)

# 打印a和b的值
print("\nVector a values:")
for i in range(256):
    print(f"a[{i}] = {a[i].item()}")

print("\nVector b values:")
for i in range(256):
    print(f"b[{i}] = {b[i].item()}")

#将a和b矩阵的二进制分别保存在两个txt文件里面
def print_fp8_2_binary(fp8_tensor, filename):
    with open(filename, 'w') as f:
        # 将FP8张量转为CPU并访问其底层数据
        cpu_tensor = fp8_tensor.cpu()
        # 遍历每个元素
        for i in range(cpu_tensor.shape[0]):
            # 转换为int8并获取其整数表示
            # 我们需要一个比较取巧的方法：先转为float32，再转为uint8
            val = cpu_tensor[i].item()
            # 根据E4M3规范手动计算二进制表示
            if val == 0:
                binary = "00000000"
            else:
                sign = 1 if val < 0 else 0
                abs_val = abs(val)
                # 计算指数部分
                if abs_val < 1:
                    exp = 0
                    while abs_val < 1:
                        abs_val *= 2
                        exp -= 1
                    exp += 7  # bias
                else:
                    exp = 0
                    while abs_val >= 2:
                        abs_val /= 2
                        exp += 1
                    exp += 7  # bias
                
                # 确保指数在允许范围内
                exp = max(0, min(15, exp))
                
                # 计算尾数部分（3位）
                abs_val -= 1.0  # 移除隐含的1
                mantissa = int(abs_val * 8)  # 3位尾数，所以乘以2^3
                
                # 构建二进制表示
                binary = f"{sign}{exp:04b}{mantissa:03b}"
            
            # 写入文件
            f.write(f"{binary}\n")

print("\nSaving a to a.txt")
print_fp8_2_binary(a, "/nfs/home/yangguolin/code/FP8fpu/src/test/scala/fpu/core/a.txt")

print("Saving b to b.txt")
print_fp8_2_binary(b, "/nfs/home/yangguolin/code/FP8fpu/src/test/scala/fpu/core/b.txt")

# 创建256x256的矩阵，确保内存布局
a_matrix = torch.zeros((256, 256), dtype=torch.float8_e4m3fn, device=device)
a_matrix[0, :256] = a  # 第一行前256个元素，行优先

b_matrix = torch.zeros((256, 256), dtype=torch.float8_e4m3fn, device=device)
b_matrix[0, :256] = b  # 第一行前256个元素，行优先
b_matrix = b_matrix.T  # 转置为列优先

c = torch._scaled_mm(a_matrix, b_matrix,out_dtype= torch.float32 ,scale_a=torch.tensor(0.25, device=device), scale_b=torch.tensor(0.25, device=device))
print("\n点积结果:", c[0, 0])  # 只取第一个元素，因为其他是填充的

#测试将fp8的矩阵转换为float32的矩阵
a_matrix_float32 = a_matrix.to(torch.float32)
b_matrix_float32 = b_matrix.to(torch.float32)

c_float32 = torch.dot(a_matrix_float32[0, :], b_matrix_float32[:, 0])
c_float32 = c_float32*0.25*0.25
print("float32点积结果:", c_float32)


