<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>


<html>
<body>
    <a class="clickable-object" href="<c:url value="/orders/${param.id}"/>">
        <div class="card order-card">
            <div class="card-body">
                <div class="order-card-restaurant">
                    <img src="<c:url value="/images/${param.restaurantLogoId}"/>" alt="${param.restaurantName}">
                    <div>
                        <small class="text-muted">Order #<c:out value="${param.id}"/></small>
                        <h5 class="card-title mb-0"><c:out value="${param.restaurantName}"/></h5>
                    </div>
                </div>
            </div>
            <ul class="list-group list-group-flush">
                <li class="list-group-item"><i class="bi bi-calendar-event"></i> <c:out value="${param.dateOrdered}"/></li>
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