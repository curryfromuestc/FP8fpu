error id: chisel3/Data#asUInt().
file://<WORKSPACE>/src/main/scala/fpu/core/RightShifter.scala
empty definition using pc, found symbol in pc: 
empty definition using semanticdb
|empty definition using fallback
non-local guesses:
	 -chisel3/lowShiftAmount/asUInt.
	 -chisel3/lowShiftAmount/asUInt#
	 -chisel3/lowShiftAmount/asUInt().
	 -chisel3/util/lowShiftAmount/asUInt.
	 -chisel3/util/lowShiftAmount/asUInt#
	 -chisel3/util/lowShiftAmount/asUInt().
	 -fpu/Parameters.lowShiftAmount.asUInt.
	 -fpu/Parameters.lowShiftAmount.asUInt#
	 -fpu/Parameters.lowShiftAmount.asUInt().
	 -fpu/lowShiftAmount/asUInt.
	 -fpu/lowShiftAmount/asUInt#
	 -fpu/lowShiftAmount/asUInt().
	 -lowShiftAmount/asUInt.
	 -lowShiftAmount/asUInt#
	 -lowShiftAmount/asUInt().
	 -scala/Predef.lowShiftAmount.asUInt.
	 -scala/Predef.lowShiftAmount.asUInt#
	 -scala/Predef.lowShiftAmount.asUInt().

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

  //第一周期移位
  printf("io.inProduct: %b\n", io.inProduct.asSInt)
  shiftReg := io.inProduct.asSInt >> lowShiftAmount.asUInt
  printf("shiftReg: %b\n", shiftReg)
  io.out := shiftReg >> (highShiftAmount.asUInt << 3)
}
```

#### Short summary: 

empty definition using pc, found symbol in pc: 