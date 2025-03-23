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
    
    // 输出接口
    val result = Output(UInt(Float32.LENGTH.W))   // 当前累加结果
    val ready = Output(Bool())                    // 累加器就绪信号
    
    // 状态标志
    val overflow = Output(Bool())                 // 溢出标志
    val underflow = Output(Bool())                // 下溢标志
    val invalid = Output(Bool())                  // 非法操作标志
  })
}   