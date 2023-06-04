<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<html>
<head>
    <spring:message var="title" code="userorders.title"/>
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
        <div class="profile-container">
            <div class="my-info-container">
                <div class="card">
                    <div class="card-body">
                            <div class="profile-title">
                                <h1><spring:message code="profile.title"/></h1>
                            </div>
                            <div class="my-info-individual">
                                <label for="name" class="form-label"><spring:message code="profile.name"/></label>
                                <input class="form-control" type="text" value="${user.name}" aria-label="readonly input example" id="name" readonly>
                            </div>
                            <div class="my-info-individual">
                                <label for="email" class="form-label"><spring:message code="profile.email"/></label>
                                <input class="form-control" type="text" value="${user.email}" aria-label="readonly input example" id="email" readonly>
                            </div>
                            <div class="my-info-individual">
                                <label class="form-label"><spring:message code="profile.addresses"/></label>
                                <ul class="list-group list-group-flush">
                                    <c:forEach var="address" items="${addresses}">
                                        <li class="list-group-item d-flex align-items-center">
                                            <i class="bi bi-geo-alt me-3"></i>
                                            <p class="mb-0"><c:out value="${address.address}"/></p>
                                        </li>
                                    </c:forEach>
                                </ul>
                            </div>

                    </div>
                </div>
            </div>

            <div class="my-reviews-container">
                <div class="page-title">
                    <h1><spring:message code="profile.reviews"/></h1>
                </div>
                <c:forEach var="review" items="${reviews}">
                    <div class="my-info-individual">
                        <div class="card">
                            <div class="card-header">
                                <div class="my-review-card-header">
                                    <div class="my-review-card-header-info">
                                        <b><c:out value="${review.order.restaurant.name}"/></b>
                                        <fmt:parseDate value="${review.date}" pattern="yyyy-MM-dd'T'HH:mm:ss" var="parsedDateOrdered" type="both"/>
                                        <fmt:formatDate pattern="dd MMMM yyyy - HH:mm" value="${parsedDateOrdered}" var="reviewDate"/>
                                        <small class="text-muted">${reviewDate}</small>
                                    </div>
                                    <div class="d-flex gap-2 align-items-baseline mb-2 ">
                                        <div class="small-ratings">
                                            <c:forEach begin="1" end="${review.rating}">
                                                <i class="bi bi-star-fill rating-color"></i>
                                            </c:forEach>
                                            <c:forEach begin="1" end="${5 - review.rating}">
                                                <i class="bi bi-star-fill"></i>
                                            </c:forEach>
                                        </div>
                                    </div>
                                </div>
                            </div>
                            <div class="card-body">
                                <p><c:out value="${review.comment}"/></p>
                            </div>
                        </div>
                    </div>
                </c:forEach>

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
                            <c:url value="/user" var="previousUrl">
                                <c:param name="page" value="${currentPage - 1}"/>
                                <c:param name="size" value="${currentSize}"/>
                            </c:url>
                            <a class="page-link ${currentPage == 1 ? "disabled" : ""}" href="${previousUrl}" aria-label="Previous">
                                <span aria-hidden="true">&laquo;</span>
                            </a>
                        </li>
                        <c:forEach begin="1" end="${pageCount}" var="pageNo">
                            <c:url value="/user" var="pageUrl">
                                <c:param name="page" value="${pageNo}"/>
                                <c:param name="size" value="${currentSize}"/>
                            </c:url>
                            <li class="page-item ${pageNo == currentPage ? "active" : ""}"><a class="page-link" href="${pageUrl}">${pageNo}</a></li>
                        </c:forEach>
                        <li class="page-item">
                            <c:url value="/user" var="nextUrl">
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
        </div>
    </div>
    <jsp:include page="/WEB-INF/jsp/components/footer.jsp"/>
</body>
</html>
