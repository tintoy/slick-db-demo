import scala.concurrent.{Await, ExecutionContext, Future}
import scala.concurrent.duration.DurationInt

/**
  * Asynchronous application support.
  */
trait AsyncApp extends App {
  protected[this] implicit val executionContext = ExecutionContext.global

  /**
    * The default timeout period for the asynchronous application entry-point.
    */
  def asyncMainTimeout = 5.minutes

  /**
    * Asynchronous entry-point for the application.
    * @return A [[Future]] representing asynchronous execution.
    */
  def asyncMain(): Future[Unit] = Future.successful()

  /**
    * Asynchronous entry-point for the application.
    * @param commandLineArgs Command-line arguments.
    * @return A [[Future]] representing asynchronous execution.
    */
  def asyncMain(commandLineArgs: Array[String]): Future[Unit] = asyncMain()

  // Ugly, but it works.
  delayedInit(
    Await.result(asyncMain(args), atMost = asyncMainTimeout)
  )
}
