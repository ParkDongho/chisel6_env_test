package detect

import chisel3._
import chisel3.util._

class DetectTwoOnes extends Module {
  val io = IO(new Bundle {
    val in      = Input(Bool())
    val out     = Output(Bool())
  })

  object State extends ChiselEnum { val sNone, sOne1, sTwo1s = Value }
  val state = RegInit(State.sNone)

  // Tmp signal 1
  val isOne = Wire(Bool())
  isOne := io.in
  // Tmp signal 2
  val willBeTwo1s = io.in && (state === State.sOne1 || state === State.sTwo1s)

  io.out := (state === State.sTwo1s)

  switch(state) {
    is(State.sNone) { when(isOne) { state := State.sOne1 } }
    is(State.sOne1) {
        when(isOne) { state := State.sTwo1s }.otherwise { state := State.sNone }
    }
    is(State.sTwo1s) { when(!isOne) { state := State.sNone } }
  }
}