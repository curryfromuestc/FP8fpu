package fpu.core

import chisel3._
import chisel3.util._
import fpu.Parameters._
import fpu._

class FP32Accumulator extends Module {
  val io = IO(new Bundle {
    // 输入接口
    val input = Input(UInt(Float32.LENGTH.W))     // 输入的FP32数据
    val valid = Input(Bool())                     // 输入有效信号
    val clear = Input(Bool())                     // 清零累加器信号
    val scale = Input(UInt(7.W))                  // 缩放因子
    
    // 输出接口
    val result = Output(UInt(Float32.SHIFTED_LENGTH.W))   // 当前累加结果
    val ready = Output(Bool())                    // 累加器就绪信号
  })

  // 累加器状态
  val accumulator = RegInit(0.U(Float32.LENGTH.W))  // 累加器初始值为0

  when(io.valid) {
    accumulator := io.input
  }
  .otherwise {
    accumulator := accumulator
  }

  when(io.clear) {
    accumulator := 0.U
  }
  .otherwise {
    accumulator := accumulator
  }

  val scaleAnchor = (21.U - io.scale).asSInt

  val exp = accumulator(30, 23)
  val mant = accumulator(22, 0).asUInt.asSInt(23.W)

  //对scaleAnchor-exp取绝对值
  val shiftAmount = scaleAnchor - exp


  val shiftAmountReg = RegInit(0.U(9.W))
  shiftAmountReg := shiftAmount

  val shiftDirection = shiftAmountReg(8)
  val shiftAmountAbs = shiftAmountReg.abs

  
}   