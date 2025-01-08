package fifo

import chisel3._
import circt.stage._
import tywaves.simulator._
import tywaves.simulator.simulatorSettings._

import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers




class MyFIFOTest extends AnyFunSpec with Matchers {

  import TywavesSimulator._

  def runTest(fifo: MyFIFO): Unit = {
    // -------------------------
    // 1) 초기 리셋
    // -------------------------
    fifo.clock.step(5)
    fifo.reset.poke(true.B)
    fifo.clock.step(5)
    fifo.reset.poke(false.B)
    fifo.clock.step(5)

    // -------------------------
    // 2) FIFO에 데이터 쓰기
    // -------------------------
    val testData = Seq(10, 20, 30, 40, 50)

    for (i <- testData.indices) {

      // FIFO가 꽉 차있으면(full이 true면) 비워질 때까지 대기

      // println(fifo.io.enq.ready.peek())

      // 쓰기 신호 활성화
      fifo.io.enq.valid.poke(true.B)
      fifo.io.enq.bits.poke(testData(i))
      fifo.clock.step(1)
      // 쓰기 신호 비활성화
      fifo.io.enq.valid.poke(false.B)
    }

    // -------------------------
    // 3) FIFO에서 데이터 읽기
    // -------------------------
    for (i <- testData.indices) {

      // FIFO가 비어있으면(empty가 true면) 채워질 때까지 대기
      // while (fifo.io.deq.valid.peek().litValue == 0) {
      //   fifo.clock.step(1)
      // }

      // 읽기 신호 활성화
      fifo.io.deq.ready.poke(true.B)

      // 읽은 값 확인
      // fifo.io.deq.bits.expect(testData(i))

      println(testData(i))
      fifo.clock.step()

      // 읽기 신호 비활성화
      fifo.io.deq.ready.poke(false.B)
    }

    // 시뮬레이션을 조금 더 진행해본다
    fifo.clock.step(10)
  }

  describe("TywavesSimulator") {
    it("runs MyFIFO correctly") {
      val chiselStage = new ChiselStage(true)

      chiselStage.execute(
        args = Array("--target", "chirrtl"),
        annotations = Seq(
          chisel3.stage.ChiselGeneratorAnnotation(() => new MyFIFO()),
          FirtoolOption("-g"),
          FirtoolOption("--emit-hgldd"),
        ),
      )

      // Simulation
      simulate(new MyFIFO(), Seq(VcdTrace, WithTywavesWaveforms(true)),
        simName = "runs_MyFIFO_correctly_launch_tywaves") {
        fifo => runTest(fifo)
      }
    }
  }
}
