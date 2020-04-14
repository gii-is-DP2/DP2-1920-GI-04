<%@ page session="false" trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="petclinic" tagdir="/WEB-INF/tags" %>

<petclinic:layout pageName="beautyServices">
    <h2>Beauty Services</h2>

    <table id="beautyServiceVisitsTable" class="table table-striped">
        <thead>
        <tr>
            <th>BeautyService</th>
            <th>Pet</th>
            <th>Date</th>
            <th>Price</th>
        </tr>
        </thead>
        <tbody>
        <c:forEach items="${beautyServiceVisits}" var="visit">
            <tr>
                <td>
                    <spring:url value="/beauty-service/{serviceId}" var="detailsUrl">
                        <spring:param name="serviceId" value="${visit.beautyService.id}"/>
                    </spring:url>
                    <a href="${fn:escapeXml(detailsUrl)}"><c:out value="${visit.beautyService.title}"/></a>
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
            </tr>
        </c:forEach>
        </tbody>
    </table>
</petclinic:layout>
