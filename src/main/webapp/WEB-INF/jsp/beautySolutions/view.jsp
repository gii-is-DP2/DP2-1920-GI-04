<%@ page session="false" trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="petclinic" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="sec"
	uri="http://www.springframework.org/security/tags"%>

<petclinic:layout pageName="beautySolutions">
    <h2><c:out value="${beautySolution.title}"/></h2>
    
    <h4>Vet</h4>
    <c:out value="${beautySolution.vet.firstName} ${beautySolution.vet.lastName}"/>
    <h4>Pet Type</h4>
    <c:out value="${beautySolution.type.name}"/>
    <h4>Price</h4>
    <c:out value="${beautySolution.price}"/>
    
	<sec:authorize access="hasAnyAuthority('owner')">
	    <spring:url value="/beauty-solution/visit/owner/create" var="visitUrl">
	        <spring:param name="beautySolutionId" value="${beautySolution.id}"/>
	    </spring:url>
	    <p><a href="${fn:escapeXml(visitUrl)}">Book visit</a></p>
	</sec:authorize>
	
	<sec:authorize access="hasAnyAuthority('admin')">
	    <spring:url value="/beauty-solution/admin/{beautySolutionId}/edit" var="editUrl">
	        <spring:param name="beautySolutionId" value="${beautySolution.id}"/>
	    </spring:url>
	    <p><a href="${fn:escapeXml(editUrl)}">Edit solution</a></p>
	    <spring:url value="/promotion/admin/create" var="promotionUrl">
	        <spring:param name="beautySolutionId" value="${beautySolution.id}"/>
	    </spring:url>
	    <p><a href="${fn:escapeXml(promotionUrl)}">Create a promotion</a></p>
	</sec:authorize>
	
    <h4>Forthcoming promotions</h4>
    
	<table id="promotionsTable" class="table table-striped">
        <thead>
        <tr>
            <th style="width: 150px;">Discount</th>
            <th style="width: 200px;">Start date</th>
            <th>End date</th>
        </tr>
        </thead>
        <tbody>
        <c:forEach items="${promotions}" var="promotion">
            <tr>
                <td>
                    <c:out value="${promotion.discount}"/>%
                </td>
                <td>
                    <c:out value="${promotion.startDate}"/>
                </td>
                <td>
                    <c:out value="${promotion.endDate}"/>
                </td>
            </tr>
        </c:forEach>
        </tbody>
    </table>
</petclinic:layout>
