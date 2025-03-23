error id: chisel3/Bits#apply(+3).
file://<WORKSPACE>/src/main/scala/fpu/core/RightShifter.scala
empty definition using pc, found symbol in pc: chisel3/Bits#apply(+3).
found definition using semanticdb; symbol fpu/core/RightShifter#shiftAmount.
|empty definition using fallback
non-local guesses:
	 -

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
    val inExp = Input(SInt(6.W))
    val out = Output(SInt(FixedPoint.SHIFTED_LENGTH.W))
  })

  val exp = Wire(SInt(8.W))
  exp := io.inExp
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

  val shiftReg = Reg(SInt(FixedPoint.FIRST_SHIFTED_LENGTH.W))
  val firstShifted = Wire(SInt(FixedPoint.FIRST_SHIFTED_LENGTH.W))
  firstShifted := Cat(io.inProduct, Fill(7,0.U)).asSInt

  //第一周期移位
  printf("io.inProduct: %b\n", io.inProduct.asSInt)
  shiftReg := firstShifted >> lowShiftAmount.asUInt
  printf("shiftReg: %b\n", shiftReg)
  val secondShifted = Wire(SInt(FixedPoint.SHIFTED_LENGTH.W))
  secondShifted := Cat(shiftReg, Fill(55,0.U)).asSInt
  io.out := secondShifted >> (highShiftAmount.asUInt << 3)
  printf("io.out: %b\n", io.out)
}
```

#### Short summary: 

empty definition using pc, found symbol in pc: chisel3/Bits#apply(+3).