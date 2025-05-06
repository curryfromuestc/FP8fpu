# include "verilated.h"
# include "verilated_fst_c.h"
# include "VTop.h"
# include <iostream>
# include <fstream>
# include <vector>
# include <string>
# include <cmath>
# include <bitset>

// 时钟周期函数
void tick(VTop* top, VerilatedFstC* tfp, vluint64_t& main_time) {   
    // 上升沿
    top->clock = 0;
    top->eval();
    tfp->dump(main_time);
    main_time++;
    
    // 下降沿
    top->clock = 1;
    top->eval();
    tfp->dump(main_time);
    main_time++;
}

int main(int argc, char **argv, char **env) {
    Verilated::commandArgs(argc, argv);
    // Enable waveform tracing
    Verilated::traceEverOn(true);
    
    VTop* top = new VTop;
    VerilatedFstC* tfp = new VerilatedFstC;
    top->trace(tfp, 99);
    tfp->open("dump.fst");

    vluint64_t main_time = 0;

    // 打开输入文件并读取
    std::ifstream input_file("src/test/scala/fpu/core/a.txt");
    if (!input_file.is_open()) {
        std::cerr << "无法打开输入文件 a.txt" << std::endl;
        return 1;
    }
    std::ifstream input_file2("src/test/scala/fpu/core/b.txt");
    if (!input_file2.is_open()) {
        std::cerr << "无法打开输入文件 b.txt" << std::endl;
        return 1;
    }

    std::vector<uint8_t> a(256);
    std::vector<uint8_t> b(256);
    std::string line;
    int idx = 0;

    // 读取a.txt
    while (std::getline(input_file, line) && idx < 256) {
        a[idx++] = static_cast<uint8_t>(std::bitset<8>(line).to_ulong());
    }
    
    // 读取b.txt
    idx = 0;
    while (std::getline(input_file2, line) && idx < 256) {
        b[idx++] = static_cast<uint8_t>(std::bitset<8>(line).to_ulong());
    }

    // 打印向量内容（二进制格式）
    std::cout << "Vector A:" << std::endl;
    for(int i = 0; i < 256; i++) {
        std::cout << std::bitset<8>(a[i]) << std::endl;
    }
    std::cout << "---" << std::endl;
    std::cout << "Vector B:" << std::endl;
    for(int i = 0; i < 256; i++) {
        std::cout << std::bitset<8>(b[i]) << std::endl;
    }

    input_file.close();
    input_file2.close();
    //进行仿真
    for (int i = 0; i < 32; i++) {
        // 在时钟周期开始时设置输入
        top->eval();  // 评估当前状态
        
        top->io_inA_0 = a[i*8];
        top->io_inA_1 = a[i*8+1];
        top->io_inA_2 = a[i*8+2];
        top->io_inA_3 = a[i*8+3];
        top->io_inA_4 = a[i*8+4];
        top->io_inA_5 = a[i*8+5];
        top->io_inA_6 = a[i*8+6];
        top->io_inA_7 = a[i*8+7];
        top->io_inB_0 = b[i*8];
        top->io_inB_1 = b[i*8+1];
        top->io_inB_2 = b[i*8+2];
        top->io_inB_3 = b[i*8+3];
        top->io_inB_4 = b[i*8+4];
        top->io_inB_5 = b[i*8+5];
        top->io_inB_6 = b[i*8+6];
        top->io_inB_7 = b[i*8+7];
        top->io_scaleA = 0;
        top->io_scaleB = 0;
        top->io_clear = (i == 0) ? 1 : 0;  // 第一次时清除累加器
        tick(top, tfp, main_time);
    }
    // for (int i = 0; i < 16; i++) {
    //     top->io_inA_0 = a[128 + i*8];
    //     top->io_inA_1 = a[128 + i*8+1];
    //     top->io_inA_2 = a[128 + i*8+2];
    //     top->io_inA_3 = a[128 + i*8+3];
    //     top->io_inA_4 = a[128 + i*8+4];
    //     top->io_inA_5 = a[128 + i*8+5];
    //     top->io_inA_6 = a[128 + i*8+6];
    //     top->io_inA_7 = a[128 + i*8+7];
    //     top->io_inB_0 = b[128 + i*8];
    //     top->io_inB_1 = b[128 + i*8+1];
    //     top->io_inB_2 = b[128 + i*8+2];
    //     top->io_inB_3 = b[128 + i*8+3];
    //     top->io_inB_4 = b[128 + i*8+4];
    //     top->io_inB_5 = b[128 + i*8+5];
    //     top->io_inB_6 = b[128 + i*8+6];
    //     top->io_inB_7 = b[128 + i*8+7];
    //     top->io_scaleA = 2;
    //     top->io_scaleB = 2;
    //     top->io_clear = 0;
    //     tick(top, tfp, main_time);
    // }
    for(int i = 0; i < 5; i++) {
        tick(top, tfp, main_time);
    }
    // 清理
    tfp->close();
    delete tfp;
    delete top;
    
    return 0;
             
}