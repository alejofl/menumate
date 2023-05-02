<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<html>
<head>
    <spring:message code="403.title" var="title"/>
    <jsp:include page="/WEB-INF/jsp/components/head.jsp">
        <jsp:param name="title" value="${title}"/>
    </jsp:include>
</head>
<body>
    <h1 class="error-number">403</h1>
    <div class="error-container">
        <div class="error-image">
            <img src="<c:url value="/static/pictures/logo.png"/>" alt="MenuMate" height="40">
        </div>
        <div class="error-message">
            <h1><spring:message code="403.error"/></h1>
        </div>
        <div class="error-message">
            <p><spring:message code="403.description"/></p>
        </div>
        <div class="error-image">
            <a href="<c:url value="/"/>">
                <button type="button" class="btn btn-primary btn-lg"><spring:message code="backtohome"/></button>
            </a>
        </div>
    </div>
</body>
</html>
