<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt"%>

<html>
<head>
    <link href="/static/css/main.css" rel="stylesheet"/>
</head>
<body>
<h2>Hello <c:out value="${user.username}" escapeXml="true"/>!</h2>
<h4>The user's id is <c:out value="${user.id}" escapeXml="true"/>!</h4>
</body>
</html>