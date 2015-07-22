package Helpers

import java.io.OutputStream

/**
 * Output stream that does nothing
 */
object NullOutputStream extends OutputStream {
  override def write(b: Int): Unit = {
  }

  override def write(b: Array[Byte]): Unit = {
  }

  override def write(b: Array[Byte], offset: Int, length: Int): Unit = {
  }
}
