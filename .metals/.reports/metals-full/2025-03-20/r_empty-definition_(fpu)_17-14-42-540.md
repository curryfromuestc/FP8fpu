error id: chisel3/Bits#asSInt().
file://<WORKSPACE>/src/main/scala/fpu/core/FiveBitsAdder.scala
empty definition using pc, found symbol in pc: 
found definition using semanticdb; symbol chisel3/Bits#
|empty definition using fallback
non-local guesses:
	 -

Document text:

```scala
package fpu.core

import chisel3._
import chisel3.util._

class FiveBitsAdder extends Module {
    val io = IO(new Bundle {
        val a = Input(UInt(5.W))
        val b = Input(UInt(5.W))
        val out = Output(UInt(6.W))
    })

    val aNobias = Wire(UInt(6.W))
    val bNobias = Wire(UInt(6.W))
    
    aNobias := Cat(0.U(1.W), io.a) + BigInt("110001", 2)
    bNobias := Cat(0.U(1.W), io.b) + BigInt("110001", 2)

    io.out := aNobias + bNobias
}


```

#### Short summary: 

empty definition using pc, found symbol in pc: 