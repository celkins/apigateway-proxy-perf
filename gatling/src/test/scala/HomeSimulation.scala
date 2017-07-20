import ch.qos.logback.classic.{Level, LoggerContext}
import io.gatling.core.Predef._
import io.gatling.http.Predef._
import org.slf4j.LoggerFactory

import scala.concurrent.duration._

class HomeSimulation extends Simulation {

  private val context = LoggerFactory.getILoggerFactory.asInstanceOf[LoggerContext]
  // Log all HTTP requests
//  context.getLogger("io.gatling.http").setLevel(Level.valueOf("TRACE"))
  // Log failed HTTP requests
  context.getLogger("io.gatling.http").setLevel(Level.valueOf("DEBUG"))

  private val baseURL = Option(System.getProperty("baseURL")) getOrElse """http://localhost:8080"""

  private val httpConf = http
    .warmUp(baseURL)
    .baseURLs(baseURL)
    .acceptHeader("application/json")
    .acceptEncodingHeader("gzip, deflate")
    .connectionHeader("keep-alive")
    .userAgentHeader("Gatling")
    .extraInfoExtractor(extraInfo => List(extraInfo.request))

  private val scn = scenario("Home")
    .repeat(5) {
      exec(http("Get Home")
        .get("/"))
        .pause(10)
    }

  setUp(
    scn.inject(rampUsers(100) over (1 minutes))
  ).protocols(httpConf)
    .assertions(
      details("Get Home").failedRequests.percent.is(0))
}