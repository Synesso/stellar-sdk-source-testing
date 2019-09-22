package stellar.sdk

import java.time.ZonedDateTime

import org.specs2.concurrent.ExecutionEnv
import org.specs2.mutable.Specification
import stellar.sdk.model.ledger.TransactionLedgerEntries
import stellar.sdk.model.{Desc, Now}

import scala.concurrent.duration._
import scala.util.{Success, Try}

class Parse24Hours(implicit val ee: ExecutionEnv) extends Specification {

  "streaming transactions" should {
    "be successful" >> {
      val oneDayAgo = ZonedDateTime.now().minusDays(1)
      PublicNetwork.transactions(Now, Desc).map { stream =>
        stream
          .takeWhile(_.createdAt.isAfter(oneDayAgo))
          .map { th => Try(th.ledgerEntries) }
          .foldLeft(List.empty[Try[TransactionLedgerEntries]]) {
            case (acc, Success(t)) => acc
            case (acc, failure) => failure +: acc
          }
      } must beEmpty[List[Try[TransactionLedgerEntries]]].awaitFor(10.minutes)
    }
  }

}