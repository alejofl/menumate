<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<html>
<head>
    <spring:message code="userorders.ordernumber" arguments="${order.orderId}" var="title"/>
    <jsp:include page="/WEB-INF/jsp/components/head.jsp">
        <jsp:param name="title" value="${title}"/>
    </jsp:include></head>
<body>
<jsp:include page="/WEB-INF/jsp/components/navbar.jsp"/>
<main class="order-details">
    <div class="card order-details-card">
        <img src="<c:url value="/images/${order.restaurant.portraitId1}"/>" class="card-img-top" alt="${order.restaurant.name}">
        <div class="card-body">
            <div class="order-details-card-restaurant">
                <img src="<c:url value="/images/${order.restaurant.logoId}"/>" alt="${order.restaurant.name}">
                <div>
                    <small class="text-muted"><spring:message code="userorders.ordernumber" arguments="${order.orderId}"/></small>
                    <h3 class="card-title mb-0"><c:out value="${order.restaurant.name}"/></h3>
                    <hr>
                    <%-- This os a workaround to make LocalDateTime formattable --%>
                    <fmt:parseDate value="${order.dateOrdered}" pattern="yyyy-MM-dd'T'HH:mm:ss" var="parsedDateOrdered" type="both"/>
                    <fmt:formatDate pattern="dd MMMM yyyy - HH:mm" value="${parsedDateOrdered}" var="dateOrdered"/>
                    <i class="bi bi-calendar-event"></i> <c:out value="${dateOrdered}"/>
                </div>
            </div>
            <div class="alert alert-${order.orderStatus == 'CANCELLED' ? "danger" : "light"} d-flex flex-column align-items-center" role="alert">
                <div class="pb-2">
                    <spring:message code="orderstatus.order"/> <spring:message code="orderstatus.singular.${order.orderStatus.messageCode}"/>
                </div>

                <c:set value="" var="statusWidth"/>
                <c:choose>
                    <c:when test="${order.orderStatus == 'PENDING'}">
                        <c:set value="5" var="statusWidth"/>
                    </c:when>
                    <c:when test="${order.orderStatus == 'CONFIRMED'}">
                        <c:set value="33" var="statusWidth"/>
                    </c:when>
                    <c:when test="${order.orderStatus == 'READY'}">
                        <c:set value="66" var="statusWidth"/>
                    </c:when>
                    <c:when test="${order.orderStatus == 'DELIVERED'}">
                        <c:set value="100" var="statusWidth"/>
                    </c:when>
                    <c:when test="${order.orderStatus == 'CANCELLED'}">
                        <c:set value="100" var="statusWidth"/>
                    </c:when>
                </c:choose>

                <div class="progress w-100" role="progressbar">
                    <div class="progress-bar ${order.orderStatus == 'CANCELLED' ? "bg-danger" : ""}" style="width: ${statusWidth}%"></div>
                </div>
            </div>
            <div>
                <h4><spring:message code="userorders.yourorder"/></h4>
                <table class="table">
                    <thead class="table-light">
                        <tr>
                            <th scope="col">#</th>
                            <th scope="col"><spring:message code="userorders.product"/></th>
                            <th scope="col"><spring:message code="userorders.quantity"/></th>
                            <th scope="col"><spring:message code="userorders.price"/></th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach var="item" items="${order.items}">
                            <tr>
                                <th scope="row">${item.lineNumber}</th>
                                <td>${item.product.name}</td>
                                <td>${item.quantity}</td>
                                <td>$${item.product.price * item.quantity}</td>
                            </tr>
                        </c:forEach>
                    </tbody>
                    <tfoot>
                        <tr>
                            <th colspan="3"><spring:message code="userorders.total"/></th>
                            <th>$${order.price}</th>
                        </tr>
                    </tfoot>
                </table>
            </div>
            <div>
                <h4><spring:message code="restaurantorders.moredetails"/></h4>
                <ul class="list-group list-group-flush">
                    <li class="list-group-item d-flex align-items-center">
                        <i class="bi bi-card-list me-3"></i>
                        <div>
                            <small class="text-muted"><spring:message code="userorders.ordertype"/></small>
                            <p class="mb-0">
                                <spring:message code="restaurant.menu.form.${order.orderType.messageCode}"/>
                            </p>
                        </div>
                    </li>
                    <c:choose>
                        <c:when test="${order.orderType.ordinal() == 0}">
                            <li class="list-group-item d-flex align-items-center">
                                <i class="bi bi-hash me-3"></i>
                                <div>
                                    <small class="text-muted"><spring:message code="restaurant.menu.form.tablenumber"/></small>
                                    <p class="mb-0">
                                        <c:out value="${order.tableNumber}"/>
                                    </p>
                                </div>
                            </li>
                        </c:when>
                        <c:when test="${order.orderType.ordinal() == 2}">
                            <li class="list-group-item d-flex align-items-center">
                                <i class="bi bi-geo-alt me-3"></i>
                                <div>
                                    <small class="text-muted"><spring:message code="restaurant.menu.form.address"/></small>
                                    <p class="mb-0">
                                        <c:out value="${order.address}"/>
                                    </p>
                                </div>
                            </li>
                        </c:when>
                    </c:choose>
                </ul>
            </div>
        </div>
    </div>
    <a type="button" class="btn btn-primary" href="<c:url value="/restaurants/${order.restaurant.restaurantId}"/>"><spring:message code="userorders.neworder"/></a>
</main>
</body>
</html>
