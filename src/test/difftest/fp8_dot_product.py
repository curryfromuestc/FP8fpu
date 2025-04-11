import torch

e4m3 = torch.float8_e4m3fn

device = torch.device("cuda" if torch.cuda.is_available() else "cpu")

# 将向量转换为矩阵形式，并填充到16x16
a = torch.tensor([1.875, 448.0, -1.875, -448.0, 0.015625, 0.45875, 0.078125, 0.125], dtype=torch.float8_e4m3fn, device=device)
b = torch.tensor([1.875, 448.0, 1.875, -448.0, 0.015625, 0.0390625, 0.078125, 0.140625], dtype=torch.float8_e4m3fn, device=device)

# 创建16x16的矩阵，确保内存布局
a_matrix = torch.zeros((16, 16), dtype=torch.float8_e4m3fn, device=device)
a_matrix[0, :8] = a  # 第一行前8个元素，行优先

b_matrix = torch.zeros((16, 16), dtype=torch.float8_e4m3fn, device=device)
b_matrix[0, :8] = b  # 第一行前8个元素，行优先
b_matrix = b_matrix.T  # 转置为列优先

c = torch._scaled_mm(a_matrix, b_matrix,out_dtype= torch.float32 ,scale_a=torch.tensor(0.25, device=device), scale_b=torch.tensor(0.25, device=device))
c = c-3215633.133333341
print("点积结果:", c[0, 0])  # 只取第一个元素，因为其他是填充的

