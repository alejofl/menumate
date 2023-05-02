<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<html>
<head>
    <spring:message code="restaurant.menu.editmenu" var="title"/>
    <jsp:include page="/WEB-INF/jsp/components/head.jsp">
        <jsp:param name="title" value="${title}"/>
    </jsp:include></head>
    <script src="<c:url value="/static/js/edit_menu.js"/>"></script>
</head>
<body>
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
        <div class="d-flex flex-column gap-2">
            <a class="btn btn-primary" href="<c:url value="/restaurants/${restaurant.restaurantId}"/>" role="button">Done</a>
            <a class="btn btn-secondary disabled" href="<c:url value="/restaurants/${restaurant.restaurantId}/orders"/>" role="button"><spring:message code="restaurant.menu.seeorders"/></a>
        </div>
    </div>
</div>
<main class="edit-menu flex-column px-4 pb-4">
    <div class="d-flex flex-column">
        <c:forEach items="${menu}" var="entry">
            <div class="card my-4">
                <div class="card-body d-flex justify-content-between align-items-center">
                    <h3 class="mb-0">${entry.key.name}</h3>
                    <a class="delete-category-button" type="button" data-bs-toggle="modal" data-bs-target="#delete-category-modal" data-category-id="${entry.key.categoryId}"><i class="bi bi-trash-fill text-danger"></i></a>
                </div>
            </div>
            <div class="edit-menu-item-container mb-4">
                <c:forEach var="product" items="${entry.value}">
                    <div class="card menu-item-card">
                        <div class="menu-item-card-img-container">
                            <img src="<c:url value="/images/${product.imageId}"/>" class="img-fluid rounded-start menu-item-card-img" alt="${product.name}">
                        </div>
                        <div class="card-body menu-item-card-body">
                            <div>
                                <div class="d-flex justify-content-between">
                                    <p class="card-text">${product.name}</p>
                                    <a class="delete-product-button" type="button" data-bs-toggle="modal" data-bs-target="#delete-item-modal" data-product-id="${product.productId}"><i class="bi bi-trash-fill text-danger"></i></a>
                                </div>
                                <c:choose>
                                    <c:when test="${not empty product.description}">
                                        <p class="card-text"><small class="text-body-secondary">${product.description}</small></p>
                                    </c:when>
                                    <c:otherwise>
                                        <p><i><spring:message code="menuitem.product.nodescription"/></i></p>
                                    </c:otherwise>
                                </c:choose>
                            </div>
                            <h5 class="card-title">$${product.price}</h5>
                        </div>
                    </div>
                </c:forEach>
                <a class="add-product-button" type="button" class="clickable-object" data-bs-toggle="modal" data-bs-target="#add-item-modal" data-category-id="${entry.key.categoryId}">
                    <div class="card add-item-card edit-menu-add">
                        <div class="card-body d-flex justify-content-center align-items-center">
                            <i class="bi bi-plus-circle-fill"></i>
                        </div>
                    </div>
                </a>
            </div>
        </c:forEach>
        <a type="button" class="clickable-object" data-bs-toggle="modal" data-bs-target="#add-category-modal">
            <div class="card my-4 edit-menu-add">
                <div class="card-body d-flex justify-content-center align-items-center">
                    <i class="bi bi-plus-circle-fill"></i>
                </div>
            </div>
        </a>
    </div>
</main>

<div class="modal fade" id="add-item-modal" tabindex="-1">
    <div class="modal-dialog modal-dialog-centered">
        <div class="modal-content">
            <div class="modal-header">
                <h1 class="modal-title fs-5">Add Product</h1>
                <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
            </div>
            <c:url value="/restaurants/${restaurant.restaurantId}/edit/add_product" var="addProductFormUrl"/>
            <form:form cssClass="mb-0" modelAttribute="addProductForm" action="${addProductFormUrl}" method="post" id="add-product-form" enctype="multipart/form-data">
                <div class="modal-body">
                    <div class="mb-3">
                        <form:label path="productName" cssClass="form-label">Name</form:label>
                        <form:input path="productName" type="text" cssClass="form-control" id="add-item-modal-name"/>
                        <form:errors path="productName" element="div" cssClass="form-error"/>
                    </div>
                    <div class="mb-3">
                        <form:label path="description" class="form-label">Description</form:label>
                        <textarea class="form-control" name="description" id="add-item-modal-description" rows="3"></textarea>
                        <form:errors path="description" element="div" cssClass="form-error"/>
                    </div>
                    <div class="mb-3">
                        <form:label path="price" cssClass="form-label">Price</form:label>
                        <form:input path="price" type="number" cssClass="form-control" id="add-item-modal-price"/>
                        <form:errors path="price" element="div" cssClass="form-error"/>
                    </div>
                    <div class="mb-3">
                        <form:label path="image" cssClass="form-label">Product Image</form:label>
                        <form:input path="image" type="file" cssClass="form-control" id="create-restaurant-logo" accept="image/*"/>
                        <form:errors path="image" element="div" cssClass="form-error"/>
                    </div>
                </div>
                <input type="hidden" name="categoryId" id="add-product-form-category-id">
                <div class="modal-footer">
                    <input type="submit" class="btn btn-primary" value="Add">
                </div>
            </form:form>
        </div>
    </div>
</div>

<div class="modal fade" id="add-category-modal" tabindex="-1">
    <div class="modal-dialog modal-dialog-centered">
        <div class="modal-content">
            <div class="modal-header">
                <h1 class="modal-title fs-5">Add Category</h1>
                <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
            </div>
            <c:url value="/restaurants/${restaurant.restaurantId}/edit/add_category" var="addCategoryFormUrl"/>
            <form:form cssClass="mb-0" modelAttribute="addCategoryForm" action="${addCategoryFormUrl}" method="post" id="add-category-form">
                <div class="modal-body">
                    <div class="mb-3">
                        <form:label path="name" cssClass="form-label">Name</form:label>
                        <form:input path="name" type="text" cssClass="form-control" id="add-category-modal-name"/>
                        <form:errors path="name" element="div" cssClass="form-error"/>
                    </div>
                </div>
                <div class="modal-footer">
                    <input type="submit" class="btn btn-primary" value="Add">
                </div>
                <input type="hidden" name="order" value="${fn:length(menu) + 1}">
                <input type="hidden" name="restaurantId" id="add-category-form-restaurant-id" value="${restaurant.restaurantId}">
            </form:form>
        </div>
    </div>
</div>

<div class="modal fade" id="delete-category-modal" tabindex="-1">
    <div class="modal-dialog modal-dialog-centered">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
            </div>
            <div class="modal-body">
                <h1 class="modal-title fs-5">Are you sure you want to delete the category?</h1>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">No</button>
                <c:url value="/restaurants/${restaurant.restaurantId}/edit/delete_category" var="deleteCategoryFormUrl"/>
                <form:form cssClass="m-0" modelAttribute="deleteCategoryForm" action="${deleteCategoryFormUrl}" method="post" id="delete-category-form">
                    <input type="hidden" name="categoryId" id="delete-category-form-category-id">
                    <input type="submit" class="btn btn-danger" value="Yes">
                </form:form>
            </div>
        </div>
    </div>
</div>

<div class="modal fade" id="delete-item-modal" tabindex="-1">
    <div class="modal-dialog modal-dialog-centered">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
            </div>
            <div class="modal-body">
                <h1 class="modal-title fs-5">Are you sure you want to delete the product?</h1>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">No</button>
                <c:url value="/restaurants/${restaurant.restaurantId}/edit/delete_product" var="deleteProductFormUrl"/>
                <form:form cssClass="m-0" modelAttribute="deleteProductForm" action="${deleteProductFormUrl}" method="post" id="delete-product-form">
                    <input type="hidden" name="productId" id="delete-product-form-product-id">
                    <input type="submit" class="btn btn-danger" value="Yes">
                </form:form>
            </div>
        </div>
    </div>
</div>
</body>
</html>
