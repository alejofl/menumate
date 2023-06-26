<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<html>
<head>
    <spring:message var="title" code="navbar.moderator"/>
    <jsp:include page="/WEB-INF/jsp/components/head.jsp">
        <jsp:param name="title" value="${title}"/>
    </jsp:include>
    <script src="<c:url value="/static/js/moderator_dashboard.js"/>"></script>
</head>
<body data-add-moderator-form-errors="${addModeratorFormErrors}">
    <div class="content">
        <jsp:include page="/WEB-INF/jsp/components/navbar.jsp"/>
        <c:if test="${error}">
            <jsp:include page="/WEB-INF/jsp/components/param_error.jsp"/>
        </c:if>
        <div class="page-title">
            <h1><spring:message code="navbar.moderator"/></h1>
        </div>
        <div class="d-flex justify-content-center mb-4">
            <button type="button" class="btn btn-secondary" data-bs-toggle="modal" data-bs-target="#moderators-modal"><spring:message code="moderatordashboard.modal.title"/></button>
        </div>
        <main class="restaurant-feed">
            <c:forEach var="restaurant" items="${restaurants}">
                <a class="clickable-object position-relative" href="<c:url value="/moderators/reports/${restaurant.key.restaurantId}"/>">
                    <div class="card restaurant-card">
                        <img
                                class="card-img restaurant-card-img"
                                style="--main_image: url(<c:url value="/images/${restaurant.key.portrait1Id}"/>); --hover_image: url(<c:url value="/images/${restaurant.key.portrait2Id}"/>)"
                        >
                        <div class="card-body">
                            <h5 class="card-title"><c:out value="${restaurant.key.name}"/></h5>
                            <p class="card-text"><c:out value="${restaurant.key.address}"/></p>
                        </div>
                    </div>
                    <h4>
                    <span class="position-absolute top-0 start-50 translate-middle badge rounded-pill bg-${restaurant.value == 0 ? "secondary" : "danger"}">
                        <spring:message code="moderatordashboard.reports.count" arguments="${restaurant.value}"/>
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


        <%--    Pagination  --%>
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
                    <c:url value="/moderators" var="previousUrl">
                        <c:param name="page" value="${currentPage - 1}"/>
                        <c:param name="size" value="${currentSize}"/>
                    </c:url>
                    <a class="page-link ${currentPage == 1 ? "disabled" : ""}" href="${previousUrl}" aria-label="Previous">
                        <span aria-hidden="true">&laquo;</span>
                    </a>
                </li>
                <c:forEach begin="1" end="${pageCount}" var="pageNo">
                    <c:url value="/moderators" var="pageUrl">
                        <c:param name="page" value="${pageNo}"/>
                        <c:param name="size" value="${currentSize}"/>
                    </c:url>
                    <li class="page-item ${pageNo == currentPage ? "active" : ""}"><a class="page-link" href="${pageUrl}">${pageNo}</a></li>
                </c:forEach>
                <li class="page-item">
                    <c:url value="/moderators" var="nextUrl">
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

    <div class="modal fade" id="moderators-modal" tabindex="-1">
        <div class="modal-dialog modal-dialog-centered modal-lg modal-dialog-scrollable">
            <div class="modal-content">
                <div class="modal-header">
                    <h1 class="modal-title fs-5"><spring:message code="moderatordashboard.modal.title"/></h1>
                    <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                </div>
                <div class="modal-body">
                    <div>
                        <h4><spring:message code="moderatordashboard.modal.add"/></h4>
                        <c:url value="/moderators/add" var="addModeratorUrl"/>
                        <form:form cssClass="m-0" modelAttribute="addModeratorForm" action="${addModeratorUrl}" method="post">
                            <div class="mb-3">
                                <form:label path="email" cssClass="form-label"><spring:message code="moderatordashboard.modal.add.email"/></form:label>
                                <form:input path="email" type="email" cssClass="form-control" id="add-employee-form-email"/>
                                <div class="form-text"><spring:message code="moderatordashboard.modal.warningmessage"/></div>
                                <form:errors path="email" element="div" cssClass="form-error"/>
                            </div>
                            <input type="submit" class="btn btn-primary" value="<spring:message code="moderatordashboard.modal.add"/>">
                        </form:form>
                    </div>
                    <div class="mt-4">
                        <h4><spring:message code="moderatordashboard.modal.moderators"/></h4>
                        <ul class="list-group list-group-flush">
                            <c:forEach var="userRole" items="${userRoles}">
                                <li class="list-group-item d-flex align-items-center">
                                    <i class="bi bi-person me-3"></i>
                                    <div class="d-flex justify-content-between align-items-center w-100">
                                        <p class="mb-0">
                                            <c:out value="${userRole.user.name}"/> &lt;<a href="mailto:<c:out value="${userRole.user.email}"/>"><c:out value="${userRole.user.email}"/></a>&gt;
                                        </p>
                                        <div class="d-flex align-items-center">
                                            <c:if test="${userRole.userId != currentUser.userId}">
                                                <a class="delete-moderator-button" type="button" data-bs-toggle="modal" data-bs-target="#delete-moderator-modal" data-user-id="${userRole.userId}"><i class="bi bi-trash-fill text-danger right-button"></i></a>
                                            </c:if>
                                        </div>
                                    </div>
                                </li>
                            </c:forEach>
                        </ul>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <div class="modal fade" id="delete-moderator-modal" tabindex="-1">
        <div class="modal-dialog modal-dialog-centered">
            <div class="modal-content">
                <div class="modal-body">
                    <h1 class="modal-title fs-5"><spring:message code="moderatordashboard.modal.delete"/></h1>
                </div>
                <div class="modal-footer">
                    <button type="button" data-bs-target="#moderators-modal" data-bs-toggle="modal" class="btn btn-secondary"><spring:message code="editmenu.form.no"/></button>
                    <c:url value="/moderators/delete" var="deleteModeratorUrl"/>
                    <form:form cssClass="m-0" modelAttribute="deleteModeratorForm" action="${deleteModeratorUrl}" method="post">
                        <input type="hidden" name="userId" id="delete-moderator-form-user-id">
                        <input type="submit" class="btn btn-danger" value="<spring:message code="editmenu.form.yes"/>">
                    </form:form>
                </div>
            </div>
        </div>
    </div>
</body>
</html>
