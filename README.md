# FP8 8-Way Dot Product Implementation

This project is an implementation of an 8-way dot product unit using FP8 (8-bit floating-point) format, based on the paper "Fused FP8 4-Way Dot Product With Scaling and FP32 Accumulation". The implementation extends the original 4-way dot product to an 8-way version using Chisel hardware description language.

## Overview

This implementation provides a hardware-efficient solution for performing 8-way dot product operations using FP8 format, which is particularly useful for deep learning applications where memory bandwidth and computational efficiency are crucial. The design supports both E5M2 and E4M3 FP8 formats and accumulates results in FP32 format.

## Features

- Support for two FP8 formats:
  - E5M2 (5-bit exponent, 2-bit significand)
  - E4M3 (4-bit exponent, 3-bit significand)
- 8-way parallel dot product computation
- FP32 accumulation for improved numerical stability
- Configurable scaling factors
- Hardware-efficient implementation using Chisel

## Architecture

The implementation consists of several key components:

1. **FP8 Converter**: Converts between different FP8 formats (E5M2 and E4M3) and an intermediate format for processing
2. **Dot Product Unit**: Performs 8-way parallel multiplication and addition
3. **Scaling Unit**: Applies configurable scaling factors to the results
4. **Accumulator**: Accumulates results in FP32 format for improved numerical stability

## Project Structure

```
src/
├── main/
│   └── scala/
│       └── fpu/
│           ├── core/
│           │   └── FP8Converter.scala
│           └── Parameters.scala
└── test/
    └── scala/
        └── fpu/
            └── core/
                └── FP8ConverterTest.scala
```

## Dependencies

- Chisel 6.6
- Scala 2.12.x or later
- Mill (Scala Build Tool)

## Building and Testing

This project uses Mill as the build tool. To build and test the project:

To build the project:
```bash
mill compile
```

To run tests:
```bash
mill test
```

To generate Verilog:
```bash
mill fpu.runMain fpu.FP8DotProduct
```

## Usage

The implementation can be used in hardware designs where efficient FP8 dot product computation is required. The design is particularly suitable for:

- Deep learning accelerators
- Matrix multiplication units
- Neural network inference engines

## License

[Add your license information here]

## Acknowledgments

This implementation is based on the paper "Fused FP8 4-Way Dot Product With Scaling and FP32 Accumulation" and extends it to support 8-way dot product operations.
