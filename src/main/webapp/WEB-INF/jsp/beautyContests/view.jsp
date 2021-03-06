<%@ page session="false" trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="petclinic" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="sec"
	uri="http://www.springframework.org/security/tags"%>

<petclinic:layout pageName="beautyContests">
    <h2>Beauty Contest</h2>
    
    <h4><c:out value="${beautyContest.getLabel()}"/></h4>
    
    <c:if test="${beautyContest.winner != null}">
    	<h4>Winner</h4>
        <c:out value="${beautyContest.winner.pet.name}"/> - <c:out value="${beautyContest.winner.pet.owner.firstName}"/> <c:out value="${beautyContest.winner.pet.owner.lastName}"/>
	    <img src="${beautyContest.winner.participationPhoto}" class="participation-winner-image"/>
    </c:if>
    
    <table id="beautyContestParticipationsTable" class="table table-striped">
        <thead>
        <tr>
            <th>Pet</th>
            <th>Owner</th>
            <th>Solution</th>
            <th>Photo</th>
			<sec:authorize access="hasAnyAuthority('admin')">
		        <c:if test="${ended && beautyContest.winner == null}">
          			<th></th>
		        </c:if>
			</sec:authorize>
        </tr>
        </thead>
        <tbody>
        <c:forEach items="${participations}" var="participation">
            <tr>
	            <td>
	            	<c:out value="${participation.pet.name}"/>
    
					<sec:authorize access="hasAnyAuthority('owner')">
				        <c:if test="${!ended && principalId == participation.pet.owner.id}">
		                    <spring:url value="/beauty-contest/owner/withdraw" var="withdrawUrl">
		                        <spring:param name="beautySolutionVisitId" value="${participation.id}"/>
		                    </spring:url>
		                    (<a href="${fn:escapeXml(withdrawUrl)}">Withdraw</a>)
				        </c:if>
					</sec:authorize>
	            </td>
	            <td>
	            	<c:out value="${participation.pet.owner.user.username}"/>
	            </td>
	            <td>
	            	<c:out value="${participation.beautySolution.title}"/>
	            </td>
	            <td>
	            	<img src="${participation.participationPhoto}" class="participation-image"/>
	            </td>
				<sec:authorize access="hasAnyAuthority('admin')">
			        <c:if test="${ended && beautyContest.winner == null}">
			            <td>
		                    <spring:url value="/beauty-contest/admin/{contestId}/{participationId}/award" var="awardUrl">
		                        <spring:param name="participationId" value="${participation.id}"/>
		                        <spring:param name="contestId" value="${beautyContest.id}"/>
		                    </spring:url>
		                    <a href="${fn:escapeXml(awardUrl)}">Select as winner</a>
			            </td>
			        </c:if>
				</sec:authorize>
            </tr>
        </c:forEach>
        </tbody>
    </table>
    
	<sec:authorize access="hasAnyAuthority('owner')">
        <c:if test="${!ended}">
		    <spring:url value="/beauty-contest/owner/{contestId}/participate" var="enterUrl">
		        <spring:param name="contestId" value="${beautyContest.id}"/>
		    </spring:url>
		    <p><a href="${fn:escapeXml(enterUrl)}">Enter the contest</a></p>
        </c:if>
	</sec:authorize>
</petclinic:layout>
