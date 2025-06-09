package fpu.core

import chisel3._
import fpu.Parameters._
import fpu._

class Reduction(val inNum: Int, val outNum: Int, val inWidth: Int, val outWidth: Int) extends Module {
  val io = IO(new Bundle {
    val in = Input(Vec(inNum, SInt(inWidth.W)))
    val out = Output(Vec(outNum, SInt(outWidth.W)))
  })

  // 初始化输出为0
  io.out.foreach(_ := 0.S)

  // 根据编译时参数选择不同的reduction模式
  if (inNum == 8 && outNum == 4) {
    // 8to4 reduction
    io.out(0) := io.in(0) +& io.in(1)
    io.out(1) := io.in(2) +& io.in(3)
    io.out(2) := io.in(4) +& io.in(5)
    io.out(3) := io.in(6) +& io.in(7)
  } else if (inNum == 4 && outNum == 2) {
    // 4to2 reduction
    io.out(0) := io.in(0) +& io.in(1)
    io.out(1) := io.in(2) +& io.in(3)
  } else if (inNum == 2 && outNum == 1) {
    // 2to1 reduction
    io.out(0) := io.in(0) +& io.in(1)
  }
}
