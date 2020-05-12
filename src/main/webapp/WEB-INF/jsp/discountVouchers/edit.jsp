<%@ page session="false" trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="petclinic" tagdir="/WEB-INF/tags" %>

<petclinic:layout pageName="discountVoucher">
    <jsp:body>
        <h2>
            New Discount Voucher
        </h2>
        <form:form modelAttribute="discountVoucher" action="/discount-voucher/admin/save" class="form-horizontal">
            <div class="form-group has-feedback">
	            <input type="hidden" name="id" value="${discountVoucher.id}"/>
	            <input type="hidden" name="owner" value="${discountVoucher.owner.id}"/>
	            <input type="hidden" name="created" value='${createdDate}'/>
	            <input type="hidden" name="redeemedBeautyServiceVisit" value="${null}"/>
                <petclinic:inputField label="Discount" name="discount"/>
                <petclinic:inputField label="Description" name="description"/>
            </div>
            <div class="form-group">
                <div class="col-sm-offset-2 col-sm-10">
                	<button class="btn btn-default" type="submit">Create Discount Voucher</button>
                </div>
            </div>
            ${status}
        </form:form>
    </jsp:body>
</petclinic:layout>
