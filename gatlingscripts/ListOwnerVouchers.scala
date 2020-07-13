package dp2

import scala.concurrent.duration._

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import io.gatling.jdbc.Predef._

class ListOwnerVouchers extends Simulation {

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
			.formParam("username", "admin1")
			.formParam("password", "admin")
			.formParam("_csrf", "${stoken}"))
		.pause(5)
	}

	object ListOwners {
		val listOwners = exec(http("List owners")
			.get("/owners")
			.headers(headers_0))
		.pause(5)
	}

	object ViewProfile {
		val viewProfile = exec(http("View profile")
			.get("/owners/1")
			.headers(headers_0))
		.pause(5)
	}

	object ViewProfile2 {
		val viewProfile2 = exec(http("View profile (owner 10)")
			.get("/owners/10")
			.headers(headers_0))
		.pause(5)
	}

	object ListVouchers {
		val listVouchers = exec(http("List vouchers")
			.get("/discount-voucher/admin/list?ownerId=1")
			.headers(headers_0))
		.pause(5)
	}

	object ListVouchers2 {
		val listVouchers2 = exec(http("List vouchers owner 10")
			.get("/discount-voucher/admin/list?ownerId=10")
			.headers(headers_0))
		.pause(5)
	}


	val listVouchersScn = scenario("US10Scenario").exec(
		Home.home,
		Login.login,
		ListOwners.listOwners,
		ViewProfile.viewProfile,
		ListVouchers.listVouchers
	)

	val listVouchersScn2 = scenario("US10Scenario2").exec(
		Home.home,
		Login.login,
		ListOwners.listOwners,
		ViewProfile2.viewProfile2,
		ListVouchers2.listVouchers2
	)
	
	setUp(listVouchersScn.inject(rampUsers(4000) during (100 seconds)), listVouchersScn2.inject(rampUsers(4000) during (100 seconds))).protocols(httpProtocol).assertions(
        global.responseTime.max.lt(5000),    
        global.responseTime.mean.lt(1000),
        global.successfulRequests.percent.gt(95)
    )
}