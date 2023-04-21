<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<html>
<head>
    <jsp:include page="/WEB-INF/jsp/components/head.jsp">
        <jsp:param name="title" value="Home"/>
    </jsp:include>
    <title>Register</title>
</head>
<body>
<h1>Register</h1>

<c:url var="registerUrl" value="/auth/register"/>
<form:form modelAttribute="registerForm" action="${registerUrl}" method="post">
    <div>
        <form:errors path="email" element="p" cssClass="error-message"/>
        <form:label path="email"> Email:
            <form:input path="email" type="text" placeholder="Email"/>
        </form:label>
    </div>
    <div>
        <form:errors path="password" element="p" cssClass="error-message"/>
        <form:label path="password"> Password:
            <form:input path="password" type="password" placeholder="Password"/>
        </form:label>
    </div>
    <div>
        <form:errors path="password" element="p" cssClass="error-message"/>
        <form:label path="password"> Repeat password:
            <form:input path="repeatPassword" type="password" placeholder="Repeat password"/>
        </form:label>
    </div>
    <div>
        <form:errors path="name" element="p" cssClass="error-message"/>
        <form:label path="name"> Name:
            <form:input path="name" type="text" placeholder="Name"/>
        </form:label>
    </div>
    <div>
        <input type="submit" value="Register!"/>
    </div>
</form:form>
</body>
</html>
