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
			window.location.href = '/beauty-solution/list';			
		} else {
			window.location.href = '/beauty-solution/list?petType=' + type;
		}
	}
</script>

<petclinic:layout pageName="beautySolutions">
    <h2>Beauty Solutions</h2>

	<div class="filter-box">
		<select id="filterText">
            <c:if test="${selectedType == null}">
				<option value="0"><fmt:message key="beautysolution.filter.placeholder"/></option>
            </c:if>
            <c:if test="${selectedType != null}">
				<option value="0" selected><fmt:message key="beautysolution.filter.placeholder"/></option>
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
		<button onclick="filterByPetType()"><fmt:message key="beautysolution.filter.button"/></button>
	</div>

    <table id="beautySolutionsTable" class="table table-striped">
        <thead>
        <tr>
            <th style="width: 150px;">Title</th>
            <th style="width: 200px;">Type</th>
            <th>Vet</th>
            <th>Price</th>
        </tr>
        </thead>
        <tbody>
        <c:forEach items="${beautySolutions}" var="solution">
            <tr>
                <td>
                    <spring:url value="/beauty-solution/{solutionId}" var="detailsUrl">
                        <spring:param name="solutionId" value="${solution.id}"/>
                    </spring:url>
                    <a href="${fn:escapeXml(detailsUrl)}"><c:out value="${solution.title}"/></a>
                </td>
                <td>
                    <c:out value="${solution.type}"/>
                </td>
                <td>
                    <c:out value="${solution.vet.firstName} ${solution.vet.lastName}"/>
                </td>
                <td>
                    <c:out value="${solution.price}"/>
                </td>
            </tr>
        </c:forEach>
        </tbody>
    </table>
	
	<sec:authorize access="hasAnyAuthority('admin')">
	    <p><a href="/beauty-solution/admin/create">Create new solution</a></p>
	</sec:authorize>
</petclinic:layout>
