<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<html>
<head>
    <spring:message code="restaurant.reviews.title" var="title" arguments="${restaurant.name}"/>
    <jsp:include page="/WEB-INF/jsp/components/head.jsp">
        <jsp:param name="title" value="${title}"/>
    </jsp:include>
    <script src="<c:url value="/static/js/restaurant_reviews.js"/>"></script>
</head>
<body data-review-reply-form-errors="${reviewReplyFormErrors}">
    <div class="content">
        <jsp:include page="/WEB-INF/jsp/components/navbar.jsp"/>
        <c:if test="${error || param.error == '1'}">
            <jsp:include page="/WEB-INF/jsp/components/param_error.jsp"/>
        </c:if>
        <div class="page-title">
            <spring:message code="restaurant.reviews.title" arguments="${restaurant.name}" var="pageTitle"/>
            <h1><c:out value="${pageTitle}"/></h1>
        </div>
        <div class="restaurant-reviews">
            <c:forEach items="${reviews}" var="review">
                <div class="card m-2">
                    <div class="card-header">
                        <div class="mt-2">
                            <b><c:out value="${review.order.user.name}"/></b>
                        </div>
                        <div class="d-flex gap-2 align-items-baseline mb-2">
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
                           class="view-order-clickeable"
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
                            <small><spring:message code="restaurant.reviews.vieworder"/></small>
                        </a>
                        <c:choose>
                            <c:when test="${review.reply != null}">
                                <button class="btn btn-primary view-reply-button" data-bs-toggle="modal" data-bs-target="#view-reply-modal" data-reply="<c:out value="${review.reply}"/>" role="button">
                                    <spring:message code="restaurant.reviews.viewreply"/>
                                </button>
                            </c:when>
                            <c:otherwise>
                                <button class="btn btn-primary reply-review-button" data-bs-toggle="modal" data-bs-target="#reply-review-modal" data-order-id="${review.orderId}" role="button">
                                    <spring:message code="restaurant.reviews.reply"/>
                                </button>
                            </c:otherwise>
                        </c:choose>
                    </div>
                </div>
            </c:forEach>
        </div>


        <div class="modal fade" id="view-reply-modal" tabindex="-1">
            <div class="modal-dialog modal-dialog-centered">
                <div class="modal-content">
                    <div class="modal-header">
                        <h1 class="modal-title fs-5"><spring:message code="restaurant.reviews.viewreply.modal.title"/></h1>
                        <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                    </div>
                    <div class="modal-body">
                        <p id="view-reply-container"></p>
                    </div>
                </div>
            </div>
        </div>

        <div class="modal fade" id="reply-review-modal" tabindex="-1">
            <div class="modal-dialog modal-dialog-centered">
                <div class="modal-content">
                    <div class="modal-header">
                        <h1 class="modal-title fs-5"><spring:message code="restaurant.reviews.reply.modal.title"/></h1>
                        <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                    </div>
                    <c:url value="/restaurants/${restaurant.restaurantId}/reviews" var="reviewReplyUrl"/>
                    <form:form cssClass="mb-0" modelAttribute="reviewReplyForm" action="${reviewReplyUrl}" method="post">
                        <div class="modal-body">
                            <div>
                                <spring:message code="restaurant.reviews.reply.form.label" var="replyLabel"/>
                                <form:textarea class="form-control" path="reply" rows="3" id="reply-review-form-reply" placeholder="${replyLabel}"/>
                                <form:errors path="reply" element="div" cssClass="form-error"/>
                            </div>
                        </div>
                        <form:input type="hidden" path="orderId" id="reply-review-form-order-id"/>
                        <div class="modal-footer">
                            <input type="submit" class="btn btn-primary" value="<spring:message code="restaurant.reviews.reply"/>">
                        </div>
                    </form:form>
                </div>
            </div>
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

        <nav class="d-flex justify-content-center mt-4">
            <ul class="pagination">
                <li class="page-item">
                    <c:url value="/restaurants/${id}/reviews" var="previousUrl">
                        <c:param name="page" value="${currentPage - 1}"/>
                        <c:param name="size" value="${currentSize}"/>
                    </c:url>
                    <a class="page-link ${currentPage == 1 ? "disabled" : ""}" href="${previousUrl}" aria-label="Previous">
                        <span aria-hidden="true">&laquo;</span>
                    </a>
                </li>
                <c:forEach begin="1" end="${pageCount}" var="pageNo">
                    <c:url value="/restaurants/${id}/reviews" var="pageUrl">
                        <c:param name="page" value="${pageNo}"/>
                        <c:param name="size" value="${currentSize}"/>
                    </c:url>
                    <li class="page-item ${pageNo == currentPage ? "active" : ""}"><a class="page-link" href="${pageUrl}">${pageNo}</a></li>
                </c:forEach>
                <li class="page-item">
                    <c:url value="/restaurants/${id}/reviews" var="nextUrl">
                        <c:param name="page" value="${currentPage + 1}"/>
                        <c:param name="size" value="${currentSize}"/>
                    </c:url>
                    <a class="page-link ${(currentPage == pageCount || pageCount == 0) ? "disabled" : ""}" href="${nextUrl}" aria-label="Next">
                        <span aria-hidden="true">&raquo;</span>
                    </a>
                </li>
            </ul>
        </nav>
    </div>

    <jsp:include page="/WEB-INF/jsp/components/footer.jsp"/>
</body>
</html>
