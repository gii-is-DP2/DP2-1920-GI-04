<%@ page session="false" trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="petclinic" tagdir="/WEB-INF/tags" %>

<petclinic:layout pageName="beautyServiceVisits">
    <jsp:body>
        <h2>Beauty Service Visit</h2>
        <form:form modelAttribute="beautyServiceVisitForm" action="/beauty-service/visit/owner/save" class="form-horizontal">
            <input type="hidden" name="beautyServiceVisit.id" value="${beautyServiceVisitForm.beautyServiceVisit.id}"/>
            <input type="hidden" name="beautyServiceVisit.beautyService" value="${beautyServiceVisitForm.beautyServiceVisit.beautyService.id}"/>
            <input type="hidden" name="beautyServiceVisit.finalPrice" value="${beautyServiceVisitForm.beautyServiceVisit.finalPrice}"/>
            <input type="hidden" name="beautyServiceVisit.cancelled" value="${beautyServiceVisitForm.beautyServiceVisit.cancelled}"/>
            <input type="hidden" name="beautyServiceVisit.awardedDiscountVoucher" value="${beautyServiceVisitForm.beautyServiceVisit.awardedDiscountVoucher.id}"/>
            <div class="form-group has-feedback">
                <petclinic:inputField label="Date" name="beautyServiceVisit.date"/>
                <div class="control-group">
                    <petclinic:selectField name="beautyServiceVisit.pet" label="Pet" itemLabel="name" names="${pets}" size="${pets.size()}"/>
                </div>
                <div class="control-group">
                    <petclinic:selectField name="discountVoucher" label="Discount voucher" itemLabel="discount" names="${availableVouchers}" size="${availableVouchers.size()}"/>
                </div>
            </div>
            <div class="form-group">
                <div class="col-sm-offset-2 col-sm-10">
                	<button class="btn btn-default" type="submit">Book Beauty Service Visit</button>
                </div>
            </div>
            ${status}
        </form:form>
    </jsp:body>
</petclinic:layout>
