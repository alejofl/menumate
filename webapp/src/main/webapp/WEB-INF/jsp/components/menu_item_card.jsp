<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<body>
  <a
    class="clickable-object menu-item-card-button"
    href=""
    data-bs-toggle="modal"
    data-bs-target="#add-item-to-cart"
    data-info-image="/static/pictures/milanga.jpg"
    data-info-title="Cheeseburger"
    data-info-description="refojgoirtjg"
    data-info-price="500">
    <div class="card menu-item-card">
      <div class="menu-item-card-img-container" style="">
        <img src="<c:url value="/static/pictures/milanga.jpg"/>" class="img-fluid rounded-start menu-item-card-img" alt="Milanga">
      </div>
      <div class="card-body menu-item-card-body">
        <div>
          <p class="card-text">${param.name}</p>
          <c:choose>
            <c:when test="${not empty param.description}">
              <p class="card-text"><small class="text-body-secondary">${param.description}</small></p>
            </c:when>
            <c:otherwise>
            <!--<p><i>Este producto no posee una descripción</i></p>-->
            </c:otherwise>
          </c:choose>
        </div>
        <h5 class="card-title">$${param.price}</h5>
      </div>
    </div>
  </a>
</body>
</html>
