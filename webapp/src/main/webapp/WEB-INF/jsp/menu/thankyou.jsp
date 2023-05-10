<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<html>
<head>
    <spring:message code="thankyou.title" var="thankyou"/>
    <jsp:include page="/WEB-INF/jsp/components/head.jsp">
        <jsp:param name="title" value="${thankyou}"/>
    </jsp:include>
</head>
<body class="position-relative" style="overflow: hidden;">
    <svg class="circle centered" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 52 52">
        <circle class="circle-circle" cx="26" cy="26" r="25" fill="none"></circle>
    </svg>
    <svg class="checkmark centered" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 52 52">
        <circle class="checkmark-circle" cx="26" cy="26" r="25" fill="none"></circle>
        <path class="checkmark-check" fill="none" d="M14.1 27.2l7.1 7.2 16.7-16.8"></path>
    </svg>
    <div class="thank-you-container centered">
        <div class="m-4">
            <img src="<c:url value="/static/pictures/logo-white.png"/>" alt="MenuMate" height="40">
        </div>
        <div class="thank-you-message text-color-white">
            <h1><spring:message code="thankyou.placed"/></h1>
        </div>
        <div class="thank-you-message text-color-white">
            <p><spring:message code="thankyou.description"/></p>
        </div>
        <div class="m-2">
            <c:choose>
                <c:when test="${currentUser != null}">
                    <a type="button" class="btn btn-primary-inverted" href="<c:url value="/orders/${orderId}"/>">
                        <spring:message code="thankyou.loggedin"/>
                    </a>
                </c:when>
                <c:when test="${userExists}">
                    <a type="button" class="btn btn-primary-inverted" href="<c:url value="/orders/${orderId}"/>">
                        <spring:message code="thankyou.userexists"/>
                    </a>
                </c:when>
                <c:otherwise>
                    <a type="button" class="btn btn-primary" href="<c:url value="/auth/register"/>">
                        <spring:message code="thankyou.newuser"/>
                    </a>
                </c:otherwise>
            </c:choose>
        </div>
    </div>
</body>
</html>
