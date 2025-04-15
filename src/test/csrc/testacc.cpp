# include "verilated.h"
# include "verilated_fst_c.h"
# include "VFpu.h"
# include <iostream>
# include <fstream>
# include <vector>
# include <string>
# include <cmath>

// 前向声明
float interpretFP32(uint32_t value);

// 用于将float转换为FP8格式的辅助函数
uint8_t float_to_fp8_e4m3(float value) {
    if (value == 0.0f) return 0;
    
    uint8_t sign = (value < 0) ? 1 : 0;
    float abs_value = std::abs(value);
    
    // 计算指数
    int exp = std::floor(std::log2(abs_value));
    exp += 7;  // 偏置值为7
    
    // 界限检查
    if (exp < 0) return 0;  // 下溢
    if (exp > 15) {
        // 上溢，返回最大值
        return sign ? 0xFF : 0x7F;
    }
    
    // 计算尾数
    float normalized = abs_value / std::pow(2, exp - 7);
    normalized = normalized - 1.0f;  // 减去隐含1
    
    uint8_t mantissa = std::round(normalized * 8); // 3位尾数
    
    return (sign << 7) | ((exp & 0xF) << 3) | (mantissa & 0x7);
}

// 将FP8格式转换为float的辅助函数
float fp8_e4m3_to_float(uint8_t value) {
    uint8_t sign_bit = (value >> 7) & 1;
    uint8_t exp_bits = (value >> 3) & 0xF;
    uint8_t mant_bits = value & 0x7;
    
    float result = (1.0f + ((float)mant_bits / 8.0f)) * std::pow(2.0f, exp_bits - 7.0f);
    return sign_bit ? -result : result;
}

// 用于解析FP32格式的辅助函数
float interpretFP32(uint32_t value) {
    uint32_t sign = (value >> 31) & 1;
    uint32_t exp = (value >> 23) & 0xFF;
    uint32_t mant = value & 0x7FFFFF;
    
    if (exp == 0) return 0.0f;  // 处理零或非规格化数
    
    float result = std::pow(-1, sign) * (1.0f + ((float)mant / 0x7FFFFF)) * std::pow(2, exp - 127);
    return result;
}

int main(int argc, char **argv) {
    // 初始化Verilator
    Verilated::commandArgs(argc, argv);
    
    // 创建FST波形追踪
    Verilated::traceEverOn(true);
    VerilatedFstC* tfp = new VerilatedFstC;
    
    // 创建模型实例
    VFpu* top = new VFpu;
    top->trace(tfp, 99);  // 追踪级别 (99 = 所有信号)
    tfp->open("fpu_sim.fst");
    
    // 读取测试向量
    std::vector<uint8_t> a_vector;
    std::vector<uint8_t> b_vector;
    
    // 从文件读取测试向量
    std::ifstream a_file("a.txt");
    std::ifstream b_file("b.txt");
    
    if (!a_file.is_open() || !b_file.is_open()) {
        std::cerr << "Error opening test vector files!" << std::endl;
        return 1;
    }
    
    std::string line;
    // 读取a向量
    while (std::getline(a_file, line)) {
        if (line.length() == 8) {
            uint8_t value = 0;
            for (int i = 0; i < 8; i++) {
                if (line[i] == '1') {
                    value |= (1 << (7-i));
                }
            }
            a_vector.push_back(value);
        }
    }
    
    // 读取b向量
    while (std::getline(b_file, line)) {
        if (line.length() == 8) {
            uint8_t value = 0;
            for (int i = 0; i < 8; i++) {
                if (line[i] == '1') {
                    value |= (1 << (7-i));
                }
            }
            b_vector.push_back(value);
        }
    }
    
    printf("========== STARTING FPU DOT PRODUCT SIMULATION ==========\n");
    printf("Loaded %lu values for vector A\n", a_vector.size());
    printf("Loaded %lu values for vector B\n", b_vector.size());
    
    // 计算需要处理的批次数
    int total_batches = (a_vector.size() + 7) / 8;
    printf("Will process data in %d batches\n", total_batches);
    
    // 设置初始时钟状态
    top->clock = 0;
    top->reset = 0;
    top->io_scale_a = 2;  // 缩放因子 0.25
    top->io_scale_b = 2;  // 缩放因子 0.25
    
    // 时钟周期计数
    int sim_time = 0;
    
    // 处理所有批次
    for (int batch = 0; batch < total_batches; batch++) {
        printf("\n----- Processing batch %d/%d -----\n", batch+1, total_batches);
        
        // 计算当前批次大小 (最后一批可能不足8个)
        int batch_size = std::min(8, (int)(a_vector.size() - batch*8));
        
        // 为FPU输入设置值
        for (int i = 0; i < 8; i++) {
            int idx = batch*8 + i;
            if (i < batch_size && idx < a_vector.size() && idx < b_vector.size()) {
                // 设置当前批次的元素
                switch (i) {
                    case 0:
                        top->io_a_0 = a_vector[idx];
                        top->io_b_0 = b_vector[idx];
                        break;
                    case 1:
                        top->io_a_1 = a_vector[idx];
                        top->io_b_1 = b_vector[idx];
                        break;
                    case 2:
                        top->io_a_2 = a_vector[idx];
                        top->io_b_2 = b_vector[idx];
                        break;
                    case 3:
                        top->io_a_3 = a_vector[idx];
                        top->io_b_3 = b_vector[idx];
                        break;
                    case 4:
                        top->io_a_4 = a_vector[idx];
                        top->io_b_4 = b_vector[idx];
                        break;
                    case 5:
                        top->io_a_5 = a_vector[idx];
                        top->io_b_5 = b_vector[idx];
                        break;
                    case 6:
                        top->io_a_6 = a_vector[idx];
                        top->io_b_6 = b_vector[idx];
                        break;
                    case 7:
                        top->io_a_7 = a_vector[idx];
                        top->io_b_7 = b_vector[idx];
                        break;
                }
                printf("Setting a[%d] = 0x%02X (%.5f)\n", i, a_vector[idx], fp8_e4m3_to_float(a_vector[idx]));
                printf("Setting b[%d] = 0x%02X (%.5f)\n", i, b_vector[idx], fp8_e4m3_to_float(b_vector[idx]));
            } else {
                // 填充剩余未使用的元素为0
                switch (i) {
                    case 0:
                        top->io_a_0 = 0;
                        top->io_b_0 = 0;
                        break;
                    case 1:
                        top->io_a_1 = 0;
                        top->io_b_1 = 0;
                        break;
                    case 2:
                        top->io_a_2 = 0;
                        top->io_b_2 = 0;
                        break;
                    case 3:
                        top->io_a_3 = 0;
                        top->io_b_3 = 0;
                        break;
                    case 4:
                        top->io_a_4 = 0;
                        top->io_b_4 = 0;
                        break;
                    case 5:
                        top->io_a_5 = 0;
                        top->io_b_5 = 0;
                        break;
                    case 6:
                        top->io_a_6 = 0;
                        top->io_b_6 = 0;
                        break;
                    case 7:
                        top->io_a_7 = 0;
                        top->io_b_7 = 0;
                        break;
                }
            }
        }
        
        // 设置clear信号 (首个批次需要清零)
        top->io_clear = (batch == 0) ? 1 : 0;
        printf("Clear signal: %d\n", top->io_clear);
        
        // 运行一个时钟周期
        printf("\nRunning calculation...\n");
        
        // 时钟下降沿
        top->clock = 0;
        top->eval();
        tfp->dump(sim_time++);
        
        // 时钟上升沿
        top->clock = 1;
        top->eval();
        tfp->dump(sim_time++);
        
        // 显示批次结果
        uint32_t result = top->io_out;
        float float_result = interpretFP32(result);
        printf("Batch result: 0x%08X (%.8f)\n", result, float_result);
    }
    
    // 再等待几个时钟周期以获取最终结果
    for (int i = 0; i < 4; i++) {
        // 时钟下降沿
        top->clock = 0;
        top->eval();
        tfp->dump(sim_time++);
        
        // 时钟上升沿
        top->clock = 1;
        top->eval();
        tfp->dump(sim_time++);
    }
    
    // 显示最终结果
    printf("\n----- Final Results -----\n");
    uint32_t final_result = top->io_out;
    // 分析FP32结果
    uint32_t sign = (final_result >> 31) & 1;
    uint32_t exp = (final_result >> 23) & 0xFF;
    uint32_t mant = final_result & 0x7FFFFF;
    
    float float_result = std::pow(-1, sign) * (1.0f + ((float)mant / 0x7FFFFF)) * std::pow(2, exp - 127);
    
    printf("Sign: %u, Exp: %u, Mant: 0x%06X\n", sign, exp, mant);
    printf("Dot Product Result: %.8f\n", float_result);
    
    printf("\n========== SIMULATION COMPLETED ==========\n");
    
    // 完成仿真
    tfp->close();
    delete top;
    delete tfp;
    
    return 0;
}