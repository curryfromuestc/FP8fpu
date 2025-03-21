package fpu.core

import chisel3._
import chisel3.util._

class FiveBitsAdder extends Module {
    val io = IO(new Bundle {
        val a = Input(UInt(5.W))
        val b = Input(UInt(5.W))
        val out = Output(SInt(6.W))
    })

    val aNobias = Wire(SInt(6.W))
    val bNobias = Wire(SInt(6.W))
    
    aNobias := Cat(0.U(1.W), io.a).asSInt - 15.S
    bNobias := Cat(0.U(1.W), io.b).asSInt - 15.S

    printf("aNobias: %d\n", aNobias)
    printf("bNobias: %d\n", bNobias)

    io.out := aNobias + bNobias
}

