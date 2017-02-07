package org.finra.datagenerator.scaffolding.utils

import java.io.{ByteArrayOutputStream, InputStream}
import java.nio.ByteBuffer
import java.nio.channels.{Channels, ReadableByteChannel}

/**
  * Created by dkopel on 10/14/16.
  */
object FileUtils {
    def loadResource(stream: InputStream): String = {
        val read: ReadableByteChannel = Channels.newChannel(stream)
        var buffer: ByteBuffer = ByteBuffer.allocate(2048)
        val out: ByteArrayOutputStream = new ByteArrayOutputStream
        while (read.read(buffer) > 0) {
            out.write(buffer.array)
            buffer = ByteBuffer.allocate(2048)
        }
        new String(out.toByteArray).trim
    }
}