<%--
  Created by IntelliJ IDEA.
  User: nehue
  Date: 4/4/2023
  Time: 17:24
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <jsp:include page="/WEB-INF/jsp/components/head.jsp">
        <jsp:param name="title" value="Home"/>
    </jsp:include>
</head>
<body>
    <div class="error-container">
        <div class="error-image">
            <img src="/static/pictures/logo.png" alt="MenuMate" height="40">
        </div>
        <div class="error-message">
            <h1>Oops! Page not found!</h1>
        </div>
        <div class="error-message">
            <p>We are sorry for the inconvenience. It looks like you're trying to access
                a page that has been deleted or never existed.</p>
        </div>
        <div class="error-image">
            <a href="/">
                <button type="button" class="btn btn-primary btn-lg text-uppercase">Back to home</button>
            </a>
        </div>
        <div class="error-item">

        </div>
    </div>

<%--    <div class="flexbox-container">--%>
<%--        <div class="flexbox-item flexbox-item1"></div>--%>
<%--        <div class="flexbox-item flexbox-item2"></div>--%>
<%--        <div class="flexbox-item flexbox-item3"></div>--%>

<%--    </div>--%>
</body>
</html>
