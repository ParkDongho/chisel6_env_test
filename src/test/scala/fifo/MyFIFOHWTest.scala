package fifo

import chisel3._
import circt.stage._
import tywaves.simulator._
import tywaves.simulator.simulatorSettings._

import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers



class MyFIFOHWTest extends AnyFunSpec with Matchers {

  import TywavesSimulator._


  describe("TyWavesSimulator with HW driver/monitor") {
    it("runs MyFIFOHWTest correctly") {
      val chiselStage = new ChiselStage(true)
      chiselStage.execute(
        args = Array("--target", "chirrtl"),
        annotations = Seq(
          chisel3.stage.ChiselGeneratorAnnotation(() => new MyFIFOHWTestTop()),
          FirtoolOption("-g"),
          FirtoolOption("--emit-hgldd"),
        ),
      )

      // 2) TyWavesSimulator 를 이용한 실제 시뮬레이션 실행
      simulate(new MyFIFOHWTestTop(), Seq(VcdTrace, WithTywavesWaveforms(true)),
        simName = "runs_MyFIFOHWTest_correctly_launch_tywaves") {
        fifo => {
          fifo.clock.step(5)
          fifo.reset.poke(true.B)
          fifo.clock.step(5)
          fifo.reset.poke(false.B)
          fifo.io.en.poke(true.B)
          fifo.clock.step(30)
        }
      }
    }
  }
}
