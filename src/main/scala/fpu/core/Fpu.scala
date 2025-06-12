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
    val clear = Input(Bool())
    val out = Output(SInt(ACC_LENGTH.W))
    val outAnchor = Output(SInt(6.W))
  })

  val aReg = RegInit(VecInit(Seq.fill(8)(0.U(FP8_LENGTH.W))))
  val bReg = RegInit(VecInit(Seq.fill(8)(0.U(FP8_LENGTH.W))))

  for (i <- 0 until 8) {
    aReg(i) := io.a(i)
    bReg(i) := io.b(i)
  }
  
  //指数相加，尾数相乘
  val multiplier = Seq.fill(8)(Module(new Multiplier))
  val product = Seq.fill(8)(Wire(SInt(FixedPoint.LENGTH.W)))
  val fiveBitsAdder = Seq.fill(8)(Module(new FiveBitsAdder))
  val productExp = Seq.fill(8)(Wire(SInt(6.W)))
  for (i <- 0 until 8) {
    multiplier(i).io.aSign := aReg(i)(7)
    multiplier(i).io.bSign := bReg(i)(7)
    multiplier(i).io.a := aReg(i)(2,0)
    multiplier(i).io.b := bReg(i)(2,0)
    product(i) := multiplier(i).io.out
    fiveBitsAdder(i).io.a := aReg(i)(6,3)
    fiveBitsAdder(i).io.b := bReg(i)(6,3)
    productExp(i) := fiveBitsAdder(i).io.out
  }
  val rightShifter = Module(new RightShifter)
  val shiftedProduct = Seq.fill(8)(Wire(SInt(FixedPoint.SHIFTED_LENGTH.W)))
  for (i <- 0 until 8) {
    rightShifter.io.inProduct(i) := product(i)
    rightShifter.io.inExp(i) := productExp(i)
    shiftedProduct(i) := rightShifter.io.outShifted(i)
  }
  //8to4 reduction
  val reduction = Module(new Reduction(8,4,FixedPoint.SHIFTED_LENGTH,REDUCTION8TO4))
  for (i <- 0 until 8) {
    reduction.io.in(i) := shiftedProduct(i)
  }
  val reducedProduct = reduction.io.out
  //4to2 reduction
  val reduction2 = Module(new Reduction(4,2,REDUCTION8TO4,REDUCTION4TO2))
  for (i <- 0 until 4) {
    reduction2.io.in(i) := reducedProduct(i)
  }
  val reducedProduct2 = reduction2.io.out
  val reduction3 = Module(new Reduction(2,1,REDUCTION4TO2,REDUCTION2TO1))
  reduction3.io.in(0) := reducedProduct2(0)
  reduction3.io.in(1) := reducedProduct2(1)
  val reducedProduct3 = Reg(SInt(REDUCTION2TO1.W))
  reducedProduct3 := reduction3.io.out(0)
  val anchor3 = Reg(SInt(6.W))
  anchor3 := rightShifter.io.outAnchor

  //对比anchor3和anchor4
  val anchor4 = Reg(SInt(6.W))
  val acc = RegInit(0.S(ACC_LENGTH.W))

  when(io.clear) {
    val addReducedProduct3 = Cat(Fill(4,reducedProduct3(REDUCTION2TO1-1)),reducedProduct3).asSInt
    acc := addReducedProduct3
    anchor4 := anchor3
  }
  .elsewhen(anchor4 > anchor3) {
    val shiftAmountACC = (anchor4 - anchor3).asUInt
    val addReducedProduct3 = Cat(Fill(4,reducedProduct3(REDUCTION2TO1-1)),reducedProduct3).asSInt
    acc := acc + (addReducedProduct3 >> shiftAmountACC)(ACC_LENGTH-1,0).asSInt
    anchor4 := anchor4
  }
  .elsewhen(anchor4 < anchor3) {
    val shiftAmountACC = (anchor3 - anchor4).asUInt
    val addReducedProduct3 = Cat(Fill(4,reducedProduct3(REDUCTION2TO1-1)),reducedProduct3).asSInt
    acc := (acc >> shiftAmountACC)(ACC_LENGTH-1,0).asSInt + addReducedProduct3
    anchor4 := anchor3
  }
  .otherwise{
    val addReducedProduct3 = Cat(Fill(4,reducedProduct3(REDUCTION2TO1-1)),reducedProduct3).asSInt
    acc := acc + addReducedProduct3
    anchor4 := anchor4
  }

  io.out := acc
  io.outAnchor := anchor4
  //测试点
  // printf("shiftedProduct(0): %b\n", shiftedProduct(0))
  // printf("leading zero position of shiftedProduct(0): %d\n", PriorityEncoder(Reverse(shiftedProduct(0).asUInt)))
  printf("shiftedProduct(1): %b\n", shiftedProduct(1))
  printf("shiftedProduct(2): %b\n", shiftedProduct(2))
  printf("shiftedProduct(3): %b\n", shiftedProduct(3))
  printf("shiftedProduct(4): %b\n", shiftedProduct(4))
  printf("shiftedProduct(5): %b\n", shiftedProduct(5))
  printf("shiftedProduct(6): %b\n", shiftedProduct(6))
  printf("shiftedProduct(7): %b\n", shiftedProduct(7))
  printf("outAnchor        : %d\n", rightShifter.io.outAnchor)
  printf("reducedProduct3  : %b\n", reducedProduct3)
  printf("anchor3          : %d\n", anchor3)
  printf("anchor4          : %d\n", anchor4)
  printf("acc              : %b\n", acc)
  //printf("expectedreduction: %b\n", shiftedProduct(0) + shiftedProduct(1)+shiftedProduct(2)+shiftedProduct(3)+shiftedProduct(4)+shiftedProduct(5)+shiftedProduct(6)+shiftedProduct(7))
  //printf("length of reducedProduct3: %d\n", reducedProduct3(0).getWidth.U)
  //printf("outreal: %b\n", io.out)
  //printf(s"outreal: ${print_fp32_binary(BigInt(io.out, 2).U)}")
  // 删除对未定义函数的调用
  //printf("--------------------------------\n")
}

//mill -i FP8fpu.runMain fpu.core.Fpu