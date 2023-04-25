<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<html>
<head>
    <spring:message code="restaurants.title" var="home"/>
    <spring:message code="restaurants.search.placeholder" var="placeholder"/>
    <jsp:include page="/WEB-INF/jsp/components/head.jsp">
        <jsp:param name="title" value="${home}"/>
    </jsp:include>
</head>
<body>
    <jsp:include page="/WEB-INF/jsp/components/navbar.jsp"/>
    <c:url var="search" value="/restaurants"/>
    <form:form modelAttribute="searchForm" action="${search}" method="get">
        <div class="input-group flex-nowrap">
            <span class="input-group-text search-input"><i class="bi bi-search"></i></span>
            <form:input type="text" path="search" cssClass="form-control search-input" placeholder="${placeholder}"/>
            <form:errors path="search" element="div" cssClass="form-error invalid-tooltip"/>
        </div>
        <input type="submit" class="btn btn-primary" value='<spring:message code="restaurants.search"/>'>
    </form:form>
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
        <c:if test="${fn:length(restaurants) == 0}">
            <div class="empty-results">
                <h1><i class="bi bi-slash-circle"></i></h1>
                <p>  <spring:message code="restaurants.search.noresult"/></p>
            </div>
        </c:if>
    </main>
</body>
</html>
