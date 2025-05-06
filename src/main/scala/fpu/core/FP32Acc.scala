package fpu.core

import chisel3._
import chisel3.util._
import chisel3.util.experimental.decode._
import fpu.Parameters._
import fpu._

class FP32Acc extends Module {
    val io = IO(new Bundle {
        val inFp32A = Input(UInt(32.W))
        val inFp32B = Input(UInt(32.W))
        val outFp32 = Output(UInt(32.W))
    })

    // 分解输入为符号、指数和尾数
    val signA = io.inFp32A(31)
    val expA = io.inFp32A(30, 23)
    val mantissaA = Cat(1.U(1.W), io.inFp32A(22, 0)) // 添加隐含的1
    
    val signB = io.inFp32B(31)
    val expB = io.inFp32B(30, 23)
    val mantissaB = Cat(1.U(1.W), io.inFp32B(22, 0)) // 添加隐含的1

    // 1. 对齐指数（将较小的数右移）
    val expDiff = Wire(SInt(9.W))
    expDiff := expA.asSInt - expB.asSInt
    
    val fractionA = Cat(0.U(1.W), mantissaA,Fill(42,0.U))
    val fractionB = Cat(0.U(1.W), mantissaB,Fill(42,0.U))

    val fractionAShifted = Wire(UInt(67.W))
    val fractionBShifted = Wire(UInt(67.W))
    val sum = Wire(UInt(67.W))
    val signResult = Wire(UInt(1.W))
    val expResult = Wire(UInt(8.W))

    when(expA > expB) {
        expResult := expA
        when(signA === signB) {
            fractionAShifted := fractionA
            fractionBShifted := fractionB >> expDiff.abs.asUInt
            sum := fractionAShifted + fractionBShifted
            signResult := signA
        }
        .otherwise {
            fractionAShifted := fractionA
            fractionBShifted := fractionB >> expDiff.abs.asUInt
            sum := fractionAShifted - fractionBShifted
            signResult := signA
        }
    }
    .otherwise {
        expResult := expB
        when(signA === signB) {
            fractionAShifted := fractionA >> expDiff.abs.asUInt
            fractionBShifted := fractionB
            sum := fractionBShifted + fractionAShifted
            signResult := signB
        }
        .otherwise {
            fractionAShifted := fractionA >> expDiff.abs.asUInt
            fractionBShifted := fractionB
            sum := fractionBShifted - fractionAShifted
            signResult := signB
        }
    }
    val result = Wire(UInt(32.W))
    
    when(sum(66) === 1.U) {
        result := Cat(signResult, expResult+1.U, sum(65,43))
    }
    .otherwise {
        result := Cat(signResult, expResult, sum(64,42))
    }

    io.outFp32 := result
}