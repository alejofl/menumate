<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>


<html>
<body>
    <a class="clickable-object" href="<c:url value="/orders/${param.id}"/>">
        <div class="card order-card">
            <div class="card-body">
                <div class="order-card-restaurant">
                    <img src="<c:url value="/images/${param.restaurantLogoId}"/>" alt="<c:out value="${param.restaurantName}"/>">
                    <div>
                        <small class="text-muted"><spring:message code="userorders.ordernumber" arguments="${param.id}"/></small>
                        <h5 class="card-title mb-0"><c:out value="${param.restaurantName}"/></h5>
                    </div>
                </div>
            </div>
            <ul class="list-group list-group-flush">
                <fmt:parseNumber var="quantity" type="number" value="${param.productQuantity}" />

                <li class="list-group-item"><i class="bi bi-card-list"></i> <c:out value="${param.orderType}"/></li>
                <li class="list-group-item"><i class="bi bi-calendar-event"></i> <c:out value="${param.dateOrdered}"/></li>
                <li class="list-group-item"><i class="bi bi-cart"></i> <spring:message code="userorders.productquantity" arguments="${quantity}"/></li>
                <li class="list-group-item"><i class="bi bi-cash-stack"></i> $<c:out value="${param.price}"/></li>
            </ul>
            <div class="card-footer">
                <spring:message code="orderstatus.order"/> <spring:message code="orderstatus.singular.${param.orderStatus}"/>
            </div>
        </div>
    </a>
</body>
</html>