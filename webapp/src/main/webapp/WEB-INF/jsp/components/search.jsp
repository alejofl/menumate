<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<html>
<body>
<spring:message code="restaurants.search.placeholder" var="placeholder"/>
<c:url var="search" value="/restaurants"/>
<form:form modelAttribute="searchForm" action="${search}" method="get">
  <div class="input-group flex-nowrap">
    <span class="input-group-text search-input"><i class="bi bi-search"></i></span>
    <form:input type="text" path="search" cssClass="form-control search-input" placeholder="${placeholder}"/>
    <form:errors path="search" element="div" cssClass="form-error invalid-tooltip"/>
  </div>
  <form:input type="hidden" path="page" value="1"/>
  <input type="submit" class="btn btn-primary" value='<spring:message code="restaurants.search"/>'>
</form:form>
</body>
</html>
