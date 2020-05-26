<%@ page session="false" trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="petclinic" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="sec"
	uri="http://www.springframework.org/security/tags"%>

<petclinic:layout pageName="beautyContests">
    <h2>Beauty Contests</h2>

    <table id="beautyContestsTable" class="table table-striped">
        <thead>
        <tr>
            <th style="width: 150px;">Contest</th>
            <th>Winner</th>
        </tr>
        </thead>
        <tbody>
        <c:forEach items="${beautyContests}" var="contest">
            <tr>
                <td>
                    <spring:url value="/beauty-contest/{contestId}" var="detailsUrl">
                        <spring:param name="contestId" value="${contest.id}"/>
                    </spring:url>
                    <a href="${fn:escapeXml(detailsUrl)}"><c:out value="${contest.getLabel()}"/></a>
                </td>
                <td>
	                <c:if test="${contest.winner != null}">
	                    <c:out value="${contest.winner.pet.owner.name}"/>
	                </c:if>
	                <c:if test="${contest.winner == null}">
	                    Not decided yet
	                </c:if>
                </td>
            </tr>
        </c:forEach>
        </tbody>
    </table>
</petclinic:layout>
