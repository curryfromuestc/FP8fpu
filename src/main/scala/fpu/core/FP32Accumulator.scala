package fpu.core

import chisel3._
import chisel3.util._
import fpu.Parameters._
import fpu._

class FP32Accumulator extends Module {
  val io = IO(new Bundle {
    // 输入接口
    val input = Input(UInt(Float32.LENGTH.W))     // 输入的FP32数据
    val clear = Input(Bool())                     // 清零累加器信号
    val scale = Input(SInt(7.W))                  // 缩放因子
    
    // 输出接口
    val result = Output(SInt(Float32.SHIFTED_LENGTH.W))   // 当前累加结果
  })

  // 累加器状态
  val accumulator = RegInit(0.U(Float32.LENGTH.W))  // 累加器初始值为0

  when(io.clear) {
    accumulator := 0.U
  }
  .otherwise {
    accumulator := io.input
  }

  val scaleAnchor = (21.S - io.scale).asSInt

  val exp = accumulator(30, 23) - 127.U
  val mant = Wire(SInt(Float32.SHIFTED_LENGTH.W))
  when(accumulator(31)) {
    mant := Cat(accumulator(31),Fill(4,accumulator(31)),~Cat(1.U(1.W),accumulator(22, 0)) +1.U,Fill(25,0.U)).asSInt
  }
  .otherwise {
    mant := Cat(accumulator(31),Fill(4,accumulator(31)),Cat(1.U(1.W),accumulator(22, 0)),Fill(25,0.U)).asSInt
  }

  //对scaleAnchor-exp取绝对值
  val shiftAmount = Wire(SInt(9.W))
  shiftAmount := scaleAnchor - exp.asSInt 

  val shiftDirection = Wire(UInt(1.W))
  shiftDirection := shiftAmount(8)
  val shiftAmountAbs = shiftAmount.abs.asUInt

  val shiftedMant = RegInit(0.S(Float32.SHIFTED_LENGTH.W))
  when(shiftDirection === 1.U) {
    shiftedMant := mant << shiftAmountAbs
  }
  .otherwise {
    shiftedMant := mant >> shiftAmountAbs
  }

  io.result := shiftedMant

  //测试点
  // printf("accumulator: %b\n", accumulator)
  // printf("exp: %b\n", exp)
  // printf("shiftAmount: %b\n", shiftAmount)
  // printf("shiftDirection: %b\n", shiftDirection)
  // printf("shiftAmountAbs: %b\n", shiftAmountAbs)
  // printf("shiftedMant: %b\n", shiftedMant)
  // printf("io.result: %b\n", io.result)
}   