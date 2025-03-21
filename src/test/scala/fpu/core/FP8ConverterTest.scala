package fpu.core

import chisel3._
import chisel3.util._
import chisel3.experimental.BundleLiterals._
import chisel3.simulator.EphemeralSimulator._
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers

class FP8ConverterTest extends AnyFreeSpec with Matchers {
  "FP8Converter should convert FP8 to adjusted FP9" in {
    simulate(new FP8Converter) { dut =>
      // 定义测试数据
      val E5M2_EXP = 14
      val E5M2_MANT = 1
      val E4M3_EXP = 6
      val E4M3_MANT = 2

      // 构造E5M2格式的输入和期望输出
      val E5M2_FP8 = (0 << 7) | (E5M2_EXP << 2) | E5M2_MANT
      val E5M2_FP9 = (0 << 8) | (E5M2_EXP << 3) | (E5M2_MANT << 1)

      // 构造E4M3格式的输入和期望输出
      val E4M3_FP8 = (0 << 7) | (E4M3_EXP << 3) | E4M3_MANT
      val E4M3_FP9 = (0 << 8) | ((E4M3_EXP + 8) << 3) | E4M3_MANT

      // 测试E5M2格式转换
      dut.io.in.poke(E5M2_FP8.U)
      dut.io.inType.poke(0.U)
      dut.clock.step(1)
      dut.io.out.expect(E5M2_FP9.U)

      // 测试E4M3格式转换
      dut.io.in.poke(E4M3_FP8.U)
      dut.io.inType.poke(1.U)
      dut.clock.step(1)
      dut.io.out.expect(E4M3_FP9.U)

      //验证两个输出是否相等
      dut.io.out.expect(E5M2_FP9.U)
    }
  }
}