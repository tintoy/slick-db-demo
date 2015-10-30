import scala.concurrent.{Await, ExecutionContext}
import scala.concurrent.duration.DurationInt
import scala.async.Async.{async, await}
import slick.driver.PostgresDriver.api._

/**
 * Simple database client.
 */
object Application extends App with DataAccess {
  implicit val executionContext = ExecutionContext.global

  val asyncRoot = async {
    val database = openDefaultDatabase()

    // Drop and re-create the database schema.
    println("Creating / re-creating database schema...")
    await(
      createSchemaAsync(database)
    )

    // Add some data.
    println("Inserting 5 rows...")
    await(
      database.run(
        queueEntries ++= Seq(
          QueueEntry(id = None, label = "Entry1"),
          QueueEntry(id = None, label = "Entry2"),
          QueueEntry(id = None, label = "Entry3"),
          QueueEntry(id = None, label = "Entry4"),
          QueueEntry(id = None, label = "Hello")
        )
      )
    )

    println("Querying...")
    val query = database.run(
      QueueEntries.latestJobs(3).result
    )

    val results = await(query)
    println("Got results")

    results.foreach(
      result => println(result)
    )
    println("Done.")

    database.close()
  }

  // Wait for root asynchronous operation to complete.
  Await.result(asyncRoot, 10.seconds)
}
