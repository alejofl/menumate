<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix = "fn" uri = "http://java.sun.com/jsp/jstl/functions" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<html>
<head>
    <jsp:include page="/WEB-INF/jsp/components/head.jsp">
        <jsp:param name="title" value="${restaurant_name}"/>
    </jsp:include>
    <script src="/static/js/restaurant_menu.js"></script>
</head>
<body>
<jsp:include page="/WEB-INF/jsp/components/navbar.jsp"/>
<div class="restaurant-header">
    <img src="/static/pictures/milanga.jpg" class="menu-item-card-img" alt="Milanga">
</div>
<div class="restaurant-information-container">
    <div class="restaurant-information">
        <img src="/static/pictures/milanga.jpg" alt="${restaurant_name}" class="restaurant-logo">
        <div>
            <h1>Atuel</h1>
            <p>Lorem ipsum dolor sit amet, consectetur adipisicing elit. Aut autem cumque ducimus eum expedita nulla, ratione repellat soluta vitae. Doloribus omnis possimus quia temporibus. Assumenda iure modi omnis quos repellat. </p>
        </div>
    </div>
</div>
<main>
    <div class="categories nav nav-pills small">
        <c:forEach items="${menu.keySet()}" var="category">
            <button class="category-item nav-link" data-category="${fn:replace(category, " ", "")}">${category}</button>
        </c:forEach>
    </div>
    <div class="items">
        <div class="items-container">
            <c:forEach items="${menu.entrySet()}" var="entry">
                <div class="clearfix" id="category-${fn:replace(entry.key, " ", "")}" style="text-align: center">
                    <h3>${entry.key}</h3>
                </div>
                <c:forEach var="item" items="${entry.value}">
                    <jsp:include page="/WEB-INF/jsp/components/menu_item_card.jsp"/>
                </c:forEach>
            </c:forEach>
        </div>
    </div>
    <div class="cart">
        <div class="card">
            <div class="card-header text-muted">My Order</div>
            <ul class="list-group list-group-flush" id="cart-container">
            </ul>
            <div class="card-body">
                <button class="btn btn-primary" id="place-order-button" type="button" data-bs-toggle="modal" data-bs-target="#checkout" disabled>Order Now</button>
            </div>
        </div>
    </div>
</main>

<!-- Add Item To Cart Modal -->
<div class="modal fade" id="add-item-to-cart" tabindex="-1">
    <div class="modal-dialog modal-dialog-centered">
        <div class="modal-content" id="add-item-to-cart-header">
            <div class="modal-header" style="--image: url(/static/pictures/milanga.jpg)">
                <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
            </div>
            <div class="modal-body">
                <h4 id="add-item-to-cart-title">Cheeseburger</h4>
                <p id="add-item-to-cart-description">Lorem ipsum dolor sit amet, consectetur adipisicing elit. Explicabo laudantium libero omnis pariatur quaerat qui quis sunt tempora totam! Distinctio dolorem error modi quia tenetur unde. Corporis eum odit officiis!</p>
                <hr>
                <form>
                    <div class="input-group">
                        <button id="add-item-to-cart-minus" class="btn btn-secondary" type="button"><i class="bi bi-dash"></i></button>
                        <input id="add-item-to-cart-quantity" type="number" class="form-control" value="1">
                        <button id="add-item-to-cart-plus" class="btn btn-secondary" type="button"><i class="bi bi-plus"></i></button>
                    </div>
                    <div class="comment-container">
                        <input id="add-item-to-cart-comments" placeholder="Comments" class="form-control">
                    </div>
                </form>
            </div>
            <div class="modal-footer">
                <button type="button" id="add-item-to-cart-add" class="btn btn-primary" data-bs-dismiss="modal">Add Item to Cart ($500)</button>
            </div>
        </div>
    </div>
</div>

<!-- Checkout Modal -->
<div class="modal fade" id="checkout" tabindex="-1">
    <div class="modal-dialog modal-dialog-centered">
        <div class="modal-content">
            <div class="modal-header">
                <h1 class="modal-title fs-5">Checkout</h1>
                <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
            </div>
            <div class="modal-body">
                <form>
                    <div class="mb-3">
                        <label for="checkout-name" class="form-label">Name</label>
                        <input type="text" class="form-control" id="checkout-name">
                    </div>
                    <div class="mb-3">
                        <label for="checkout-email" class="form-label">Email Address</label>
                        <input type="email" class="form-control" id="checkout-email">
                    </div>
                    <nav>
                        <div class="nav nav-pills nav-fill mb-3" role="tablist">
                            <button class="nav-link active" id="checkout-dinein-tab" data-bs-toggle="tab" data-bs-target="#checkout-dinein" type="button" role="tab">Dine-In</button>
                            <button class="nav-link" id="checkout-takeaway-tab" data-bs-toggle="tab" data-bs-target="#checkout-takeaway" type="button" role="tab">Take-Away</button>
                            <button class="nav-link" id="checkout-delivery-tab" data-bs-toggle="tab" data-bs-target="#checkout-delivery" type="button" role="tab">Delivery</button>
                        </div>
                    </nav>
                    <div class="tab-content">
                        <div class="tab-pane fade show active" id="checkout-dinein" role="tabpanel" tabindex="0">
                            <div class="mb-3">
                                <label for="checkout-table-number" class="form-label">Table Number</label>
                                <input type="number" class="form-control" id="checkout-table-number">
                            </div>
                        </div>
                        <div class="tab-pane fade" id="checkout-takeaway" role="tabpanel" tabindex="0">
                            <p>Your food will be ready for pickup on approximately <strong>20 minutes</strong>.</p>
                        </div>
                        <div class="tab-pane fade" id="checkout-delivery" role="tabpanel" tabindex="0">
                            <div class="mb-3">
                                <label for="checkout-address" class="form-label">Address</label>
                                <input type="text" class="form-control" id="checkout-address">
                            </div>
                        </div>
                    </div>
                </form>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-primary" id="checkout-button">Place Order ($5000)</button>
            </div>
        </div>
    </div>
</div>

</body>
</html>
