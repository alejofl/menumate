<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<html>
<head>
    <jsp:include page="/WEB-INF/jsp/components/head.jsp">
        <jsp:param name="title" value="404 Not found"/>
    </jsp:include>
</head>
<body>
    <div class="error-container">
        <div class="error-image">
            <img src="<c:url value="/static/pictures/logo.png"/>" alt="MenuMate" height="40">
        </div>
        <div class="error-message">
            <h1>Oops! Page not found!</h1>
        </div>
        <div class="error-message">
            <p>It looks like the page you're looking for doesn't exist.</p>
        </div>
        <div class="error-image">
            <a href="<c:url value="/"/>">
                <button type="button" class="btn btn-primary btn-lg text-uppercase">Back to home</button>
            </a>
        </div>
    </div>
</body>
</html>
