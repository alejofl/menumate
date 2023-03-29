<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt"%>

<html>
<head>
    <link href="/static/css/main.css" rel="stylesheet"/>
</head>
<body>
<h2>Hello <c:out value="${user}" escapeXml="true"/>!</h2>
</body>
</html>