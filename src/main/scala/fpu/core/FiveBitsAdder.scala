package fpu.core

import chisel3._
import chisel3.util._
import _root_.circt.stage.ChiselStage

class FiveBitsAdder extends Module {
    val io = IO(new Bundle {
        val a = Input(UInt(4.W))
        val b = Input(UInt(4.W))
        val out = Output(SInt(6.W))
    })

    val aNobias = Wire(SInt(6.W))
    val bNobias = Wire(SInt(6.W))
    
    aNobias := Cat(0.U(1.W), io.a).asSInt - 7.S
    bNobias := Cat(0.U(1.W), io.b).asSInt - 7.S

    // printf("aNobias: %d\n", aNobias)
    // printf("bNobias: %d\n", bNobias)

    io.out := aNobias + bNobias
}

object FiveBitsAdder extends App {
  ChiselStage.emitSystemVerilogFile(
    new FiveBitsAdder,
    firtoolOpts = Array("-disable-all-randomization", "-strip-debug-info"),
  )
}

