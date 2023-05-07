<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<html>
<head>
    <spring:message code="restaurantorders.title" var="title"/>
    <jsp:include page="/WEB-INF/jsp/components/head.jsp">
        <jsp:param name="title" value="${title}"/>
    </jsp:include>
    <script src="<c:url value="/static/js/restaurant_orders.js"/>"></script>
</head>
<body>
<jsp:include page="/WEB-INF/jsp/components/navbar.jsp"/>
<c:if test="${error || param.error == '1'}">
    <jsp:include page="/WEB-INF/jsp/components/param_error.jsp"/>
</c:if>
<div class="page-title">
    <h1><spring:message code="restaurantorders.orders"/></h1>
</div>
<main class="restaurant-orders-view">
    <nav>
        <ul class="nav nav-pills nav-fill mb-3">
            <li class="nav-item">
                <a class="nav-link ${status == "pending" ? "active" : ""}" aria-current="page" href="<c:url value="/restaurants/${id}/orders/pending"/>">Pending</a>
            </li>
            <li class="nav-item">
                <a class="nav-link ${status == "confirmed" ? "active" : ""}" aria-current="page" href="<c:url value="/restaurants/${id}/orders/confirmed"/>">Confirmed</a>
            </li>
            <li class="nav-item">
                <a class="nav-link ${status == "ready" ? "active" : ""}" aria-current="page" href="<c:url value="/restaurants/${id}/orders/ready"/>">Ready</a>
            </li>
            <li class="nav-item">
                <a class="nav-link ${status == "delivered" ? "active" : ""}" aria-current="page" href="<c:url value="/restaurants/${id}/orders/delivered"/>">Delivered</a>
            </li>
        </ul>
    </nav>
    <div class="table-responsive w-75">
        <table class="table table-hover restaurant-orders-table">
            <thead class="table-light">
            <tr>
                <th class="text-start" scope="col"><spring:message code="restaurantorders.table.id"/></th>
                <th class="text-center" scope="col"><spring:message code="restaurantorders.table.order_type"/></th>
                <th class="text-center" scope="col"><spring:message code="restaurantorders.table.table_number"/></th>
                <th class="text-center" scope="col"><spring:message code="restaurantorders.table.address"/></th>
                <th class="text-end" scope="col"><spring:message code="restaurantorders.table.order_date"/></th>
            </tr>
            </thead>
            <tbody class="table-striped">
            <c:forEach items="${orders}" var="order">
                <tr
                        class="clickable-object clickable-row"
                        data-bs-toggle="modal"
                        data-bs-target="#order-details"
                        data-order-type="${order.orderType.ordinal()}"
                        data-order-id="${order.orderId}"
                        <c:forEach items="${order.items}" var="item">
                            data-order-item-${item.lineNumber}-line-number="<c:out value="${item.lineNumber}"/>"
                            data-order-item-${item.lineNumber}-comment="<c:out value="${item.comment}"/>"
                            data-order-item-${item.lineNumber}-product-name="<c:out value="${item.product.name}"/>"
                            data-order-item-${item.lineNumber}-product-price="<c:out value="${item.product.price}"/>"
                            data-order-item-${item.lineNumber}-quantity="<c:out value="${item.quantity}"/>"
                        </c:forEach>
                        data-order-items-quantity="${fn:length(order.items)}"
                        data-order-total-price="${order.price}"
                        data-order-customer-name="<c:out value="${order.user.name}"/>"
                        data-order-customer-email="<c:out value="${order.user.email}"/>"
                        data-order-table-number="<c:out value="${order.tableNumber}"/>"
                        data-order-address="<c:out value="${order.address}"/>"
                >
                    <fmt:parseDate value="${order.dateOrdered}" pattern="yyyy-MM-dd'T'HH:mm:ss" var="parsedDateOrdered" type="both"/>
                    <fmt:formatDate pattern="yyyy-MM-dd HH:mm" value="${parsedDateOrdered}" var="dateOrdered"/>
                    <td class="text-start"><c:out value="${order.orderId}"/></td>
                    <td class="text-center"><spring:message code="restaurant.menu.form.${order.orderType.messageCode}"/></td>
                    <c:choose>
                        <c:when test="${order.orderType == 'DINE_IN'}">
                            <td class="text-center"><c:out value="${order.tableNumber}"/></td>
                            <td class="text-center">-</td>
                        </c:when>
                        <c:when test="${order.orderType == 'TAKEAWAY'}">
                            <td class="text-center">-</td>
                            <td class="text-center">-</td>
                        </c:when>
                        <c:when test="${order.orderType == 'DELIVERY'}">
                            <td class="text-center">-</td>
                            <td class="text-center"><c:out value="${order.address}"/></td>
                        </c:when>
                    </c:choose>
                    <td class="text-end"><c:out value="${dateOrdered}"/></td>
                </tr>
            </c:forEach>
            </tbody>
        </table>
    </div>
</main>

<div class="modal fade" id="order-details" tabindex="-1">
    <div class="modal-dialog modal-dialog-scrollable modal-lg modal-dialog-centered">
        <div class="modal-content">
            <div class="modal-header">
                <h5 class="modal-title"><spring:message code="restaurantorders.orderid"/><span id="order-title"></span></h5>
                <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
            </div>
            <div class="modal-body">
                <h4>Order Items</h4>
                <table class="table table-hover">
                    <thead class="table-light">
                    <tr>
                        <th scope="col" class="text-start">#</th>
                        <th scope="col" class="text-center"><spring:message code="restaurantorders.modal.product_name"/></th>
                        <th scope="col" class="text-center"><spring:message code="restaurantorders.modal.comments"/></th>
                        <th scope="col" class="text-center"><spring:message code="restaurantorders.modal.quantity"/></th>
                        <th scope="col" class="text-end"><spring:message code="restaurantorders.modal.product_price"/></th>
                    </tr>
                    </thead>
                    <tbody id="order-items"></tbody>
                </table>
                <h4>More Details</h4>
                <ul class="list-group list-group-flush">
                    <li class="list-group-item d-flex align-items-center">
                        <i class="bi bi-person me-3"></i>
                        <div>
                            <small class="text-muted">Customer</small>
                            <p class="mb-0" id="order-details-customer">
                            </p>
                        </div>
                    </li>
                    <li class="list-group-item d-flex align-items-center order-details-0-data">
                        <i class="bi bi-card-list me-3"></i>
                        <div>
                            <small class="text-muted"><spring:message code="userorders.ordertype"/></small>
                            <p class="mb-0">
                                <spring:message code="restaurant.menu.form.dinein"/>
                            </p>
                        </div>
                    </li>
                    <li class="list-group-item d-flex align-items-center order-details-0-data">
                        <i class="bi bi-hash me-3"></i>
                        <div>
                            <small class="text-muted"><spring:message code="restaurant.menu.form.tablenumber"/></small>
                            <p class="mb-0" id="order-details-table-number">
                            </p>
                        </div>
                    </li>
                    <li class="list-group-item d-flex align-items-center order-details-1-data">
                        <i class="bi bi-card-list me-3"></i>
                        <div>
                            <small class="text-muted"><spring:message code="userorders.ordertype"/></small>
                            <p class="mb-0">
                                <spring:message code="restaurant.menu.form.takeaway"/>
                            </p>
                        </div>
                    </li>
                    <li class="list-group-item d-flex align-items-center order-details-2-data">
                        <i class="bi bi-card-list me-3"></i>
                        <div>
                            <small class="text-muted"><spring:message code="userorders.ordertype"/></small>
                            <p class="mb-0">
                                <spring:message code="restaurant.menu.form.delivery"/>
                            </p>
                        </div>
                    </li>
                    <li class="list-group-item d-flex align-items-center order-details-2-data">
                        <i class="bi bi-geo-alt me-3"></i>
                        <div>
                            <small class="text-muted"><spring:message code="restaurant.menu.form.address"/></small>
                            <p class="mb-0" id="order-details-address">
                            </p>
                        </div>
                    </li>
                    <li class="list-group-item d-flex align-items-center">
                        <i class="bi bi-cash me-3"></i>
                        <div>
                            <small class="text-muted"><spring:message code="restaurantorders.modal.total"/></small>
                            <p class="mb-0" id="order-total-price">
                            </p>
                        </div>
                    </li>
                </ul>
            </div>
            <c:if test="${status != 'delivered'}">
                <div class="modal-footer">
                    <form action="<c:url value="/orders/$1/cancel"/>" method="post" id="cancel-order-form">
                        <button type="submit" class="btn btn-danger">Cancel</button>
                    </form>
                    <c:choose>
                        <c:when test="${status == 'pending'}">
                            <form action="<c:url value="/orders/$1/confirm"/>" method="post" id="change-order-status-form">
                                <button type="submit" class="btn btn-primary">Confirm</button>
                            </form>
                        </c:when>
                        <c:when test="${status == 'confirmed'}">
                            <form action="<c:url value="/orders/$1/ready"/>" method="post" id="change-order-status-form">
                                <button type="submit" class="btn btn-primary">Ready</button>
                            </form>
                        </c:when>
                        <c:when test="${status == 'ready'}">
                            <form action="<c:url value="/orders/$1/deliver"/>" method="post" id="change-order-status-form">
                                <button type="submit" class="btn btn-primary">Deliver</button>
                            </form>
                        </c:when>
                    </c:choose>
                </div>
            </c:if>
        </div>
    </div>
</div>

<%-- PAGINATION --%>
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
            <c:url value="/restaurants/${id}/orders/${status}" var="previousUrl">
                <c:param name="page" value="${currentPage - 1}"/>
                <c:param name="size" value="${currentSize}"/>
            </c:url>
            <a class="page-link ${currentPage == 1 ? "disabled" : ""}" href="${previousUrl}" aria-label="Previous">
                <span aria-hidden="true">&laquo;</span>
            </a>
        </li>
        <c:forEach begin="1" end="${pageCount}" var="pageNo">
            <c:url value="/restaurants/${id}/orders/${status}" var="pageUrl">
                <c:param name="page" value="${pageNo}"/>
                <c:param name="size" value="${currentSize}"/>
            </c:url>
            <li class="page-item ${pageNo == currentPage ? "active" : ""}"><a class="page-link" href="${pageUrl}">${pageNo}</a></li>
        </c:forEach>
        <li class="page-item">
            <c:url value="/restaurants/${id}/orders/${status}" var="nextUrl">
                <c:param name="page" value="${currentPage + 1}"/>
                <c:param name="size" value="${currentSize}"/>
            </c:url>
            <a class="page-link ${currentPage == pageCount ? "disabled" : ""}" href="${nextUrl}" aria-label="Next">
                <span aria-hidden="true">&raquo;</span>
            </a>
        </li>
    </ul>
</nav>

</body>
</html>
