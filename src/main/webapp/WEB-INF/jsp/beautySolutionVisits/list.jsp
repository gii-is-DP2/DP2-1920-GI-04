<%@ page session="false" trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="petclinic" tagdir="/WEB-INF/tags" %>

<petclinic:layout pageName="beautySolutionVisits">
    <h2>Beauty Solutions</h2>

    <table id="beautySolutionVisitsTable" class="table table-striped">
        <thead>
        <tr>
            <th>BeautySolution</th>
            <th>Pet</th>
            <th>Date</th>
            <th>Price</th>
            <th></th>
        </tr>
        </thead>
        <tbody>
        <c:forEach items="${beautySolutionVisits}" var="visit">
            <tr>
                <td>
                    <spring:url value="/beauty-solution/{solutionId}" var="detailsUrl">
                        <spring:param name="solutionId" value="${visit.beautySolution.id}"/>
                    </spring:url>
                    <a href="${fn:escapeXml(detailsUrl)}"><c:out value="${visit.beautySolution.title}"/></a>
                </td>
                <td>
                    <c:out value="${visit.pet.name}"/>
                </td>
                <td>
                    <c:out value="${visit.date}"/>
                </td>
                <td>
                    <c:out value="${visit.finalPrice}"/>
                </td>
                <td>
	                <c:if test="${visit.date > now}">
	                    <spring:url value="/beauty-solution/visit/owner/{beautySolutionId}/cancel" var="cancelUrl">
	                        <spring:param name="beautySolutionId" value="${visit.id}"/>
	                    </spring:url>
	                    <a href="${fn:escapeXml(cancelUrl)}">Cancel</a>
	                </c:if>
                </td>
            </tr>
        </c:forEach>
        </tbody>
    </table>
</petclinic:layout>
