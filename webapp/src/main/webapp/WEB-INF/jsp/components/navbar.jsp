<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<html>
<body>
<nav class="navbar navbar-expand-md bg-body-tertiary sticky-top" data-bs-theme="dark">
    <a class="navbar-brand" href="<c:url value="/"/>">
        <c:url var="logoUrl" value="/static/pictures/logo.png"/>
        <img src="${logoUrl}" alt="MenuMate" height="24">
    </a>
    <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarNav" aria-controls="navbarNav" aria-expanded="false" aria-label="Toggle navigation">
        <span class="navbar-toggler-icon"></span>
    </button>
    <div class="collapse navbar-collapse justify-content-between" id="navbarNav">
        <ul class="navbar-nav">
            <li class="nav-item">
                <c:url var="restaurants" value="/restaurants"/>
                <a class="nav-link active" aria-current="page" href="${restaurants}"><spring:message code="navbar.explore"/></a>
            </li>
            <!--
            <li class="nav-item">
              <a class="nav-link disabled" href="#">My Orders</a>
            </li>
            <li class="nav-item">
              <a class="nav-link disabled" href="#">My Profile</a>
            </li>
            -->
        </ul>
        <c:choose>
            <c:when test="${currentUser != null}">
                <div class="text-color-white">
                    <div class="dropdown">
                        <button class="btn btn-secondary dropdown-toggle" type="button" data-bs-toggle="dropdown" aria-expanded="false">
                            <i class="bi bi-person-circle"></i> <c:out value="${currentUser.name}"/>
                        </button>
                        <ul class="dropdown-menu dropdown-menu-end">
                            <li><a class="dropdown-item" href="<c:url value="/user"/>"><spring:message code="navbar.myprofile"/></a></li>
                            <li><a class="dropdown-item" href="<c:url value="/user/orders"/>"><spring:message code="navbar.myorders"/></a></li>
                            <li><hr class="dropdown-divider"></li>
                            <li><a class="dropdown-item" href="<c:url value="/restaurants/create"/>"><spring:message code="navbar.createrestaurant"/></a></li>
                            <li><a class="dropdown-item" href="<c:url value="/user/restaurants"/>"><spring:message code="navbar.myrestaurants"/></a></li>
                            <li><hr class="dropdown-divider"></li>
                            <li><a class="dropdown-item" href="<c:url value="/auth/logout"/>"><spring:message code="navbar.logout"/></a></li>
                        </ul>
                    </div>
                </div>
            </c:when>
            <c:otherwise>
                <ul class="navbar-nav">
                    <li class="nav-item">
                        <a class="nav-link active" aria-current="page" href="<c:url value="/auth/login"/>"><spring:message code="login"/></a>
                    </li>
                    <li class="nav-item">
                        <a class="nav-link active" aria-current="page" href="<c:url value="/auth/register"/>"><spring:message code="signup"/></a>
                    </li>
                </ul>
            </c:otherwise>
        </c:choose>
    </div>
</nav>
</body>
</html>