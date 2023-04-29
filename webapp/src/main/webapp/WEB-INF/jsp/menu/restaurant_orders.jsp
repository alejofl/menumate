<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<html>
<head>
    <spring:message code="restaurant.menu.comments" var="comments"/>
    <jsp:include page="/WEB-INF/jsp/components/head.jsp">
        <jsp:param name="title" value="${restaurant.name}"/>
    </jsp:include>
    <script src="<c:url value="/static/js/restaurant_menu.js"/>"></script>
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
                        <th scope="col">#</th>
                        <th scope="col">Header</th>
                        <th scope="col">Header</th>
                        <th scope="col">Header</th>
                        <th scope="col">Header</th>
                    </tr>
                    </thead>
                    <tbody>
                    <c:forEach items="${orders}" var="order">
                        <tr>
                            <td><c:out value="${order.orderId}"> </c:out></td>
                            <td><c:out value="${order.address}"> </c:out></td>
                            <td>data</td>
                            <td>placeholder</td>
                            <td>text</td>
                        </tr>
                    </c:forEach>
                    </tbody>
                </table>
            </div>
        </div>
    </div>
</main>
</body>
</html>
