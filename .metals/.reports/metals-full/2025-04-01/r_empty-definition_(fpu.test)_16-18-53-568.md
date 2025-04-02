error id: org/scalatest/matchers/must/Matchers.
file://<WORKSPACE>/src/test/scala/fpu/core/ReductionTest.scala
empty definition using pc, found symbol in pc: org/scalatest/matchers/must/Matchers.
empty definition using semanticdb
empty definition using fallback
non-local guesses:
	 -chisel3/Matchers.
	 -chisel3/Matchers#
	 -chisel3/Matchers().
	 -chisel3/util/Matchers.
	 -chisel3/util/Matchers#
	 -chisel3/util/Matchers().
	 -chisel3/experimental/BundleLiterals.Matchers.
	 -chisel3/experimental/BundleLiterals.Matchers#
	 -chisel3/experimental/BundleLiterals.Matchers().
	 -chisel3/simulator/EphemeralSimulator.Matchers.
	 -chisel3/simulator/EphemeralSimulator.Matchers#
	 -chisel3/simulator/EphemeralSimulator.Matchers().
	 -org/scalatest/matchers/must/Matchers.
	 -org/scalatest/matchers/must/Matchers#
	 -org/scalatest/matchers/must/Matchers().
	 -Matchers.
	 -Matchers#
	 -Matchers().
	 -scala/Predef.Matchers.
	 -scala/Predef.Matchers#
	 -scala/Predef.Matchers().
offset: 233
uri: file://<WORKSPACE>/src/test/scala/fpu/core/ReductionTest.scala
text:
```scala
package fpu.core

import chisel3._
import chisel3.util._
import chisel3.experimental.BundleLiterals._
import chisel3.simulator.EphemeralSimulator._
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers@@

class ReductionTest extends AnyFreeSpec with Matchers {
    "Reduction should handle 4to2 reduction correctly" in {
        simulate(new Reduction(4, 2)) { dut =>
            // 测试4to2 reduction
            dut.io.in(0).poke(10.S)
            dut.io.in(1).poke(20.S)
            dut.io.in(2).poke(30.S)
            dut.io.in(3).poke(40.S)
            
            dut.clock.step(1)
            
            // 验证第一组：10 + 20 = 30
            dut.io.out(0).expect(30.S)
            // 验证第二组：30 + 40 = 70
            dut.io.out(1).expect(70.S)
        }
    }

    "Reduction should handle 3to2 reduction correctly" in {
        simulate(new Reduction(3, 2)) { dut =>
            // 测试3to2 reduction
            // 确保只设置3个输入值
            dut.io.in(0).poke(10.S)
            dut.io.in(1).poke(20.S)
            dut.io.in(2).poke(30.S)
            
            dut.clock.step(1)
            
            // 验证第一组：10 + 20 = 30
            dut.io.out(0).expect(30.S)
            // 验证第二组：30
            dut.io.out(1).expect(30.S)
        }
    }

    "Reduction should handle invalid reduction modes" in {
        simulate(new Reduction(5, 2)) { dut =>
            // 测试无效的reduction模式
            dut.io.in(0).poke(10.S)
            dut.io.in(1).poke(20.S)
            dut.io.in(2).poke(30.S)
            dut.io.in(3).poke(40.S)
            dut.io.in(4).poke(50.S)
            
            dut.clock.step(1)
            
            // 验证输出应该都是0
            dut.io.out(0).expect(0.S)
            dut.io.out(1).expect(0.S)
        }
    }
} 
```


#### Short summary: 

empty definition using pc, found symbol in pc: org/scalatest/matchers/must/Matchers.