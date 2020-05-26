<%@ page session="false" trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="petclinic" tagdir="/WEB-INF/tags" %>

<petclinic:layout pageName="promotion">
    <jsp:body>
        <h2>
            New Promotion
        </h2>
        <form:form modelAttribute="promotion" action="/promotion/admin/save" class="form-horizontal">
            <input type="hidden" name="id" value="${promotion.id}"/>
            <input type="hidden" name="beautySolution" value="${promotion.beautySolution.id}"/>
            <div class="form-group has-feedback">
                <petclinic:inputField label="Discount" name="discount"/>
                <petclinic:inputField label="Start date" name="startDate"/>
                <petclinic:inputField label="End date" name="endDate"/>
            </div>
            <div class="form-group">
                <div class="col-sm-offset-2 col-sm-10">
                	<button class="btn btn-default" type="submit">Create Promotion</button>
                </div>
            </div>
            ${status}
        </form:form>
    </jsp:body>
</petclinic:layout>
