package fpu.core

import chisel3._
import chisel3.util._
import chisel3.experimental.BundleLiterals._
import chisel3.simulator.EphemeralSimulator._
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers


class FP32AccumulatorTest extends AnyFreeSpec with Matchers {
    "FP32Accumulator should accumulate the input data correctly" in {
        simulate(new FP32Accumulator) { dut =>
            dut.io.input.poke("b1_10010100_1000_1000_1000_1000_1000_100".U)
            dut.io.clear.poke(false.B)
            dut.io.scale.poke(2.S)
            dut.clock.step(3)
        }
    }
}

