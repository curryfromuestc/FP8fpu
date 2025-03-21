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
            dut.io.a.poke(1.U(5.W))
            dut.io.b.poke(1.U(5.W))
            
            dut.clock.step(1)  // 添加时钟步进
            
            dut.io.out.expect(-28.S(6.W))
        }
    }
}