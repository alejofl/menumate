<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>

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
        <jsp:include page="/WEB-INF/jsp/components/order_card.jsp">
            <jsp:param name="id" value="${order.orderId}"/>
            <jsp:param name="restaurant" value="${order.restaurant}"/>
            <jsp:param name="dateOrdered" value="${order.dateOrdered}"/>
            <jsp:param name="productQuantity" value="${order.items.size}"/>
            <jsp:param name="price" value="${order.price}"/>
            <jsp:param name="orderType" value="${order.orderType}"/>
        </jsp:include>
    </c:forEach>
</main>
</body>
</html>
