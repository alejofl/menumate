<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<html>
<head>
    <spring:message code="restaurantorders.head" var="title" arguments="${restaurant.name}"/>
    <jsp:include page="/WEB-INF/jsp/components/head.jsp">
        <jsp:param name="title" value="${title}"/>
    </jsp:include>
    <script src="<c:url value="/static/js/restaurant_reviews.js"/>"></script>
</head>
<body>
    <div class="content">
        <jsp:include page="/WEB-INF/jsp/components/navbar.jsp"/>
        <c:if test="${error || param.error == '1'}">
            <jsp:include page="/WEB-INF/jsp/components/param_error.jsp"/>
        </c:if>
        <div class="restaurant-reviews">
            <c:forEach items="${reviews}" var="review">
                <div class="card m-2">
                    <div class="card-header">
                        <b><c:out value="${review.order.user.name}"/></b>
                        <div class="d-flex gap-2 align-items-baseline my-2">
                            <div class="small-ratings">
                                <c:forEach begin="1" end="${review.rating}">
                                    <i class="bi bi-star-fill rating-color"></i>
                                </c:forEach>
                                <c:forEach begin="1" end="${5 - review.rating}">
                                    <i class="bi bi-star-fill"></i>
                                </c:forEach>
                            </div>
                                <%-- This os a workaround to make LocalDateTime formattable --%>
                            <fmt:parseDate value="${review.date}" pattern="yyyy-MM-dd'T'HH:mm:ss" var="parsedDateOrdered" type="both"/>
                            <fmt:formatDate pattern="dd MMMM yyyy - HH:mm" value="${parsedDateOrdered}" var="reviewDate"/>
                            <small class="text-muted">${reviewDate}</small>
                        </div>
                    </div>
                    <div class="card-body">
                        <p><c:out value="${review.comment}"/></p>
                    </div>
                    <div class="card-footer">
                        <a href=""
                           class="clickable-object"
                           data-bs-toggle="modal"
                           data-bs-target="#view-order-modal"
                           data-order-type="${review.order.orderType.ordinal()}"
                           data-order-id="${review.order.orderId}"
                                <c:forEach items="${review.order.items}" var="item" varStatus="loop">
                                    data-order-item-${loop.index}-line-number="<c:out value="${item.lineNumber}"/>"
                                    data-order-item-${loop.index}-comment="<c:out value="${item.comment}"/>"
                                    data-order-item-${loop.index}-product-name="<c:out value="${item.product.name}"/>"
                                    data-order-item-${loop.index}-product-price="<c:out value="${item.product.price}"/>"
                                    data-order-item-${loop.index}-quantity="<c:out value="${item.quantity}"/>"
                                </c:forEach>
                           data-order-items-quantity="${fn:length(review.order.items)}"
                           data-order-total-price="${review.order.price}"
                           data-order-customer-name="<c:out value="${review.order.user.name}"/>"
                           data-order-customer-email="<c:out value="${review.order.user.email}"/>"
                           data-order-table-number="<c:out value="${review.order.tableNumber}"/>"
                           data-order-address="<c:out value="${review.order.address}"/>"
                        >
                            <small>View Order</small>
                        </a>
                        <a class="btn btn-primary" href="#" role="button">Reply</a>
                    </div>
                </div>
            </c:forEach>
        </div>

        <div class="modal fade" id="view-order-modal" tabindex="-1">
            <div class="modal-dialog modal-dialog-scrollable modal-lg modal-dialog-centered">
                <div class="modal-content">
                    <div class="modal-header">
                        <h5 class="modal-title"><spring:message code="restaurantorders.orderid"/><span id="order-title"></span></h5>
                        <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                    </div>
                    <div class="modal-body">
                        <h4><spring:message code="restaurantorders.orderitems"/></h4>
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
                        <h4><spring:message code="restaurantorders.moredetails"/></h4>
                        <ul class="list-group list-group-flush">
                            <li class="list-group-item d-flex align-items-center">
                                <i class="bi bi-person me-3"></i>
                                <div>
                                    <small class="text-muted"><spring:message code="restaurantorders.customer"/></small>
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
                </div>
            </div>
        </div>

    </div>

    <jsp:include page="/WEB-INF/jsp/components/footer.jsp"/>
</body>
</html>
