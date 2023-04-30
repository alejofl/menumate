<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<html>
<head>
    <jsp:include page="/WEB-INF/jsp/components/head.jsp">
        <jsp:param name="title" value="My Orders"/>
    </jsp:include>
</head>
<body>
<jsp:include page="/WEB-INF/jsp/components/navbar.jsp"/>
<div class="page-title">
    <h1 class="">My Orders</h1>
</div>
<main class="restaurant-feed">
    <c:forEach var="order" items="${orders}">
        <%-- This os a workaround to make LocalDateTime formattable --%>
        <fmt:parseDate value="${order.dateOrdered}" pattern="yyyy-MM-dd'T'HH:mm:ss" var="parsedDateOrdered" type="both"/>
        <fmt:formatDate pattern="yyyy-MM-dd HH:mm" value="${parsedDateOrdered}" var="dateOrdered"/>

        <jsp:include page="/WEB-INF/jsp/components/order_card.jsp">
            <jsp:param name="id" value="${order.orderId}"/>
            <jsp:param name="restaurantLogoId" value="${order.restaurant.logoId}"/>
            <jsp:param name="restaurantName" value="${order.restaurant.name}"/>
            <jsp:param name="dateOrdered" value="${dateOrdered}"/>
            <jsp:param name="productQuantity" value="${fn:length(order.items)}"/>
            <jsp:param name="price" value="${order.price}"/>
            <jsp:param name="orderType" value="${order.orderType}"/>
        </jsp:include>
    </c:forEach>
</main>
</body>
</html>
