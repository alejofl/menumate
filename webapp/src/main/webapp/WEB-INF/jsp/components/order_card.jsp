<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<html>
<body>
    <a class="clickable-object" href="<c:url value="/orders/${param.id}"/>">
        <div class="card order-card">
            <div class="card-body">
                <p class="text-muted">#<c:out value="${param.id}"/></p>
                <div class="order-card-restaurant">
                    <img src="<c:url value="/orders/${param.restaurant.logoId}"/>" alt="${param.restaurant.name}">
                    <h5 class="card-title"><c:out value="${param.restaurant.name}"/></h5>
                </div>
            </div>
            <ul class="list-group list-group-flush">
                <li class="list-group-item"><i class="bi bi-calendar-event"></i> <c:out value="${param.dateOrdered}"/>/li>
                <li class="list-group-item"><i class="bi bi-cart"></i> <c:out value="${param.productQuantity}"/> products</li>
                <li class="list-group-item"><i class="bi bi-cash-stack"></i> $<c:out value="${param.price}"/></li>
            </ul>
            <div class="card-footer">
                <c:out value="${param.orderType}"/>
            </div>
        </div>
    </a>
</body>
</html>