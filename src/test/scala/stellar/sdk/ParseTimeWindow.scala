package stellar.sdk

import java.time.ZonedDateTime

import com.typesafe.scalalogging.LazyLogging
import org.specs2.concurrent.ExecutionEnv
import org.specs2.mutable.Specification
import stellar.sdk.model.ledger.TransactionLedgerEntries
import stellar.sdk.model.{Desc, Now}

import scala.concurrent.Await
import scala.concurrent.duration._
import scala.util.{Failure, Success, Try}

class ParseTimeWindow(implicit val ee: ExecutionEnv) extends Specification with LazyLogging {

  sequential

  val timeWindow = ZonedDateTime.now().minusMinutes(65)

  "streaming transactions" should {
    "be successful" >> {
      val (failures, successCount) = Await.result(
        PublicNetwork.transactions(Now, Desc).map { stream =>
        stream
          .takeWhile(_.createdAt.isAfter(timeWindow))
          .map { th => th.hash -> Try(th.ledgerEntries) }
          .foldLeft((List.empty[(String, Failure[TransactionLedgerEntries])], 0)) {
            case ((failures, successCount), _ -> Success(_)) => (failures, successCount + 1)
            case ((failures, successCount), hash -> Failure(f)) => {
              val elem: (String, Failure[TransactionLedgerEntries]) = hash -> Failure(f)
              (elem +: failures) -> successCount
            }
          }
      }, 10.minutes)
      logger.info(s"Processed $successCount transactions")
      logger.info(s"Encountered ${failures.size} errors parsing ledger entries")
      failures.take(20).foreach { case (hash, Failure(f)) =>
        logger.error(s"Transaction $hash failed", f)
      }
      failures.size mustEqual 0
    }
  }

  "streaming ledgers" should {
    "be successful" >> {
      val (ops, ledgers) = Await.result(PublicNetwork.ledgers(Now, Desc).map { stream =>
        stream
          .takeWhile(_.closedAt.isAfter(timeWindow))
          .foldLeft((0, 0)) { case ((opCount, ledgerCount), next) => (opCount + next.operationCount, ledgerCount + 1) }
      }, 10.minutes)
      logger.info(s"Processed $ledgers ledgers, with $ops operations")
      ledgers must beGreaterThan(0)
      ops must beGreaterThan(0)
    }
  }

  "streaming effects" should {
    "be successful" >> {
      val effectCount = Await.result(PublicNetwork.effects(Now, Desc).map { stream =>
        stream
          .takeWhile(_.createdAt.isAfter(timeWindow))
          .size
      }, 10.minutes)
      logger.info(s"Processed $effectCount effects")
      effectCount must beGreaterThan(0)
    }
  }

}