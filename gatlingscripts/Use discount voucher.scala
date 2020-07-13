package dp2

import scala.concurrent.duration._

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import io.gatling.jdbc.Predef._

class UseDiscountVoucher extends Simulation {

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

	object Login {
		val login = exec(http("Login GET")
			.get("/login")
			.headers(headers_0)
			.check(css("input[name=_csrf]", "value").saveAs("stoken")))
		.pause(4)
		.exec(http("Login POST")
			.post("/login")
			.headers(headers_3)
			.formParam("username", "owner1")
			.formParam("password", "owner")
			.formParam("_csrf", "${stoken}"))
		.pause(5)
	}

	object ListSolutions {
		val listSolutions = exec(http("List solutions")
			.get("/beauty-solution/list")
			.headers(headers_0))
		.pause(5)
	}

	object DisplaySolution {
		val displaySolution = exec(http("Display solution")
			.get("/beauty-solution/21")
			.headers(headers_0))
		.pause(5)
	}

	object CreateVisit {
		val createVisit = exec(http("Book visit GET")
			.get("/beauty-solution/visit/owner/create?beautySolutionId=21")
			.headers(headers_0)
			.check(css("input[name=_csrf]", "value").saveAs("stoken")))
		.pause(13)
		.exec(http("Book visit POST")
			.post("/beauty-solution/visit/owner/save")
			.headers(headers_3)
			.formParam("beautySolutionVisit.id", "")
			.formParam("beautySolutionVisit.beautySolution", "23")
			.formParam("beautySolutionVisit.finalPrice", "15.0")
			.formParam("beautySolutionVisit.cancelled", "false")
			.formParam("beautySolutionVisit.awardedDiscountVoucher", "")
			.formParam("beautySolutionVisit.date", "2020/07/28")
			.formParam("beautySolutionVisit.pet", "1")
			.formParam("beautySolutionVisit.discountVoucher", "1")
			.formParam("_csrf", "${stoken}"))
		.pause(20)
	}


	val createVisitScn = scenario("US05Scenario").exec(
		Home.home,
		Login.login,
		ListSolutions.listSolutions,
		DisplaySolution.displaySolution,
		CreateVisit.createVisit
	)
	
	setUp(createVisitScn.inject(rampUsers(10000) during (100 seconds))).protocols(httpProtocol).assertions(
        global.responseTime.max.lt(5000),    
        global.responseTime.mean.lt(1000),
        global.successfulRequests.percent.gt(95)
    )
}