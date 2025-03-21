error id: chisel3/package.fromBigIntToLiteral#U().
file://<WORKSPACE>/src/main/scala/fpu/core/Multiplier.scala
empty definition using pc, found symbol in pc: 
found definition using semanticdb; symbol chisel3/package.fromBigIntToLiteral#U().
|empty definition using fallback
non-local guesses:
	 -

Document text:

```scala
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
        val out = Output(UInt(FixedPoint.LENGTH.W))
        val overflow = Output(UInt(1.W))
        val outSign = Output(UInt(1.W))
    })

    val inA = Cat(1.U(1.W), io.a)
    val inB = Cat(1.U(1.W), io.b)

    val PartialProduct = Seq.tabulate(IntermediateFormat.sigWidth+1) { i =>
        val shiftedA = inA << i
        Mux(inB(i), shiftedA, 0.U)
    }

    val alignedPartialProduct = PartialProduct.map { pp =>
        Cat(0.U((8-pp.getWidth).W), pp)
    }

    val sum = alignedPartialProduct.reduce(_ +&  _)

    val outSign = io.aSign ^ io.bSign
    val out = Cat(outSign, 0.U((8-sum.getWidth).W), sum)
    val overflow = out(7)
    //转化为补码
    val out_twos_complement = Cat(out(8), ~out(7,0) + 1.U)


    io.out := out_twos_complement
    io.overflow := overflow
    io.outSign := out(8)
}


```

#### Short summary: 

empty definition using pc, found symbol in pc: 