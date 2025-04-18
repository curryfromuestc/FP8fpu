package fpu.core

package fpu.core

import chisel3._
import chisel3.util._
import chisel3.experimental.BundleLiterals._
import chisel3.simulator.EphemeralSimulator._
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers

class FiveBitsAdderTest extends AnyFreeSpec with Matchers {
    "FiveBitsAdder should add two 5-bit numbers correctly" in {
        simulate(new FiveBitsAdder) { dut =>
            dut.io.a.poke(1.U(4.W))
            dut.io.b.poke(1.U(4.W))
            
            dut.clock.step(1)  // 添加时钟步进
            
            dut.io.out.expect(-12.S(6.W))

            dut.io.a.poke(15.U(4.W))
            dut.io.b.poke(15.U(4.W))
            dut.clock.step(1)
            dut.io.out.expect(16.S(6.W))
        }
    }
}