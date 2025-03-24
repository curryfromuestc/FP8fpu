package fpu.core

import chisel3._
import fpu.Parameters._
import fpu._

class Reduction(val inNum: Int, val outNum: Int) extends Module {
  val io = IO(new Bundle {
    val in = Input(Vec(inNum, SInt(FixedPoint.SHIFTED_LENGTH.W)))
    val out = Output(Vec(outNum, SInt(FixedPoint.SHIFTED_LENGTH.W)))
  })

  // 初始化输出为0
  io.out.foreach(_ := 0.S)

  // 根据编译时参数选择不同的reduction模式
  if (inNum == 4 && outNum == 2) {
    // 4to2 reduction
    io.out(0) := io.in(0) + io.in(1)
    io.out(1) := io.in(2) + io.in(3)
  } else if (inNum == 3 && outNum == 2) {
    // 3to2 reduction
    io.out(0) := io.in(0) + io.in(1)
    io.out(1) := io.in(2)
  }
}
