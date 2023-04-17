<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>

<html>
<head>
    <jsp:include page="/WEB-INF/jsp/components/head.jsp">
        <jsp:param name="title" value="Home"/>
    </jsp:include>
</head>
<body>
<jsp:include page="/WEB-INF/jsp/components/navbar.jsp"/>
<main class="restaurant-feed">
    <c:forEach items="${restaurants}" var="restaurant">
        <c:url var="restaurantUrl" value="/restaurants/${restaurant.restaurantId}"/>
        <c:url var="mainImage" value="/images/${restaurant.portraitId1}"/>
        <c:url var="hoverImage" value="/images/${restaurant.portraitId2}"/>
        <jsp:include page="/WEB-INF/jsp/components/restaurant_card.jsp">
            <jsp:param name="name" value="${restaurant.name}"/>
            <jsp:param name="address" value="${restaurant.address}"/>
            <jsp:param name="main_image" value="${mainImage}"/>
            <jsp:param name="hover_image" value="${hoverImage}"/>
            <jsp:param name="link" value="${restaurantUrl}"/>
        </jsp:include>
    </c:forEach>
</main>
</body>
</html>