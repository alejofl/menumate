<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<html>
<head>
    <spring:message code="moderator.reports.title" var="title" arguments="${restaurant.name}"/>
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
        <div class="page-title">
            <h1><c:out value="${title}"/></h1>
        </div>
        <div class="d-flex justify-content-center gap-2 mb-4">
            <a type="button" class="btn btn-secondary" href="<c:url value="/restaurants/${id}"/>"><spring:message code="moderator.reports.viewRestaurant"/></a>
            <button type="button" class="btn btn-danger" data-bs-toggle="modal" data-bs-target="#delete-restaurant-modal"><spring:message code="restaurant.menu.deleterestaurant"/></button>
        </div>
        <div class="restaurant-reviews">
            <c:forEach items="${reports}" var="report">
                <div class="card m-2">
                    <div class="card-header">
                        <div class="mt-2">
                            <b><c:out value="${report.reporter == null ? 'Usuario anonimo' : report.reporter.name}"/></b>
                        </div>
                        <%-- This is a workaround to make LocalDateTime formattable --%>
                        <fmt:parseDate value="${report.dateReported}" pattern="yyyy-MM-dd'T'HH:mm:ss" var="parsedDateReported" type="both"/>
                        <fmt:formatDate pattern="dd MMMM yyyy - HH:mm" value="${parsedDateReported}" var="dateReported"/>
                        <div class="mb-2">
                            <small class="text-muted">${dateReported}</small>
                        </div>
                    </div>
                    <div class="card-body">
                        <p><c:out value="${report.comment}"/></p>
                    </div>
                    <div class="card-footer d-flex justify-content-end">
                        <c:choose>
                            <c:when test="${report.dateHandled == null}">
                                <c:url value="/moderators/reports/${id}/handle" var="handleReportUrl"/>
                                <form:form cssClass="m-0" modelAttribute="handleReportForm" action="${handleReportUrl}" method="post">
                                    <input type="hidden" name="reportId" value="${report.reportId}">
                                    <input type="submit" class="btn btn-primary" value="<spring:message code="moderators.reports.markAsHandled"/>">
                                </form:form>
                            </c:when>
                            <c:otherwise>
                                <button class="btn btn-primary disabled"  role="button">
                                    <spring:message code="moderators.reports.alreadyMarkedAsHandled"/>
                                </button>
                            </c:otherwise>
                        </c:choose>
                    </div>
                </div>
            </c:forEach>
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
                    <c:url value="/moderators/reports/${id}" var="previousUrl">
                        <c:param name="page" value="${currentPage - 1}"/>
                        <c:param name="size" value="${currentSize}"/>
                    </c:url>
                    <a class="page-link ${currentPage == 1 ? "disabled" : ""}" href="${previousUrl}" aria-label="Previous">
                        <span aria-hidden="true">&laquo;</span>
                    </a>
                </li>
                <c:forEach begin="1" end="${pageCount}" var="pageNo">
                    <c:url value="/moderators/reports/${id}" var="pageUrl">
                        <c:param name="page" value="${pageNo}"/>
                        <c:param name="size" value="${currentSize}"/>
                    </c:url>
                    <li class="page-item ${pageNo == currentPage ? "active" : ""}"><a class="page-link" href="${pageUrl}">${pageNo}</a></li>
                </c:forEach>
                <li class="page-item">
                    <c:url value="/moderators/reports/${id}" var="nextUrl">
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

    <div class="modal fade" id="delete-restaurant-modal" tabindex="-1">
        <div class="modal-dialog modal-dialog-centered">
            <div class="modal-content">
                <div class="modal-body">
                    <h1 class="modal-title fs-5"><spring:message code="restaurant.menu.deleterestaurant.modal.title"/></h1>
                </div>
                <div class="modal-footer">
                    <button type="button" data-bs-dismiss="modal" class="btn btn-secondary"><spring:message code="editmenu.form.no"/></button>
                    <c:url value="/restaurants/${restaurant.restaurantId}/delete" var="deleteRestaurantUrl"/>
                    <form action="${deleteRestaurantUrl}" method="post">
                        <input type="submit" class="btn btn-danger" value="<spring:message code="editmenu.form.yes"/>">
                    </form>
                </div>
            </div>
        </div>
    </div>
</body>
</html>
