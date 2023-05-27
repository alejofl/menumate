package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.exception.*;
import ar.edu.itba.paw.model.*;
import ar.edu.itba.paw.service.*;
import ar.edu.itba.paw.util.PaginatedResult;
import ar.edu.itba.paw.util.Pair;
import ar.edu.itba.paw.util.Triplet;
import ar.edu.itba.paw.webapp.auth.PawAuthUserDetails;
import ar.edu.itba.paw.webapp.form.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Controller
public class UserController {
    @Autowired
    private OrderService orderService;

    @Autowired
    private RestaurantRoleService restaurantRoleService;

    @Autowired
    private RestaurantService restaurantService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private ProductService productService;

    @Autowired
    private UserService userService;

    @Autowired
    private ReviewService reviewService;

    @RequestMapping(value = "/user/restaurants", method = RequestMethod.GET)
    public ModelAndView myRestaurants() {
        ModelAndView mav = new ModelAndView("user/myrestaurants");
        List<RestaurantRoleDetails> restaurants = restaurantRoleService.getByUser(ControllerUtils.getCurrentUserIdOrThrow());
        mav.addObject("restaurants", restaurants);
        return mav;
    }

    @RequestMapping(value = "/user/orders", method = RequestMethod.GET)
    public ModelAndView myOrders() {
        return new ModelAndView("redirect:/user/orders/inprogress");
    }

    @RequestMapping(value = "/user/orders/{status}", method = RequestMethod.GET)
    public ModelAndView myOrdersByStatus(
            @Valid final PagingForm paging,
            final BindingResult errors,
            @PathVariable final String status
    ) {
        ModelAndView mav = new ModelAndView("user/myorders");

        if (errors.hasErrors()) {
            mav.addObject("error", Boolean.TRUE);
            paging.clear();
        }

        PaginatedResult<Order> orders;
        // FIXME
        switch (status) {
            case "inprogress":
                orders = orderService.getByUser(ControllerUtils.getCurrentUserIdOrThrow(), paging.getPageOrDefault(), paging.getSizeOrDefault(ControllerUtils.DEFAULT_ORDERS_PAGE_SIZE));
                break;
            case "all":
                orders = orderService.getByUser(ControllerUtils.getCurrentUserIdOrThrow(), paging.getPageOrDefault(), paging.getSizeOrDefault(ControllerUtils.DEFAULT_ORDERS_PAGE_SIZE));
                break;
            default:
                throw new ResourceNotFoundException();
        }

        mav.addObject("orders", orders.getResult());
        mav.addObject("orderCount", orders.getTotalCount());
        mav.addObject("pageCount", orders.getTotalPageCount());
        mav.addObject("status", status);

        return mav;
    }

    @RequestMapping(value = "/orders/{id:\\d+}", method = RequestMethod.GET)
    public ModelAndView order(
            @PathVariable final int id,
            @ModelAttribute("reviewForm") final ReviewForm reviewForm,
            final Boolean error
    ) {
        ModelAndView mav = new ModelAndView("user/order");
        mav.addObject("order", orderService.getById(id).orElseThrow(OrderNotFoundException::new));
        mav.addObject("has_review", reviewService.getByOrder(id).isPresent());
        mav.addObject("error", error);
        return mav;
    }

    @RequestMapping(value = "/orders/{id:\\d+}/review", method = RequestMethod.POST)
    public ModelAndView review(
            @PathVariable final int id,
            @Valid @ModelAttribute("reviewForm") final ReviewForm reviewForm,
            final BindingResult errors
    ) {
        if (errors.hasErrors()) {
            return order(id, reviewForm, true);
        }

        reviewService.create(id, reviewForm.getRating(), reviewForm.getComment());

        return new ModelAndView(String.format("redirect:/orders/%d", id));
    }

    @RequestMapping(value = "/restaurants/create", method = RequestMethod.GET)
    public ModelAndView createRestaurant(
            @ModelAttribute("createRestaurantForm") final CreateRestaurantForm form
    ) {
        final ModelAndView mav = new ModelAndView("user/create_restaurant");
        mav.addObject("specialties", RestaurantSpecialty.values());
        mav.addObject("tags", RestaurantTags.values());
        return mav;
    }

    @RequestMapping(value = "/restaurants/create", method = RequestMethod.POST)
    public ModelAndView createRestaurantForm(
            @Valid @ModelAttribute("createRestaurantForm") final CreateRestaurantForm form,
            final BindingResult errors
    ) throws IOException {
        if (errors.hasErrors()) {
            return createRestaurant(form);
        }

        List<RestaurantTags> tags = form.getTags().stream().map(RestaurantTags::fromOrdinal).collect(Collectors.toList());

        PawAuthUserDetails userDetails = ControllerUtils.getCurrentUserDetailsOrThrow();
        Restaurant restaurant = restaurantService.create(
                form.getName(),
                userDetails.getUsername(),
                RestaurantSpecialty.fromOrdinal(form.getSpecialty()),
                userDetails.getUserId(),
                form.getAddress(),
                form.getDescription(),
                form.getMaxTables(),
                form.getLogo().getBytes(),
                form.getPortrait1().getBytes(),
                form.getPortrait2().getBytes(),
                false,
                tags
        );

        return new ModelAndView(String.format("redirect:/restaurants/%d", restaurant.getRestaurantId()));
    }

    @RequestMapping(value = "/restaurants/{id:\\d+}/edit", method = RequestMethod.GET)
    public ModelAndView editRestaurant(
            @PathVariable final int id,
            @ModelAttribute("addProductForm") final AddProductForm addProductForm,
            @ModelAttribute("addCategoryForm") final AddCategoryForm addCategoryForm,
            @ModelAttribute("deleteProductForm") final DeleteProductForm deleteProductForm,
            @ModelAttribute("deleteCategoryForm") final DeleteCategoryForm deleteCategoryForm,
            @ModelAttribute("addEmployeeForm") final AddEmployeeForm addEmployeeForm,
            @ModelAttribute("deleteEmployeeForm") final DeleteEmployeeForm deleteEmployeeForm,
            @ModelAttribute("editProductPriceForm") final EditProductPriceForm editProductPriceForm,
            final Boolean addProductErrors,
            final Boolean addCategoryErrors,
            final Boolean addEmployeeErrors,
            final Boolean editProductErrors
    ) {
        ModelAndView mav = new ModelAndView("user/edit_menu");

        final Restaurant restaurant = restaurantService.getById(id).orElseThrow(RestaurantNotFoundException::new);
        mav.addObject("restaurant", restaurant);

        final List<Category> menu = categoryService.getByRestaurantSortedByOrder(id);
        mav.addObject("menu", menu);

        final List<Pair<User, RestaurantRoleLevel>> employees = restaurantRoleService.getByRestaurant(id);
        mav.addObject("employees", employees);

        mav.addObject("roles", RestaurantRoleLevel.VALUES_EXCEPT_OWNER);
        mav.addObject("is_owner", restaurant.getOwnerUserId() == ControllerUtils.getCurrentUserIdOrThrow());

        mav.addObject("addProductErrors", addProductErrors);
        mav.addObject("addCategoryErrors", addCategoryErrors);
        mav.addObject("addEmployeeErrors", addEmployeeErrors);
        mav.addObject("editProductErrors", editProductErrors);

        return mav;
    }

    @RequestMapping(value = "/restaurants/{id:\\d+}/products/add", method = RequestMethod.POST)
    public ModelAndView addProductToRestaurant(
            @PathVariable final int id,
            @Valid @ModelAttribute("addProductForm") final AddProductForm addProductForm,
            final BindingResult errors,
            @ModelAttribute("addCategoryForm") final AddCategoryForm addCategoryForm,
            @ModelAttribute("deleteProductForm") final DeleteProductForm deleteProductForm,
            @ModelAttribute("deleteCategoryForm") final DeleteCategoryForm deleteCategoryForm,
            @ModelAttribute("addEmployeeForm") final AddEmployeeForm addEmployeeForm,
            @ModelAttribute("deleteEmployeeForm") final DeleteEmployeeForm deleteEmployeeForm,
            @ModelAttribute("editProductPriceForm") final EditProductPriceForm editProductPriceForm
    ) throws IOException {
        if (errors.hasErrors()) {
            return editRestaurant(
                    id,
                    addProductForm,
                    addCategoryForm,
                    deleteProductForm,
                    deleteCategoryForm,
                    addEmployeeForm,
                    deleteEmployeeForm,
                    editProductPriceForm,
                    true,
                    false,
                    false,
                    false
            );
        }

        productService.create(
                addProductForm.getCategoryId(),
                addProductForm.getProductName(),
                addProductForm.getDescription(),
                addProductForm.getImage().getBytes(),
                addProductForm.getPrice()
        );

        return new ModelAndView(String.format("redirect:/restaurants/%d/edit", id));
    }

    @RequestMapping(value = "/restaurants/{id:\\d+}/categories/add", method = RequestMethod.POST)
    public ModelAndView addCategoryToRestaurant(
            @PathVariable final int id,
            @ModelAttribute("addProductForm") final AddProductForm addProductForm,
            @Valid @ModelAttribute("addCategoryForm") final AddCategoryForm addCategoryForm,
            final BindingResult errors,
            @ModelAttribute("deleteProductForm") final DeleteProductForm deleteProductForm,
            @ModelAttribute("deleteCategoryForm") final DeleteCategoryForm deleteCategoryForm,
            @ModelAttribute("addEmployeeForm") final AddEmployeeForm addEmployeeForm,
            @ModelAttribute("deleteEmployeeForm") final DeleteEmployeeForm deleteEmployeeForm,
            @ModelAttribute("editProductPriceForm") final EditProductPriceForm editProductPriceForm
    ) {
        if (errors.hasErrors()) {
            return editRestaurant(
                    id,
                    addProductForm,
                    addCategoryForm,
                    deleteProductForm,
                    deleteCategoryForm,
                    addEmployeeForm,
                    deleteEmployeeForm,
                    editProductPriceForm,
                    false,
                    true,
                    false,
                    false
            );
        }

        categoryService.create(addCategoryForm.getRestaurantId(), addCategoryForm.getName());

        return new ModelAndView(String.format("redirect:/restaurants/%d/edit", id));
    }

    @RequestMapping(value = "/restaurants/{id:\\d+}/products/delete", method = RequestMethod.POST)
    public ModelAndView deleteProductForRestaurant(
            @PathVariable final int id,
            @ModelAttribute("addProductForm") final AddProductForm addProductForm,
            @ModelAttribute("addCategoryForm") final AddCategoryForm addCategoryForm,
            @Valid @ModelAttribute("deleteProductForm") final DeleteProductForm deleteProductForm,
            final BindingResult errors,
            @ModelAttribute("deleteCategoryForm") final DeleteCategoryForm deleteCategoryForm,
            @ModelAttribute("addEmployeeForm") final AddEmployeeForm addEmployeeForm,
            @ModelAttribute("deleteEmployeeForm") final DeleteEmployeeForm deleteEmployeeForm,
            @ModelAttribute("editProductPriceForm") final EditProductPriceForm editProductPriceForm
    ) {
        if (errors.hasErrors()) {
            throw new IllegalStateException();
        }

        productService.delete(deleteProductForm.getProductId());

        return new ModelAndView(String.format("redirect:/restaurants/%d/edit", id));
    }

    @RequestMapping(value = "/restaurants/{id:\\d+}/categories/delete", method = RequestMethod.POST)
    public ModelAndView deleteCategoryForRestaurant(
            @PathVariable final int id,
            @ModelAttribute("addProductForm") final AddProductForm addProductForm,
            @ModelAttribute("addCategoryForm") final AddCategoryForm addCategoryForm,
            @ModelAttribute("deleteProductForm") final DeleteProductForm deleteProductForm,
            @Valid @ModelAttribute("deleteCategoryForm") final DeleteCategoryForm deleteCategoryForm,
            final BindingResult errors,
            @ModelAttribute("addEmployeeForm") final AddEmployeeForm addEmployeeForm,
            @ModelAttribute("deleteEmployeeForm") final DeleteEmployeeForm deleteEmployeeForm,
            @ModelAttribute("editProductPriceForm") final EditProductPriceForm editProductPriceForm
    ) {
        if (errors.hasErrors()) {
            throw new IllegalStateException();
        }

        categoryService.delete(deleteCategoryForm.getCategoryId());

        return new ModelAndView(String.format("redirect:/restaurants/%d/edit", id));
    }

    @RequestMapping(value = "/restaurants/{id:\\d+}/employees/add", method = RequestMethod.POST)
    public ModelAndView addEmployeeForRestaurant(
            @PathVariable final int id,
            @ModelAttribute("addProductForm") final AddProductForm addProductForm,
            @ModelAttribute("addCategoryForm") final AddCategoryForm addCategoryForm,
            @ModelAttribute("deleteProductForm") final DeleteProductForm deleteProductForm,
            @ModelAttribute("deleteCategoryForm") final DeleteCategoryForm deleteCategoryForm,
            @Valid @ModelAttribute("addEmployeeForm") final AddEmployeeForm addEmployeeForm,
            final BindingResult errors,
            @ModelAttribute("deleteEmployeeForm") final DeleteEmployeeForm deleteEmployeeForm,
            @ModelAttribute("editProductPriceForm") final EditProductPriceForm editProductPriceForm
    ) {
        if (errors.hasErrors()) {
            return editRestaurant(
                    id,
                    addProductForm,
                    addCategoryForm,
                    deleteProductForm,
                    deleteCategoryForm,
                    addEmployeeForm,
                    deleteEmployeeForm,
                    editProductPriceForm,
                    false,
                    false,
                    true,
                    false
            );
        }

        User user = userService.getByEmail(addEmployeeForm.getEmail()).orElseThrow(UserNotFoundException::new);
        restaurantRoleService.setRole(user.getUserId(), id, RestaurantRoleLevel.fromOrdinal(addEmployeeForm.getRole()));

        return new ModelAndView(String.format("redirect:/restaurants/%d/edit", id));
    }

    @RequestMapping(value = "/restaurants/{id:\\d+}/employees/delete", method = RequestMethod.POST)
    public ModelAndView deleteEmployeeForRestaurant(
            @PathVariable final int id,
            @ModelAttribute("addProductForm") final AddProductForm addProductForm,
            @ModelAttribute("addCategoryForm") final AddCategoryForm addCategoryForm,
            @ModelAttribute("deleteProductForm") final DeleteProductForm deleteProductForm,
            @ModelAttribute("deleteCategoryForm") final DeleteCategoryForm deleteCategoryForm,
            @ModelAttribute("addEmployeeForm") final AddEmployeeForm addEmployeeForm,
            @Valid @ModelAttribute("deleteEmployeeForm") final DeleteEmployeeForm deleteEmployeeForm,
            final BindingResult errors,
            @ModelAttribute("editProductPriceForm") final EditProductPriceForm editProductPriceForm
    ) {
        if (errors.hasErrors()) {
            throw new IllegalStateException();
        }

        restaurantRoleService.deleteRole(deleteEmployeeForm.getUserId(), id);

        return new ModelAndView(String.format("redirect:/restaurants/%d/edit", id));
    }

    @RequestMapping(value = "/restaurants/{id:\\d+}/products/edit", method = RequestMethod.POST)
    public ModelAndView editPriceForProduct(
            @PathVariable final int id,
            @ModelAttribute("addProductForm") final AddProductForm addProductForm,
            @ModelAttribute("addCategoryForm") final AddCategoryForm addCategoryForm,
            @ModelAttribute("deleteProductForm") final DeleteProductForm deleteProductForm,
            @ModelAttribute("deleteCategoryForm") final DeleteCategoryForm deleteCategoryForm,
            @ModelAttribute("addEmployeeForm") final AddEmployeeForm addEmployeeForm,
            @ModelAttribute("deleteEmployeeForm") final DeleteEmployeeForm deleteEmployeeForm,
            @Valid @ModelAttribute("editProductPriceForm") final EditProductPriceForm editProductPriceForm,
            final BindingResult errors
    ) {
        if (errors.hasErrors()) {
            return editRestaurant(
                    id,
                    addProductForm,
                    addCategoryForm,
                    deleteProductForm,
                    deleteCategoryForm,
                    addEmployeeForm,
                    deleteEmployeeForm,
                    editProductPriceForm,
                    false,
                    false,
                    false,
                    true
            );
        }

        Product product = productService.getById(editProductPriceForm.getProductId()).orElseThrow(ProductNotFoundException::new);
        productService.update(product.getProductId(), product.getName(), editProductPriceForm.getPrice(), product.getDescription());

        return new ModelAndView(String.format("redirect:/restaurants/%d/edit", id));
    }
}
