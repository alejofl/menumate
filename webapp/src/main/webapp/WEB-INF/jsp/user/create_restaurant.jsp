<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<html>
<head>
    <spring:message code="navbar.createrestaurant" var="title"/>
    <jsp:include page="/WEB-INF/jsp/components/head.jsp">
        <jsp:param name="title" value="${title}"/>
    </jsp:include></head>
    <script src="<c:url value="/static/js/create_restaurant.js"/>"></script>
</head>
<body>
<jsp:include page="/WEB-INF/jsp/components/navbar.jsp"/>
<main class="create-restaurant">
    <div class="create-restaurant-form">
        <h2><spring:message code="navbar.createrestaurant"/></h2>

        <c:url var="formAction" value="/restaurants/create"/>
        <form:form method="post" action="${formAction}" modelAttribute="createRestaurantForm" enctype="multipart/form-data">
            <div class="mb-3">
                <form:label path="name" cssClass="form-label"><spring:message code="createrestaurant.form.name"/></form:label>
                <form:input path="name" type="text" cssClass="form-control" id="create-restaurant-name"/>
                <form:errors path="name" element="div" cssClass="form-error"/>
            </div>
            <div class="mb-3">
                <form:label path="address" cssClass="form-label"><spring:message code="createrestaurant.form.address"/></form:label>
                <form:input path="address" type="text" cssClass="form-control" id="create-restaurant-address"/>
                <form:errors path="address" element="div" cssClass="form-error"/>
            </div>
            <div class="mb-3">
                <form:label path="specialty" cssClass="form-label"><spring:message code="createrestaurant.form.specialty"/></form:label>
                <form:select path="specialty" cssClass="form-select" multiple="false">
                    <c:forEach var="spec" items="${specialties}">
                        <form:option value="${spec.ordinal()}"><spring:message code="restaurantspecialties.${spec.messageCode}"/></form:option>
                    </c:forEach>
                </form:select>
                <form:errors path="specialty" element="div" cssClass="form-error"/>
            </div>
            <div class="mb-3">
                <form:label path="tags" cssClass="form-label"><spring:message code="createrestaurant.form.tags"/></form:label>
                <form:select path="tags" cssClass="form-select" multiple="true">
                    <c:forEach var="tag" items="${tags}">
                        <form:option value="${tag.ordinal()}"><spring:message code="restauranttags.${tag.messageCode}"/></form:option>
                    </c:forEach>
                </form:select>
                <form:errors path="tags" element="div" cssClass="form-error"/>
            </div>
            <div class="mb-3">
                <form:label path="description" cssClass="form-label"><spring:message code="createrestaurant.form.description"/></form:label>
                <form:textarea class="form-control" path="description" id="create-restaurant-description" rows="3"/>
                <form:errors path="description" element="div" cssClass="form-error"/>
            </div>
            <div class="mb-3">
                <form:label path="maxTables" cssClass="form-label"><spring:message code="createrestaurant.form.maxtables"/></form:label>
                <form:input type="number" path="maxTables" class="form-control" id="create-restaurant-max-tables"/>
                <form:errors path="maxTables" element="div" cssClass="form-error"/>
            </div>
            <div class="mb-3">
                <form:label path="logo" cssClass="form-label"><spring:message code="createrestaurant.form.logo"/></form:label>
                <form:input path="logo" type="file" cssClass="form-control" id="create-restaurant-logo" accept="image/*"/>
                <form:errors path="logo" element="div" cssClass="form-error"/>
            </div>
            <div class="mb-3">
                <form:label path="portrait1" cssClass="form-label"><spring:message code="createrestaurant.form.portrait1"/></form:label>
                <form:input path="portrait1" type="file" cssClass="form-control" id="create-restaurant-portrait1" accept="image/*"/>
                <form:errors path="portrait1" element="div" cssClass="form-error"/>
            </div>
            <div class="mb-3">
                <form:label path="portrait2" cssClass="form-label"><spring:message code="createrestaurant.form.portrait2"/></form:label>
                <form:input path="portrait2" type="file" cssClass="form-control" id="create-restaurant-portrait2" accept="image/*"/>
                <form:errors path="portrait2" element="div" cssClass="form-error"/>
            </div>
            <div class="mt-4">
                <form:errors element="div" cssClass="form-error"/>
                <input type="submit" class="btn btn-primary" value="<spring:message code="navbar.createrestaurant"/>">
            </div>
        </form:form>
    </div>
    <div class="create-restaurant-preview">
        <h2 class=""><spring:message code="createrestaurant.preview.title"/></h2>
        <div class="card restaurant-card">
            <img
                    class="card-img restaurant-card-img"
                    style="--main_image: url(<c:url value="/static/pictures/image_placeholder.png"/>); --hover_image: url(<c:url value="/static/pictures/image_placeholder.png"/>)"
            >
            <div class="card-body">
                <h5 class="card-title"><spring:message code="createrestaurant.preview.name"/></h5>
                <p class="card-text"><spring:message code="createrestaurant.preview.address"/></p>
            </div>
        </div>
    </div>
</main>
</body>
</html>
