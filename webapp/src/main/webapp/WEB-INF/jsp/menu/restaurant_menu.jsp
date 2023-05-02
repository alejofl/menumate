<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix = "fn" uri = "http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<html>
<head>
    <spring:message code="restaurant.menu.comments" var="comments"/>
    <jsp:include page="/WEB-INF/jsp/components/head.jsp">
        <jsp:param name="title" value="${restaurant.name}"/>
    </jsp:include>
    <script src="<c:url value="/static/js/restaurant_menu.js"/>"></script>
</head>
<body data-form-error="${formError}">
<jsp:include page="/WEB-INF/jsp/components/navbar.jsp"/>
<div class="restaurant-header">
    <img src="<c:url value="/images/${restaurant.portraitId1}"/>" class="menu-item-card-img" alt="${restaurant.name}">
</div>
<div class="restaurant-information-container">
    <div class="restaurant-information">
        <img src="<c:url value="/images/${restaurant.logoId}"/>" alt="${restaurant.name}" class="restaurant-logo">
        <div style="flex-grow: 1;">
            <h1>${restaurant.name}</h1>
            <c:choose>
                <c:when test="${not empty restaurant.description}">
                    <p>${restaurant.description}</p>
                </c:when>
                <c:otherwise>
                    <p><i> <spring:message code="restaurant.menu.nodescription"/></i></p>
                </c:otherwise>
            </c:choose>
        </div>
        <c:if test="${owner}">
            <div class="d-flex flex-column gap-2">
                <a class="btn btn-secondary" href="<c:url value="/restaurants/${restaurant.restaurantId}/edit"/>" role="button"><spring:message code="restaurant.menu.editmenu"/></a>
                <a class="btn btn-secondary" href="<c:url value="/restaurants/${restaurant.restaurantId}/orders"/>" role="button"><spring:message code="restaurant.menu.seeorders"/></a>
            </div>
        </c:if>
    </div>
</div>
<main>
    <div class="categories restaurant-menu-sticky-card">
        <div class="card">
            <div class="card-header text-muted"> <spring:message code="restaurant.menu.categories"/></div>
            <div class="card-body">
                <div class="nav nav-pills small">
                    <c:forEach items="${menu}" var="entry">
                        <button class="category-item nav-link" data-category="${entry.key.categoryId}">${entry.key.name}</button>
                    </c:forEach>
                </div>
            </div>
        </div>
    </div>

    <div class="items">
        <div class="items-container">
            <c:forEach items="${menu}" var="entry">
                <div class="clearfix" id="category-${entry.key.categoryId}" style="text-align: center">
                    <h3>${entry.key.name}</h3>
                </div>
                <c:forEach var="product" items="${entry.value}">
                    <jsp:include page="/WEB-INF/jsp/components/menu_item_card.jsp">
                        <jsp:param name="product_imageId" value="${product.imageId}"/>
                        <jsp:param name="product_productId" value="${product.productId}"/>
                        <jsp:param name="product_name" value="${product.name}"/>
                        <jsp:param name="product_description" value="${product.description}"/>
                        <jsp:param name="product_price" value="${product.price}"/>
                    </jsp:include>
                </c:forEach>
            </c:forEach>
        </div>
    </div>
    <div class="cart restaurant-menu-sticky-card">
        <div class="card">
            <div class="card-header text-muted"><spring:message code="restaurant.menu.myorder"/></div>
            <ul class="list-group list-group-flush" id="cart-container">
            </ul>
            <div class="card-body">
                <button class="btn btn-primary" id="place-order-button" type="button" data-bs-toggle="modal" data-bs-target="#checkout" disabled><spring:message code="restaurant.menu.ordernow"/></button>
            </div>
        </div>
    </div>
</main>

<!-- Add Item To Cart Modal -->
<div class="modal fade" id="add-item-to-cart" tabindex="-1">
    <div class="modal-dialog modal-dialog-centered">
        <div class="modal-content" id="add-item-to-cart-header">
            <div class="modal-header">
                <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
            </div>
            <div class="modal-body">
                <h4 id="add-item-to-cart-title"></h4>
                <p id="add-item-to-cart-description"></p>
                <hr>
                <form>
                    <div class="input-group">
                        <button id="add-item-to-cart-minus" class="btn btn-secondary" type="button"><i class="bi bi-dash"></i></button>
                        <input id="add-item-to-cart-quantity" type="number" class="form-control" value="1" min="1">
                        <button id="add-item-to-cart-plus" class="btn btn-secondary" type="button"><i class="bi bi-plus"></i></button>
                    </div>
                    <div class="comment-container">
                        <input id="add-item-to-cart-comments" placeholder="${comments}" class="form-control">
                    </div>
                </form>
            </div>
            <div class="modal-footer">
                <button type="button" id="add-item-to-cart-add" class="btn btn-primary" data-bs-dismiss="modal"><spring:message code="restaurant.menu.addtocart"/></button>
            </div>
        </div>
    </div>
</div>

<!-- CheckOut Modal -->
<div class="modal fade" id="checkout" tabindex="-1">
    <div class="modal-dialog modal-dialog-centered">
        <div class="modal-content">
            <div class="modal-header">
                <h1 class="modal-title fs-5"><spring:message code="restaurant.menu.checkout"/></h1>
                <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
            </div>
            <c:url value="/restaurants/${restaurant.restaurantId}" var="checkout"/>
            <form:form modelAttribute="checkoutForm" action="${checkout}" method="post" id="checkout-form">
            <div class="modal-body">
                <c:choose>
                    <c:when test="${currentUser != null}">
                        <div class="mb-4">
                            <h5><spring:message code="restaurant.menu.form.user" arguments="${currentUser.name}"/></h5>
                            <form:input type="hidden" path="name" cssClass="form-control" id="checkout-name" value="${currentUser.name}"/>
                            <form:input type="hidden" path="email" cssClass="form-control" id="checkout-email" value="${currentUser.email}"/>
                        </div>
                    </c:when>
                    <c:otherwise>
                        <div class="mb-3">
                            <form:label path="name" cssClass="form-label"><spring:message code="restaurant.menu.form.name"/></form:label>
                            <form:input type="text" path="name" cssClass="form-control" id="checkout-name"/>
                            <form:errors path="name" element="div" cssClass="form-error"/>
                        </div>
                        <div class="mb-3">
                            <form:label path="email" cssClass="form-label"><spring:message code="restaurant.menu.form.email"/></form:label>
                            <form:input type="email" path="email" cssClass="form-control" id="checkout-email"/>
                            <form:errors path="email" element="div" cssClass="form-error"/>
                        </div>
                    </c:otherwise>
                </c:choose>
                <nav>
                    <div class="nav nav-pills nav-fill mb-3" role="tablist">
                        <button class="nav-link" id="checkout-dinein-tab" data-bs-toggle="tab" data-bs-target="#checkout-dinein" type="button" role="tab"><spring:message code="restaurant.menu.form.dinein"/></button>
                        <button class="nav-link" id="checkout-takeaway-tab" data-bs-toggle="tab" data-bs-target="#checkout-takeaway" type="button" role="tab"><spring:message code="restaurant.menu.form.takeaway"/></button>
                        <button class="nav-link" id="checkout-delivery-tab" data-bs-toggle="tab" data-bs-target="#checkout-delivery" type="button" role="tab"><spring:message code="restaurant.menu.form.delivery"/></button>
                    </div>
                </nav>
                <div class="tab-content">
                    <div class="tab-pane fade" id="checkout-dinein" role="tabpanel" tabindex="0">
                        <div class="mb-3">
                            <form:label path="tableNumber" cssClass="form-label"><spring:message code="restaurant.menu.form.tablenumber"/></form:label>
                            <form:input type="number" path="tableNumber" cssClass="form-control" id="checkout-table-number"/>
                            <form:errors path="tableNumber" element="div" cssClass="form-error"/>
                        </div>
                    </div>
                    <div class="tab-pane fade" id="checkout-takeaway" role="tabpanel" tabindex="0">
                        <p><spring:message code="restaurant.menu.form.takeaway.message"/></p>
                    </div>
                    <div class="tab-pane fade" id="checkout-delivery" role="tabpanel" tabindex="0">
                        <div class="mb-3">
                            <form:label path="address" cssClass="form-label"><spring:message code="restaurant.menu.form.address"/></form:label>
                            <form:input type="text" path="address" cssClass="form-control" id="checkout-address"/>
                            <form:errors path="address" element="div" cssClass="form-error"/>
                        </div>
                    </div>
                </div>
                <div id="checkout-cart-additional-info">
                    <form:input path="restaurantId" type="hidden" value="${restaurant.restaurantId}"/>
                    <form:input path="orderType" type="hidden" id="checkout-order-type"/>
                </div>
                <div id="checkout-cart-items" data-cart-size="${fn:length(checkoutForm.cart)}">
                    <c:forEach items="${checkoutForm.cart}" var="item" varStatus="loop">
                        <form:input path="cart[${loop.index}].productId" type="hidden" id="cart${loop.index}-productId"/>
                        <form:input path="cart[${loop.index}].quantity" type="hidden" id="cart${loop.index}-quantity"/>
                        <form:input path="cart[${loop.index}].comment" type="hidden" id="cart${loop.index}-comment"/>
                    </c:forEach>
                </div>
                <form:errors path="cart"/> <!-- Errors when none or too many items in cart -->
                <form:errors/> <!-- General errors -->
            </div>
            <div class="modal-footer">
                <input type="submit" class="btn btn-primary" id="checkout-button" data-button-text="<spring:message code="restaurant.menu.placeorder"/>" value=""/>
            </div>
            </form:form>
        </div>
    </div>
</div>

</body>
</html>
