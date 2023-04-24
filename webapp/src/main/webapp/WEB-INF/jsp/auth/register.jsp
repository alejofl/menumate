<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<html>
<head>
    <jsp:include page="/WEB-INF/jsp/components/head.jsp">
        <jsp:param name="title" value="Register"/>
    </jsp:include>
</head>
<body>
<jsp:include page="/WEB-INF/jsp/components/navbar.jsp"/>
<c:url var="registerUrl" value="/auth/register"/>
<main class="login-register-main">
    <div class="card mb-3 login-register-card" style="width: 25rem;">
        <div class="card-body">
            <h2 class="card-title mb-3">Register</h2>
            <form:form modelAttribute="registerForm" action="${registerUrl}" method="post">
                <div class="mb-3">
                    <form:label path="name" cssClass="form-label">Name</form:label>
                    <form:input type="text" cssClass="form-control" path="name"/>
                    <form:errors path="name" element="div" cssClass="form-error"/>
                </div>
                <div class="mb-3">
                    <form:label path="email" cssClass="form-label">Email address</form:label>
                    <form:input type="email" cssClass="form-control" path="email"/>
                    <form:errors path="email" element="div" cssClass="form-error"/>
                </div>
                <div class="mb-3">
                    <form:label path="password" cssClass="form-label">Password</form:label>
                    <form:input type="password" cssClass="form-control" path="password"/>
                    <form:errors path="password" element="div" cssClass="form-error"/>
                </div>
                <div class="mb-3">
                    <form:label path="repeatPassword" cssClass="form-label">Repeat password</form:label>
                    <form:input type="password" cssClass="form-control" path="repeatPassword"/>
                    <form:errors path="repeatPassword" element="div" cssClass="form-error"/>
                </div>
                <input type="submit" class="btn btn-primary" id="login-submit" value="Register">
            </form:form>
            <p class="mt-3">Already have an account? <a href="<c:url value="/auth/login"/>">Log in!</a></p>
        </div>
    </div>
</main>
</body>
</html>
