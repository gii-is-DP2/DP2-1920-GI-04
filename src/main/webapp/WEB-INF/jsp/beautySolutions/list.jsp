<%@ page session="false" trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="petclinic" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="sec"
	uri="http://www.springframework.org/security/tags"%>

<script>
	function filterByPetType(){
		var type = $('#filterText').val();
		if(type == 0){
			window.location.href = '/beauty-service/list';			
		} else {
			window.location.href = '/beauty-service/list?petType=' + type;
		}
	}
</script>

<petclinic:layout pageName="beautyServices">
    <h2>Beauty Services</h2>

	<div class="filter-box">
		<select id="filterText">
            <c:if test="${selectedType == null}">
				<option value="0"><fmt:message key="beautyservice.filter.placeholder"/></option>
            </c:if>
            <c:if test="${selectedType != null}">
				<option value="0" selected><fmt:message key="beautyservice.filter.placeholder"/></option>
            </c:if>
	        <c:forEach items="${petTypes}" var="petType">
                <c:if test="${selectedType != petType.id}">
					<option value="${petType.id}"><c:out value="${petType.name}"/></option>
                </c:if>
                <c:if test="${selectedType == petType.id}">
					<option value="${petType.id}" selected><c:out value="${petType.name}"/></option>
                </c:if>
	        </c:forEach>
		</select>
		<button onclick="filterByPetType()"><fmt:message key="beautyservice.filter.button"/></button>
	</div>

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
	
	<sec:authorize access="hasAnyAuthority('admin')">
	    <p><a href="/beauty-service/admin/create">Create new service</a></p>
	</sec:authorize>
</petclinic:layout>
