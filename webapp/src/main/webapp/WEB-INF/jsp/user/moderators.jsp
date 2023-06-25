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
</head>
<body>
    <div class="content">
        <jsp:include page="/WEB-INF/jsp/components/navbar.jsp"/>
        <div class="table-responsive w-75">
            <table class="table table-hover restaurant-orders-table">
                <thead class="table-light">
                <tr>
                    <th class="text-start" scope="col">Name</th>
                    <th class="text-center" scope="col">Email</th>
                    <th class="text-center" scope="col">Role</th>
                </tr>
                </thead>
                <tbody class="table-striped">
                <c:forEach items="${userRoles}" var="role">
                    <tr>
                        <td class="text-start"><c:out value="${role.user.name}"/></td>
                        <td class="text-center"><c:out value="${role.user.email}"/></td>
                        <td class="text-end">Moderator</td>
                    </tr>
                </c:forEach>
                </tbody>
            </table>
        </div>
        <c:if test="${userRoleCount == 0}">
            <div class="empty-results">
                <h1><i class="bi bi-slash-circle"></i></h1>
                <p>There are no moderators</p>
            </div>
        </c:if>
    </div>




    <!-- TODO: Turn this into a proper modal -->
    <!-- <div class="modal fade" id="add-address-modal" tabindex="-1"> -->
        <!-- <div class="modal-dialog modal-dialog-centered"> -->
            <div class="modal-content">
                <div class="modal-header">
                    <h1 class="modal-title fs-5">Add moderator</h1>
                    <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                </div>
                <c:url value="/moderators/add" var="addModeratorUrl"/>
                <form:form cssClass="mb-0" modelAttribute="addModeratorForm" action="${addModeratorUrl}" method="post">
                    <div class="modal-body">
                        <div class="mb-3">
                            <form:label path="email" cssClass="form-label"><spring:message code="profile.email"/></form:label>
                            <form:input path="email" type="text" cssClass="form-control"/>
                            <form:errors path="email" element="div" cssClass="form-error"/>
                        </div>
                        <div class="form-text">Adding this person as moderator gives them permission to delete anything on the entire site.</div>
                    </div>
                    <input type="hidden" name="userId" id="delete-address-form-user-id" value="${currentUser.userId}">
                    <div class="modal-footer">
                        <input type="submit" class="btn btn-primary" value="<spring:message code="editmenu.form.add"/>">
                    </div>
                </form:form>
            <!-- </div> -->
        <!-- </div> -->
    </div>

    <jsp:include page="/WEB-INF/jsp/components/footer.jsp"/>
</body>
</html>
