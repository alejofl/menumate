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
<c:if test="${error}">
    <jsp:include page="/WEB-INF/jsp/components/param_error.jsp"/>
</c:if>
<div class="page-title">
    <h1><spring:message code="userorders.title"/></h1>
</div>
<main class="restaurant-feed">
    <c:forEach var="order" items="${orders}">
        <%-- This os a workaround to make LocalDateTime formattable --%>
        <fmt:parseDate value="${order.dateOrdered}" pattern="yyyy-MM-dd'T'HH:mm:ss" var="parsedDateOrdered" type="both"/>
        <fmt:formatDate pattern="yyyy-MM-dd HH:mm" value="${parsedDateOrdered}" var="dateOrdered"/>

        <spring:message var="orderType" code="restaurant.menu.form.${order.orderType.messageCode}"/>

        <jsp:include page="/WEB-INF/jsp/components/order_card.jsp">
            <jsp:param name="id" value="${order.orderId}"/>
            <jsp:param name="restaurantLogoId" value="${order.restaurant.logoId}"/>
            <jsp:param name="restaurantName" value="${order.restaurant.name}"/>
            <jsp:param name="dateOrdered" value="${dateOrdered}"/>
            <jsp:param name="productQuantity" value="${order.itemCount}"/>
            <jsp:param name="price" value="${order.price}"/>
            <jsp:param name="orderType" value="${orderType}"/>
            <jsp:param name="orderStatus" value="${order.orderStatus.messageCode}"/>
        </jsp:include>
    </c:forEach>

    <c:if test="${fn:length(orders) == 0}">
        <div class="empty-results">
            <h1><i class="bi bi-slash-circle"></i></h1>
            <p><spring:message code="userorders.noorders"/></p>
        </div>
    </c:if>
</main>

<c:choose>
    <c:when test="${empty param.page}">
        <c:set var="currentPage" value="1"/>
    </c:when>
    <c:otherwise>
        <c:set var="currentPage" value="${param.page}"/>
    </c:otherwise>
</c:choose>
<c:choose>
    <c:when test="${empty param.size}">
        <c:set var="currentSize" value="20"/>
    </c:when>
    <c:otherwise>
        <c:set var="currentSize" value="${param.size}"/>
    </c:otherwise>
</c:choose>

<nav class="d-flex justify-content-center">
    <ul class="pagination">
        <li class="page-item">
            <c:url value="/orders" var="previousUrl">
                <c:param name="page" value="${currentPage - 1}"/>
                <c:param name="size" value="${currentSize}"/>
            </c:url>
            <a class="page-link ${currentPage == 1 ? "disabled" : ""}" href="${previousUrl}" aria-label="Previous">
                <span aria-hidden="true">&laquo;</span>
            </a>
        </li>
        <c:forEach begin="1" end="${pageCount}" var="pageNo">
            <c:url value="/orders" var="pageUrl">
                <c:param name="page" value="${pageNo}"/>
                <c:param name="size" value="${currentSize}"/>
            </c:url>
            <li class="page-item ${pageNo == currentPage ? "active" : ""}"><a class="page-link" href="${pageUrl}">${pageNo}</a></li>
        </c:forEach>
        <li class="page-item">
            <c:url value="/orders" var="nextUrl">
                <c:param name="page" value="${currentPage + 1}"/>
                <c:param name="size" value="${currentSize}"/>
            </c:url>
            <a class="page-link ${(currentPage == pageCount || pageCount == 0) ? "disabled" : ""}" href="${nextUrl}" aria-label="Next">
                <span aria-hidden="true">&raquo;</span>
            </a>
        </li>
    </ul>
</nav>
</body>
</html>
