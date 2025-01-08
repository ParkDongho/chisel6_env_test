package fifo

import chisel3._
import chisel3.stage.ChiselGeneratorAnnotation
import circt.stage.{ChiselStage, FirtoolOption}

class MyFIFOHWTestTop extends Module {
  import hwtest._
  val io = IO(new Bundle{
    val done = Output(Bool())
    val en = Input(Bool())
  })
  val target = Module(new MyFIFO)

  val driver = Module(new DecoupledDriver(UInt(8.W),
    Seq(10.U, 20.U, 30.U, 40.U))
  )
  io.done := driver.io.done
  driver.io.en := io.en

  val monitor = Module(new DecoupledMonitor(UInt(8.W), 16))

  target.io.enq <> driver.io.out
  target.io.deq <> monitor.io.in

}

object MyFIFOHWTestTopSyn extends App {
  (new ChiselStage).execute(
    Array("--target", "systemverilog"),
    Seq(
      ChiselGeneratorAnnotation(() => new MyFIFOHWTestTop()),
      FirtoolOption("--disable-all-randomization"),
      FirtoolOption("--strip-debug-info"),
    )
  )
}
