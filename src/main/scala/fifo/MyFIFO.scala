package fifo

import chisel3._
import chisel3.util._

class MyFIFO extends Module {
  val io: MyFIFOBundle = IO(new MyFIFOBundle)

  val ququeModule: Queue[UInt] = Module(new Queue(UInt(8.W), 16))
  io.deq <> ququeModule.io.deq
  io.enq <> ququeModule.io.enq

}


class MyFIFOBundle extends Bundle {
  val enq: DecoupledIO[UInt] = Flipped(Decoupled(UInt(8.W)))
  val deq: DecoupledIO[UInt] = Decoupled(UInt(8.W))
}
