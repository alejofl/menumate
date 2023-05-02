<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<html>
<head>
    <spring:message code="500.title" var="title"/>
    <jsp:include page="/WEB-INF/jsp/components/head.jsp">
        <jsp:param name="title" value="${title}"/>
    </jsp:include>
</head>
<body>
    <h1 class="error-number">500</h1>
    <div class="error-container">
        <div class="error-image">
            <img src="<c:url value="/static/pictures/logo.png"/>" alt="MenuMate" height="40">
        </div>
        <div class="error-message">
            <h1><spring:message code="500.error"/></h1>
        </div>
        <div class="error-message">
            <p><spring:message code="500.description"/></p>
        </div>
        <div class="error-image">
            <a href="<c:url value="/"/>">
                <button type="button" class="btn btn-primary btn-lg"><spring:message code="backtohome"/></button>
            </a>
        </div>
    </div>
</body>
</html>
