error id: chisel3/Module#
file://<WORKSPACE>/src/main/scala/fpu/core/RightShifter.scala
empty definition using pc, found symbol in pc: chisel3/Module#
empty definition using semanticdb
|empty definition using fallback
non-local guesses:
	 -chisel3/Module#
	 -chisel3/util/Module#
	 -fpu/Parameters.Module#
	 -fpu/Module#
	 -Module#
	 -scala/Predef.Module#

Document text:

```scala
package fpu.core

import chisel3._
import chisel3.util._
import fpu.Parameters._
import fpu._
class RightShifter extends Module {
  val io = IO(new Bundle {
    val inProduct = Input(UInt(FixedPoint.LENGTH.W))
    val isOverflow = Input(UInt(1.W))
    val inExp = Input(SInt(6.W))
    val out = Output(UInt(FixedPoint.SHIFTED_LENGTH.W))
  })

  val exp = Wire(SInt(8.W))
  exp := io.inExp + io.isOverflow.asSInt
  val sign = Wire(UInt(1.W))
  sign := io.inProduct(FixedPoint.LENGTH - 1)
  val shiftAmount = Wire(SInt(8.W))
  shiftAmount := 34.S - exp
  printf("shiftAmount: %d\n", shiftAmount)
  val lowShiftAmount = Wire(UInt(3.W))
  lowShiftAmount := shiftAmount(2,0)
  printf("lowShiftAmount: %d\n", lowShiftAmount)
  val highShiftAmount = Wire(UInt(3.W))
  highShiftAmount := shiftAmount(5,3)
  printf("highShiftAmount: %d\n", highShiftAmount)

  // 使用移位寄存器实现右移
  val shiftReg = Reg(UInt(FixedPoint.FIRST_SHIFTED_LENGTH.W))
//   val inputData = Wire(UInt(FixedPoint.FIRST_SHIFTED_LENGTH.W))
//   inputData := Cat(io.inProduct(FixedPoint.LENGTH-2, 0), 0.U(7.W))
  // 打印inputData,用二进制
  printf("inputData: %b\n", io.inProduct)
  // 第一次移位
  val stage1 = Mux(lowShiftAmount(0), 
      Cat(sign,io.inProduct(FixedPoint.LENGTH-2,0),0.U(6.W)),
      Cat(io.inProduct(FixedPoint.LENGTH-2,0),0.U(7.W)))
  val stage2 = Mux(lowShiftAmount(1),
      Cat(Fill(3, sign), stage1 >> 2),
      stage1)
  val stage3 = Mux(lowShiftAmount(2),
      Cat(Fill(7, sign), stage2 >> 4),
      stage2)

  val firstShifted = stage3
  printf("firstShifted: %b\n", firstShifted)

  shiftReg := firstShifted
  printf("shiftReg: %b\n", shiftReg)
  val secondShifted = Wire(UInt((FixedPoint.SHIFTED_LENGTH-1).W))
  secondShifted := Cat(shiftReg,Fill(55,0.U))
  printf("secondShifted: %b\n", secondShifted)

  // 第二周期移位
  val stage4 = Mux(highShiftAmount(0),
      Cat(Fill(15, sign), secondShifted >> 8),
      secondShifted)
  printf("stage4: %b\n", stage4)
  val stage5 = Mux(highShiftAmount(1),
      Cat(Fill(31, sign), secondShifted >> 16),
      secondShifted)
  printf("stage5: %b\n", stage5)
  val stage6 = Mux(highShiftAmount(2),
      Cat(Fill(63, sign), secondShifted >> 32),
      secondShifted)
  printf("stage6: %b\n", stage6)

  io.out := Cat(stage6)
}
```

#### Short summary: 

empty definition using pc, found symbol in pc: chisel3/Module#