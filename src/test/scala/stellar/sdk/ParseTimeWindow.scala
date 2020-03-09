package stellar.sdk

import java.time.ZonedDateTime

import com.typesafe.scalalogging.LazyLogging
import org.specs2.concurrent.ExecutionEnv
import org.specs2.mutable.Specification
import stellar.sdk.model.ledger.TransactionLedgerEntries
import stellar.sdk.model.response.LedgerResponse
import stellar.sdk.model.{Desc, Now}

import scala.concurrent.Await
import scala.concurrent.duration._
import scala.util.{Success, Try}

class ParseTimeWindow(implicit val ee: ExecutionEnv) extends Specification with LazyLogging {

  "streaming transactions" should {
    "be successful" >> {
      val timeWindow = ZonedDateTime.now().minusHours(1)
      val (failures, successCount) = Await.result(
        PublicNetwork.transactions(Now, Desc).map { stream =>
        stream
          .takeWhile(_.createdAt.isAfter(timeWindow))
          .map { th => Try(th.ledgerEntries) }
          .foldLeft((List.empty[Try[TransactionLedgerEntries]], 0)) {
            case ((failures, successCount), Success(_)) => (failures, successCount + 1)
            case ((failures, successCount), failure) => (failure +: failures, successCount)
          }
      }, 10.minutes)
      logger.info(s"Processed $successCount transactions")
      failures must beEmpty[List[Try[TransactionLedgerEntries]]]
    }
  }

  "streaming ledgers" should {
    "be successful" >> {
      val timeWindow = ZonedDateTime.now().minusHours(1)
      val (ops, ledgers) = Await.result(PublicNetwork.ledgers(Now, Desc).map { stream =>
        stream
          .takeWhile(_.closedAt.isAfter(timeWindow))
          .foldLeft((0, 0)) { case ((opCount, ledgerCount), next) => (opCount + next.operationCount, ledgerCount + 1) }
      }, 10.minutes)
      logger.info(s"Processed $ledgers ledgers, with $ops operations.")
      ledgers must beGreaterThan(0)
      ops must beGreaterThan(0)
    }
  }

}