package org.finra.datagenerator.scaffolding.messaging

import java.util.UUID

import org.finra.datagenerator.scaffolding.messaging.config.ResponseConfigurationBuilder
import org.finra.datagenerator.scaffolding.messaging.response.Response

import scala.concurrent.Future

/**
  * Created by dkopel on 6/30/16.
  */
trait MessagingService {
    def subscribe(topic: String, handlers: List[MessageHandler]): UUID

    def subscribe[A <: java.io.Serializable](predicate: (Message[A]) => Boolean, handlers: List[MessageHandler]): UUID

    def unsubscribe(subscriptionId: UUID)

    def publish[A <: java.io.Serializable](message: Message[A], responseConfiguration: ResponseConfigurationBuilder): Reply

    // Fire and forget
    def publish[A <: java.io.Serializable](message: Message[A]): Future[Seq[Response[_]]]
}
