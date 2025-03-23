error id: fpu/core/RightShifter#shiftAmount.
file://<WORKSPACE>/src/main/scala/fpu/core/RightShifter.scala
empty definition using pc, found symbol in pc: 
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

}
```

#### Short summary: 

empty definition using pc, found symbol in pc: 