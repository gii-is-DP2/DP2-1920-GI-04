<%@ tag trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="petclinic" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<%@ attribute name="pageName" required="true" %>
<%@ attribute name="customScript" required="false" fragment="true"%>

<!doctype html>
<html>
<petclinic:htmlHeader/>

<body>
<petclinic:bodyHeader menuName="${pageName}"/>

<div class="container-fluid">
    <div class="container xd-container">

       	<c:if test="${errorMessage != null}">
    		<p class="error-message"><fmt:message key="${errorMessage}"/></p>
       	</c:if>
       	
        <jsp:doBody/>
        

        <petclinic:pivotal/>
    </div>
</div>
<petclinic:footer/>
<jsp:invoke fragment="customScript" />

</body>

</html>
