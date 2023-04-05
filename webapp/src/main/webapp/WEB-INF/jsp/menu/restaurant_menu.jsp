<%@ page contentType="text/html;charset=UTF-8" language="java" %>


<html>
<head>
    <jsp:include page="/WEB-INF/jsp/components/head.jsp">
        <jsp:param name="title" value="${restaurant_name}"/>
    </jsp:include>
</head>
<body>
<jsp:include page="/WEB-INF/jsp/components/navbar.jsp"/>
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
    <div class="items" style="background-color: blue">
        ${restaurant_name}
    </div>
    <div class="cart" style="background-color: green">
        ${restaurant_name}
    </div>
</main>
</body>
</html>
