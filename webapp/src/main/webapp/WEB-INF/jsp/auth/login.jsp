<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<html>
<head>
    <jsp:include page="/WEB-INF/jsp/components/head.jsp">
        <jsp:param name="title" value="Login"/>
    </jsp:include>
</head>
<body>
<h1>Login</h1>

<form action="<c:url value="/auth/login"/>" method="post">
    <div>
        <label> Email:
            <input type="text" name="email" placeholder="Email">
        </label>
    </div>
    <div>
        <label> Password:
            <input type="password" name="password" placeholder="Password">
        </label>
    </div>
    <div>
        <label> Remember me?
            <input type="checkbox" name="rememberme">
        </label>
    </div>
    <div>
        <input type="submit" value="Login!"/>
    </div>
</form>
</body>
</html>
