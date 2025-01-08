package hwtest

import chisel3._
import chisel3.util._

class DecoupledMonitor[T <: Data](gen: T, bufferSize: Int) extends Module {
  val io = IO(new Bundle {
    // DUT 출력이 연결될 입력 Decoupled
    val in = Flipped(Decoupled(gen))
    // 모니터가 수집한 결과를 외부에서 확인할 수 있게 해주는 인터페이스(옵션)
    val outData = Output(Vec(bufferSize, gen))
    // 실제 몇 개의 데이터를 받았는지
    val outCount = Output(UInt(log2Ceil(bufferSize + 1).W))
  })

  // 데이터를 저장할 레지스터 혹은 메모리
  val buffer = Reg(Vec(bufferSize, gen))
  val count  = RegInit(0.U(log2Ceil(bufferSize + 1).W))

  // 항상 수신 준비 상태
  io.in.ready := true.B

  // valid & ready가 동시에 1일 때 데이터 수신
  when(io.in.valid && io.in.ready) {
    // 버퍼에 저장
    buffer(count) := io.in.bits
    // outCount 갱신
    when(count =/= (bufferSize - 1).U) {
      count := count + 1.U
    }
  }

  // 모니터가 수집한 데이터를 외부로 내보냄
  io.outData := buffer
  io.outCount := count
}