package org.finra.datagenerator.scaffolding.messaging

import java.util.UUID

import com.fasterxml.jackson.annotation.JsonIgnore

/**
  * Created by dkopel on 6/30/16.
  */
sealed case class Message[+T <: java.io.Serializable](
                                           body: T, headers: MessageHeaders=EmptyMessageHeaders,
                                           id: UUID = UUID.randomUUID(),
                                           time: Long = System.currentTimeMillis()) extends Serializable {
    @JsonIgnore def metadata = headers.metadata
}