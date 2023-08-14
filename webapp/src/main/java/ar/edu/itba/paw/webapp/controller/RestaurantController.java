package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.exception.RestaurantNotFoundException;
import ar.edu.itba.paw.model.Category;
import ar.edu.itba.paw.model.Product;
import ar.edu.itba.paw.model.Restaurant;
import ar.edu.itba.paw.model.RestaurantDetails;
import ar.edu.itba.paw.service.CategoryService;
import ar.edu.itba.paw.service.ProductService;
import ar.edu.itba.paw.service.RestaurantService;
import ar.edu.itba.paw.util.PaginatedResult;
import ar.edu.itba.paw.webapp.dto.CategoryDto;
import ar.edu.itba.paw.webapp.dto.ProductDto;
import ar.edu.itba.paw.webapp.dto.RestaurantDetailsDto;
import ar.edu.itba.paw.webapp.dto.RestaurantDto;
import ar.edu.itba.paw.webapp.form.FilterForm;
import ar.edu.itba.paw.webapp.form.UpdateRestaurantForm;
import ar.edu.itba.paw.webapp.utils.ControllerUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.util.List;

@Path("restaurants")
@Component
public class RestaurantController {
    private final RestaurantService restaurantService;
    private final CategoryService categoryService;
    private final ProductService productService;

    @Context
    private UriInfo uriInfo;

    @Autowired
    public RestaurantController(final RestaurantService restaurantService, final CategoryService categoryService, final ProductService productService) {
        this.restaurantService = restaurantService;
        this.categoryService = categoryService;
        this.productService = productService;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getRestaurants(@Valid @BeanParam final FilterForm filterForm) {
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
        final Response.ResponseBuilder responseBuilder = Response.ok(new GenericEntity<List<RestaurantDetailsDto>>(dtoList){});
        return ControllerUtils.addPagingLinks(responseBuilder, pagedResult, uriInfo).build();
    }

    @GET
    @Path("/{restaurantId:\\d+}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getRestaurantById(@PathParam("restaurantId") final long restaurantId) {
        final Restaurant restaurant = restaurantService.getById(restaurantId).orElseThrow(RestaurantNotFoundException::new);
        return Response.ok(RestaurantDto.fromRestaurant(uriInfo, restaurant)).build();
    }

    @PUT
    @Path("/{restaurantId:\\d+}")
    public Response updateRestaurantById(
            @PathParam("restaurantId") final long restaurantId,
            @Valid @NotNull final UpdateRestaurantForm updateRestaurantForm
    ) {
        restaurantService.update(
                restaurantId,
                updateRestaurantForm.getName(),
                updateRestaurantForm.getSpecialtyAsEnum(),
                updateRestaurantForm.getAddress(),
                updateRestaurantForm.getDescription(),
                updateRestaurantForm.getTagsAsEnum()
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
    public Response getRestaurantCategories(@PathParam("restaurantId") final long restaurantId) {
        final List<Category> categories = categoryService.getByRestaurantSortedByOrder(restaurantId);
        final List<CategoryDto> dtoList = CategoryDto.fromCategoryCollection(uriInfo, categories);
        return Response.ok(new GenericEntity<List<CategoryDto>>(dtoList){}).build();
    }

    @GET
    @Path("/{restaurantId:\\d+}/categories/{categoryId:\\d+}")
    public Response getCategory(
            @PathParam("restaurantId") final long restaurantId,
            @PathParam("categoryId") final long categoryId
    ) {
        final Category category = categoryService.getByIdChecked(restaurantId, categoryId);
        final CategoryDto dto = CategoryDto.fromCategory(uriInfo, category);
        return Response.ok(new GenericEntity<CategoryDto>(dto){}).build();
    }

    @GET
    @Path("/{restaurantId:\\d+}/categories/{categoryId:\\d+}/products")
    public Response getCategoryProducts(
            @PathParam("restaurantId") final long restaurantId,
            @PathParam("categoryId") final long categoryId
    ) {
        final List<Product> products = categoryService.getByIdChecked(restaurantId, categoryId).getProducts();
        final List<ProductDto> dtoList = ProductDto.fromProductCollection(uriInfo, products);
        return Response.ok(new GenericEntity<List<ProductDto>>(dtoList){}).build();
    }

    @GET
    @Path("/{restaurantId:\\d+}/categories/{categoryId:\\d+}/products/{productId:\\d+}")
    public Response getCategoryProducts(
            @PathParam("restaurantId") final long restaurantId,
            @PathParam("categoryId") final long categoryId,
            @PathParam("productId") final long productId
    ) {
        final Product product = productService.getByIdChecked(restaurantId, categoryId, productId);
        final ProductDto dto = ProductDto.fromProduct(uriInfo, product);
        return Response.ok(new GenericEntity<ProductDto>(dto){}).build();
    }
}
