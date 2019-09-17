package stellar.sdk

import java.time.ZonedDateTime

import org.specs2.concurrent.ExecutionEnv
import org.specs2.mutable.Specification
import stellar.sdk.model.{Desc, Now}

import scala.concurrent.Await
import scala.concurrent.duration._

class Parse24Hours(implicit val ee: ExecutionEnv) extends Specification {

  "streaming transactions" should {
    "be successful" >> {
      val oneDayAgo = ZonedDateTime.now().minusHours(1)
      Await.result(PublicNetwork.transactions(Now, Desc).map { stream =>
        stream
          .takeWhile(_.createdAt.isAfter(oneDayAgo))
          .map { th => print('.'); th }
          .toList
      }, 10.minutes) must not(beEmpty)
    }
  }

}