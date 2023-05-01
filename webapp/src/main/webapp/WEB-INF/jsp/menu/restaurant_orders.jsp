<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<html>
<head>
    <spring:message code="restaurant.menu.comments" var="comments"/>
    <jsp:include page="/WEB-INF/jsp/components/head.jsp">
        <jsp:param name="title" value="${restaurant.name}"/>
    </jsp:include>
    <script src="<c:url value="/static/js/restaurant_orders.js"/>"></script>
</head>
<body>
<jsp:include page="/WEB-INF/jsp/components/navbar.jsp"/>
<main>
    <div class="card">
        <div class="card-body">
            <div class="table-responsive">
                <table class="table">
                    <thead>
                    <tr>
                        <th scope="col">Order ID</th>
                        <th scope="col">Order Type</th>
                        <th scope="col">Table Number</th>
                        <th scope="col">Address</th>
                        <th scope="col">Time Ordered</th>
                    </tr>
                    </thead>
                    <tbody>
                    <c:forEach items="${orders}" var="order">
                            <tr
                                class="clickable-object clickable-row"
                                data-bs-toggle="modal"
                                data-bs-target="#order-details"
                                data-order-id="${order.orderId}"
                                data-order-items="${order.items}"
                            >
                                <td><c:out value="${order.orderId}"> </c:out></td>
                                <c:choose>
                                    <c:when test="${order.orderType == 'DINE_IN'}">
                                        <td>Dine In</td>
                                        <td><c:out value="${order.tableNumber}"> </c:out></td>
                                        <td>-</td>
                                    </c:when>
                                    <c:when test="${order.orderType == 'TAKEAWAY'}">
                                        <td>Takeaway</td>
                                        <td>-</td>
                                        <td>-</td>
                                    </c:when>
                                    <c:when test="${order.orderType == 'DELIVERY'}">
                                        <td>Delivery</td>
                                        <td>-</td>
                                        <td><c:out value="${order.address}"> </c:out></td>
                                    </c:when>
                                </c:choose>
                                <td><c:out value="${order.dateOrdered}"></c:out></td>
                            </tr>
                    </c:forEach>
                    </tbody>
                </table>
            </div>
        </div>
    </div>

    <div class="modal fade" id="order-details" tabindex="-1">
        <div class="modal-dialog">
            <div class="modal-content">
                <div class="modal-header">
                    <h5 class="modal-title" id="order-title"></h5>
                    <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                </div>
                <div class="modal-body">
                    <p id="order-items"></p>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Close</button>
                    <button type="button" class="btn btn-primary">Save changes</button>
                </div>
            </div>
        </div>
    </div>
</main>
</body>
</html>
