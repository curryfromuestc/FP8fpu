error id: `<none>`.
file://<WORKSPACE>/src/main/scala/fpu/core/Multiplier.scala
empty definition using pc, found symbol in pc: `<none>`.
empty definition using semanticdb
|empty definition using fallback
non-local guesses:
	 -chisel3/IntermediateFormat.sigWidth.
	 -chisel3/util/IntermediateFormat.sigWidth.
	 -fpu/Parameters.IntermediateFormat.sigWidth.
	 -fpu/IntermediateFormat.sigWidth.
	 -IntermediateFormat.sigWidth.
	 -scala/Predef.IntermediateFormat.sigWidth.

Document text:

```scala
package fpu.core

import chisel3._
import chisel3.util._
import fpu.Parameters._
import fpu._

class Multiplier extends Module {
    val io = IO(new Bundle {
        val a = Input(UInt(IntermediateFormat.sigWidth.W))
        val b = Input(UInt(IntermediateFormat.sigWidth.W))
        val out = Output(UInt(FixedPoint.FRACTION.W))
    })
}


```

#### Short summary: 

empty definition using pc, found symbol in pc: `<none>`.