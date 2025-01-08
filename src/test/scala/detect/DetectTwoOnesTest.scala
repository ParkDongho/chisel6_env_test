package detect

import circt.stage._
import tywaves.simulator._
import tywaves.simulator.simulatorSettings._

import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers


class DetectTwoOnesTest extends AnyFunSpec with Matchers {

    import TywavesSimulator._
    def runTest(fsm: DetectTwoOnes) = {
        // Inputs and expected results
        val inputs   = Seq(0, 0, 0, 0, 0, 0, 0, 0, 0, 0)
        val expected = Seq(0, 0, 0, 0, 0, 0, 0, 0, 0, 0)

        // Reset
        fsm.io.in.poke(0)
        fsm.clock.step(1)

        for (i <- inputs.indices) {
            fsm.io.in.poke(inputs(i))
            //c.clock.getStepCount
            fsm.clock.step(1)
            fsm.io.out.expect(expected(i))
            System.out.println(s"In: ${inputs(i)}, out: ${expected(i)}")
        }
    }

  describe("TywavesSimulator") {
    it("runs DetectTwoOnes correctly") {
      val chiselStage = new ChiselStage(true)

      chiselStage.execute(
        args = Array("--target", "chirrtl"),
        annotations = Seq(
          chisel3.stage.ChiselGeneratorAnnotation(() => new DetectTwoOnes()),
          FirtoolOption("-g"),
          FirtoolOption("--emit-hgldd"),
        ),
      )

      simulate(new DetectTwoOnes(), Seq(VcdTrace, WithTywavesWaveforms(true)), simName = "runs_detect2ones") {
        fsm => runTest(fsm)
      }

    }
  }
}


