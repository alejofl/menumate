<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<html>
<head>
    <spring:message var="title" code="profile.title"/>
    <jsp:include page="/WEB-INF/jsp/components/head.jsp">
        <jsp:param name="title" value="${title}"/>
    </jsp:include>
    <script src="<c:url value="/static/js/myprofile.js"/>"></script>
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
                    <div class="card-body p-4">
                        <h3 class="card-title mb-3"><spring:message code="profile.title"/></h3>
                        <div class="mb-2">
                            <label for="name" class="form-label"><spring:message code="profile.name"/></label>
                            <input class="form-control" type="text" value="${currentUser.name}" aria-label="readonly input example" id="name" readonly>
                        </div>
                        <div class="mb-3">
                            <label for="email" class="form-label"><spring:message code="profile.email"/></label>
                            <input class="form-control" type="text" value="${currentUser.email}" aria-label="readonly input example" id="email" readonly>
                        </div>
                        <hr>
                        <h4 class="mb-2"><spring:message code="profile.addresses"/></h4>
                        <ul class="list-group list-group-flush mb-2">
                            <c:forEach var="address" items="${currentUser.addresses}">
                                <li class="list-group-item d-flex align-items-center justify-content-between px-0 address-list">
                                    <div class="d-flex align-items-center ">
                                        <i class="bi bi-geo-alt"></i>
                                        <div>
                                            <c:if test="${address.name != null}">
                                                <small class="text-muted"><c:out value="${address.name}"/></small>
                                            </c:if>
                                            <p class="mb-0">
                                                <c:out value="${address.address}"/>
                                            </p>
                                        </div>
                                    </div>
                                    <div class="d-flex gap-3">
                                        <c:if test="${address.name == null}">
                                            <a class="add-address-modal-button" type="button" data-bs-toggle="modal" data-bs-target="#add-address-modal" data-address="<c:out value="${address.address}"/>"><i class="bi bi-save-fill text-success right-button"></i></a>
                                        </c:if>
                                        <a class="delete-address-modal-button" type="button" data-bs-toggle="modal" data-bs-target="#delete-address-modal" data-address="<c:out value="${address.address}"/>"><i class="bi bi-trash-fill text-danger right-button"></i></a>
                                    </div>
                                </li>
                            </c:forEach>
                        </ul>
                        <div class="d-flex">
                            <button class="btn btn-primary flex-grow-1" id="add-address-button" type="button" data-bs-toggle="modal" data-bs-target="#add-address-modal"><spring:message code="profile.addaddress.modal.title"/></button>
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

    <div class="modal fade" id="delete-address-modal" tabindex="-1">
        <div class="modal-dialog modal-dialog-centered">
            <div class="modal-content">
                <div class="modal-body">
                    <h1 class="modal-title fs-5"><spring:message code="profile.deleteaddress.modal.title"/></h1>
                </div>
                <div class="modal-footer">
                    <button type="button" data-bs-dismiss="modal" aria-label="Close" class="btn btn-secondary"><spring:message code="editmenu.form.no"/></button>
                    <c:url value="/user/addresses/delete" var="deleteAddressUrl"/>
                    <form:form cssClass="m-0" modelAttribute="deleteAddressForm" action="${deleteAddressUrl}" method="post" id="delete-address-form">
                        <input type="hidden" name="userId" id="delete-address-form-user-id" value="${currentUser.userId}">
                        <input type="hidden" name="address" id="delete-address-form-address">
                        <input type="submit" class="btn btn-danger" value="<spring:message code="editmenu.form.yes"/>">
                    </form:form>
                </div>
            </div>
        </div>
    </div>

    <div class="modal fade" id="add-address-modal" tabindex="-1">
        <div class="modal-dialog modal-dialog-centered">
            <div class="modal-content">
                <div class="modal-header">
                    <h1 class="modal-title fs-5"><spring:message code="profile.addaddress.modal.title"/></h1>
                    <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                </div>
                <c:url value="/user/addresses/add" var="addAddressUrl"/>
                <form:form cssClass="mb-0" modelAttribute="addAddressForm" action="${addAddressUrl}" method="post">
                    <div class="modal-body">
                        <div class="mb-3">
                            <form:label path="name" cssClass="form-label"><spring:message code="profile.addaddress.form.name"/></form:label>
                            <form:input path="name" type="text" cssClass="form-control" id="add-address-form-name"/>
                            <form:errors path="name" element="div" cssClass="form-error"/>
                        </div>
                        <div class="mb-3">
                            <form:label path="address" cssClass="form-label"><spring:message code="profile.addaddress.form.address"/></form:label>
                            <form:input path="address" type="text" cssClass="form-control" id="add-address-form-address"/>
                            <form:errors path="address" element="div" cssClass="form-error"/>
                        </div>
                        <div class="form-text"><spring:message code="profile.addaddress.form.disclaimer"/></div>
                    </div>
                    <input type="hidden" name="userId" id="delete-address-form-user-id" value="${currentUser.userId}">
                    <div class="modal-footer">
                        <input type="submit" class="btn btn-primary" value="<spring:message code="editmenu.form.add"/>">
                    </div>
                </form:form>
            </div>
        </div>
    </div>

    <jsp:include page="/WEB-INF/jsp/components/footer.jsp"/>
</body>
</html>
