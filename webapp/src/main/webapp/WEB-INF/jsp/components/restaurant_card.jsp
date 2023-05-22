<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<body>
<a class="clickable-object" href="${param.link}">
    <div class="card restaurant-card">
        <img
                class="card-img restaurant-card-img"
                style="--main_image: url(${param.main_image}); --hover_image: url(${param.hover_image})"
        >
        <div class="card-body">
            <h5 class="card-title"><c:out value="${param.name}"/></h5>
            <p class="card-text"><c:out value="${param.address}"/></p>
            <div class="small-ratings">
                <c:forEach begin="1" end="${param.rating}">
                    <i class="bi bi-star-fill rating-color"></i>
                </c:forEach>
                <c:forEach begin="1" end="${5 - param.rating}">
                    <i class="bi bi-star-fill"></i>
                </c:forEach>
                <small class="text-muted ps-1">
                    <fmt:parseNumber var="ratingCount" type="number" value="${param.rating_count}"/>
                    <spring:message code="restaurants.ratingcount" arguments="${ratingCount}"/>
                </small>
            </div>
            <div class="tags-container">
                <c:forEach var="tag" items="${requestScope.tags}">
                    <span class="badge rounded-pill text-bg-secondary"><spring:message code="restauranttags.${tag.messageCode}"/></span>
                </c:forEach>
            </div>
        </div>
    </div>
</a>
</body>
</html>
