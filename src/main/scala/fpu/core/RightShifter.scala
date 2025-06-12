package fpu.core

import chisel3._
import chisel3.util._
import fpu.Parameters._
import fpu._
class RightShifter extends Module {
  val io = IO(new Bundle {
    val inProduct = Input(Vec(8,SInt(FixedPoint.LENGTH.W)))
    val inExp = Input(Vec(8,SInt(6.W)))
    val outShifted = Output(Vec(8,SInt(FixedPoint.SHIFTED_LENGTH.W)))
    val outAnchor = Output(SInt(6.W))
  })

  val exp = Wire(Vec(8,SInt(6.W)))
  val anchor = Wire(SInt(6.W))
  val shiftAmount = Wire(Vec(8,UInt(5.W)))
  val shifted = Wire(Vec(8,SInt(FixedPoint.SHIFTED_LENGTH.W)))

  val firstShifted = Reg(Vec(8,SInt(FixedPoint.FIRST_SHIFTED_LENGTH.W)))
  val anchorReg = Reg(SInt(6.W))

  anchor := exp.reduce((a,b) => Mux(a > b, a, b))
  anchorReg := anchor

  for (i <- 0 until 8) {
    exp(i) := io.inExp(i)
    shiftAmount(i) := (anchor - exp(i)).asUInt
    firstShifted(i) := io.inProduct(i) << shiftAmount(i)(2,0)
    shifted(i) := (Cat(firstShifted(i), Fill(21,0.U)).asSInt << shiftAmount(i)(4,3))(36,10).asSInt
  }

  io.outShifted := shifted
  io.outAnchor := anchorReg
}