package fpu.core

import chisel3._
import chisel3.util._
import chisel3.experimental.BundleLiterals._
import chisel3.simulator.EphemeralSimulator._
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers
class MultiplierTest extends AnyFreeSpec with Matchers {
    "Multiplier should multiply two numbers correctly" in {
        simulate(new Multiplier) { dut =>
            val a = 7.U(3.W)
            val b = 7.U(3.W)
            val aSign = 0.U(1.W)
            val bSign = 0.U(1.W)
            val expected = 225.S(9.W)
            dut.io.aSign.poke(aSign)
            dut.io.bSign.poke(bSign)
            dut.io.a.poke(a)
            dut.io.b.poke(b)
            dut.clock.step(1)
            dut.io.out.expect(expected)

            val a2 = 3.U(3.W)
            val b2 = 3.U(3.W)
            val aSign2 = 1.U(1.W)
            val bSign2 = 0.U(1.W)
            val expected2 = -135.S(9.W)
            dut.io.aSign.poke(aSign2)
            dut.io.bSign.poke(bSign2)
            dut.io.a.poke(a2)
            dut.io.b.poke(b2)
            dut.clock.step(1)
        
            dut.io.out.expect(expected2)
        }
    }
}