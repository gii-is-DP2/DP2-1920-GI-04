<%@ page session="false" trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="petclinic" tagdir="/WEB-INF/tags" %>

<petclinic:layout pageName="beautyServices">
    <h2>Beauty Services</h2>

    <table id="beautyServicesTable" class="table table-striped">
        <thead>
        <tr>
            <th style="width: 150px;">Title</th>
            <th style="width: 200px;">Type</th>
            <th>Vet</th>
            <th>Price</th>
        </tr>
        </thead>
        <tbody>
        <c:forEach items="${beautyServices}" var="service">
            <tr>
                <td>
                    <spring:url value="/beauty-service/{serviceId}" var="detailsUrl">
                        <spring:param name="serviceId" value="${service.id}"/>
                    </spring:url>
                    <a href="${fn:escapeXml(detailsUrl)}"><c:out value="${service.title}"/></a>
                </td>
                <td>
                    <c:out value="${service.type}"/>
                </td>
                <td>
                    <c:out value="${service.vet.firstName} ${service.vet.lastName}"/>
                </td>
                <td>
                    <c:out value="${service.price}"/>
                </td>
            </tr>
        </c:forEach>
        </tbody>
    </table>
</petclinic:layout>
