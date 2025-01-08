package fifo

import chisel3._
import chisel3.stage.ChiselGeneratorAnnotation
import circt.stage._
import tywaves.simulator._
import tywaves.simulator.simulatorSettings._
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers


class MyFIFOHWSimTest extends AnyFunSpec with Matchers {

  import TywavesSimulator._
  describe("TyWavesSimulator with HW driver/monitor") {

    it("runs MyFIFOHWTest correctly") {

      // 1) chirrtl 생성
      val chiselStage = new ChiselStage(true)
      chiselStage.execute(
        args = Array("--target", "chirrtl"),
        annotations = Seq(
          chisel3.stage.ChiselGeneratorAnnotation(() => new MyFIFOHWTestTop()),
          FirtoolOption("-g"),
          FirtoolOption("--emit-hgldd"),
        ),
      )

      // 2) system verilog 생성
      (new ChiselStage).execute(
        Array(
          "--target", "systemverilog",
        ),
        Seq(
          ChiselGeneratorAnnotation(() => new MyFIFOHWTestTop()),
          // target directory
          FirtoolOption("--split-verilog"),
          FirtoolOption("-o"),
          FirtoolOption("generated/"),

          // option
          FirtoolOption("--disable-all-randomization"),
          FirtoolOption("--strip-debug-info"),
          FirtoolOption("--preserve-aggregate"),
          FirtoolOption("all")
        )
      )

      // 3) TyWavesSimulator 를 이용한 ChiselSim 시뮬레이션 실행
      simulate(new MyFIFOHWTestTop(), Seq(VcdTrace, WithTywavesWaveforms(false)),
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
