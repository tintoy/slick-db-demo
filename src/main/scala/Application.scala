import slick.driver.PostgresDriver.api._

import scala.async.Async.{async, await}
import scala.concurrent.Future

/**
 * Simple database client.
 */
object Application extends AsyncApp with DataAccess {
  /**
    * Asynchronous entry-point for the application.
    * @return A [[Future]] representing asynchronous execution.
    */
  override def asyncMain(): Future[Unit] = async {
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
}
