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
                <button class="btn btn-primary" type="button">Order Now</button>
            </div>
        </div>
    </div>
</main>
</body>
</html>
