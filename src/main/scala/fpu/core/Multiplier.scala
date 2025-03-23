package fpu.core

import chisel3._
import chisel3.util._
import fpu.Parameters._
import fpu._

class Multiplier extends Module {
    val io = IO(new Bundle {
        val aSign = Input(UInt(1.W))
        val bSign = Input(UInt(1.W))
        val a = Input(UInt(IntermediateFormat.sigWidth.W))
        val b = Input(UInt(IntermediateFormat.sigWidth.W))
        val out = Output(SInt(FixedPoint.LENGTH.W))
    })

    val inA = Cat(1.U(1.W), io.a)
    val inB = Cat(1.U(1.W), io.b)

    val partialProducts = Seq.tabulate(IntermediateFormat.sigWidth + 1) { i =>
        val shiftedA = inA << i
        Mux(inB(i), shiftedA, 0.U)
    }

    val alignedPartialProducts = partialProducts.map { pp =>
        Cat(0.U((8-pp.getWidth).W), pp)
    }

    val sum = alignedPartialProducts.reduce(_ + _)

    val outSign = io.aSign ^ io.bSign

    val out = Cat(outSign, sum)
    //printf("out: %b\n", out)
    //printf("out.asSInt: %b\n", out.asSInt)

    io.out := out.asSInt
}

