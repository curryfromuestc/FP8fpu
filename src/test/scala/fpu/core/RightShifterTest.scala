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
            dut.io.isOverflow.poke(0.U)
            
            dut.clock.step(2)
            
            dut.io.out.expect(193.U)
        }
    }
}