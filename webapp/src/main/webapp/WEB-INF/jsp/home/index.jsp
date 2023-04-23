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
    <div class="landing-container">
        <img src="/static/pictures/food.jpg" class="img-fluid landing-img">
        <div class="landing-img-container">
            <p class="landing-text">Order whatever you want, whenever you want and however you want!</p>
        </div>
    </div>
    <div class="landing-restaurants">
        <p class="landing-restaurants-title">Restaurants</p>
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
        <a href="<c:url value="/restaurants/"/>">
            <button type="button" class="btn btn-primary btn-lg text-uppercase">More Restaurants</button>
        </a>
    </div>
    <div class="landing-footer">
        <p class="landing-footer-text">© 2023 MenuMate FLAN Inc. All rights reserved.</p>
    </div>
</body>
</html>