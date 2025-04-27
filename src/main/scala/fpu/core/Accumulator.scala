package fpu

import chisel3._
import chisel3.util._
import fpu.Float32._

class Accumulator extends Module {
    val io = IO(new Bundle {
        val inA = Input(SInt(Float32.LENGTH.W))
        val inB = Input(SInt(Float32.LENGTH.W))
        val out = Output(SInt(Float32.LENGTH.W))
    })

    val reg = Reg(SInt(Float32.LENGTH.W))
    reg := io.in
    io.out := reg
}