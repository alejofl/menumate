<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>

<html>
<head>
    <title>Register</title>
</head>
<body>
<h1>Register</h1>

<form action="<c:url value="/auth/register"/>" method="post">
    <div>
        <label> Email:
            <input type="text" name="email">
        </label>
    </div>
    <div>
        <label> Password:
            <input type="password" name="password">
        </label>
    </div>
    <div>
        <label> Name:
            <input type="text" name="name">
        </label>
    </div>
    <div>
        <input type="submit" value="Lets go!"/>
    </div>
</form>
</body>
</html>
