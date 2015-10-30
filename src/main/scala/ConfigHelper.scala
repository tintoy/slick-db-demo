import com.typesafe.config.{Config, ConfigFactory}
import scala.collection._
import scala.collection.JavaConversions._

/**
 * Helper functions for working with [[Config]]
 */
object ConfigHelper {
  /**
   * Parse a Scala [[Map]] to construct a [[Config]].
   * @param map The Scala [[Map]].
   * @return The resulting [[Config]].
   */
  def parseScalaMap(map: Map[String, AnyRef]): Config = {
    if (map == null)
      throw new IllegalArgumentException("Requirement argument: 'map'.")

    ConfigFactory.parseMap(
      mapAsJavaMap[String, AnyRef](map)
    )
  }
}
