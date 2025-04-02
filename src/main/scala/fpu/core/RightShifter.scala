package fpu.core

import chisel3._
import chisel3.util._
import fpu.Parameters._
import fpu._
class RightShifter extends Module {
  val io = IO(new Bundle {
    val inProduct = Input(SInt(FixedPoint.LENGTH.W))
    val inExp = Input(SInt(5.W))
    val out = Output(SInt(FixedPoint.SHIFTED_LENGTH.W))
  })

  val exp = Wire(SInt(8.W))
  exp := io.inExp
  val shiftAmount = Wire(SInt(8.W))
  shiftAmount := 21.S - exp
  val lowShiftAmount = Wire(UInt(3.W))
  lowShiftAmount := shiftAmount(2,0)
  val highShiftAmount = Wire(UInt(3.W))
  highShiftAmount := shiftAmount(5,3)

  val shiftReg = Reg(SInt(FixedPoint.FIRST_SHIFTED_LENGTH.W))
  val firstShifted = Wire(SInt(FixedPoint.FIRST_SHIFTED_LENGTH.W))
  firstShifted := Cat(io.inProduct, Fill(7,0.U)).asSInt

  //第一周期移位
  shiftReg := firstShifted >> lowShiftAmount.asUInt
  val secondShifted = Wire(SInt(FixedPoint.SHIFTED_LENGTH.W))
  secondShifted := Cat(shiftReg(15),shiftReg(12,0), Fill(28,0.U)).asSInt
  io.out := secondShifted >> (highShiftAmount.asUInt << 3)
  //printf("shiftAmount: %d\n", shiftAmount)
  //printf("lowShiftAmount: %d\n", lowShiftAmount)
  //printf("highShiftAmount: %d\n", highShiftAmount)
  //printf("io.inProduct: %b\n", io.inProduct.asSInt)
  //printf("io.inExp: %d\n", io.inExp)
  //printf("io.out: %b\n", io.out)
}