<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="petclinic" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="sec"
	uri="http://www.springframework.org/security/tags"%>
<!--  >%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%-->
<%@ attribute name="name" required="true" rtexprvalue="true"
	description="Name of the active menu: home, owners, vets or error"%>

<nav class="navbar navbar-default" role="navigation">
	<div class="container">
		<div class="navbar-header">
			<a class="navbar-brand"
				href="<spring:url value="/" htmlEscape="true" />"><span></span></a>
			<button type="button" class="navbar-toggle" data-toggle="collapse"
				data-target="#main-navbar">
				<span class="sr-only"><os-p>Toggle navigation</os-p></span> <span
					class="icon-bar"></span> <span class="icon-bar"></span> <span
					class="icon-bar"></span>
			</button>
		</div>
		<div class="navbar-collapse collapse" id="main-navbar">
			<ul class="nav navbar-nav">

				<petclinic:menuItem active="${name eq 'home'}" url="/"
					title="home page">
					<span class="glyphicon glyphicon-home" aria-hidden="true"></span>
					<span>Home</span>
				</petclinic:menuItem>

				<petclinic:menuItem active="${name eq 'beautySolutions'}" url="/beauty-solution/list"
					title="find solutions">
					<span class="glyphicon glyphicon-heart" aria-hidden="true"></span>
					<span>Beauty Solutions</span>
				</petclinic:menuItem>
				
				<sec:authorize access="hasAnyAuthority('owner')">
					<petclinic:menuItem active="${name eq 'beautySolutionVisits'}" url="/beauty-solution/visit/owner/list"
						title="check your visits">
						<span class="glyphicon glyphicon-tags" aria-hidden="true"></span>
						<span>Beauty visits</span>
					</petclinic:menuItem>
				</sec:authorize>

				<petclinic:menuItem active="${name eq 'beautyContests'}" url="/beauty-contest/list"
					title="find solutions">
					<span class="glyphicon glyphicon-star" aria-hidden="true"></span>
					<span>Contests</span>
				</petclinic:menuItem>
				
				<sec:authorize access="hasAnyAuthority('owner')">
					<petclinic:menuItem active="${name eq 'owners'}" url="/owners/principal"
						title="find solutions">
						<span class="glyphicon glyphicon-user" aria-hidden="true"></span>
						<span>Profile</span>
					</petclinic:menuItem>
				</sec:authorize>
				
				<sec:authorize access="hasAnyAuthority('admin')">
					<li class="dropdown"><a href="#" class="dropdown-toggle"
						data-toggle="dropdown"> <span class="glyphicon glyphicon-user"></span> 
							<strong>Administrator</strong> <span
							class="glyphicon glyphicon-chevron-down"></span>
					</a>
						<ul class="dropdown-menu">
							<petclinic:menuItem active="${name eq 'owners'}"
								title="List owners" url="/owners">
								<span class="glyphicon glyphicon-user" aria-hidden="true"></span>
								<span>List owners</span>
							</petclinic:menuItem>
						</ul>
					</li>
				</sec:authorize>

			</ul>


			<ul class="nav navbar-nav navbar-right">
				<sec:authorize access="!isAuthenticated()">
					<li><a href="<c:url value="/login" />">Login</a></li>
					<li><a href="<c:url value="/users/new" />">Register</a></li>
				</sec:authorize>
				<sec:authorize access="isAuthenticated()">
					<li class="dropdown"><a href="#" class="dropdown-toggle"
						data-toggle="dropdown"> <span class="glyphicon glyphicon-cog"></span> 
							<strong><sec:authentication property="name" /></strong> <span
							class="glyphicon glyphicon-chevron-down"></span>
					</a>
						<ul class="dropdown-menu">
							<li>
								<div class="navbar-login">
									<div class="row">
										<div class="col-lg-4">
											<p class="text-center">
												<span class="glyphicon glyphicon-user icon-size"></span>
											</p>
										</div>
										<div class="col-lg-8">
											<p class="text-left">
												<strong><sec:authentication property="name" /></strong>
											</p>
											<p class="text-left">
												<a href="<c:url value="/logout" />"
													class="btn btn-primary btn-block btn-sm">Logout</a>
											</p>
										</div>
									</div>
								</div>
							</li>
							<li class="divider"></li>
<!-- 							
                            <li> 
								<div class="navbar-login navbar-login-session">
									<div class="row">
										<div class="col-lg-12">
											<p>
												<a href="#" class="btn btn-primary btn-block">My Profile</a>
												<a href="#" class="btn btn-danger btn-block">Change
													Password</a>
											</p>
										</div>
									</div>
								</div>
							</li>
-->
						</ul></li>
				</sec:authorize>
			</ul>
		</div>



	</div>
</nav>
