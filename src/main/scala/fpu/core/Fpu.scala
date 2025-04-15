package fpu.core

import chisel3._
import chisel3.util._
import fpu.Parameters._
import fpu._
import _root_.circt.stage.ChiselStage

class Fpu extends Module {
  val io = IO(new Bundle {
    val a = Input(Vec(8, UInt(FP8_LENGTH.W)))
    val b = Input(Vec(8, UInt(FP8_LENGTH.W)))
    val scale_a = Input(SInt(7.W))
    val scale_b = Input(SInt(7.W))
    val clear = Input(Bool())
    val out = Output(UInt(Float32.LENGTH.W))
  })

  //指数相加，尾数相乘
  val multiplier = Seq.fill(8)(Module(new Multiplier))
  val product = Seq.fill(8)(Wire(SInt(FixedPoint.LENGTH.W)))
  val fiveBitsAdder = Seq.fill(8)(Module(new FiveBitsAdder))
  val productExp = Seq.fill(8)(Wire(SInt(6.W)))
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

  //第二级流水
  val reduction3 = Module(new Reduction(2,1))
  reduction3.io.in(0) := reducedProduct2(0)
  reduction3.io.in(1) := reducedProduct2(1)
  val reducedProduct3 = reduction3.io.out

  val acc = RegInit(0.S(FixedPoint.SHIFTED_LENGTH.W))
  when(io.clear === 1.U) {
    acc := 0.S(FixedPoint.SHIFTED_LENGTH.W)
  }
  .otherwise {
    acc := acc + reducedProduct3(0)
  }

  //规格化
  val normalizationShifter = Module(new NormalizationShifter)
  normalizationShifter.io.in := acc
  normalizationShifter.io.scale_a := io.scale_a
  normalizationShifter.io.scale_b := io.scale_b
  val normalizedResult = normalizationShifter.io.out
  io.out := normalizedResult

  //测试点
  // printf("input a(0): %b\n", io.a(0))
  // printf("input a(1): %b\n", io.a(1))
  // printf("input a(2): %b\n", io.a(2))
  // printf("input a(3): %b\n", io.a(3))
  // printf("input a(4): %b\n", io.a(4))
  // printf("input a(5): %b\n", io.a(5))
  // printf("input a(6): %b\n", io.a(6))
  // printf("input a(7): %b\n", io.a(7))
  // printf("input b(0): %b\n", io.b(0))
  // printf("input b(1): %b\n", io.b(1))
  // printf("input b(2): %b\n", io.b(2))
  // printf("input b(3): %b\n", io.b(3))
  // printf("input b(4): %b\n", io.b(4))
  // printf("input b(5): %b\n", io.b(5))
  // printf("input b(6): %b\n", io.b(6))
  // printf("input b(7): %b\n", io.b(7))
  // printf("productExp(1): %d\n", productExp(1))
  // printf("productExp(2): %d\n", productExp(2))
  // printf("productExp(3): %d\n", productExp(3))
  // printf("productExp(4): %d\n", productExp(4))
  // printf("productExp(5): %d\n", productExp(5))
  // printf("productExp(6): %d\n", productExp(6))
  // printf("productExp(7): %d\n", productExp(7))
  // printf("shiftedProduct(0): %b\n", shiftedProduct(0))
  // printf("leading zero position of shiftedProduct(0): %d\n", PriorityEncoder(Reverse(shiftedProduct(0).asUInt)))
  // printf("shiftedProduct(1): %b\n", shiftedProduct(1))
  // printf("shiftedProduct(2): %b\n", shiftedProduct(2))
  // printf("shiftedProduct(3): %b\n", shiftedProduct(3))
  // printf("shiftedProduct(4): %b\n", shiftedProduct(4))
  // printf("shiftedProduct(5): %b\n", shiftedProduct(5))
  // printf("shiftedProduct(6): %b\n", shiftedProduct(6))
  // printf("shiftedProduct(7): %b\n", shiftedProduct(7))
  // printf("reducedProduct(1): %b\n", reducedProduct(1))
  // printf("reducedProduct3  : %b\n", reducedProduct3(0))
  // printf("acc              : %b\n", acc)
  //printf("expectedreduction: %b\n", shiftedProduct(0) + shiftedProduct(1)+shiftedProduct(2)+shiftedProduct(3)+shiftedProduct(4)+shiftedProduct(5)+shiftedProduct(6)+shiftedProduct(7))
  //printf("length of reducedProduct3: %d\n", reducedProduct3(0).getWidth.U)
  //printf("outreal: %b\n", io.out)
  //printf(s"outreal: ${print_fp32_binary(BigInt(io.out, 2).U)}")
  // 删除对未定义函数的调用
  //printf("--------------------------------\n")
}

//mill -i FP8fpu.runMain fpu.core.Fpu
object Fpu extends App {
  ChiselStage.emitSystemVerilogFile(
    new Fpu,
    firtoolOpts = Array("-disable-all-randomization", "-strip-debug-info"),
    args = Array("--target-dir", "src/main/resources")
  )
}