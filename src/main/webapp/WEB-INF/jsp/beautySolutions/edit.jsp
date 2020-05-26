<%@ page session="false" trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="petclinic" tagdir="/WEB-INF/tags" %>

<petclinic:layout pageName="beautySolution">
    <jsp:body>
        <h2>
            <c:if test="${beautySolution['new']}">New </c:if> Beauty Solution
        </h2>
        <form:form modelAttribute="beautySolution" action="/beauty-solution/admin/save" class="form-horizontal">
            <input type="hidden" name="id" value="${beautySolution.id}"/>
            <div class="form-group has-feedback">
                <petclinic:inputField label="Title" name="title"/>
                <c:if test="${beautySolution['new']}">
	                <div class="control-group">
	                    <petclinic:selectField name="type" label="Type" names="${types}" size="${types.size()}"/>
	                </div>
                </c:if>
                <c:if test="${!beautySolution['new']}">
                	<input type="hidden" name="type" value="${beautySolution.type}"/>
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
                        <c:when test="${beautySolution['new']}">
                            <button class="btn btn-default" type="submit">Create Beauty Solution</button>
                        </c:when>
                        <c:otherwise>
                            <button class="btn btn-default" type="submit">Update Beauty Solution</button>
                        </c:otherwise>
                    </c:choose>
                </div>
            </div>
            ${status}
        </form:form>
    </jsp:body>
</petclinic:layout>
