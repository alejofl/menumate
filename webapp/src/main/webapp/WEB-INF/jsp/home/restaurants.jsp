<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<html>
<head>
    <spring:message code="restaurants.title" var="home"/>
    <jsp:include page="/WEB-INF/jsp/components/head.jsp">
        <jsp:param name="title" value="${home}"/>
    </jsp:include>
    <script src="<c:url value="/static/js/restaurants.js"/>"></script>
</head>
<body>
    <div class="content">
        <jsp:include page="/WEB-INF/jsp/components/navbar.jsp"/>
        <c:if test="${error}">
            <jsp:include page="/WEB-INF/jsp/components/param_error.jsp"/>
        </c:if>
        <spring:message code="restaurants.search.placeholder" var="searchPlaceholder"/>
        <spring:message code="restaurants.filter.specialties" var="specialtiesPlaceholder"/>
        <spring:message code="restaurants.filter.tags" var="tagsPlaceholder"/>
        <spring:message code="restaurants.filter.order_by" var="orderByPlaceholder"/>
        <c:url var="search" value="/restaurants"/>
        <form:form modelAttribute="searchForm" action="${search}" method="get">
            <div class="search-form-container">
                <div class="pb-2">
                    <div class="input-group flex-nowrap">
                        <span class="input-group-text search-input"><i class="bi bi-search"></i></span>
                        <form:input type="text" path="search" cssClass="form-control search-input" placeholder="${searchPlaceholder}"/>
                        <form:errors path="search" element="div" cssClass="form-error invalid-tooltip"/>
                    </div>
                </div>
                <div class="d-flex gap-2">
                    <div class="input-group flex-nowrap">
                        <span class="input-group-text search-input"><i class="bi bi-filter"></i></span>
                        <form:select path="specialties" cssClass="form-select search-input" multiple="true" placeholder="${specialtiesPlaceholder}" multiselect-max-items="1">
                            <c:forEach var="spec" items="${specialties}">
                                <form:option value="${spec.ordinal()}"><spring:message code="restaurantspecialties.${spec.messageCode}"/></form:option>
                            </c:forEach>
                        </form:select>
                        <form:errors path="specialties" element="div" cssClass="form-error invalid-tooltip"/>
                    </div>
                    <div class="input-group flex-nowrap">
                        <span class="input-group-text search-input"><i class="bi bi-tags"></i></span>
                        <form:select path="tags" cssClass="form-select search-input" multiple="true" placeholder="${tagsPlaceholder}" multiselect-max-items="1">
                            <c:forEach var="tag" items="${tags}">
                                <form:option value="${tag.ordinal()}"><spring:message code="restauranttags.${tag.messageCode}"/></form:option>
                            </c:forEach>
                        </form:select>
                        <form:errors path="tags" element="div" cssClass="form-error invalid-tooltip"/>
                    </div>
                    <div class="input-group flex-nowrap">
                        <span class="input-group-text search-input"><i class="bi bi-text-left"></i></span>
                        <form:select path="orderBy" cssClass="form-select search-input" multiple="false" placeholder="${orderByPlaceholder}">
                            <c:forEach var="order" items="${order_by}">
                                <form:option value="${order.ordinal()}"><spring:message code="restaurantorderby.${order.messageCode}"/></form:option>
                            </c:forEach>
                        </form:select>
                        <button class="btn btn-secondary" id="change-descending" type="button"></button>
                        <form:errors path="orderBy" element="div" cssClass="form-error invalid-tooltip"/>
                    </div>
                </div>
                <form:input path="descending" type="hidden" value="${searchForm.descending ? searchForm.descending : false}" id="descending-input"/>
                <form:input type="hidden" path="page" value="1"/>
            </div>
            <input type="submit" class="btn btn-primary" value='<spring:message code="restaurants.search"/>'>
        </form:form>
        <main class="restaurant-feed">
            <c:forEach items="${restaurants}" var="restaurantDetails">
                <c:url var="restaurantUrl" value="/restaurants/${restaurantDetails.restaurant.restaurantId}"/>
                <c:url var="mainImage" value="/images/${restaurantDetails.restaurant.portraitId1}"/>
                <c:url var="hoverImage" value="/images/${restaurantDetails.restaurant.portraitId2}"/>
                <c:set var="tags" value="${restaurantDetails.tags}" scope="request"/>
                <jsp:include page="/WEB-INF/jsp/components/restaurant_card.jsp">
                    <jsp:param name="name" value="${restaurantDetails.restaurant.name}"/>
                    <jsp:param name="address" value="${restaurantDetails.restaurant.address}"/>
                    <jsp:param name="rating" value="${restaurantDetails.averageRating}"/>
                    <jsp:param name="main_image" value="${mainImage}"/>
                    <jsp:param name="hover_image" value="${hoverImage}"/>
                    <jsp:param name="link" value="${restaurantUrl}"/>
                </jsp:include>
            </c:forEach>
            <c:if test="${fn:length(restaurants) == 0}">
                <div class="empty-results">
                    <h1><i class="bi bi-slash-circle"></i></h1>
                    <p>  <spring:message code="restaurants.search.noresult"/></p>
                </div>
            </c:if>
        </main>

        <c:choose>
            <c:when test="${empty searchForm.page}">
                <c:set var="currentPage" value="1"/>
            </c:when>
            <c:otherwise>
                <c:set var="currentPage" value="${searchForm.page}"/>
            </c:otherwise>
        </c:choose>
        <c:choose>
            <c:when test="${empty searchForm.size}">
                <c:set var="currentSize" value="12"/>
            </c:when>
            <c:otherwise>
                <c:set var="currentSize" value="${searchForm.size}"/>
            </c:otherwise>
        </c:choose>

        <nav class="d-flex justify-content-center">
            <ul class="pagination">
                <li class="page-item">
                    <c:url value="/restaurants" var="previousUrl">
                        <c:forEach var="par" items="${paramValues}">
                            <c:if test="${par.key != 'page'}">
                                <c:forEach var="parValue" items="${par.value}">
                                    <c:param name="${par.key}" value="${parValue}"/>
                                </c:forEach>
                            </c:if>
                        </c:forEach>
                        <c:param name="page" value="${currentPage - 1}"/>
                    </c:url>
                    <a class="page-link ${currentPage == 1 ? "disabled" : ""}" href="${previousUrl}" aria-label="Previous">
                        <span aria-hidden="true">&laquo;</span>
                    </a>
                </li>
                <c:forEach begin="1" end="${pageCount}" var="pageNo">
                    <c:url value="/restaurants" var="pageUrl">
                        <c:forEach var="par" items="${paramValues}">
                            <c:if test="${par.key != 'page'}">
                                <c:forEach var="parValue" items="${par.value}">
                                    <c:param name="${par.key}" value="${parValue}"/>
                                </c:forEach>
                            </c:if>
                        </c:forEach>
                        <c:param name="page" value="${pageNo}"/>
                    </c:url>
                    <li class="page-item ${pageNo == currentPage ? "active" : ""}"><a class="page-link" href="${pageUrl}">${pageNo}</a></li>
                </c:forEach>
                <li class="page-item">
                    <c:url value="/restaurants" var="nextUrl">
                        <c:forEach var="par" items="${paramValues}">
                            <c:if test="${par.key != 'page'}">
                                <c:forEach var="parValue" items="${par.value}">
                                    <c:param name="${par.key}" value="${parValue}"/>
                                </c:forEach>
                            </c:if>
                        </c:forEach>
                        <c:param name="page" value="${currentPage + 1}"/>
                    </c:url>
                    <a class="page-link ${(currentPage == pageCount || pageCount == 0) ? "disabled" : ""}" href="${nextUrl}" aria-label="Next">
                        <span aria-hidden="true">&raquo;</span>
                    </a>
                </li>
            </ul>
        </nav>
    </div>
    <jsp:include page="/WEB-INF/jsp/components/footer.jsp"/>
</body>
</html>
