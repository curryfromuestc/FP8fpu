package fpu.core

import chisel3._
import chisel3.util._
import fpu.Parameters._
import fpu._
import _root_.circt.stage.ChiselStage

class Top extends Module {
    val io = IO(new Bundle {
        val inA = Input(Vec(8, UInt(FP8_LENGTH.W)))
        val inB = Input(Vec(8, UInt(FP8_LENGTH.W)))
        val clear = Input(Bool())
        val scaleA = Input(SInt(7.W))
        val scaleB = Input(SInt(7.W))
        val out = Output(UInt(Float32.LENGTH.W))
    })

    val fpu = Module(new Fpu())
    fpu.io.a := io.inA
    fpu.io.b := io.inB
    fpu.io.clear := io.clear

    val normalizationShifter = Module(new NormalizationShifter())
    normalizationShifter.io.in := fpu.io.out
    normalizationShifter.io.clear := io.clear
    normalizationShifter.io.scaleA := io.scaleA
    normalizationShifter.io.scaleB := io.scaleB

    io.out := normalizationShifter.io.out
}

object Top extends App {
  ChiselStage.emitSystemVerilogFile(
    new Top,
    firtoolOpts = Array("-disable-all-randomization", "-strip-debug-info"),
    args = Array("--target-dir", "src/main/resources")
  )
}