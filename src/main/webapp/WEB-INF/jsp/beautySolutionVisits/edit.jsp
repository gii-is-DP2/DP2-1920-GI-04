<%@ page session="false" trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="petclinic" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<petclinic:layout pageName="beautySolutionVisits">
    <jsp:body>
        <h2>Beauty Solution Visit</h2>
        <c:if test="${pets.size() > 0}">
	        <form:form modelAttribute="beautySolutionVisitForm" action="/beauty-solution/visit/owner/save" class="form-horizontal">
	            <input type="hidden" name="beautySolutionVisit.id" value="${beautySolutionVisitForm.beautySolutionVisit.id}"/>
	            <input type="hidden" name="beautySolutionVisit.beautySolution" value="${beautySolutionVisitForm.beautySolutionVisit.beautySolution.id}"/>
	            <input type="hidden" name="beautySolutionVisit.finalPrice" value="${beautySolutionVisitForm.beautySolutionVisit.finalPrice}"/>
	            <input type="hidden" name="beautySolutionVisit.cancelled" value="${beautySolutionVisitForm.beautySolutionVisit.cancelled}"/>
	            <input type="hidden" name="beautySolutionVisit.awardedDiscountVoucher" value="${beautySolutionVisitForm.beautySolutionVisit.awardedDiscountVoucher.id}"/>
	            <input type="hidden" name="beautySolutionVisit.cancelled" value="${beautySolutionVisitForm.beautySolutionVisit.participationPhoto}"/>
	            <input type="hidden" name="beautySolutionVisit.cancelled" value="${beautySolutionVisitForm.beautySolutionVisit.participationDate}"/>
	            <div class="form-group has-feedback">
	                <petclinic:inputField label="Date" name="beautySolutionVisit.date"/>
	                <petclinic:selectField name="beautySolutionVisit.pet" label="Pet" itemLabel="name" names="${pets}" size="1"/>
	                
			        <c:if test="${availableVouchers.size() > 0}">
	                	<petclinic:selectField name="discountVoucher" label="Discount voucher" itemLabel="label" names="${availableVouchers}" size="1"/>
			        </c:if>
			        <c:if test="${availableVouchers.size() == 0}">
	            		<input type="hidden" name="beautySolutionVisit.discountVoucher"/>
			        </c:if>
	            </div>
	            <div class="form-group">
	                <div class="col-sm-offset-2 col-sm-10">
	                	<button class="btn btn-default" type="submit">Book Beauty Solution Visit</button>
	                </div>
	            </div>
	            ${status}
	        </form:form>
        </c:if>
        <c:if test="${pets.size() == 0}">
        	You don't have any pet suitable for this beauty solution.
            <spring:url value="/beauty-solution/{solutionId}" var="backUrl">
                <spring:param name="solutionId" value="${beautySolutionVisitForm.beautySolutionVisit.beautySolution.id}"/>
            </spring:url>
            <a href="${fn:escapeXml(backUrl)}">Go back</a>
        </c:if>
    </jsp:body>
</petclinic:layout>
