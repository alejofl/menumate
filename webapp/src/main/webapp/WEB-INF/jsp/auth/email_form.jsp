<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<html>
<head>
  <c:if test="${actionType=='verify'}">
    <spring:message code="email.form.verify" var="viewTitle"/>
  </c:if>
  <c:if test="${actionType=='reset-password'}">
    <spring:message code="email.form.resetPassword" var="viewTitle"/>
  </c:if>
  <spring:message code="send" var="send"/>
  <jsp:include page="/WEB-INF/jsp/components/head.jsp">
    <jsp:param name="title" value="${viewTitle}"/>
  </jsp:include>
</head>
<body>
<jsp:include page="/WEB-INF/jsp/components/navbar.jsp"/>
<c:url var="url" value="${url}"/>
<main class="login-register-main">
  <div class="card mb-3 login-register-card" style="width: 25rem;">
    <div class="card-body">
      <h2 class="card-title mb-3">${viewTitle}</h2>
      <form:form modelAttribute="emailForm" action="${url}" method="post">
        <div class="mb-3">
          <form:label path="email" cssClass="form-label"><spring:message code="email"/></form:label>
          <form:input type="text" cssClass="form-control" path="email"/>
          <form:errors path="email" element="div" cssClass="form-error"/>
        </div>
        <input type="submit" class="btn btn-primary" id="login-submit" value="${send}">
      </form:form>
      <c:if test="${actionType=='verify'}">
        <p class="mt-3"><spring:message code="email.form.verify.alreadyverified"/> <a href="<c:url value="/auth/login"/>"><spring:message code="email.form.verify.login"/></a></p>
      </c:if>
    </div>
  </div>
</main>
</body>
</html>