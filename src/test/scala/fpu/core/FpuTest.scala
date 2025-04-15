package fpu.core

import chisel3._
import chisel3.util._
import chisel3.experimental.BundleLiterals._
import chisel3.simulator.EphemeralSimulator._
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers
import scala.math._
import scala.io.Source
import java.io.File

class FpuTest extends AnyFreeSpec with Matchers {
    def print_fp8_binary(value: UInt): Double = {
        val value_int = value.peek().litValue
        val sign_bit = (value_int >> 7) & 1
        val exp_bit = (value_int >> 3) & 0xF
        val mant_bit = value_int & 0x7
        val decimal_value = if(sign_bit == 0){
            (1.0 + mant_bit.toDouble/8.0) * math.pow(2, exp_bit.toDouble-7.0)
        } else {
            -(1.0 + mant_bit.toDouble/8.0) * math.pow(2, exp_bit.toDouble-7.0)
        }
        return decimal_value
    }
    def print_fp32_binary(value: UInt): Double = {
        val value_int = try {
            value.peek().litValue
        } catch {
            case _: java.util.NoSuchElementException => value.litValue
        }
        
        val sign_bit = (value_int >> 31) & 1
        val exp_bit = (value_int >> 23) & 0xFF
        val mant_bit = value_int & 0x7FFFFF
        println(s"Sign: $sign_bit, Exp: $exp_bit, Mant: $mant_bit")
        val decimal_value = if(sign_bit == 0){
            (1.0 + mant_bit.toDouble/0x7FFFFF) * math.pow(2, exp_bit.toDouble-127.0)
        } else {
            -(1.0 + mant_bit.toDouble/0x7FFFFF) * math.pow(2, exp_bit.toDouble-127.0)
        }
        return decimal_value
    }
    //将fp32转化为二进制
    def fp32_to_binary(value: Double): String = {
        val sign_bit = if(value < 0) 1 else 0
        val abs_value = math.abs(value)
        val exp_bit = math.floor(math.log(abs_value)/math.log(2)).toInt + 127
        val mant_bit = ((abs_value/math.pow(2, exp_bit-127) - 1.0) * 0x7FFFFF).toInt
        val binary_value = (sign_bit << 31) | (exp_bit << 23) | mant_bit
        return binary_value.toBinaryString
    }
    
    "Fpu should work correctly" in {
        simulate(new Fpu) { dut =>
            val a_txt = Source.fromFile("/nfs/home/yangguolin/code/FP8fpu/src/test/scala/fpu/core/a.txt").getLines().toArray
            val b_txt = Source.fromFile("/nfs/home/yangguolin/code/FP8fpu/src/test/scala/fpu/core/b.txt").getLines().toArray
            for (i <- 0 until 32){
                for (j <- 0 until 8){
                    dut.io.a(j).poke(("b" + a_txt(i*8+j)).U)
                    dut.io.b(j).poke(("b" + b_txt(i*8+j)).U)
                }
                dut.io.clear.poke((i == 0).B)  // 第一次计算时清除累加器
                dut.io.scale_a.poke(2.S)
                dut.io.scale_b.poke(2.S)
                dut.clock.step(1)
                println(s"clock step: $i")
            }
            dut.clock.step(6)
            
            // dut.io.a(0).poke("b00111111".U)
            // val a0 = print_fp8_binary(dut.io.a(0))
            // println(s"a0: $a0")
            // dut.io.b(0).poke("b00111111".U)
            // val b0 = print_fp8_binary(dut.io.b(0))
            // println(s"b0: $b0")
            // dut.io.a(1).poke("b01111110".U)
            // val a1 = print_fp8_binary(dut.io.a(1))
            // println(s"a1: $a1")
            // dut.io.b(1).poke("b11111110".U)
            // val b1 = print_fp8_binary(dut.io.b(1))
            // println(s"b1: $b1")
            // dut.io.a(2).poke("b10111111".U)
            // val a2 = print_fp8_binary(dut.io.a(2))
            // println(s"a2: $a2")
            // dut.io.b(2).poke("b00111111".U)
            // val b2 = print_fp8_binary(dut.io.b(2))
            // println(s"b2: $b2")
            // dut.io.a(3).poke("b11111110".U)
            // val a3 = print_fp8_binary(dut.io.a(3))
            // println(s"a3: $a3")
            // dut.io.b(3).poke("b01111110".U)
            // val b3 = print_fp8_binary(dut.io.b(3))
            // println(s"b3: $b3")
            // dut.io.a(4).poke("b00001000".U)
            // val a4 = print_fp8_binary(dut.io.a(4))
            // println(s"a4: $a4")
            // dut.io.b(4).poke("b00001000".U)
            // val b4 = print_fp8_binary(dut.io.b(4))
            // println(s"b4: $b4")
            // dut.io.a(5).poke("b00010100".U)
            // val a5 = print_fp8_binary(dut.io.a(5))
            // println(s"a5: $a5")
            // dut.io.b(5).poke("b00010010".U)
            // val b5 = print_fp8_binary(dut.io.b(5))
            // println(s"b5: $b5")
            // dut.io.a(6).poke("b00011010".U)
            // val a6 = print_fp8_binary(dut.io.a(6))
            // println(s"a6: $a6")
            // dut.io.b(6).poke("b00011010".U)
            // val b6 = print_fp8_binary(dut.io.b(6))
            // println(s"b6: $b6")
            // dut.io.a(7).poke("b00100000".U)
            // val a7 = print_fp8_binary(dut.io.a(7))
            // println(s"a7: $a7")
            // dut.io.b(7).poke("b00100001".U)
            // val b7 = print_fp8_binary(dut.io.b(7))
            // println(s"b7: $b7")
            // dut.io.clear.poke(false.B)
            // dut.io.scale_a.poke(2.S)
            // dut.io.scale_b.poke(2.S)
            // dut.clock.step(5)

            // val out = (a0*b0 + a1*b1 + a2*b2 + a3*b3 + a4*b4 + a5*b5 + a6*b6 + a7*b7)*0.25*0.25
            // println(s"out: $out")
            // println(s"outbinary: ${fp32_to_binary(out)}")
            //println(s"fixed point value: ${BigInt("000000001100010000000000000000000101001100000", 2).toDouble * pow(2, -18)}")
            //println(s"product: ${(a0*b0 + a1*b1 + a2*b2 + a3*b3 + a4*b4 + a5*b5 + a6*b6 + a7*b7)}")
            //println(s"outreal: ${print_fp32_binary(BigInt("11000110110001000000000000000000", 2).U)}")
        }
    }
}