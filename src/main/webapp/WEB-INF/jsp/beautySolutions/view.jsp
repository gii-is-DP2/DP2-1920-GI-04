<%@ page session="false" trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="petclinic" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="sec"
	uri="http://www.springframework.org/security/tags"%>

<petclinic:layout pageName="beautyServices">
    <h2><c:out value="${beautyService.title}"/></h2>
    
    <h4>Vet</h4>
    <c:out value="${beautyService.vet.firstName} ${beautyService.vet.lastName}"/>
    <h4>Pet Type</h4>
    <c:out value="${beautyService.type.name}"/>
    <h4>Price</h4>
    <c:out value="${beautyService.price}"/>
    
	<sec:authorize access="hasAnyAuthority('owner')">
	    <spring:url value="/beauty-service/visit/owner/create" var="visitUrl">
	        <spring:param name="beautyServiceId" value="${beautyService.id}"/>
	    </spring:url>
	    <p><a href="${fn:escapeXml(visitUrl)}">Book visit</a></p>
	</sec:authorize>
	
	<sec:authorize access="hasAnyAuthority('admin')">
	    <spring:url value="/beauty-service/admin/{beautyServiceId}/edit" var="editUrl">
	        <spring:param name="beautyServiceId" value="${beautyService.id}"/>
	    </spring:url>
	    <p><a href="${fn:escapeXml(editUrl)}">Edit service</a></p>
	    <spring:url value="/promotion/admin/create" var="promotionUrl">
	        <spring:param name="beautyServiceId" value="${beautyService.id}"/>
	    </spring:url>
	    <p><a href="${fn:escapeXml(promotionUrl)}">Create a promotion</a></p>
	</sec:authorize>
</petclinic:layout>
