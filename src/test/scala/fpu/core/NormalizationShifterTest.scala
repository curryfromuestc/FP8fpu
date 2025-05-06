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
            dut.io.in.poke(BigInt("111111110001010001100100001100000000010000000",2).S)
            dut.io.scaleA.poke(0.S)
            dut.io.scaleB.poke(0.S)
            dut.clock.step(2)
            // print("test1----------------------------------\n")
            // dut.io.in.poke(BigInt("111000101000101110000001010100111010001110100011001000",2).S)
            // dut.clock.step(3)
        }
    }
}