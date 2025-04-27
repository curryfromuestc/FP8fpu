package fpu.core

import chisel3._
import chisel3.util._
import chisel3.util.experimental.decode._
import fpu.Parameters._
import fpu._
import fpu.Float32.SHIFTED_LENGTH

class NormalizationShifter extends Module {
    val io = IO(new Bundle {
        val in = Input(SInt((FixedPoint.SHIFTED_LENGTH).W))
        val out = Output(UInt((Float32.LENGTH).W))
        val scale_a = Input(SInt(7.W))
        val scale_b = Input(SInt(7.W))
    })

    val scale = Wire(SInt(8.W))
    scale := io.scale_a + io.scale_b
    val in = io.in
    val preShift = Wire(UInt((FixedPoint.SHIFTED_LENGTH-1).W))

    //对计算结果进行规格化，首先除符号位以外，其他位取反加一
    val sign = Wire(UInt(1.W))
    sign := in(FixedPoint.SHIFTED_LENGTH-1)
    val mant = in(FixedPoint.SHIFTED_LENGTH-2, 0)
    when(in(FixedPoint.SHIFTED_LENGTH-1) === 1.U) {
        preShift := ~mant + 1.U
    }
    .otherwise {
        preShift := mant
    }
    //判断第一个1的位置，将第一个1到48位的距离作为移位量，大于就右移，小于就左移
    val numLeadingZero = Wire(UInt(log2Ceil(FixedPoint.SHIFTED_LENGTH).W))
    numLeadingZero := PriorityEncoder(Reverse(preShift))
    //第三周期，计算出移位量和移位方向
    val shiftedMant = Wire(UInt((Float32.sigWidth+1).W))
    shiftedMant := (preShift << numLeadingZero)(FixedPoint.SHIFTED_LENGTH - 3,FixedPoint.SHIFTED_LENGTH - 26)
    val exp = Wire(UInt(8.W))
    exp := (26.S - (numLeadingZero+1.U).asSInt + 127.S - scale).asUInt
    //第四周期，RNE舍入
    val roundedMant = Wire(UInt((Float32.sigWidth).W))
    when(shiftedMant(0) === 1.U && shiftedMant(1) === 1.U) {
        roundedMant := shiftedMant(23,1) + 1.U
    }
    .otherwise {
        roundedMant := shiftedMant(23,1)
    }
    val fp32Accumulator = RegInit(0.U(Float32.LENGTH.W))
    def fp32Add(a: UInt, b: UInt): UInt = {
        val aSign = a(31)
        val bSign = b(31)
        val aExp = a(30, 23)
        val bExp = b(30, 23)
        val aSig = a(22, 0)
        val bSig = b(22, 0)
        val diffExp = aExp - bExp
        val largerSig = if (diffExp > 0) aSig else bSig
        val smallerSig = if (diffExp > 0) bSig else aSig
        val largerSigShifted = Cat(largerSig, 0.U(diffExp.W))
        val smallerSigShifted = Cat(smallerSig, 0.U(diffExp.W))
        val sumSig = largerSigShifted + smallerSigShifted
        val sumSigNormalized = sumSig(53, 29)
        val sumExp = aExp
        val sumSign = aSign
        Cat(sumSign, sumExp, sumSigNormalized)
    }
    fp32Accumulator := fp32Add(fp32Accumulator, Cat(sign, exp, roundedMant))
    //----------------------测试点--------------------------------
    printf("scale         : %d\n", scale)
    printf("in            : %b\n", in)
    printf("sign          : %b\n", sign)
    printf("mant          : %b\n", mant)
    printf("preShift      : %b\n", preShift)
    printf("numLeadingZero: %b\n", numLeadingZero)
    printf("shiftedMant   : %b\n", shiftedMant)
    printf("roundedMant   : %b\n", roundedMant)
    printf("exp           : %b\n", exp)
    printf("out           : %b\n", io.out)
    //----------------------------------------------------------
}