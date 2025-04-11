package fpu.core

import chisel3._
import chisel3.util._
import chisel3.experimental.BundleLiterals._
import chisel3.simulator.EphemeralSimulator._
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers
import chisel3.simulator.EphemeralSimulator._

class NormalizationShifterTest extends AnyFreeSpec with Matchers {
    "NormalizationShifter should shift the input data correctly" in {
        simulate(new NormalizationShifter) { dut =>
            dut.io.in.poke(BigInt("111110000011101101110111100000001101001100000000000000",2).S)
            dut.io.scale_a.poke(2.S)
            dut.io.scale_b.poke(2.S)
            dut.clock.step(3)
            // print("test1----------------------------------\n")
            // dut.io.in.poke(BigInt("111000101000101110000001010100111010001110100011001000",2).S)
            // dut.clock.step(3)
        }
    }
}