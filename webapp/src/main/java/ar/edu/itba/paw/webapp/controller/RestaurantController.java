package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.exception.RestaurantDetailsNotFoundException;
import ar.edu.itba.paw.exception.RestaurantNotFoundException;
import ar.edu.itba.paw.model.*;
import ar.edu.itba.paw.service.*;
import ar.edu.itba.paw.util.PaginatedResult;
import ar.edu.itba.paw.util.Pair;
import ar.edu.itba.paw.webapp.api.CustomMediaType;
import ar.edu.itba.paw.webapp.auth.AccessValidator;
import ar.edu.itba.paw.webapp.dto.*;
import ar.edu.itba.paw.webapp.form.*;
import ar.edu.itba.paw.webapp.utils.ControllerUtils;
import ar.edu.itba.paw.webapp.utils.UriUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.time.Duration;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Path(UriUtils.RESTAURANTS_URL)
@Component
public class RestaurantController {
    private final RestaurantService restaurantService;
    private final CategoryService categoryService;
    private final ProductService productService;
    private final RestaurantRoleService restaurantRoleService;
    private final UserService userService;
    private final ReportService reportService;
    private final AccessValidator accessValidator;

    @Context
    private UriInfo uriInfo;

    @Autowired
    public RestaurantController(final RestaurantService restaurantService, final CategoryService categoryService, final ProductService productService, final RestaurantRoleService restaurantRoleService, final AccessValidator accessValidator, final UserService userService, final ReportService reportService) {
        this.restaurantService = restaurantService;
        this.categoryService = categoryService;
        this.productService = productService;
        this.restaurantRoleService = restaurantRoleService;
        this.accessValidator = accessValidator;
        this.userService = userService;
        this.reportService =  reportService;
    }

    @GET
    @PreAuthorize("#filterForm.forEmployeeId == null or hasRole('MODERATOR') or @accessValidator.checkIsUser(#filterForm.forEmployeeId)")
    @Produces(CustomMediaType.APPLICATION_RESTAURANT_DETAILS)
    public Response getRestaurants(@Valid @BeanParam final FilterForm filterForm) {
        if (filterForm.getForEmployeeId() != null) {
            final PaginatedResult<RestaurantRoleDetails> pagedResult = restaurantRoleService.getByUser(
                    filterForm.getForEmployeeId(),
                    filterForm.getPageOrDefault(),
                    filterForm.getSizeOrDefault(ControllerUtils.DEFAULT_MYRESTAURANTS_PAGE_SIZE)
            );
            final List<RestaurantWithOrderCountDto> dtoList = RestaurantWithOrderCountDto.fromCollection(uriInfo, pagedResult.getResult());
            final Response.ResponseBuilder responseBuilder = Response.ok(new GenericEntity<List<RestaurantWithOrderCountDto>>(dtoList) {});
            return ControllerUtils.addPagingLinks(responseBuilder, pagedResult, uriInfo).build();
        }

        final PaginatedResult<RestaurantDetails> pagedResult = restaurantService.search(
                filterForm.getSearch(),
                filterForm.getPageOrDefault(),
                filterForm.getSizeOrDefault(ControllerUtils.DEFAULT_SEARCH_PAGE_SIZE),
                filterForm.getOrderByAsEnum(),
                filterForm.getDescendingOrDefault(),
                filterForm.getTagsAsEnum(),
                filterForm.getSpecialtiesAsEnum()
        );

        final List<RestaurantDetailsDto> dtoList = RestaurantDetailsDto.fromRestaurantDetailsCollection(uriInfo, pagedResult.getResult());
        final Response.ResponseBuilder responseBuilder = Response.ok(new GenericEntity<List<RestaurantDetailsDto>>(dtoList) {});
        return ControllerUtils.addPagingLinks(responseBuilder, pagedResult, uriInfo).build();
    }

    @GET
    @Path("/{restaurantId:\\d+}")
    @Produces(CustomMediaType.APPLICATION_RESTAURANT)
    public Response getRestaurantById(@PathParam("restaurantId") final long restaurantId, @Context Request request) {
        final Restaurant restaurant = restaurantService.getById(restaurantId).orElseThrow(RestaurantNotFoundException::new);
        return ControllerUtils.buildResponseUsingEtag(request, restaurant.hashCode(), () -> RestaurantDto.fromRestaurant(uriInfo, restaurant));
    }

    @GET
    @Path("/{restaurantId:\\d+}")
    @Produces(CustomMediaType.APPLICATION_RESTAURANT_DETAILS)
    public Response getRestaurantDetailsWithCompletionTime(@PathParam("restaurantId") final long restaurantId, @Context Request request) {
        final RestaurantDetails restaurantDetails = restaurantService.getRestaurantDetails(restaurantId).orElseThrow(RestaurantDetailsNotFoundException::new);
        final Duration dineInAverageTime = restaurantService.getAverageOrderCompletionTime(restaurantId, OrderType.DINE_IN).orElse(null);
        final Duration deliveryAverageTime = restaurantService.getAverageOrderCompletionTime(restaurantId, OrderType.DELIVERY).orElse(null);
        final Duration takeawayAverageTime = restaurantService.getAverageOrderCompletionTime(restaurantId, OrderType.TAKEAWAY).orElse(null);
        return ControllerUtils.buildResponseUsingEtag(
                request,
                Objects.hash(restaurantDetails.hashCode(), dineInAverageTime, deliveryAverageTime, takeawayAverageTime),
                () -> RestaurantDetailsDto.fromRestaurantDetailsWithCompletionTime(uriInfo, restaurantDetails, dineInAverageTime, deliveryAverageTime, takeawayAverageTime)
        );
    }

    @POST
    @Consumes(CustomMediaType.APPLICATION_RESTAURANT)
    public Response createRestaurant(@Valid @NotNull final RestaurantForm restaurantForm) {
        final User currentUser = ControllerUtils.getCurrentUserOrThrow(userService);
        final Restaurant restaurant = restaurantService.create(
                restaurantForm.getName(),
                currentUser.getEmail(),
                restaurantForm.getSpecialtyAsEnum(),
                currentUser.getUserId(),
                restaurantForm.getAddress(),
                restaurantForm.getDescription(),
                restaurantForm.getMaxTables(),
                null,
                null,
                null,
                true,
                restaurantForm.getTagsAsEnum()
        );

        return Response.created(UriUtils.getRestaurantUri(uriInfo, restaurant.getRestaurantId())).build();
    }

    @PUT
    @Path("/{restaurantId:\\d+}")
    public Response updateRestaurantById(
            @PathParam("restaurantId") final long restaurantId,
            @Valid @NotNull final RestaurantForm restaurantForm
    ) {
        restaurantService.update(
                restaurantId,
                restaurantForm.getName(),
                restaurantForm.getSpecialtyAsEnum(),
                restaurantForm.getAddress(),
                restaurantForm.getDescription(),
                restaurantForm.getTagsAsEnum()
        );

        return Response.noContent().build();
    }

    @DELETE
    @Path("/{restaurantId:\\d+}")
    public Response deleteRestaurantById(@PathParam("restaurantId") final long restaurantId) {
        restaurantService.delete(restaurantId);
        return Response.noContent().build();
    }

    @GET
    @Path("/{restaurantId:\\d+}/categories")
    @Produces(CustomMediaType.APPLICATION_RESTAURANT_CATEGORIES)
    public Response getRestaurantCategories(@PathParam("restaurantId") final long restaurantId) {
        final List<Category> categories = categoryService.getByRestaurantSortedByOrder(restaurantId);
        final List<CategoryDto> dtoList = CategoryDto.fromCategoryCollection(uriInfo, categories);
        return Response.ok(new GenericEntity<List<CategoryDto>>(dtoList) {}).build();
    }

    @GET
    @Path("/{restaurantId:\\d+}/categories/{categoryId:\\d+}")
    @Produces(CustomMediaType.APPLICATION_RESTAURANT_CATEGORIES)
    public Response getCategory(
            @PathParam("restaurantId") final long restaurantId,
            @PathParam("categoryId") final long categoryId,
            @Context Request request
    ) {
        final Category category = categoryService.getByIdChecked(restaurantId, categoryId, true);
        return ControllerUtils.buildResponseUsingEtag(request, category.hashCode(), () -> CategoryDto.fromCategory(uriInfo, category));
    }

    @POST
    @Path("/{restaurantId:\\d+}/categories")
    @Consumes(CustomMediaType.APPLICATION_RESTAURANT_CATEGORIES)
    public Response createCategory(
            @PathParam("restaurantId") final long restaurantId,
            @Valid @NotNull final CategoryForm categoryForm
    ) {
        final Category category = categoryService.create(restaurantId, categoryForm.getNameTrimmed());
        return Response.created(UriUtils.getCategoryUri(uriInfo, category)).build();
    }

    @PATCH
    @Path("/{restaurantId:\\d+}/categories/{categoryId:\\d+}")
    @Consumes(CustomMediaType.APPLICATION_RESTAURANT_CATEGORIES)
    public Response updateCategory(
            @PathParam("restaurantId") final long restaurantId,
            @PathParam("categoryId") final long categoryId,
            @Valid @NotNull final UpdateCategoryForm categoryForm
    ) {
        final Category category;
        String name = categoryForm.getNameTrimmed();
        if (name == null)
            category = categoryService.getByIdChecked(restaurantId, categoryId, false);
        else
            category = categoryService.updateName(restaurantId, categoryId, name);

        Integer orderNum = categoryForm.getOrderNum();
        if (orderNum != null)
            categoryService.setOrder(category, orderNum);

        return Response.noContent().build();
    }

    @DELETE
    @Path("/{restaurantId:\\d+}/categories/{categoryId:\\d+}")
    public Response deleteCategory(
            @PathParam("restaurantId") final long restaurantId,
            @PathParam("categoryId") final long categoryId
    ) {
        categoryService.delete(restaurantId, categoryId);
        return Response.noContent().build();
    }

    @GET
    @Path("/{restaurantId:\\d+}/categories/{categoryId:\\d+}/products")
    @Produces(CustomMediaType.APPLICATION_RESTAURANT_PRODUCTS)
    public Response getCategoryProducts(
            @PathParam("restaurantId") final long restaurantId,
            @PathParam("categoryId") final long categoryId
    ) {
        final List<Product> products = categoryService.getByIdChecked(restaurantId, categoryId, true).getProducts();
        final List<ProductDto> dtoList = ProductDto.fromProductCollection(uriInfo, products);
        return Response.ok(new GenericEntity<List<ProductDto>>(dtoList) {}).build();
    }

    @GET
    @Path("/{restaurantId:\\d+}/categories/{categoryId:\\d+}/products/{productId:\\d+}")
    @Produces(CustomMediaType.APPLICATION_RESTAURANT_PRODUCTS)
    public Response getProduct(
            @PathParam("restaurantId") final long restaurantId,
            @PathParam("categoryId") final long categoryId,
            @PathParam("productId") final long productId,
            @Context Request request
    ) {
        final Product product = productService.getByIdChecked(restaurantId, categoryId, productId, true);
        return ControllerUtils.buildResponseUsingEtag(request, product.hashCode(), () -> ProductDto.fromProduct(uriInfo, product));
    }

    @POST
    @Path("/{restaurantId:\\d+}/categories/{categoryId:\\d+}/products")
    @Consumes(CustomMediaType.APPLICATION_RESTAURANT_PRODUCTS)
    public Response createProduct(
            @PathParam("restaurantId") final long restaurantId,
            @PathParam("categoryId") final long categoryId,
            @Valid @NotNull final ProductForm productForm
    ) {
        final Product product = productService.create(
                restaurantId,
                categoryId,
                productForm.getName(),
                productForm.getDescription(),
                null,
                productForm.getPrice()
        );

        return Response.created(UriUtils.getProductUri(uriInfo, product)).build();
    }

    @PUT
    @Path("/{restaurantId:\\d+}/categories/{categoryId:\\d+}/products/{productId:\\d+}")
    @Consumes(CustomMediaType.APPLICATION_RESTAURANT_PRODUCTS)
    public Response updateProduct(
            @PathParam("restaurantId") final long restaurantId,
            @PathParam("categoryId") final long categoryId,
            @PathParam("productId") final long productId,
            @Valid @NotNull final ProductForm productForm
    ) {
        productService.update(restaurantId, categoryId, productId, productForm.getName(), productForm.getPrice(), productForm.getDescription());
        return Response.noContent().build();
    }

    @DELETE
    @Path("/{restaurantId:\\d+}/categories/{categoryId:\\d+}/products/{productId:\\d+}")
    public Response deleteProduct(
            @PathParam("restaurantId") final long restaurantId,
            @PathParam("categoryId") final long categoryId,
            @PathParam("productId") final long productId
    ) {
        productService.delete(restaurantId, categoryId, productId);
        return Response.noContent().build();
    }

    @GET
    @Path("/{restaurantId:\\d+}/promotions")
    @Produces(CustomMediaType.APPLICATION_RESTAURANT_PROMOTIONS)
    @PreAuthorize("#living == false || @accessValidator.checkRestaurantAdmin(#restaurantId)")
    public Response getRestaurantPromotions(
            @PathParam("restaurantId") final long restaurantId,
            @QueryParam("living") @DefaultValue("false") final boolean living
    ) {
        List<Promotion> promotions = living ? restaurantService.getLivingPromotions(restaurantId) : restaurantService.getActivePromotions(restaurantId);
        final List<PromotionDto> dtoList = PromotionDto.fromPromotionCollection(uriInfo, promotions, restaurantId);
        return Response.ok(new GenericEntity<List<PromotionDto>>(dtoList) {}).build();
    }

    @GET
    @Path("/{restaurantId:\\d+}/promotions/{promotionId:\\d+}")
    @Produces(CustomMediaType.APPLICATION_RESTAURANT_PROMOTIONS)
    public Response getPromotion(
            @PathParam("restaurantId") final long restaurantId,
            @PathParam("promotionId") final long promotionId,
            @Context Request request
    ) {
        final Promotion promotion = productService.getPromotionById(restaurantId, promotionId);
        return ControllerUtils.buildResponseUsingEtag(request, promotion.hashCode(), () -> PromotionDto.fromPromotion(uriInfo, promotion, restaurantId));
    }

    @POST
    @Path("/{restaurantId:\\d+}/promotions")
    @Consumes(CustomMediaType.APPLICATION_RESTAURANT_PROMOTIONS)
    public Response createPromotion(
            @PathParam("restaurantId") final long restaurantId,
            @Valid @NotNull final PromotionForm promotionForm
    ) {
        final Promotion promotion = productService.createPromotion(
                restaurantId,
                promotionForm.getSourceProductId(),
                promotionForm.getPromotionStartDate(),
                promotionForm.getPromotionEndDate(),
                promotionForm.getPercentage()
        );

        return Response.created(UriUtils.getPromotionUri(uriInfo, restaurantId, promotion.getPromotionId())).build();
    }

    @DELETE
    @Path("/{restaurantId:\\d+}/promotions/{promotionId:\\d+}")
    public Response stopPromotion(
            @PathParam("restaurantId") final long restaurantId,
            @PathParam("promotionId") final long promotionId
    ) {
        productService.stopPromotion(restaurantId, promotionId);
        return Response.noContent().build();
    }

    @POST
    @Path("/{restaurantId:\\d+}/reports")
    @Consumes(CustomMediaType.APPLICATION_RESTAURANT_REPORTS)
    public Response createReport(
            @PathParam("restaurantId") final long restaurantId,
            @Valid @NotNull final ReportRestaurantForm restaurantForm
    ) {
        final Report report = reportService.create(restaurantId, ControllerUtils.getCurrentUserIdOrNull(), restaurantForm.getComment());
        return Response.created(UriUtils.getReportUri(uriInfo, report.getReportId(), restaurantId)).build();
    }

    @DELETE
    @Path("/{restaurantId:\\d+}/reports/{reportId:\\d+}")
    public Response deleteReport(
            @PathParam("restaurantId") final long restaurantId,
            @PathParam("reportId") final long reportId
    ) {
        reportService.delete(reportId, restaurantId);
        return Response.noContent().build();
    }

    @PATCH
    @Path("/{restaurantId:\\d+}/reports/{reportId:\\d+}")
    public Response markReportAsHandled(
            @PathParam("restaurantId") final long restaurantId,
            @PathParam("reportId") final long reportId
    ) {
        reportService.markHandled(reportId, restaurantId, ControllerUtils.getCurrentUserIdOrThrow());
        return Response.noContent().build();
    }

    @GET
    @Path("/{restaurantId:\\d+}/reports/{reportId:\\d+}")
    @Produces(CustomMediaType.APPLICATION_RESTAURANT_REPORTS)
    public Response getReport(
            @PathParam("restaurantId") final long restaurantId,
            @PathParam("reportId") final long reportId,
            @Context Request request
    ) {
        final Report report = reportService.getByIdChecked(reportId, restaurantId);
        return ControllerUtils.buildResponseUsingLastModified(request, report.getReportLastUpdate(), () -> ReportDto.fromReport(uriInfo, report));
    }

    @GET
    @PreAuthorize("hasRole('MODERATOR')")
    @Produces(CustomMediaType.APPLICATION_RESTAURANTS_UNHANDLED_REPORTS)
    public Response getRestaurantsWithUnhandledReports(
            @Valid @BeanParam GetRestaurantsWithReports restaurantsWithReportsForm
    ) {
        PaginatedResult<Pair<Restaurant, Integer>> pagedResult = reportService.getCountByRestaurant(
            restaurantsWithReportsForm.getPageOrDefault(),
            restaurantsWithReportsForm.getSizeOrDefault(ControllerUtils.DEFAULT_REPORTS_PAGE_SIZE)
        );

        List<RestaurantDto> dtoList = pagedResult.getResult().stream()
                .map(pair -> {
                    Restaurant restaurant = pair.getKey();
                    Integer reportCount = pair.getValue();
                    RestaurantDto restaurantDto = RestaurantDto.fromRestaurant(uriInfo, restaurant);
                    restaurantDto.setUnhandledReportsCount(reportCount.longValue());
                    return restaurantDto;
                })
                .collect(Collectors.toList());

        final Response.ResponseBuilder responseBuilder = Response.ok(new GenericEntity<List<RestaurantDto>>(dtoList) {});
        return ControllerUtils.addPagingLinks(responseBuilder, pagedResult, uriInfo).build();
    }

    @GET
    @Path("/{restaurantId:\\d+}/reports")
    @Produces(CustomMediaType.APPLICATION_RESTAURANT_REPORTS)
    public Response getRestaurantReports(
            @PathParam("restaurantId") final long restaurantId,
            @Valid @BeanParam PagingForm pagingForm
    ) {
        PaginatedResult<Report> pagedResult = reportService.getByRestaurant(
                restaurantId,
                pagingForm.getPageOrDefault(),
                pagingForm.getSizeOrDefault(ControllerUtils.DEFAULT_REPORTS_PAGE_SIZE)
        );
        List<ReportDto> dtoList = ReportDto.fromReportCollection(uriInfo, pagedResult.getResult());
        final Response.ResponseBuilder responseBuilder = Response.ok(new GenericEntity<List<ReportDto>>(dtoList) {});
        return ControllerUtils.addPagingLinks(responseBuilder, pagedResult, uriInfo).build();
    }
}
