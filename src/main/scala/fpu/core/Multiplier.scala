package fpu.core

import chisel3._
import chisel3.util._
import fpu.Parameters._
import fpu._

class Multiplier extends Module {
    val io = IO(new Bundle {
        val aSign = Input(UInt(1.W))
        val bSign = Input(UInt(1.W))
        val a = Input(UInt(Float8E4M3.sigWidth.W))
        val b = Input(UInt(Float8E4M3.sigWidth.W))
        val out = Output(SInt(FixedPoint.LENGTH.W))
    })

    val inA = Cat(1.U(1.W), io.a)
    val inB = Cat(1.U(1.W), io.b)

    val partialProducts = Seq.tabulate(Float8E4M3.sigWidth + 1) { i =>
        val shiftedA = inA << i
        Mux(inB(i), shiftedA, 0.U)
    }

    val alignedPartialProducts = partialProducts.map { pp =>
        Cat(0.U((8-pp.getWidth).W), pp)
    }

    val sum = alignedPartialProducts.reduce(_ + _)

    val outSign = io.aSign ^ io.bSign

    val rawOut = Cat(outSign, sum)
    
    // 补码转换
    val out = Wire(UInt(9.W))
    when(rawOut(8)) {
        out := Cat(rawOut(8), ~rawOut(7,0) + 1.U)
    }
    .otherwise {
        out := rawOut
    }
    
    //printf("out: %b\n", out)
    //printf("out.asSInt: %b\n", out.asSInt)

    io.out := out.asSInt
}

