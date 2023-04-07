<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<html>
<head>
    <jsp:include page="/WEB-INF/jsp/components/head.jsp">
        <jsp:param name="title" value="${restaurant_name}"/>
    </jsp:include>
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
        <button class="category-item nav-link active">Appetizers</button>
        <button class="category-item nav-link">Soups and Salads</button>
        <button class="category-item nav-link">Entrees (Main courses)</button>
        <button class="category-item nav-link">Sandwiches</button>
        <button class="category-item nav-link">Burgers</button>
        <button class="category-item nav-link">Pizza and Pasta</button>
        <button class="category-item nav-link">Seafood</button>
        <button class="category-item nav-link">Chicken dishes</button>
        <button class="category-item nav-link">Beef dishes</button>
        <button class="category-item nav-link">Pork dishes</button>
        <button class="category-item nav-link">Vegetarian and Vegan dishes</button>
        <button class="category-item nav-link">Side dishes</button>
        <button class="category-item nav-link">Desserts</button>
        <button class="category-item nav-link">Beverages</button>
        <button class="category-item nav-link">Alcoholic Beverages</button>
        <button class="category-item nav-link">Breakfast/Brunch</button>
        <button class="category-item nav-link">Kids Menu</button>
    </div>
    <div class="items">
        <c:forEach var = "i" begin = "1" end = "32">
            <jsp:include page="/WEB-INF/jsp/components/menu_item_card.jsp"/>
        </c:forEach>
    </div>
    <div class="cart">
        <div class="card">
            <div class="card-header text-muted">My Order</div>
            <ul class="list-group list-group-flush">
                <c:forEach var = "i" begin = "1" end = "32">
                    <jsp:include page="/WEB-INF/jsp/components/cart_item.jsp"/>
                </c:forEach>
            </ul>
            <div class="card-body">
                <button class="btn btn-primary" type="button" data-bs-toggle="modal" data-bs-target="#checkout">Order Now</button>
            </div>
        </div>
    </div>
</main>

<!-- Add Item To Cart Modal -->
<div class="modal fade" id="add-item-to-cart" tabindex="-1">
    <div class="modal-dialog modal-dialog-centered">
        <div class="modal-content">
            <div class="modal-header" style="--image: url(/static/pictures/milanga.jpg)">
                <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
            </div>
            <div class="modal-body">
                <h4>Cheeseburger</h4>
                <p>Lorem ipsum dolor sit amet, consectetur adipisicing elit. Explicabo laudantium libero omnis pariatur quaerat qui quis sunt tempora totam! Distinctio dolorem error modi quia tenetur unde. Corporis eum odit officiis!</p>
                <hr>
                <form>
                    <div class="input-group">
                        <button class="btn btn-secondary" type="button"><i class="bi bi-dash"></i></button>
                        <input type="number" class="form-control" value="1">
                        <button class="btn btn-secondary" type="button"><i class="bi bi-plus"></i></button>
                    </div>
                    <div class="comment-container">
                        <input id="comment-input" placeholder="Comments" class="form-control">
                    </div>
                </form>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-primary">Add Item to Cart ($500)</button>
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
                <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
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
                <button type="button" class="btn btn-primary">Place Order ($5000)</button>
            </div>
        </div>
    </div>
</div>

</body>
</html>
