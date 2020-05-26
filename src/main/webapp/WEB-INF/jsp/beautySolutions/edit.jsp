<%@ page session="false" trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="petclinic" tagdir="/WEB-INF/tags" %>

<petclinic:layout pageName="beautyService">
    <jsp:body>
        <h2>
            <c:if test="${beautyService['new']}">New </c:if> Beauty Service
        </h2>
        <form:form modelAttribute="beautyService" action="/beauty-service/admin/save" class="form-horizontal">
            <input type="hidden" name="id" value="${beautyService.id}"/>
            <div class="form-group has-feedback">
                <petclinic:inputField label="Title" name="title"/>
                <c:if test="${beautyService['new']}">
	                <div class="control-group">
	                    <petclinic:selectField name="type" label="Type" names="${types}" size="${types.size()}"/>
	                </div>
                </c:if>
                <c:if test="${!beautyService['new']}">
                	<input type="hidden" name="type" value="${beautyService.type}"/>
                </c:if>
                <div class="control-group">
                    <petclinic:selectField name="vet" label="Vet" itemLabel="firstName" names="${vets}" size="${vets.size()}"/>
                </div>
                <petclinic:inputField label="Price" name="price"/>
                <petclinic:checkboxField label="Enabled" name="enabled"/>
            </div>
            <div class="form-group">
                <div class="col-sm-offset-2 col-sm-10">
                    <c:choose>
                        <c:when test="${beautyService['new']}">
                            <button class="btn btn-default" type="submit">Create Beauty Service</button>
                        </c:when>
                        <c:otherwise>
                            <button class="btn btn-default" type="submit">Update Beauty Service</button>
                        </c:otherwise>
                    </c:choose>
                </div>
            </div>
            ${status}
        </form:form>
    </jsp:body>
</petclinic:layout>
