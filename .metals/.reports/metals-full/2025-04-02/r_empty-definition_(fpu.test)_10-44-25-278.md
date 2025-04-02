error id: chisel3/experimental/package.BundleLiterals.
file://<WORKSPACE>/src/test/scala/fpu/core/FpuTest.scala
empty definition using pc, found symbol in pc: 
found definition using semanticdb; symbol chisel3/experimental/package.BundleLiterals.
empty definition using fallback
non-local guesses:

offset: 99
uri: file://<WORKSPACE>/src/test/scala/fpu/core/FpuTest.scala
text:
```scala
package fpu.core

import chisel3._
import chisel3.util._
import chisel3.experimental.BundleLiterals@@._
import chisel3.simulator.EphemeralSimulator._
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers

class FpuTest extends AnyFreeSpec with Matchers {
    "Fpu should work correctly" in {
        simulate(new Fpu) { dut =>
            dut.io.a(0).poke("b00111111".U)
            dut.io.b(0).poke("b00111111".U)
            dut.io.a(1).poke("b01111111".U)
            dut.io.b(1).poke("b01111111".U)
            dut.io.a(2).poke("b10111111".U)
            dut.io.b(2).poke("b10111111".U)
            dut.io.a(3).poke("b11111111".U)
            dut.io.b(3).poke("b11111111".U)
            dut.io.a(4).poke("b00001000".U)
            dut.io.b(4).poke("b00001000".U)
            dut.io.a(5).poke("b00010100".U)
            dut.io.b(5).poke("b00010010".U)
            dut.io.a(6).poke("b00011010".U)
            dut.io.b(6).poke("b00011010".U)
            dut.io.a(7).poke("b00100000".U)
            dut.io.b(7).poke("b00100001".U)
            dut.io.accIn.poke("b1_10010100_0000_0000_0000_0000_0000_000".U)
            dut.io.clear.poke(false.B)
            dut.clock.step(6)
        }
    }
}
```


#### Short summary: 

empty definition using pc, found symbol in pc: 