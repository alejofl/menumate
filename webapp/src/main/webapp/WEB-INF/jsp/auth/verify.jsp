<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<html>
<head>
  <spring:message code="verify.send" var="send"/>
  <spring:message code="verify" var="verify"/>
  <jsp:include page="/WEB-INF/jsp/components/head.jsp">
    <jsp:param name="title" value="${verify}"/>
  </jsp:include>
</head>
<body>
<jsp:include page="/WEB-INF/jsp/components/navbar.jsp"/>
<c:url var="verifyUrl" value="/auth/verify"/>
<main class="login-register-main">
  <div class="card mb-3 login-register-card" style="width: 25rem;">
    <div class="card-body">
      <h2 class="card-title mb-3">${verify}</h2>
      <form:form modelAttribute="verifyForm" action="${verifyUrl}" method="post">
        <div class="mb-3">
          <form:label path="email" cssClass="form-label"><spring:message code="verify.email"/></form:label>
          <form:input type="text" cssClass="form-control" path="email"/>
          <form:errors path="email" element="div" cssClass="form-error"/>
        </div>
        <input type="submit" class="btn btn-primary" id="login-submit" value="${send}">
      </form:form>
      <p class="mt-3"><spring:message code="verify.alreadyverified"/> <a href="<c:url value="/auth/login"/>"><spring:message code="verify.login"/></a></p>
    </div>
  </div>
</main>
</body>
</html>