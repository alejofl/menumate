<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<html>
<head>
    <jsp:include page="/WEB-INF/jsp/components/head.jsp">
        <jsp:param name="title" value="Login"/>
    </jsp:include>
</head>
<body>
<jsp:include page="/WEB-INF/jsp/components/navbar.jsp"/>
<main class="login-register-main">
    <div class="card mb-3 login-register-card" style="width: 25rem;">
        <div class="card-body">
            <h2 class="card-title mb-3">Login</h2>
            <c:if test="${param.error != null}">
                <div class="alert alert-danger" role="alert">
                    The email and/or password provided are incorrect.
                </div>
            </c:if>
            <form action="<c:url value="/auth/login"/>" method="post">
                <div class="mb-3">
                    <label for="login-email" class="form-label">Email address</label>
                    <input type="email" class="form-control" name="email" id="login-email">
                </div>
                <div class="mb-3">
                    <label for="login-password" class="form-label">Password</label>
                    <input type="password" class="form-control" name="password" id="login-password">
                </div>
                <div class="mb-3 form-check">
                    <input type="checkbox" class="form-check-input" id="exampleCheck1">
                    <label class="form-check-label" for="exampleCheck1">Remember me</label>
                </div>
                <input type="submit" class="btn btn-primary" id="login-submit" value="Login">
            </form>
            <p class="mt-3">Not have an account? <a href="<c:url value="/auth/register"/>">Sign up!</a></p>
        </div>
    </div>
</main>
</body>
</html>
