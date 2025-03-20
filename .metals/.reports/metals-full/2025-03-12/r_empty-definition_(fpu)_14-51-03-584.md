error id: chisel3/experimental/BaseModule#IO().
file://<WORKSPACE>/src/main/scala/fpu/core/FP8Converter.scala
empty definition using pc, found symbol in pc: 
found definition using semanticdb; symbol chisel3/experimental/BaseModule#IO().
|empty definition using fallback
non-local guesses:
	 -

Document text:

```scala
package fpu.core

import chisel3._
import chisel3.util._
//import fpu.Parameters._
import fpu._

class FP8Converter extends Module {
    val io = IO(new Bundle {
        val in = Input(Vec(WAY_SIZE, UInt(Parameters.FP8_LENGTH.W)))
        val inType = Input(Bool())
        val out = Output(Vec(WAY_SIZE, UInt(IntermediateFormat.LENGTH.W)))
    })

    
}
```

#### Short summary: 

empty definition using pc, found symbol in pc: 