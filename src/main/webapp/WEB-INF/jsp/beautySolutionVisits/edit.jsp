<%@ page session="false" trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="petclinic" tagdir="/WEB-INF/tags" %>

<petclinic:layout pageName="beautySolutionVisits">
    <jsp:body>
        <h2>Beauty Solution Visit</h2>
        <form:form modelAttribute="beautySolutionVisitForm" action="/beauty-solution/visit/owner/save" class="form-horizontal">
            <input type="hidden" name="beautySolutionVisit.id" value="${beautySolutionVisitForm.beautySolutionVisit.id}"/>
            <input type="hidden" name="beautySolutionVisit.beautySolution" value="${beautySolutionVisitForm.beautySolutionVisit.beautySolution.id}"/>
            <input type="hidden" name="beautySolutionVisit.finalPrice" value="${beautySolutionVisitForm.beautySolutionVisit.finalPrice}"/>
            <input type="hidden" name="beautySolutionVisit.cancelled" value="${beautySolutionVisitForm.beautySolutionVisit.cancelled}"/>
            <input type="hidden" name="beautySolutionVisit.awardedDiscountVoucher" value="${beautySolutionVisitForm.beautySolutionVisit.awardedDiscountVoucher.id}"/>
            <input type="hidden" name="beautySolutionVisit.cancelled" value="${beautySolutionVisitForm.beautySolutionVisit.participationPhoto}"/>
            <input type="hidden" name="beautySolutionVisit.cancelled" value="${beautySolutionVisitForm.beautySolutionVisit.paticipationDate}"/>
            <div class="form-group has-feedback">
                <petclinic:inputField label="Date" name="beautySolutionVisit.date"/>
                <div class="control-group">
                    <petclinic:selectField name="beautySolutionVisit.pet" label="Pet" itemLabel="name" names="${pets}" size="${pets.size()}"/>
                </div>
                <div class="control-group">
                    <petclinic:selectField name="discountVoucher" label="Discount voucher" itemLabel="discount" names="${availableVouchers}" size="${availableVouchers.size()}"/>
                </div>
            </div>
            <div class="form-group">
                <div class="col-sm-offset-2 col-sm-10">
                	<button class="btn btn-default" type="submit">Book Beauty Solution Visit</button>
                </div>
            </div>
            ${status}
        </form:form>
    </jsp:body>
</petclinic:layout>
