<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt"%>

<html>
<head>
    <title>Register</title>
</head>
<body>
<h1>Register</h1>
<c:url var="registerUrl" value="/"/>
<form action="${registerUrl}" method="post">
    <div>
        <label> Username:
            <input type="text" name="username">
        </label>
    </div>
    <div>
        <label> Password:
            <input type="password" name="password">
        </label>
    </div>
    <div>
        <input type="submit" value="Lets go!"/>
    </div>

</form>
</body>
</html>
