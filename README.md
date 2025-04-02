# FP8 浮点运算单元 (FPU)

这是一个基于 Chisel 实现的 FP8 浮点运算单元，支持 8 个 FP8 数的并行乘加运算。

## 项目结构

```
src/main/scala/fpu/
├── core/                    # 核心模块
│   ├── Fpu.scala           # 主 FPU 模块
│   ├── FP32Accumulator.scala # FP32 累加器
│   ├── Multiplier.scala    # FP8 乘法器
│   ├── FiveBitsAdder.scala # 5位加法器
│   ├── RightShifter.scala  # 右移器
│   ├── NormalizationShifter.scala # 规格化移位器
│   └── Reduction.scala     # 归约模块
├── Parameters.scala        # 全局参数定义
└── Float32.scala          # FP32 相关定义
```

## 功能特性

- 支持 8 个 FP8 数的并行乘加运算
- 支持 FP32 累加器
- 支持动态缩放因子
- 流水线化设计
- 支持规格化和舍入

## 数据格式

### FP8 格式
- 1 位符号位
- 4 位指数位
- 3 位尾数位

### FP32 格式
- 1 位符号位
- 8 位指数位
- 23 位尾数位

## 主要模块说明

### Fpu
主模块，实现 8 个 FP8 数的并行乘加运算。包含以下处理阶段：
1. FP8 乘法运算
2. 指数对齐
3. 多级归约（8→4→2→1）
4. FP32 累加
5. 结果规格化

### FP32Accumulator
实现 FP32 累加器，支持动态缩放和清零功能。

### Multiplier
实现 FP8 乘法运算，处理符号位和尾数乘法。

### FiveBitsAdder
实现 5 位加法器，用于指数计算。

### RightShifter
实现右移操作，用于指数对齐。

### NormalizationShifter
实现规格化移位，处理舍入。

### Reduction
实现多级归约，将多个输入归约为单个输出。

## 接口定义

### 输入接口
- `a`: 8个 FP8 输入
- `b`: 8个 FP8 输入
- `scale`: 缩放因子 (SInt<7>)
- `accIn`: FP32 累加器输入
- `clear`: 清零信号

### 输出接口
- `out`: FP32 输出结果

## 使用说明

1. 确保已安装 Chisel 开发环境
2. 克隆项目到本地
3. 运行测试：
   ```bash
   mill FP8fpu.test.testOnly fpu.core.FpuTest
   ```

## 注意事项

- 输入数据需要符合 FP8 格式
- 缩放因子范围为 [-63, 63]
- 累加器支持动态清零
