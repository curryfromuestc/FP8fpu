error id: local7
file://<WORKSPACE>/src/main/scala/fpu/core/Fpu.scala
empty definition using pc, found symbol in pc: 
found definition using semanticdb; symbol local7
empty definition using fallback
non-local guesses:

offset: 411
uri: file://<WORKSPACE>/src/main/scala/fpu/core/Fpu.scala
text:
```scala
package fpu.core

import chisel3._
import chisel3.util._
import fpu.Parameters._
import fpu._

class Fpu extends Module {
  val io = IO(new Bundle {
    val a = Input(Vec(8, UInt(FP8_LENGTH.W)))
    val b = Input(Vec(8, UInt(FP8_LENGTH.W)))
    val scale = Input(SInt(7.W))
    val accIn = Input(UInt(Float32.LENGTH.W))
    val clear = Input(Bool())
    val out = Output(UInt(Float32.LENGTH.W))
    val outValid@@ = Output(Bool())
  })
  //累加器
  val acc = Module(new FP32Accumulator)
  acc.io.input := io.accIn
  acc.io.clear := io.clear
  acc.io.scale := io.scale
  val shiftedAcc = Wire(SInt(Float32.SHIFTED_LENGTH.W))
  shiftedAcc := acc.io.result

  //指数相加，尾数相乘
  val multiplier = Seq.fill(8)(Module(new Multiplier))
  val product = Seq.fill(8)(Wire(UInt(FixedPoint.LENGTH.W)))
  val fiveBitsAdder = Seq.fill(8)(Module(new FiveBitsAdder))
  val productExp = Seq.fill(8)(Wire(SInt(5.W)))
  for (i <- 0 until 8) {
    multiplier(i).io.aSign := io.a(i)(7)
    multiplier(i).io.bSign := io.b(i)(7)
    multiplier(i).io.a := io.a(i)(2,0)
    multiplier(i).io.b := io.b(i)(2,0)
    product(i) := multiplier(i).io.out
    fiveBitsAdder(i).io.a := io.a(i)(6,3)
    fiveBitsAdder(i).io.b := io.b(i)(6,3)
    productExp(i) := fiveBitsAdder(i).io.out
  }
  val rightShifter = Seq.fill(8)(Module(new RightShifter))
  val shiftedProduct = Seq.fill(8)(Wire(SInt(FixedPoint.SHIFTED_LENGTH.W)))
  for (i <- 0 until 8) {
    rightShifter(i).io.inProduct := product(i)
    rightShifter(i).io.inExp := productExp(i)
    shiftedProduct(i) := rightShifter(i).io.out
  }
  //8to4 reduction
  val reduction = Module(new Reduction(8,4))
  for (i <- 0 until 8) {
    reduction.io.in(i) := shiftedProduct(i)
  }
  val reducedProduct = reduction.io.out
  //4to2 reduction
  val reduction2 = Module(new Reduction(4,2))
  for (i <- 0 until 4) {
    reduction2.io.in(i) := reducedProduct(i)
  }
  val reducedProduct2 = reduction2.io.out

  //第三级流水
  val reduction3 = Module(new Reduction(2,1))
  reduction3.io.in(0) := reducedProduct2(0)
  reduction3.io.in(1) := reducedProduct2(1)
  val reducedProduct3 = reduction3.io.out

  val preNormalization = RegInit(0.S((Float32.SHIFTED_LENGTH+1).W))
  preNormalization := reducedProduct3(0) + shiftedAcc

  //规格化
  val normalizationShifter = Module(new NormalizationShifter)
  normalizationShifter.io.in := preNormalization
  val normalizedResult = normalizationShifter.io.out
  io.out := normalizedResult
}
```


#### Short summary: 

empty definition using pc, found symbol in pc: 