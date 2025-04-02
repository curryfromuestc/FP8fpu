import random

#生成一个53位的二进制数，二进制表示，位数要补齐53位
print(bin(random.randint(0, 2**53 - 1))[2:].zfill(53))