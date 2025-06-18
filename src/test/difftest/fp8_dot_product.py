import torch
import struct

e4m3 = torch.float8_e4m3fn

device = torch.device("cuda" if torch.cuda.is_available() else "cpu")

#直接从txt文件中读取数据，txt文件每一行是8位二进制数
def fp8_e4m3_binstr_to_float(binstr):
    # NVIDIA E4M3: 1 sign, 4 exp, 3 mantissa, bias=7
    sign = int(binstr[0], 2)
    exp = int(binstr[1:5], 2)
    man = int(binstr[5:], 2)
    bias = 7
    # 只处理普通数，不考虑特殊值
    value = (1 + man / 8) * (2 ** (exp - bias))
    if sign == 1:
        value = -value
    print(value)
    return value

a = []
b = []
with open("../scala/fpu/core/a.txt", "r") as file:
    for line in file:
        a.append(fp8_e4m3_binstr_to_float(line.strip()))
with open("../scala/fpu/core/b.txt", "r") as file:
    for line in file:
        b.append(fp8_e4m3_binstr_to_float(line.strip()))



# # 创建256x256的矩阵，确保内存布局
# a_matrix = torch.zeros((256, 256), dtype=torch.float8_e4m3fn, device=device)
# a_matrix[0, :256] = a  # 第一行前256个元素，行优先

# b_matrix = torch.zeros((256, 256), dtype=torch.float8_e4m3fn, device=device)
# b_matrix[0, :256] = b  # 第一行前256个元素，行优先
# b_matrix = b_matrix.T  # 转置为列优先

# c = torch._scaled_mm(a_matrix, b_matrix,out_dtype= torch.float32 ,scale_a=torch.tensor(0.25, device=device), scale_b=torch.tensor(0.25, device=device))
# print("\n点积结果:", c[0, 0])  # 只取第一个元素，因为其他是填充的

# #测试将fp8的矩阵转换为float32的矩阵
# a_matrix_float32 = a_matrix.to(torch.float32)
# b_matrix_float32 = b_matrix.to(torch.float32)

# c_float32 = torch.dot(a_matrix_float32[0, :], b_matrix_float32[:, 0])
# c_float32 = c_float32*0.25*0.25
# print("float32点积结果:", c_float32)

#转化为float32的矩阵
a_matrix_float32 = torch.tensor(a, dtype=torch.float32)
b_matrix_float32 = torch.tensor(b, dtype=torch.float32)

print(a_matrix_float32.shape)
print(b_matrix_float32.shape)
print(a_matrix_float32[0])
print(b_matrix_float32[0])
print(a_matrix_float32[1])
print(b_matrix_float32[1])
print(a_matrix_float32[0:8])
print(b_matrix_float32[0:8])

c_float32 = torch.dot(a_matrix_float32[0:128], b_matrix_float32[0:128])
print("float32点积结果:", c_float32)



