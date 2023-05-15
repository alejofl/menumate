<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<html>
<head>
    <spring:message code="index.home" var="home"/>
    <jsp:include page="/WEB-INF/jsp/components/head.jsp">
        <jsp:param name="title" value="${home}"/>
    </jsp:include>
</head>
<body>
    <div class="content">
        <jsp:include page="/WEB-INF/jsp/components/navbar.jsp"/>
        <div class="landing-container">
            <p class="landing-text"><spring:message code="index.landingtext"/></p>
            <spring:message code="restaurants.search.placeholder" var="searchPlaceholder"/>
            <c:url var="search" value="/restaurants"/>
            <form:form modelAttribute="searchForm" action="${search}" method="get">
                <div class="search-form-container">
                    <div>
                        <div class="input-group flex-nowrap">
                            <span class="input-group-text search-input"><i class="bi bi-search"></i></span>
                            <form:input type="text" path="search" cssClass="form-control search-input" placeholder="${searchPlaceholder}"/>
                            <form:errors path="search" element="div" cssClass="form-error invalid-tooltip"/>
                        </div>
                        <form:input type="hidden" path="page" value="1"/>
                    </div>
                </div>
                <input type="submit" class="btn btn-primary" value='<spring:message code="restaurants.search"/>'>
            </form:form>
        </div>
        <div class="landing-restaurants">
            <p class="landing-restaurants-title"><spring:message code="index.restaurant.title"/></p>
            <main class="restaurant-feed">
                <c:forEach items="${restaurants}" var="restaurantDetails">
                    <c:url var="restaurantUrl" value="/restaurants/${restaurantDetails.restaurant.restaurantId}"/>
                    <c:url var="mainImage" value="/images/${restaurantDetails.restaurant.portraitId1}"/>
                    <c:url var="hoverImage" value="/images/${restaurantDetails.restaurant.portraitId2}"/>
                    <c:set var="tags" value="${restaurantDetails.tags}" scope="request"/>
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
    </div>
    <jsp:include page="/WEB-INF/jsp/components/footer.jsp"/>
</body>
</html>