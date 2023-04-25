<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<html>
<body>
  <a
    class="clickable-object menu-item-card-button"
    href=""
    data-bs-toggle="modal"
    data-bs-target="#add-item-to-cart"
    data-info-image="<c:url value="/images/${param.product_imageId}"/>"
    data-info-title="${param.product_name}"
    data-info-description="${param.product_description}"
    data-info-price="${param.product_price}"
    data-info-id="${param.product_productId}"
  >
    <div class="card menu-item-card">
      <div class="menu-item-card-img-container" style="">
        <img src="<c:url value="/images/${param.product_imageId}"/>" class="img-fluid rounded-start menu-item-card-img" alt="${param.product_name}">
      </div>
      <div class="card-body menu-item-card-body">
        <div>
          <p class="card-text">${param.product_name}</p>
          <c:choose>
            <c:when test="${not empty param.product_description}">
              <p class="card-text"><small class="text-body-secondary">${param.product_description}</small></p>
            </c:when>
            <c:otherwise>
              <p><i><spring:message code="menuitem.product.nodescription"/></i></p>
            </c:otherwise>
          </c:choose>
        </div>
        <h5 class="card-title">$${param.product_price}</h5>
      </div>
    </div>
  </a>
</body>
</html>
