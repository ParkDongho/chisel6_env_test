package fsm

import tywaves.simulator._
import tywaves.simulator.simulatorSettings._

import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers
import circt.stage.ChiselStage


class MyFSMTest extends AnyFunSpec with Matchers {

  describe("TywavesSimulator") {

    it("runs MyFSM correctly") {
      import TywavesSimulator._
      val chiselStage = new ChiselStage(true)

      chiselStage.execute(
        args = Array("--target", "chirrtl"),
        annotations = Seq(
          chisel3.stage.ChiselGeneratorAnnotation(() => new MyFSM()),
          circt.stage.FirtoolOption("-g"),
          circt.stage.FirtoolOption("--emit-hgldd"),
        ),
      )

      // simulate(new MyFSM(), Seq(VcdTrace, WithTywavesWaveformsGo(true)),
      //   simName = "runs_MYFSM_correctly_launch_tywaves_and_go") {
      //   fsm =>
      //     fsm.clock.step(10)
      //     fsm.clock.step(10)
      // }

      // Simulation
      simulate(new MyFSM(), Seq(VcdTrace, WithTywavesWaveforms(true)),
        simName = "runs_MYFSM_correctly_launch_tywaves") {
        fsm =>
          fsm.clock.step(10)
          fsm.io.inputState.poke(MyFSMStates.StateA)
          fsm.clock.step(10)
      }

    }
  }

}