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
  val shiftAmount2Reg = Reg(Vec(8,UInt(2.W)))
  val shifted = Wire(Vec(8,SInt(FixedPoint.SHIFTED_LENGTH.W)))

  val firstShifted = Reg(Vec(8,SInt(FixedPoint.FIRST_SHIFTED_LENGTH.W)))
  val anchorReg = Reg(SInt(6.W))

  anchor := exp.reduce((a,b) => Mux(a > b, a, b))
  anchorReg := anchor

  for (i <- 0 until 8) {
    exp(i) := io.inExp(i)
    shiftAmount(i) := (anchor - exp(i)).asUInt
    shiftAmount2Reg(i) := shiftAmount(i)(4,3)
    firstShifted(i) := Cat(io.inProduct(i), Fill(7,0.U)).asSInt >> shiftAmount(i)(2,0)
    shifted(i) := (Cat(firstShifted(i), Fill(21,0.U)).asSInt >> (shiftAmount2Reg(i) << 3))(36,10).asSInt
  }

  io.outShifted := shifted
  io.outAnchor := anchorReg

  // printf("anchor           : %d\n", anchor)
  // printf("inProduct(0)      : %b\n", io.inProduct(0))
  // printf("inProduct(1)      : %b\n", io.inProduct(1))
  // printf("inProduct(2)      : %b\n", io.inProduct(2))
  // printf("inProduct(3)      : %b\n", io.inProduct(3))
  // printf("inProduct(4)      : %b\n", io.inProduct(4))
  // printf("inProduct(5)      : %b\n", io.inProduct(5))
  // printf("inProduct(6)      : %b\n", io.inProduct(6))
  // printf("inProduct(7)      : %b\n", io.inProduct(7))
  // printf("shiftAmount(0)   : %d\n", shiftAmount(0))
  // printf("shiftAmount(1)   : %d\n", shiftAmount(1))
  // printf("shiftAmount(2)   : %d\n", shiftAmount(2))
  // printf("shiftAmount(3)   : %d\n", shiftAmount(3))
  // printf("shiftAmount(4)   : %d\n", shiftAmount(4))
  // printf("shiftAmount(5)   : %d\n", shiftAmount(5))
  // printf("shiftAmount(6)   : %d\n", shiftAmount(6))
  // printf("firstShifted(0)  : %b\n", firstShifted(0))
  // printf("firstShifted(1)  : %b\n", firstShifted(1))
  // printf("firstShifted(2)  : %b\n", firstShifted(2))
  // printf("firstShifted(3)  : %b\n", firstShifted(3))
  // printf("firstShifted(4)  : %b\n", firstShifted(4))
  // printf("firstShifted(5)  : %b\n", firstShifted(5))
  // printf("firstShifted(6)  : %b\n", firstShifted(6))
  // printf("firstShifted(7)  : %b\n", firstShifted(7))
}