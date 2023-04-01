<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt"%>

<html>
<jsp:include page="/WEB-INF/jsp/head.jsp">
    <jsp:param name="title" value="Home"/>
</jsp:include>
<body>
<jsp:include page="/WEB-INF/jsp/components/navbar.jsp"/>
<main style="display: flex;
    flex-direction: row;
    gap: 15px;
    align-items: start;
    align-content: start;
    flex-wrap: wrap;">
    <jsp:include page="/WEB-INF/jsp/components/restaurant_card.jsp">
        <jsp:param name="name" value="Atuel"/>
        <jsp:param name="address" value="Atuel 200"/>
        <jsp:param name="main_image" value="/static/pictures/milanga.jpg"/>
        <jsp:param name="hover_image" value="/static/pictures/milanga2.jpg"/>
    </jsp:include>
</main>
</body>
</html>