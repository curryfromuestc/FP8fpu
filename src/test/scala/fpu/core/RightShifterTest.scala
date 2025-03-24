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
            dut.io.inProduct.poke(193.S)
            dut.io.inExp.poke(-14.S)
            dut.clock.step(2)
            dut.io.out.expect(193.S)

            dut.io.inProduct.poke(-127.S)
            dut.io.inExp.poke(16.S)
            dut.clock.step(2)
            //dut.io.out.expect((BigInt(-127) << 58).S)
        }
    }
}