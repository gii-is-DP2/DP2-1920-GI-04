<%@ page session="false" trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="petclinic" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<petclinic:layout pageName="beautyContests">
    <jsp:body>
        <h2>
            Enter the contest
        </h2>
            
        <c:if test="${visits.size() > 0}">
	        <form:form modelAttribute="participationForm" action="/beauty-contest/owner/participate" class="form-horizontal">
	            <div class="form-group has-feedback">
		            <input type="hidden" name="beautyContestId" value="${participationForm.beautyContestId}"/>
		            <petclinic:selectField name="visitId" label="Pet and visit" itemLabel="visitLabel" names="${visits}" size="${visits.size()}"/>
	                <petclinic:inputField label="Photo url" name="participationPhoto"/>
	            </div>
	            <div class="form-group">
	                <div class="col-sm-offset-2 col-sm-10">
	                    <button class="btn btn-default" type="submit">Enter the participation</button>
	                </div>
	            </div>
	            ${status}
	        </form:form>
        </c:if>
        
        <c:if test="${visits.size() == 0}">
        	You don't have any visit suitable for a participation on this month. Remember you need to wait for the visit to take place.
            <spring:url value="/beauty-contest/{contestId}" var="backUrl">
                <spring:param name="contestId" value="${participationForm.beautyContestId}"/>
            </spring:url>
            <a href="${fn:escapeXml(backUrl)}">Go back</a>
        </c:if>
    </jsp:body>
</petclinic:layout>
