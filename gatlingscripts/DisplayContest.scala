package dp2

import scala.concurrent.duration._

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import io.gatling.jdbc.Predef._

class DisplayContest extends Simulation {

	val httpProtocol = http
		.baseUrl("http://www.dp2.com")
		.inferHtmlResources(BlackList(""".*.css""", """.*.js""", """.*.ico""", """.*.png"""), WhiteList())
		.acceptHeader("text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9")
		.acceptEncodingHeader("gzip, deflate")
		.acceptLanguageHeader("es-ES,es;q=0.9")
		.userAgentHeader("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/81.0.4044.138 Safari/537.36")

	val headers_0 = Map(
		"Proxy-Connection" -> "keep-alive",
		"Upgrade-Insecure-Requests" -> "1")

	val headers_2 = Map(
		"Accept" -> "image/webp,image/apng,image/*,*/*;q=0.8",
		"Proxy-Connection" -> "keep-alive")

	val headers_3 = Map(
		"Origin" -> "http://www.dp2.com",
		"Proxy-Connection" -> "keep-alive",
		"Upgrade-Insecure-Requests" -> "1")

	object Home {
		val home = exec(http("Home")
			.get("/")
			.headers(headers_0))
		.pause(4)
	}

	object ListContests {
		val listContests = exec(http("List contests")
			.get("/beauty-contest/list")
			.headers(headers_0))
		.pause(5)
	}

	object DisplayContest {
		val displayContest = exec(http("Display contest 49")
			.get("/beauty-contest/49")
			.headers(headers_0))
		.pause(5)
	}

	object DisplayContest2 {
		val displayContest2 = exec(http("Display contest 126")
			.get("/beauty-contest/126")
			.headers(headers_0))
		.pause(5)
	}


	val displayContestsScn = scenario("US14Scenario").exec(
		Home.home,
		ListContests.listContests,
		DisplayContest.displayContest
	)


	val displayContestsScn2 = scenario("US14Scenario2").exec(
		Home.home,
		ListContests.listContests,
		DisplayContest2.displayContest2
	)
	
	setUp(displayContestsScn.inject(rampUsers(8000) during (100 seconds)), displayContestsScn2.inject(rampUsers(8000) during (100 seconds))).protocols(httpProtocol).assertions(
        global.responseTime.max.lt(5000),    
        global.responseTime.mean.lt(1000),
        global.successfulRequests.percent.gt(95)
    )
}