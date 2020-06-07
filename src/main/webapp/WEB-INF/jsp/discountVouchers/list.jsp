<%@ page session="false" trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="petclinic" tagdir="/WEB-INF/tags" %>

<petclinic:layout pageName="owners">
    <h2>${ownerUserName}'s Discount Vouchers</h2>

    <table id="discountVouchersTable" class="table table-striped">
        <thead>
        <tr>
            <th>Description</th>
            <th>Discount</th>
            <th>Date</th>
            <th>Used</th>
        </tr>
        </thead>
        <tbody>
        <c:forEach items="${discountVouchers}" var="voucher">
            <tr>
                <td>
                    <c:out value="${voucher.description}"/>
                </td>
                <td>
                    <c:out value="${voucher.discount} "/>
                </td>
                <td>
                    <c:out value="${voucher.created}"/>
                </td>
                <td>
	                <c:if test="${voucher.redeemedBeautySolutionVisit != null}">
                    	<c:out value="${voucher.redeemedBeautySolutionVisit.visitLabel}"/>
	                </c:if>
	                <c:if test="${voucher.redeemedBeautySolutionVisit == null || voucher.redeemedBeautySolutionVisit.date < now}">
		                No
	                </c:if>
                </td>
            </tr>
        </c:forEach>
        </tbody>
    </table>
</petclinic:layout>
