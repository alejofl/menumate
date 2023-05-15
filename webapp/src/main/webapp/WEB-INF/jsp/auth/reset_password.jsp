<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<html>
<head>
    <spring:message code="resetPassword" var="viewTitle"/>
    <spring:message code="send" var="send"/>
    <jsp:include page="/WEB-INF/jsp/components/head.jsp">
        <jsp:param name="title" value="${viewTitle}"/>
    </jsp:include>
</head>
<body>
<jsp:include page="/WEB-INF/jsp/components/navbar.jsp"/>
<c:url var="url" value="/auth/reset-password-form?token=${token}"/>
<main class="login-register-main">
    <div class="card mb-3 login-register-card" style="width: 25rem;">
        <div class="card-body">
            <h2 class="card-title mb-3">${viewTitle}</h2>
            <form:form modelAttribute="resetPasswordForm" action="${url}" method="post">
                <div class="mb-3">
                    <form:label path="password" cssClass="form-label"><spring:message code="resetPassword.newPassword"/></form:label>
                    <form:input type="password" cssClass="form-control" path="password"/>
                    <form:errors path="password" element="div" cssClass="form-error"/>
                </div>
                <div class="mb-3">
                    <form:label path="repeatPassword" cssClass="form-label"><spring:message code="resetPassword.confirmNewPassword"/></form:label>
                    <form:input type="password" cssClass="form-control" path="repeatPassword"/>
                    <form:errors path="repeatPassword" element="div" cssClass="form-error"/>
                    <form:errors element="div" cssClass="form-error"/>
                </div>
                <input type="submit" class="btn btn-primary" id="login-submit" value="${send}">
            </form:form>
        </div>
    </div>
</main>
<jsp:include page="/WEB-INF/jsp/components/footer.jsp"/>
</body>
</html>