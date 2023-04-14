<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>

<html>
<body>
<nav class="navbar navbar-expand-md bg-body-tertiary sticky-top" data-bs-theme="dark">
    <a class="navbar-brand" href="<c:url value="/"/>">
      <c:url var="logoUrl" value="/static/pictures/logo.png"/>
      <img src="${logoUrl}" alt="MenuMate" height="24">
    </a>
    <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarNav" aria-controls="navbarNav" aria-expanded="false" aria-label="Toggle navigation">
      <span class="navbar-toggler-icon"></span>
    </button>
    <div class="collapse navbar-collapse" id="navbarNav">
      <ul class="navbar-nav">
        <li class="nav-item">
          <c:url var="homeUrl" value="/"/>
          <a class="nav-link active" aria-current="page" href="${homeUrl}">Home</a>
        </li>
        <li class="nav-item">
          <a class="nav-link disabled" href="#">My Orders</a>
        </li>
        <li class="nav-item">
          <a class="nav-link disabled" href="#">My Profile</a>
        </li>
      </ul>
    </div>
</nav>
</body>
</html>
