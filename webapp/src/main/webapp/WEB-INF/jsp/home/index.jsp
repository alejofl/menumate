<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<html>
<head>
    <spring:message code="index.home" var="home"/>
    <jsp:include page="/WEB-INF/jsp/components/head.jsp">
        <jsp:param name="title" value="${home}"/>
    </jsp:include>
</head>
<body>
    <jsp:include page="/WEB-INF/jsp/components/navbar.jsp"/>
    <div class="landing-container">
        <p class="landing-text"><spring:message code="index.landingtext"/></p>
        <jsp:include page="/WEB-INF/jsp/components/search.jsp"/>
    </div>
    <div class="landing-restaurants">
        <p class="landing-restaurants-title"><spring:message code="index.restaurant.title"/></p>
        <main class="restaurant-feed">
            <c:forEach items="${restaurants}" var="restaurantDetails">
                <c:url var="restaurantUrl" value="/restaurants/${restaurantDetails.restaurant.restaurantId}"/>
                <c:url var="mainImage" value="/images/${restaurantDetails.restaurant.portraitId1}"/>
                <c:url var="hoverImage" value="/images/${restaurantDetails.restaurant.portraitId2}"/>
                <jsp:include page="/WEB-INF/jsp/components/restaurant_card.jsp">
                    <jsp:param name="name" value="${restaurantDetails.restaurant.name}"/>
                    <jsp:param name="address" value="${restaurantDetails.restaurant.address}"/>
                    <jsp:param name="rating" value="${restaurantDetails.averageRating}"/>
                    <jsp:param name="main_image" value="${mainImage}"/>
                    <jsp:param name="hover_image" value="${hoverImage}"/>
                    <jsp:param name="link" value="${restaurantUrl}"/>
                </jsp:include>
            </c:forEach>
        </main>
        <a href="<c:url value="/restaurants"/>" class="btn btn-primary"><spring:message code="index.restaurant.button"/></a>
    </div>
    <div class="landing-footer">
        <small class="landing-footer-text"><spring:message code="copyright"/></small>
    </div>
</body>
</html>