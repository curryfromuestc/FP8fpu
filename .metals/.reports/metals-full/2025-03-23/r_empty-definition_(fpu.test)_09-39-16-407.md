error id: 
file://<WORKSPACE>/src/test/scala/fpu/core/RightShifterTest.scala
empty definition using pc, found symbol in pc: 
empty definition using semanticdb
|empty definition using fallback
non-local guesses:
	 -

Document text:

```scala
package fpu.core

import chisel3._
import chisel3.util._
import chisel3.experimental.BundleLiterals._
import chisel3.simulator.EphemeralSimulator._
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers

class RightShifterTest extends AnyFreeSpec with Matchers {
    "RightShifter should shift the input data correctly" in {
        simulate(new RightShifter) { dut =>
            dut.io.inProduct.poke(193.U)
            dut.io.inExp.poke(-28.S)
            dut.clock.step(2)
            dut.io.out.expect(193.S)

            dut.io.inProduct.poke(321.U)
            dut.io.inExp.poke(30.S)
            dut.clock.step(2)
            dut.io.out.expect((-127 * (2 ^ 58) ).S)
        }
    }
}
```

#### Short summary: 

empty definition using pc, found symbol in pc: 