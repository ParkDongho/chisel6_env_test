package hwtest

import chisel3._
import chisel3.util._

class DecoupledDriver[T <: Data](gen: T, inputData: Seq[UInt]) extends Module {
  val io = IO(new Bundle {
    // DUT에 연결할 출력 Decoupled
    val out = Decoupled(gen)
    // 드라이버의 진행상태 관찰 (옵션)
    val en  = Input(Bool())
    val done = Output(Bool())
  })

  // 내부에서 사용할 큐 인덱스
  val dataQueue = RegInit(VecInit(inputData))
  val idx       = RegInit(0.U(log2Ceil(inputData.length + 1).W))
  val validReg  = RegInit(true.B)
  val doneReg   = RegInit(false.B)

  io.out.bits  := dataQueue(idx)
  io.out.valid := validReg
  io.done      := doneReg
  io.out.valid := io.en

  when(io.out.ready && io.en) {
    // 한 사이클 동안 valid & ready가 동시에 1이면 전송 성공
    when(idx === (inputData.length - 1).U) {
      // 모든 데이터를 전송했다면 valid를 끄고 done 표시
      validReg := false.B
      doneReg  := true.B
    }.otherwise {
      // 다음 데이터로 넘어가기
      idx := idx + 1.U
    }
  }
}


