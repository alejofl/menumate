<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<html>
<head>
    <spring:message code="restaurant.menu.editmenu" var="title"/>
    <jsp:include page="/WEB-INF/jsp/components/head.jsp">
        <jsp:param name="title" value="${title}"/>
    </jsp:include></head>
    <script src="<c:url value="/static/js/edit_menu.js"/>"></script>
</head>
<body
        data-add-category-errors="${addCategoryErrors}"
        data-add-product-errors="${addProductErrors}"
        data-category-id="${addProductErrors ? addProductForm.categoryId : ""}"
        data-add-employee-errors="${addEmployeeErrors}"
        data-edit-product-errors="${editProductErrors}"
        data-product-id="${editProductErrors ? editProductForm.productId : ""}"
        data-edit-category-errors="${editCategoryErrors}"
        data-edit-information-errors="${editRestaurantErrors}"
        data-create-promotion-errors="${createPromotionErrors}"
>
<div class="content">
    <jsp:include page="/WEB-INF/jsp/components/navbar.jsp"/>
    <div class="restaurant-header">
        <img src="<c:url value="/images/${restaurant.portrait1Id}"/>" class="menu-item-card-img" alt="<c:out value="${restaurant.name}"/>">
    </div>
    <div class="restaurant-information-container">
        <div class="restaurant-information">
            <img src="<c:url value="/images/${restaurant.logoId}"/>" alt="<c:out value="${restaurant.name}"/>" class="restaurant-logo">
            <div style="flex-grow: 1;">
                <h1><c:out value="${restaurant.name}"/></h1>
                <c:choose>
                    <c:when test="${not empty restaurant.description}">
                        <p class="mb-1"><c:out value="${restaurant.description}"/></p>
                    </c:when>
                    <c:otherwise>
                        <p class="mb-1"><i><spring:message code="restaurant.menu.nodescription"/></i></p>
                    </c:otherwise>
                </c:choose>
                <p><i class="bi bi-geo-alt"></i> <c:out value="${restaurant.address}"/></p>
                <div class="tags-container">
                    <c:forEach var="tag" items="${restaurant.tags}">
                        <c:url value="/restaurants" var="tagUrl">
                            <c:param name="tags" value="${tag.ordinal()}"/>
                        </c:url>
                        <a href="${tagUrl}" class="clickable-object">
                            <span class="badge rounded-pill text-bg-secondary"><spring:message code="restauranttags.${tag.messageCode}"/></span>
                        </a>
                    </c:forEach>
                </div>
            </div>
            <div class="d-flex flex-column gap-2">
                <a class="btn btn-primary" href="<c:url value="/restaurants/${restaurant.restaurantId}"/>" role="button"><spring:message code="editmenu.done"/></a>
                <button type="button" class="btn btn-secondary" data-bs-toggle="modal" data-bs-target="#edit-information-modal"><spring:message code="editmenu.editinformation"/></button>
                <button type="button" class="btn btn-secondary ${is_owner ? "" : "disabled"}" data-bs-toggle="modal" data-bs-target="#employees-modal" id="add-employees-button"><spring:message code="editmenu.editemployees"/></button>
            </div>
        </div>
    </div>
    <main class="edit-menu flex-column px-4 pb-4">
        <div class="d-flex flex-column">
            <c:if test="${not empty promotions}">
                <div class="card mb-4 bg-promotion">
                    <div class="card-body d-flex justify-content-between align-items-center">
                        <h3 class="mb-0 text-white"><spring:message code="editmenu.livingpromotions.title"/></h3>
                    </div>
                </div>
                <div class="items-container">
                    <c:forEach var="promotion" items="${promotions}">
                        <div class="card menu-item-card ${promotion.hasStarted() ? "" : "promotion-disabled"}">
                            <div class="menu-item-card-img-container">
                                <c:choose>
                                    <c:when test="${promotion.destination.imageId == null}">
                                        <img src="<c:url value="/static/pictures/image_placeholder.png"/>" class="img-fluid rounded-start menu-item-card-img" alt="<c:out value="${promotion.destination.name}"/>">
                                    </c:when>
                                    <c:otherwise>
                                        <img src="<c:url value="/images/${promotion.destination.imageId}"/>" class="img-fluid rounded-start menu-item-card-img" alt="<c:out value="${promotion.destination.name}"/>">
                                    </c:otherwise>
                                </c:choose>
                            </div>
                            <div class="d-flex flex-column space-between">
                                <div class="card-body menu-item-card-body">
                                    <div>
                                        <div class="d-flex justify-content-between">
                                            <p class="card-text"><c:out value="${promotion.destination.name}"/></p>
                                            <div class="d-flex gap-2 ps-2">
                                                <a class="stop-promotion-button" type="button" data-bs-toggle="modal" data-bs-target="#stop-promotion-modal" data-product-id="${promotion.destination.productId}"><i class="bi bi-x-circle text-danger"></i></a>
                                            </div>
                                        </div>
                                        <c:choose>
                                            <c:when test="${not empty promotion.destination.description}">
                                                <p class="card-text pb-1"><small class="text-body-secondary"><c:out value="${promotion.destination.description}"/></small></p>
                                            </c:when>
                                            <c:otherwise>
                                                <p class="card-text pb-1"><small class="text-body-secondary"><i><spring:message code="menuitem.product.nodescription"/></i></small></p>
                                            </c:otherwise>
                                        </c:choose>
                                    </div>
                                    <h5 class="card-title">$${promotion.destination.price} <span class="badge bg-promotion">-${promotion.discountPercentage}%</span></h5>
                                </div>
                                <div class="card-footer">
                                        <%-- This is a workaround to make LocalDateTime formattable --%>
                                    <fmt:parseDate value="${promotion.startDate}" pattern="yyyy-MM-dd'T'HH:mm" var="parsedStartDate" type="both"/>
                                    <fmt:formatDate pattern="yyyy-MM-dd HH:mm" value="${parsedStartDate}" var="startDate"/>
                                    <fmt:parseDate value="${promotion.endDate}" pattern="yyyy-MM-dd'T'HH:mm" var="parsedEndDate" type="both"/>
                                    <fmt:formatDate pattern="yyyy-MM-dd HH:mm" value="${parsedEndDate}" var="endDate"/>
                                    <small><span class="text-promotion"><spring:message code="editmenu.promotions.date.from"/></span> ${startDate}</small>
                                    <br>
                                    <small><span class="text-promotion"><spring:message code="editmenu.promotions.date.to"/></span> ${endDate}</small>
                                </div>
                            </div>
                        </div>
                    </c:forEach>
                </div>
            </c:if>
            <c:forEach items="${menu}" var="entry" varStatus="status">
                <div class="card mb-4">
                    <div class="card-body d-flex justify-content-between align-items-center">
                        <h3 class="mb-0"><c:out value="${entry.name}"/></h3>
                        <div class="d-flex gap-3">
                            <c:url value="/restaurants/${restaurant.restaurantId}/categories/move" var="editCategoryOrderUrl"/>
                            <c:if test="${!status.first}">
                                <form:form cssClass="mb-0" modelAttribute="editCategoryOrderForm" action="${editCategoryOrderUrl}" method="post">
                                    <form:input path="restaurantId" type="hidden" value="${restaurant.restaurantId}"/>
                                    <form:input path="currentOrder" type="hidden" value="${entry.orderNum}"/>
                                    <form:input path="newOrder" type="hidden" value="${menu[status.index - 1].orderNum}"/>
                                    <button type="submit" class="btn p-0 text-secondary"><i class="bi bi-arrow-up-circle-fill"></i></button>
                                </form:form>
                            </c:if>
                            <c:if test="${!status.last}">
                                <form:form cssClass="mb-0" modelAttribute="editCategoryOrderForm" action="${editCategoryOrderUrl}" method="post">
                                    <form:input path="restaurantId" type="hidden" value="${restaurant.restaurantId}"/>
                                    <form:input path="currentOrder" type="hidden" value="${entry.orderNum}"/>
                                    <form:input path="newOrder" type="hidden" value="${menu[status.index + 1].orderNum}"/>
                                    <button type="submit" class="btn p-0 text-secondary"><i class="bi bi-arrow-down-circle-fill"></i></button>
                                </form:form>
                            </c:if>
                            <a
                                    class="edit-category-button"
                                    type="button"
                                    data-bs-toggle="modal"
                                    data-bs-target="#edit-category-modal"
                                    data-category-id="${entry.categoryId}"
                                    data-category-name="<c:out value="${entry.name}"/>"
                            >
                                <i class="bi bi-pencil-fill"></i>
                            </a>
                            <a class="delete-category-button" type="button" data-bs-toggle="modal" data-bs-target="#delete-category-modal" data-category-id="${entry.categoryId}"><i class="bi bi-trash-fill text-danger"></i></a>
                        </div>
                    </div>
                </div>
                <div class="items-container">
                    <c:forEach var="product" items="${entry.products}">
                        <div class="card menu-item-card">
                            <div class="menu-item-card-img-container">
                                <c:choose>
                                    <c:when test="${product.imageId == null}">
                                        <img src="<c:url value="/static/pictures/image_placeholder.png"/>" class="img-fluid rounded-start menu-item-card-img" alt="<c:out value="${product.name}"/>">
                                    </c:when>
                                    <c:otherwise>
                                        <img src="<c:url value="/images/${product.imageId}"/>" class="img-fluid rounded-start menu-item-card-img" alt="<c:out value="${product.name}"/>">
                                    </c:otherwise>
                                </c:choose>
                            </div>
                            <div class="card-body menu-item-card-body">
                                <div>
                                    <div class="d-flex justify-content-between">
                                        <p class="card-text"><c:out value="${product.name}"/></p>
                                        <div class="d-flex gap-2 ps-2">
                                            <a class="create-promotion-button promotion-anchor" type="button" data-bs-toggle="modal" data-bs-target="#create-promotion-modal" data-product-id="${product.productId}"><i class="bi bi-percent"></i></a>
                                            <a
                                                class="edit-product-button"
                                                type="button"
                                                data-bs-toggle="modal"
                                                data-bs-target="#edit-item-modal"
                                                data-product-id="${product.productId}"
                                                data-category-id="${product.categoryId}"
                                                data-image-id="${product.imageId}"
                                                data-product-name="<c:out value="${product.name}"/>"
                                                data-description="<c:out value="${product.description}"/>"
                                                data-product-price="<c:out value="${product.price}"/>"
                                            >
                                                <i class="bi bi-pencil-fill"></i>
                                            </a>
                                            <a class="delete-product-button" type="button" data-bs-toggle="modal" data-bs-target="#delete-item-modal" data-product-id="${product.productId}"><i class="bi bi-trash-fill text-danger"></i></a>
                                        </div>
                                    </div>
                                    <c:choose>
                                        <c:when test="${not empty product.description}">
                                            <p class="card-text"><small class="text-body-secondary"><c:out value="${product.description}"/></small></p>
                                        </c:when>
                                        <c:otherwise>
                                            <p class="card-text"><small class="text-body-secondary"><i><spring:message code="menuitem.product.nodescription"/></i></small></p>
                                        </c:otherwise>
                                    </c:choose>
                                </div>
                                <h5 class="card-title">$${product.price}</h5>
                            </div>
                        </div>
                    </c:forEach>
                    <a class="add-product-button clickable-object" type="button" data-bs-toggle="modal" data-bs-target="#add-item-modal" data-category-id="${entry.categoryId}">
                        <div class="card add-item-card edit-menu-add">
                            <div class="card-body d-flex justify-content-center flex-column align-items-center gap-2">
                                <i class="bi bi-plus-circle-fill"></i>
                                <span><spring:message code="editmenu.addproduct.modal.title"/></span>
                            </div>
                        </div>
                    </a>
                </div>
            </c:forEach>
            <a type="button" class="clickable-object" id="add-category-button" data-bs-toggle="modal" data-bs-target="#add-category-modal">
                <div class="card my-4 edit-menu-add">
                    <div class="card-body d-flex justify-content-center align-items-center gap-2">
                        <i class="bi bi-plus-circle-fill"></i>
                        <span><spring:message code="editmenu.addcategory.modal.title"/></span>
                    </div>
                </div>
            </a>
        </div>
    </main>

    <div class="modal fade" id="create-promotion-modal" tabindex="-1">
        <div class="modal-dialog modal-dialog-centered">
            <div class="modal-content">
                <div class="modal-header">
                    <h1 class="modal-title fs-5"><spring:message code="editmenu.createpromotion.modal.title"/></h1>
                    <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                </div>
                <c:url value="/restaurants/${restaurant.restaurantId}/promotions/add" var="createPromotionFormUrl"/>
                <form:form cssClass="mb-0" modelAttribute="createPromotionForm" action="${createPromotionFormUrl}" method="post">
                    <div class="modal-body">
                        <div class="mb-3">
                            <form:label path="percentage" cssClass="form-label"><spring:message code="editmenu.createpromotion.form.percentage"/></form:label>
                            <div class="input-group">
                                <form:input path="percentage" type="number" min="1" max="99" cssClass="form-control" id="create-promotion-modal-percentage"/>
                                <span class="input-group-text">%</span>
                            </div>
                            <form:errors path="percentage" element="div" cssClass="form-error"/>
                        </div>
                        <div class="nav nav-pills nav-justified mb-3" role="tablist">
                            <button class="nav-link" id="promotion-instant-tab" data-bs-toggle="tab" data-bs-target="#promotion-instant" type="button" role="tab"><spring:message code="editmenu.createpromotion.form.instant"/></button>
                            <button class="nav-link" id="promotion-scheduled-tab" data-bs-toggle="tab" data-bs-target="#promotion-scheduled" type="button" role="tab"><spring:message code="editmenu.createpromotion.form.scheduled"/></button>
                        </div>
                        <div class="tab-content">
                            <div class="tab-pane fade" id="promotion-instant" role="tabpanel" tabindex="0">
                                <div class="mb-3">
                                    <label class="form-label"><spring:message code="editmenu.createpromotion.form.instant.title"/></label>
                                    <div class="d-flex gap-3">
                                        <div class="input-group">
                                            <form:input path="days" type="number" min="0" class="form-control promotion-instant-duration" id="promotion-instant-days" value="${createPromotionForm.days == null ? 0 : createPromotionForm.days}"/>
                                            <span class="input-group-text"><spring:message code="editmenu.createpromotion.form.instant.days"/></span>
                                        </div>
                                        <div class="input-group">
                                            <form:input path="hours" type="number" min="0" class="form-control promotion-instant-duration" id="promotion-instant-hours" value="${createPromotionForm.hours == null ? 0 : createPromotionForm.hours}"/>
                                            <span class="input-group-text"><spring:message code="editmenu.createpromotion.form.instant.hours"/></span>
                                        </div>
                                        <div class="input-group">
                                            <form:input path="minutes" type="number" min="0" class="form-control promotion-instant-duration" id="promotion-instant-minutes" value="${createPromotionForm.minutes == null ? 0 : createPromotionForm.minutes}"/>
                                            <span class="input-group-text"><spring:message code="editmenu.createpromotion.form.instant.minutes"/></span>
                                        </div>
                                    </div>
                                    <form:errors element="div" cssClass="form-error"/>
                                </div>
                            </div>
                            <div class="tab-pane fade" id="promotion-scheduled" role="tabpanel" tabindex="0">
                                <div class="mb-3">
                                    <form:label path="startDateTime" cssClass="form-label"><spring:message code="editmenu.createpromotion.form.startdate"/></form:label>
                                    <form:input path="startDateTime" type="datetime-local" cssClass="form-control" id="create-promotion-modal-start-datetime"/>
                                    <form:errors path="startDateTime" element="div" cssClass="form-error"/>
                                </div>
                                <div class="mb-3">
                                    <form:label path="endDateTime" cssClass="form-label"><spring:message code="editmenu.createpromotion.form.enddate"/></form:label>
                                    <form:input path="endDateTime" type="datetime-local" cssClass="form-control" id="create-promotion-modal-end-datetime"/>
                                    <form:errors path="endDateTime" element="div" cssClass="form-error"/>
                                    <form:errors element="div" cssClass="form-error"/>
                                </div>
                            </div>
                        </div>
                    </div>
                    <form:input path="type" type="hidden" id="create-promotion-modal-promotion-type"/>
                    <form:input path="sourceProductId" type="hidden" cssClass="form-control" id="create-promotion-modal-source-product-id"/>
                    <div class="modal-footer">
                        <input type="submit" id="add-promotion" class="btn btn-primary promotion-button" value="<spring:message code="editmenu.form.create"/>">
                    </div>
                </form:form>
            </div>
        </div>
    </div>

    <div class="modal fade" id="stop-promotion-modal" tabindex="-1">
        <div class="modal-dialog modal-dialog-centered">
            <div class="modal-content">
                <div class="modal-header">
                    <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                </div>
                <div class="modal-body">
                    <h1 class="modal-title fs-5"><spring:message code="editmenu.stoppromotion.modal.title"/></h1>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-secondary" data-bs-dismiss="modal"><spring:message code="editmenu.form.no"/></button>
                    <c:url value="/restaurants/${restaurant.restaurantId}/promotions/stop" var="stopPromotionFormUrl"/>
                    <form:form cssClass="m-0" modelAttribute="deleteProductForm" action="${stopPromotionFormUrl}" method="post">
                        <input type="hidden" name="productId" id="stop-promotion-form-product-id">
                        <input type="submit" class="btn btn-danger" value="<spring:message code="editmenu.form.yes"/>">
                    </form:form>
                </div>
            </div>
        </div>
    </div>

    <div class="modal fade" id="edit-information-modal" tabindex="-1">
        <div class="modal-dialog modal-dialog-centered">
            <div class="modal-content">
                <div class="modal-header">
                    <h1 class="modal-title fs-5"><spring:message code="editmenu.editinformation.modal.title"/></h1>
                    <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                </div>
                <c:url value="/restaurants/${restaurant.restaurantId}/edit" var="editInformationFormUrl"/>
                <form:form cssClass="mb-0" modelAttribute="editRestaurantForm" action="${editInformationFormUrl}" method="post" enctype="multipart/form-data">
                    <div class="modal-body">
                        <div>
                            <div class="mb-3">
                                <form:label path="name" cssClass="form-label"><spring:message code="editmenu.editinformation.form.name"/></form:label>
                                <form:input path="name" type="text" cssClass="form-control"/>
                                <form:errors path="name" element="div" cssClass="form-error"/>
                            </div>
                            <div class="mb-3">
                                <form:label path="address" cssClass="form-label"><spring:message code="editmenu.editinformation.form.address"/></form:label>
                                <form:input path="address" type="text" cssClass="form-control"/>
                                <form:errors path="address" element="div" cssClass="form-error"/>
                            </div>
                            <div class="mb-3">
                                <form:label path="specialty" cssClass="form-label"><spring:message code="editmenu.editinformation.form.specialty"/></form:label>
                                <form:select path="specialty" cssClass="form-select" multiple="false">
                                    <c:forEach var="spec" items="${specialties}">
                                        <form:option value="${spec.ordinal()}"><spring:message code="restaurantspecialties.${spec.messageCode}"/></form:option>
                                    </c:forEach>
                                </form:select>
                                <form:errors path="specialty" element="div" cssClass="form-error"/>
                            </div>
                            <div class="mb-3">
                                <form:label path="description" class="form-label"><spring:message code="editmenu.editinformation.form.description"/></form:label>
                                <form:textarea class="form-control" path="description" rows="3"/>
                                <form:errors path="description" element="div" cssClass="form-error"/>
                            </div>
                            <div class="mb-3">
                                <form:label path="maxTables" cssClass="form-label"><spring:message code="editmenu.editinformation.form.maxtables"/></form:label>
                                <form:input type="number" path="maxTables" class="form-control" id="create-restaurant-max-tables"/>
                                <form:errors path="maxTables" element="div" cssClass="form-error"/>
                            </div>
                            <div class="mb-3">
                                <form:label path="tags" cssClass="form-label"><spring:message code="editmenu.editinformation.form.tags"/></form:label>
                                <spring:message code="createrestaurant.form.tags.placeholder" var="tagsPlaceholder"/>
                                <form:select path="tags" cssClass="form-select" multiple="true" placeholder="${tagsPlaceholder}">
                                    <c:forEach var="tag" items="${tags}">
                                        <form:option value="${tag.ordinal()}"><spring:message code="restauranttags.${tag.messageCode}"/></form:option>
                                    </c:forEach>
                                </form:select>
                                <form:errors path="tags" element="div" cssClass="form-error"/>
                            </div>
                            <div class="mb-3">
                                <form:label path="logo" cssClass="form-label"><spring:message code="editmenu.addproduct.form.logo"/></form:label>
                                <form:input path="logo" type="file" cssClass="form-control" accept="image/*"/>
                                <form:errors path="logo" element="div" cssClass="form-error"/>
                            </div>
                            <div class="mb-3">
                                <form:label path="portrait1" cssClass="form-label"><spring:message code="editmenu.addproduct.form.portrait1"/></form:label>
                                <form:input path="portrait1" type="file" cssClass="form-control" accept="image/*"/>
                                <form:errors path="portrait1" element="div" cssClass="form-error"/>
                            </div>
                            <div class="mb-3">
                                <form:label path="portrait2" cssClass="form-label"><spring:message code="editmenu.addproduct.form.portrait2"/></form:label>
                                <form:input path="portrait2" type="file" cssClass="form-control" accept="image/*"/>
                                <form:errors path="portrait2" element="div" cssClass="form-error"/>
                            </div>
                        </div>
                    </div>
                    <input type="hidden" name="restaurantId" value="${restaurant.restaurantId}">
                    <div class="modal-footer">
                        <input type="submit" class="btn btn-primary" value="<spring:message code="editmenu.form.update"/>">
                    </div>
                </form:form>
            </div>
        </div>
    </div>

    <div class="modal fade" id="add-item-modal" tabindex="-1">
        <div class="modal-dialog modal-dialog-centered">
            <div class="modal-content">
                <div class="modal-header">
                    <h1 class="modal-title fs-5"><spring:message code="editmenu.addproduct.modal.title"/></h1>
                    <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                </div>
                <c:url value="/restaurants/${restaurant.restaurantId}/products/add" var="addProductFormUrl"/>
                <form:form cssClass="mb-0" modelAttribute="addProductForm" action="${addProductFormUrl}" method="post" id="add-product-form" enctype="multipart/form-data">
                    <div class="modal-body">
                        <div>
                            <div class="mb-3">
                                <form:label path="productName" cssClass="form-label"><spring:message code="editmenu.addproduct.form.name"/></form:label>
                                <form:input path="productName" type="text" cssClass="form-control" id="add-item-modal-name"/>
                                <form:errors path="productName" element="div" cssClass="form-error"/>
                            </div>
                            <div class="mb-3">
                                <form:label path="description" class="form-label"><spring:message code="editmenu.addproduct.form.description"/></form:label>
                                <form:textarea class="form-control" path="description" id="add-item-modal-description" rows="3"/>
                                <form:errors path="description" element="div" cssClass="form-error"/>
                            </div>
                            <div class="mb-3">
                                <form:label path="price" cssClass="form-label"><spring:message code="editmenu.addproduct.form.price"/></form:label>
                                <div class="input-group">
                                    <span class="input-group-text">$</span>
                                    <form:input path="price" step="0.01" min="0" type="number" cssClass="form-control" id="add-item-modal-price"/>
                                </div>
                                <form:errors path="price" element="div" cssClass="form-error"/>
                            </div>
                            <div class="mb-3">
                                <form:label path="image" cssClass="form-label"><spring:message code="editmenu.addproduct.form.image"/></form:label>
                                <form:input path="image" type="file" cssClass="form-control" id="add-item-modal-image" accept="image/*"/>
                                <form:errors path="image" element="div" cssClass="form-error"/>
                            </div>
                        </div>
                        <hr>
                        <div class="d-flex justify-content-center">
                            <div class="card menu-item-card">
                                <div class="menu-item-card-img-container" style="">
                                    <img src="<c:url value="/static/pictures/image_placeholder.png"/>" class="img-fluid rounded-start menu-item-card-img" id="add-item-modal-image-preview" data-default="<c:url value="/static/pictures/image_placeholder.png"/>">
                                </div>
                                <div class="card-body menu-item-card-body">
                                    <div>
                                        <p class="card-text" id="add-item-modal-name-preview" data-default="<spring:message code="editmenu.addproduct.preview.name"/>"><spring:message code="editmenu.addproduct.preview.name"/></p>
                                        <p class="card-text"><small class="text-body-secondary" id="add-item-modal-description-preview" data-default="<spring:message code="editmenu.addproduct.preview.description"/>"><spring:message code="editmenu.addproduct.preview.name"/></small></p>
                                    </div>
                                    <h5 class="card-title">$<span id="add-item-modal-price-preview" data-default="1.00">1.00</span></h5>
                                </div>
                            </div>
                        </div>
                    </div>
                    <input type="hidden" name="categoryId" id="add-product-form-category-id">
                    <div class="modal-footer">
                        <input type="submit" class="btn btn-primary" value="<spring:message code="editmenu.form.add"/>">
                    </div>
                </form:form>
            </div>
        </div>
    </div>

    <div class="modal fade" id="add-category-modal" tabindex="-1">
        <div class="modal-dialog modal-dialog-centered">
            <div class="modal-content">
                <div class="modal-header">
                    <h1 class="modal-title fs-5"><spring:message code="editmenu.addcategory.modal.title"/></h1>
                    <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                </div>
                <c:url value="/restaurants/${restaurant.restaurantId}/categories/add" var="addCategoryFormUrl"/>
                <form:form cssClass="mb-0" modelAttribute="addCategoryForm" action="${addCategoryFormUrl}" method="post" id="add-category-form">
                    <div class="modal-body">
                        <div class="mb-3">
                            <form:label path="name" cssClass="form-label"><spring:message code="editmenu.addcategory.form.name"/></form:label>
                            <form:input path="name" type="text" cssClass="form-control" id="add-category-modal-name"/>
                            <form:errors path="name" element="div" cssClass="form-error"/>
                        </div>
                    </div>
                    <div class="modal-footer">
                        <input type="submit" class="btn btn-primary" value="<spring:message code="editmenu.form.add"/>">
                    </div>
                    <input type="hidden" name="restaurantId" id="add-category-form-restaurant-id" value="${restaurant.restaurantId}">
                </form:form>
            </div>
        </div>
    </div>

    <div class="modal fade" id="edit-category-modal" tabindex="-1">
        <div class="modal-dialog modal-dialog-centered">
            <div class="modal-content">
                <div class="modal-header">
                    <h1 class="modal-title fs-5"><spring:message code="editmenu.editcategory.modal.title"/></h1>
                    <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                </div>
                <c:url value="/restaurants/${restaurant.restaurantId}/categories/edit" var="editCategoryFormUrl"/>
                <form:form cssClass="mb-0" modelAttribute="editCategoryForm" action="${editCategoryFormUrl}" method="post" id="edit-category-form">
                    <div class="modal-body">
                        <div class="mb-3">
                            <form:label path="name" cssClass="form-label"><spring:message code="editmenu.editcategory.form.name"/></form:label>
                            <form:input path="name" type="text" cssClass="form-control" id="edit-category-modal-name"/>
                            <form:errors path="name" element="div" cssClass="form-error"/>
                        </div>
                    </div>
                    <form:input path="categoryId" type="hidden" cssClass="form-control" id="edit-category-modal-category"/>
                    <div class="modal-footer">
                        <input type="submit" class="btn btn-primary" value="<spring:message code="editmenu.form.update"/>">
                    </div>
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
                    <h1 class="modal-title fs-5"><spring:message code="editmenu.deletecategory.modal.title"/></h1>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-secondary" data-bs-dismiss="modal"><spring:message code="editmenu.form.no"/></button>
                    <c:url value="/restaurants/${restaurant.restaurantId}/categories/delete" var="deleteCategoryFormUrl"/>
                    <form:form cssClass="m-0" modelAttribute="deleteCategoryForm" action="${deleteCategoryFormUrl}" method="post" id="delete-category-form">
                        <input type="hidden" name="categoryId" id="delete-category-form-category-id">
                        <input type="submit" class="btn btn-danger" value="<spring:message code="editmenu.form.yes"/>">
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
                    <h1 class="modal-title fs-5"><spring:message code="editmenu.deleteproduct.modal.title"/></h1>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-secondary" data-bs-dismiss="modal"><spring:message code="editmenu.form.no"/></button>
                    <c:url value="/restaurants/${restaurant.restaurantId}/products/delete" var="deleteProductFormUrl"/>
                    <form:form cssClass="m-0" modelAttribute="deleteProductForm" action="${deleteProductFormUrl}" method="post" id="delete-product-form">
                        <input type="hidden" name="productId" id="delete-product-form-product-id">
                        <input type="submit" class="btn btn-danger" value="<spring:message code="editmenu.form.yes"/>">
                    </form:form>
                </div>
            </div>
        </div>
    </div>

    <div class="modal fade" id="edit-item-modal" tabindex="-1">
        <div class="modal-dialog modal-dialog-centered">
            <div class="modal-content">
                <div class="modal-header">
                    <h1 class="modal-title fs-5"><spring:message code="editmenu.editproduct.modal.title"/></h1>
                    <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                </div>
                <c:url value="/restaurants/${restaurant.restaurantId}/products/edit" var="editProductFormUrl"/>
                <form:form cssClass="mb-0" modelAttribute="editProductForm" action="${editProductFormUrl}" method="post" id="edit-product-form" enctype="multipart/form-data">
                    <div class="modal-body">
                        <div>
                            <div class="mb-3">
                                <form:label path="productName" cssClass="form-label"><spring:message code="editmenu.addproduct.form.name"/></form:label>
                                <form:input path="productName" type="text" cssClass="form-control" id="edit-item-modal-name" value="${editProductForm.productName}"/>
                                <form:errors path="productName" element="div" cssClass="form-error"/>
                            </div>
                            <div class="mb-3">
                                <form:label path="description" class="form-label"><spring:message code="editmenu.addproduct.form.description"/></form:label>
                                <form:textarea class="form-control" path="description" id="edit-item-modal-description" rows="3" value="${editProductForm.description}"/>
                                <form:errors path="description" element="div" cssClass="form-error"/>
                            </div>
                            <div class="mb-3">
                                <form:label path="price" cssClass="form-label"><spring:message code="editmenu.addproduct.form.price"/></form:label>
                                <div class="input-group">
                                    <span class="input-group-text">$</span>
                                    <form:input path="price" step="0.01" min="0" type="number" cssClass="form-control" id="edit-item-modal-price" value="${editProductForm.price}"/>
                                </div>
                                <form:errors path="price" element="div" cssClass="form-error"/>
                            </div>
                            <div class="mb-3">
                                <form:label path="categoryId" cssClass="form-label"><spring:message code="editmenu.editproduct.form.category"/></form:label>
                                <form:select path="categoryId" cssClass="form-select" multiple="false" id="edit-item-modal-category">
                                    <c:forEach var="category" items="${menu}">
                                        <option value="${category.categoryId}" ${category.categoryId == editProductForm.categoryId ? "selected" : ""}><c:out value="${category.name}"/></option>
                                    </c:forEach>
                                </form:select>
                                <form:errors path="categoryId" element="div" cssClass="form-error"/>
                            </div>
                            <div class="mb-3">
                                <form:label path="image" cssClass="form-label"><spring:message code="editmenu.editproduct.form.image"/></form:label>
                                <form:input path="image" type="file" cssClass="form-control" id="edit-item-modal-image" accept="image/*"/>
                                <form:errors path="image" element="div" cssClass="form-error"/>
                            </div>
                        </div>
                        <hr>
                        <div class="d-flex justify-content-center">
                            <div class="card menu-item-card">
                                <div class="menu-item-card-img-container" style="">
                                    <img src="<c:url value="/static/pictures/image_placeholder.png"/>" class="img-fluid rounded-start menu-item-card-img" id="edit-item-modal-image-preview">
                                </div>
                                <div class="card-body menu-item-card-body">
                                    <div>
                                        <p class="card-text" id="edit-item-modal-name-preview"><spring:message code="editmenu.addproduct.preview.name"/></p>
                                        <p class="card-text"><small class="text-body-secondary" id="edit-item-modal-description-preview"><spring:message code="editmenu.addproduct.preview.name"/></small></p>
                                    </div>
                                    <h5 class="card-title">$<span id="edit-item-modal-price-preview">1.00</span></h5>
                                </div>
                            </div>
                        </div>
                    </div>
                    <form:input path="productId" type="hidden" id="edit-item-modal-product-id" value="${editProductForm.productId}"/>
                    <div class="modal-footer">
                        <input type="submit" class="btn btn-primary" value="<spring:message code="editmenu.form.update"/>">
                    </div>
                </form:form>
            </div>
        </div>
    </div>

    <div class="modal fade" id="employees-modal" tabindex="-1">
        <div class="modal-dialog modal-dialog-centered modal-lg modal-dialog-scrollable">
            <div class="modal-content">
                <div class="modal-header">
                    <h1 class="modal-title fs-5"><spring:message code="editmenu.editemployees"/></h1>
                    <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                </div>
                <div class="modal-body">
                    <div>
                        <h4><spring:message code="editmenu.empoyees.modal.addemployee"/></h4>
                        <c:url value="/restaurants/${restaurant.restaurantId}/employees/add" var="addEmployeeUrl"/>
                        <form:form cssClass="m-0" modelAttribute="addEmployeeForm" action="${addEmployeeUrl}" method="post" id="delete-product-form">
                            <div class="mb-3">
                                <form:label path="email" cssClass="form-label"><spring:message code="editmenu.empoyees.modal.employeeemail"/></form:label>
                                <form:input path="email" type="email" cssClass="form-control" id="add-employee-form-email"/>
                                <form:errors path="email" element="div" cssClass="form-error"/>
                                <form:errors element="div" cssClass="form-error"/>
                            </div>
                            <div class="mb-3">
                                <form:label path="role" cssClass="form-label"><spring:message code="editmenu.empoyees.modal.role"/></form:label>
                                <form:select path="role" cssClass="form-select" multiple="false" aria-labelledby="role-help-text">
                                    <c:forEach var="role" items="${roles}">
                                        <form:option value="${role.ordinal()}"><spring:message code="restaurantroles.${role.messageCode}"/></form:option>
                                    </c:forEach>
                                </form:select>
                                <div id="role-help-text" class="form-text">
                                    <spring:message code="editmenu.employees.modal.role.helptext"/>
                                </div>
                                <form:errors path="role" element="div" cssClass="form-error"/>
                            </div>
                            <form:input path="restaurantId" type="hidden" value="${restaurant.restaurantId}"/>
                            <input type="submit" class="btn btn-primary" value="<spring:message code="editmenu.empoyees.modal.addemployee"/>">
                        </form:form>
                    </div>
                    <div class="mt-4">
                        <h4><spring:message code="editmenu.empoyees.modal.restaurantemployees"/></h4>
                        <ul class="list-group list-group-flush">
                            <c:forEach var="employee" items="${employees}">
                                <li class="list-group-item d-flex align-items-center">
                                    <i class="bi bi-person me-3"></i>
                                    <div class="d-flex justify-content-between align-items-center w-100">
                                        <p class="mb-0">
                                            <c:out value="${employee.key.name}"/> &lt;<a href="mailto:<c:out value="${employee.key.email}"/>"><c:out value="${employee.key.email}"/></a>&gt;
                                        </p>
                                        <div class="d-flex align-items-center">
                                            <div id="edit-employee-${employee.key.userId}-edit-disabled">
                                                <p class="mb-0">
                                                    <spring:message code="restaurantroles.${employee.value.messageCode}"/>
                                                </p>
                                            </div>
                                            <c:if test="${employee.value.ordinal() != 0}">
                                                <div id="edit-employee-${employee.key.userId}-edit-enabled" style="display: none">
                                                    <c:url value="/restaurants/${restaurant.restaurantId}/employees/add" var="addEmployeeUrl"/>
                                                    <form:form cssClass="m-0" modelAttribute="addEmployeeForm" action="${addEmployeeUrl}" method="post">
                                                        <form:input path="email" type="hidden" value="${employee.key.email}"/>
                                                        <form:select path="role" cssClass="form-select" multiple="false" >
                                                            <c:forEach var="role" items="${roles}">
                                                                <option value="${role.ordinal()}" ${employee.value.ordinal() == role.ordinal() ? "selected" : ""}><spring:message code="restaurantroles.${role.messageCode}"/></option>
                                                            </c:forEach>
                                                        </form:select>
                                                        <form:errors path="role" element="div" cssClass="form-error"/>
                                                        <form:input path="restaurantId" type="hidden" value="${restaurant.restaurantId}"/>
                                                    </form:form>
                                                </div>
                                            </c:if>
                                            <c:if test="${employee.value.ordinal() != 0}">
                                                <div id="edit-employee-${employee.key.userId}-edit-enabled-button" style="display: none">
                                                    <a class="save-employee-button ms-3" type="button" data-user-id="${employee.key.userId}"><i class="bi bi-check-circle-fill text-success right-button"></i></a>
                                                </div>
                                                <div id="edit-employee-${employee.key.userId}-edit-disabled-button">
                                                    <a class="edit-employee-button ms-3" type="button" data-user-id="${employee.key.userId}"><i class="bi bi-pencil-fill right-button"></i></a>
                                                </div>
                                                <a class="delete-employee-button ms-3" type="button" data-bs-toggle="modal" data-bs-target="#delete-employee-modal" data-user-id="${employee.key.userId}"><i class="bi bi-trash-fill text-danger right-button"></i></a>
                                            </c:if>
                                        </div>
                                    </div>
                                </li>
                            </c:forEach>
                        </ul>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <div class="modal fade" id="delete-employee-modal" tabindex="-1">
        <div class="modal-dialog modal-dialog-centered">
            <div class="modal-content">
                <div class="modal-body">
                    <h1 class="modal-title fs-5"><spring:message code="editmenu.deleteemployee.modal.title"/></h1>
                </div>
                <div class="modal-footer">
                    <button type="button" data-bs-target="#employees-modal" data-bs-toggle="modal" class="btn btn-secondary"><spring:message code="editmenu.form.no"/></button>
                    <c:url value="/restaurants/${restaurant.restaurantId}/employees/delete" var="deleteEmployeeUrl"/>
                    <form:form cssClass="m-0" modelAttribute="deleteEmployeeForm" action="${deleteEmployeeUrl}" method="post" id="delete-employee-form">
                        <input type="hidden" name="userId" id="delete-employee-form-user-id">
                        <input type="submit" class="btn btn-danger" value="<spring:message code="editmenu.form.yes"/>">
                    </form:form>
                </div>
            </div>
        </div>
    </div>
</div>
<jsp:include page="/WEB-INF/jsp/components/footer.jsp"/>
</body>
</html>
