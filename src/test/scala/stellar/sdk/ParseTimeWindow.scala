package stellar.sdk

import java.time.ZonedDateTime

import org.specs2.concurrent.ExecutionEnv
import org.specs2.mutable.Specification
import stellar.sdk.model.ledger.TransactionLedgerEntries
import stellar.sdk.model.{Desc, Now}

import scala.concurrent.duration._
import scala.util.{Success, Try}

class ParseTimeWindow(implicit val ee: ExecutionEnv) extends Specification {

  "streaming transactions" should {
    "be successful" >> {
      val timeWindow = ZonedDateTime.now().minusHours(3)
      PublicNetwork.transactions(Now, Desc).map { stream =>
        stream
          .takeWhile(_.createdAt.isAfter(timeWindow))
          .map { th => Try(th.ledgerEntries) }
          .foldLeft(List.empty[Try[TransactionLedgerEntries]]) {
            case (acc, Success(t)) => acc
            case (acc, failure) => failure +: acc
          }
      } must beEmpty[List[Try[TransactionLedgerEntries]]].awaitFor(10.minutes)
    }
  }

}