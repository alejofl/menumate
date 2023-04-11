<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt"%>

<html>
<head>
    <jsp:include page="/WEB-INF/jsp/components/head.jsp">
        <jsp:param name="title" value="Home"/>
    </jsp:include>
</head>
<body>
<jsp:include page="/WEB-INF/jsp/components/navbar.jsp"/>
<main class="restaurant-feed">
    <c:url var="milanga1Url" value="/static/pictures/milanga.jpg"/>
    <c:url var="milanga2Url" value="/static/pictures/milanga2.jpg"/>
    <c:forEach var = "i" begin = "1" end = "32">
        <jsp:include page="/WEB-INF/jsp/components/restaurant_card.jsp">
            <jsp:param name="name" value="Atuel"/>
            <jsp:param name="address" value="Atuel 200"/>
            <jsp:param name="main_image" value="${milanga1Url}"/>
            <jsp:param name="hover_image" value="${milanga2Url}"/>
        </jsp:include>
    </c:forEach>
</main>
</body>
</html>