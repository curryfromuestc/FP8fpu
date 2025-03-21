package fpu

import chisel3._
import chisel3.util._

object Float32 {
  val LENGTH = 32
  val expWidth = 8
  val sigWidth = 23
}

object Float8E5M2 {
  val LENGTH = 8
  val expWidth = 5
  val sigWidth = 2
}

object Float8E4M3 {
  val LENGTH = 8
  val expWidth = 4
  val sigWidth = 3
}

object IntermediateFormat {
  val LENGTH = 9
  val expWidth = 5
  val sigWidth = 3
}

object FixedPoint {
  val LENGTH = 9
  val SIGN = 1
  val FRACTION = 8
  val FIRST_SHIFTED_LENGTH = 15
  val SHIFTED_LENGTH = 71
}

object Parameters {
  val WAY_SIZE = 4
  val FP8_LENGTH = 8
}