<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<html>
<head>
    <spring:message var="title" code="userorders.title"/>
    <jsp:include page="/WEB-INF/jsp/components/head.jsp">
        <jsp:param name="title" value="${title}"/>
    </jsp:include>
</head>
<body>
<jsp:include page="/WEB-INF/jsp/components/navbar.jsp"/>
<div class="page-title">
    <h1><spring:message code="userorders.title"/></h1>
</div>
<main class="restaurant-feed">
    <spring:message var="dinein" code="restaurant.menu.form.dinein"/>
    <spring:message var="takeaway" code="restaurant.menu.form.takeaway"/>
    <spring:message var="delivery" code="restaurant.menu.form.delivery"/>

    <c:forEach var="order" items="${orders}">
        <%-- This os a workaround to make LocalDateTime formattable --%>
        <fmt:parseDate value="${order.dateOrdered}" pattern="yyyy-MM-dd'T'HH:mm:ss" var="parsedDateOrdered" type="both"/>
        <fmt:formatDate pattern="yyyy-MM-dd HH:mm" value="${parsedDateOrdered}" var="dateOrdered"/>

        <c:choose>
            <c:when test="${order.orderType.ordinal() == 0}">
                <c:set var="orderType" value="${dinein}"/>
            </c:when>
            <c:when test="${order.orderType.ordinal() == 1}">
                <c:set var="orderType" value="${takeaway}"/>
            </c:when>
            <c:otherwise>
                <c:set var="orderType" value="${delivery}"/>
            </c:otherwise>
        </c:choose>

        <jsp:include page="/WEB-INF/jsp/components/order_card.jsp">
            <jsp:param name="id" value="${order.orderId}"/>
            <jsp:param name="restaurantLogoId" value="${order.restaurant.logoId}"/>
            <jsp:param name="restaurantName" value="${order.restaurant.name}"/>
            <jsp:param name="dateOrdered" value="${dateOrdered}"/>
            <jsp:param name="productQuantity" value="${fn:length(order.items)}"/>
            <jsp:param name="price" value="${order.price}"/>
            <jsp:param name="orderType" value="${orderType}"/>
        </jsp:include>
    </c:forEach>
</main>
</body>
</html>
