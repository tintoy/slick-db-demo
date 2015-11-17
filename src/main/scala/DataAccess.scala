import com.typesafe.config.{ConfigFactory, Config}
import slick.driver.PostgresDriver.api._
import scala.concurrent.Future

/**
 * Data-access facilities.
 */
trait DataAccess {
  /**
   * The configuration for all databases.
   */
  private val dbConfig: Config = ConfigFactory.load().getConfig("database")

  /**
   * The "QueueEntry" entity set.
   */
  val queueEntries = TableQuery[QueueEntries]

  /**
   * An entry in the queue.
   * @param id The queue entry Id.
   * @param label The entry label (just dummy data at this stage).
   */
  case class QueueEntry(id: Option[Int], label: String)

  /**
   * The `QueueEntry` table.
   */
  class QueueEntries(tag: Tag) extends Table[QueueEntry](tag, "QueueEntry") {
    /**
     * The `Id` column of the `QueueEntry` table.
     * @note Primary key.
     */
    def entryId = column[Option[Int]]("Id", O.PrimaryKey, O.AutoInc)

    /**
     * The `Label` column of the `QueueEntry` table.
     */
    def label = column[String]("Label")

    /**
     * All columns for the `QueueEntry` table.
     */
    def * = (entryId, label) <> (QueueEntry.tupled, QueueEntry.unapply)
  }

  /**
   * Standard queries and commands for the `QueueEntries table.`
   */
  object QueueEntries {
    /**
     * Get the latest jobs in the queue.
     * @param batchSize The number of jobs to retrieve.
     * @return A query retrieves the jobs.
     */
    def latestJobs(batchSize: Int) = queueEntries.sortBy(_.entryId.desc).take(batchSize)
  }

  /**
   * Default values for database settings.
   */
  private val databaseSettingDefaults: Config =
    ConfigHelper.parseScalaMap(Map(
      "server" -> "localhost",
      "userName" -> "postgres",
      "password" -> "postgres"
    ))

  /**
   * Open a connection to the default database.
   * @return The database connection.
   */
  def openDefaultDatabase(): Database = openDatabaseFromConfiguration("default")

  /**
   * Open a database using settings from a configuration entry.
   * @param name The name of the sub-configuration under "database" in the application configuration.
   * @return The database connection.
   */
  def openDatabaseFromConfiguration(name: String): Database = {
    if (name == null || name.isEmpty)
      throw new IllegalArgumentException("Requirement argument: 'database'.")

    val configuration =
      dbConfig.getConfig(name)
        .withFallback(databaseSettingDefaults)

    openDatabase(
      configuration.getString("name"),
      configuration.getString("server"),
      configuration.getString("userName"),
      configuration.getString("password")
    )
  }

  /**
   * Open a connection to the database.
   * @param databaseName The name of the database to connect to.
   * @param serverName The name of the database server to connect to.
   * @param userName The name of the database user (default is `postgres`).
   * @param password The password for the database user (default is `postgres`).
   * @return The database connection.
   */
  def openDatabase(
    databaseName: String,
    serverName: String = "localhost",
    userName: String = "postgress",
    password: String = "postgres"): Database = {

      Database.forURL(
        s"jdbc:postgresql://$serverName/$databaseName",
        userName,
        password
      )
  }

  /**
   * Asynchronously create / re-create the database schema.
   * @param database The database in which to create the schema.
   * @return A [[Future]] representing the asynchronous operation.
   */
  def createSchemaAsync(database: Database): Future[Unit] = {
    if (database == null)
      throw new scala.IllegalArgumentException("Requirement argument: 'database'.")

    database.run(
      DBIO.seq(
        queueEntries.schema.drop,
        queueEntries.schema.create
      )
    )
  }
}
