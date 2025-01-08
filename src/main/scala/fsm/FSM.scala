package fsm

import chisel3.{IO, _}

// Enum of possible states
object MyFSMStates extends ChiselEnum {
  val IDLE, StateA, StateB, END = Value
}

class FSM extends Bundle {

  val inputState = IO(Input(MyFSMStates()))
  val state      = RegInit(MyFSMStates.IDLE)
  val stateNxt   = WireInit(MyFSMStates.IDLE)

  val endConst = WireInit(MyFSMStates.END)

  when(state === MyFSMStates.IDLE) {
    stateNxt := MyFSMStates.StateA
  }.elsewhen(state === MyFSMStates.StateA) {
    stateNxt := MyFSMStates.StateB
  }.elsewhen(state === MyFSMStates.StateB) {
    stateNxt := MyFSMStates.END
  }.otherwise {
    stateNxt := MyFSMStates.IDLE
  }

  when(inputState === MyFSMStates.END) {
    state := MyFSMStates.IDLE
  }.otherwise {
    state := stateNxt
  }
}

class MyFSM extends Module {
  val fsm = new FSM

  val io = IO(new Bundle {
    val inputState = Input(MyFSMStates())
    val input = Input(UInt(32.W))
    val output = Output(UInt(32.W))
  })

  val submodule = Module(new SubModule)
  submodule.io.input := io.input
  io.output := submodule.io.output

  when(fsm.state === MyFSMStates.StateA) {
    submodule.io.en := true.B
  } .otherwise {
    submodule.io.en := false.B
  }

  val aConstBundle = Wire(new Bundle {
    val bit = Bool()
    val bv = UInt(32.W)
    val subbundle = new Bundle {
      val x = SInt(3.W)
    }
  })
  aConstBundle.bit := 1.B
  aConstBundle.bv := 34.U
  aConstBundle.subbundle.x := 2.S


}

class SubModule extends Module{
  val io = IO(new Bundle {
    val input = Input(UInt(32.W))
    val output = Output(UInt(32.W))
    val en = Input(Bool())
  })

  val reg = Reg(UInt(32.W))

  when(io.en) {
    reg := io.input + 21.U
  } .otherwise {
    reg := io.input
  }

  io.output := reg

}