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
<%--                        <a href="" data-bs-toggle="modal" data-bs-target="#view-reviews-modal"><small><spring:message code="restaurant.menu.viewreviews"/></small></a>--%>
                        <a href=""><small>View Order</small></a>
                        <a class="btn btn-primary" href="#" role="button">Reply</a>
                    </div>
                </div>
            </c:forEach>
        </div>
    </div>
    <jsp:include page="/WEB-INF/jsp/components/footer.jsp"/>
</body>
</html>
