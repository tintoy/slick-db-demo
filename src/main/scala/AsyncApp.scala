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
  def asyncMain(): Future[Unit] = Future.successful(Unit)

  /**
    * Asynchronous entry-point for the application.
    * @param commandLineArgs Command-line arguments.
    * @return A [[Future]] representing asynchronous execution.
    */
  def asyncMain(commandLineArgs: Array[String]): Future[Unit] = asyncMain()

  /**
    * Ugly hack to call "deprecated" method without compiler finger-wagging.
    * @note Why isn't there an option to selectively suppress this stuff? Lame.
    */
  @deprecated("Not really deprecated", since = "Never")
  private[this] class NotReallyDeprecated {
    def addDelayedInit(body: => Unit) = delayedInit(body)
  }
  private[this] object NotReallyDeprecated extends NotReallyDeprecated

  NotReallyDeprecated.addDelayedInit(
    Await.result(asyncMain(args), atMost = asyncMainTimeout)
  )
}
