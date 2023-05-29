<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<html>
<head>
    <spring:message var="title" code="navbar.myrestaurants"/>
    <jsp:include page="/WEB-INF/jsp/components/head.jsp">
        <jsp:param name="title" value="${title}"/>
    </jsp:include>
</head>
<body>
    <div class="content">
        <jsp:include page="/WEB-INF/jsp/components/navbar.jsp"/>
        <c:if test="${error}">
            <jsp:include page="/WEB-INF/jsp/components/param_error.jsp"/>
        </c:if>
        <div class="page-title mb-4">
            <h1><spring:message code="navbar.myrestaurants"/></h1>
        </div>
        <main class="restaurant-feed">
            <c:forEach var="roleDetails" items="${restaurants}">
                <a class="clickable-object position-relative" href="<c:url value="/restaurants/${roleDetails.restaurantId}"/>">
                    <div class="card restaurant-card">
                        <img
                                class="card-img restaurant-card-img"
                                style="--main_image: url(<c:url value="/images/${roleDetails.restaurant.portrait1Id}"/>); --hover_image: url(<c:url value="/images/${roleDetails.restaurant.portrait2Id}"/>)"
                        >
                        <div class="card-body">
                            <h5 class="card-title"><c:out value="${roleDetails.restaurant.name}"/></h5>
                            <p class="card-text"><c:out value="${roleDetails.restaurant.address}"/></p>
                        </div>
                    </div>
                    <h4>
                    <span class="position-absolute top-0 start-50 translate-middle badge rounded-pill bg-${roleDetails.pendingOrderCount == 0 ? "success" : "danger"}">
                        <spring:message code="restaurantorders.pending" var="pending"/>
                        ${roleDetails.pendingOrderCount} ${fn:toLowerCase(pending)}
                    </span>
                    </h4>
                </a>
            </c:forEach>

            <c:if test="${fn:length(restaurants) == 0}">
                <div class="empty-results">
                    <h1><i class="bi bi-slash-circle"></i></h1>
                    <p>  <spring:message code="userorders.noorders"/></p>
                </div>
            </c:if>
        </main>
    </div>
    <jsp:include page="/WEB-INF/jsp/components/footer.jsp"/>
</body>
</html>
