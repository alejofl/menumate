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
            <div class="error-item">
                <h1>Error 404.</h1>
            </div>
            <div class="error-item">
                <h2>Page Not Found.</h2>
            </div>
            <div class="error-item">
                <button type="button" class="btn btn-primary btn-lg text-uppercase">Back to home</button>
            </div>
        </div>

<%--    <div class="flexbox-container">--%>
<%--        <div class="flexbox-item flexbox-item1"></div>--%>
<%--        <div class="flexbox-item flexbox-item2"></div>--%>
<%--        <div class="flexbox-item flexbox-item3"></div>--%>

<%--    </div>--%>
</body>
</html>
