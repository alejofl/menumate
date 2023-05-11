package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.model.*;
import ar.edu.itba.paw.model.util.PaginatedResult;
import ar.edu.itba.paw.model.util.Pair;
import ar.edu.itba.paw.model.util.Triplet;
import ar.edu.itba.paw.service.*;
import ar.edu.itba.paw.webapp.auth.PawAuthUserDetails;
import ar.edu.itba.paw.webapp.exception.ResourceNotFoundException;
import ar.edu.itba.paw.webapp.exception.RestaurantNotFoundException;
import ar.edu.itba.paw.webapp.exception.UserNotFoundException;
import ar.edu.itba.paw.webapp.form.*;
import ar.edu.itba.paw.webapp.exception.OrderNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Controller
public class UserController {
    @Autowired
    private OrderService orderService;

    @Autowired
    private RolesService rolesService;

    @Autowired
    private RestaurantService restaurantService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private ProductService productService;

    @Autowired
    private UserService userService;

    @RequestMapping(value = "/user/restaurants", method = RequestMethod.GET)
    public ModelAndView myRestaurants() {
        ModelAndView mav = new ModelAndView("user/myrestaurants");
        List<Triplet<Restaurant, RestaurantRoleLevel, Integer>> restaurants = rolesService.getByUser(ControllerUtils.getCurrentUserIdOrThrow());
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

        PaginatedResult<OrderItemless> orders;
        switch (status) {
            case "inprogress":
                orders = orderService.getInProgressByUserExcludeItems(ControllerUtils.getCurrentUserIdOrThrow(), paging.getPageOrDefault(), paging.getSizeOrDefault(ControllerUtils.DEFAULT_ORDERS_PAGE_SIZE));
                break;
            case "all":
                orders = orderService.getByUserExcludeItems(ControllerUtils.getCurrentUserIdOrThrow(), paging.getPageOrDefault(), paging.getSizeOrDefault(ControllerUtils.DEFAULT_ORDERS_PAGE_SIZE));
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
            @ModelAttribute("reviewForm") final ReviewForm reviewForm
    ) {
        ModelAndView mav = new ModelAndView("user/order");
        mav.addObject("order", orderService.getById(id).orElseThrow(OrderNotFoundException::new));
        return mav;
    }

    @RequestMapping(value = "/restaurants/create", method = RequestMethod.GET)
    public ModelAndView createRestaurant(
            @ModelAttribute("createRestaurantForm") final CreateRestaurantForm form
    ) {
        return new ModelAndView("user/create_restaurant");
    }

    @RequestMapping(value = "/restaurants/create", method = RequestMethod.POST)
    public ModelAndView createRestaurantForm(
            @Valid @ModelAttribute("createRestaurantForm") final CreateRestaurantForm form,
            final BindingResult errors
    ) throws IOException {
        // TODO recover images when errors occur on other field
        if (errors.hasErrors()) {
            return createRestaurant(form);
        }

        PawAuthUserDetails userDetails = ControllerUtils.getCurrentUserDetailsOrThrow();
        int restaurantId = restaurantService.create(
                form.getName(),
                userDetails.getUsername(),
                userDetails.getUserId(),
                form.getDescription(),
                form.getAddress(),
                form.getMaxTables(),
                form.getLogo().getBytes(),
                form.getPortrait1().getBytes(),
                form.getPortrait2().getBytes()
        );

        return new ModelAndView(String.format("redirect:/restaurants/%d", restaurantId));
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
            final Boolean addProductErrors,
            final Boolean addCategoryErrors,
            final Boolean addEmployeeErrors
    ) {
        ModelAndView mav = new ModelAndView("user/edit_menu");

        final Restaurant restaurant = restaurantService.getById(id).orElseThrow(RestaurantNotFoundException::new);
        mav.addObject("restaurant", restaurant);

        final List<Pair<Category, List<Product>>> menu = restaurantService.getMenu(id);
        mav.addObject("menu", menu);

        final List<Pair<User, RestaurantRoleLevel>> employees = rolesService.getByRestaurant(id);
        mav.addObject("employees", employees);

        mav.addObject("roles", RestaurantRoleLevel.VALUES_EXCEPT_OWNER);
        mav.addObject("is_owner", restaurant.getOwnerUserId() == ControllerUtils.getCurrentUserIdOrThrow());
        mav.addObject("addProductErrors", addProductErrors);
        mav.addObject("addCategoryErrors", addCategoryErrors);
        mav.addObject("addEmployeeErrors", addEmployeeErrors);

        return mav;
    }

    @RequestMapping(value = "/restaurants/{id:\\d+}/edit/add_product", method = RequestMethod.POST)
    public ModelAndView addProductToRestaurant(
            @PathVariable final int id,
            @Valid @ModelAttribute("addProductForm") final AddProductForm addProductForm,
            final BindingResult errors,
            @ModelAttribute("addCategoryForm") final AddCategoryForm addCategoryForm,
            @ModelAttribute("deleteProductForm") final DeleteProductForm deleteProductForm,
            @ModelAttribute("deleteCategoryForm") final DeleteCategoryForm deleteCategoryForm,
            @ModelAttribute("addEmployeeForm") final AddEmployeeForm addEmployeeForm,
            @ModelAttribute("deleteEmployeeForm") final DeleteEmployeeForm deleteEmployeeForm
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
                    true,
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

    @RequestMapping(value = "/restaurants/{id:\\d+}/edit/add_category", method = RequestMethod.POST)
    public ModelAndView addCategoryToRestaurant(
            @PathVariable final int id,
            @ModelAttribute("addProductForm") final AddProductForm addProductForm,
            @Valid @ModelAttribute("addCategoryForm") final AddCategoryForm addCategoryForm,
            final BindingResult errors,
            @ModelAttribute("deleteProductForm") final DeleteProductForm deleteProductForm,
            @ModelAttribute("deleteCategoryForm") final DeleteCategoryForm deleteCategoryForm,
            @ModelAttribute("addEmployeeForm") final AddEmployeeForm addEmployeeForm,
            @ModelAttribute("deleteEmployeeForm") final DeleteEmployeeForm deleteEmployeeForm
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
                    false,
                    true,
                    false
            );
        }

        categoryService.create(addCategoryForm.getRestaurantId(), addCategoryForm.getName());

        return new ModelAndView(String.format("redirect:/restaurants/%d/edit", id));
    }

    @RequestMapping(value = "/restaurants/{id:\\d+}/edit/delete_product", method = RequestMethod.POST)
    public ModelAndView deleteProductForRestaurant(
            @PathVariable final int id,
            @ModelAttribute("addProductForm") final AddProductForm addProductForm,
            @ModelAttribute("addCategoryForm") final AddCategoryForm addCategoryForm,
            @Valid @ModelAttribute("deleteProductForm") final DeleteProductForm deleteProductForm,
            final BindingResult errors,
            @ModelAttribute("deleteCategoryForm") final DeleteCategoryForm deleteCategoryForm,
            @ModelAttribute("addEmployeeForm") final AddEmployeeForm addEmployeeForm,
            @ModelAttribute("deleteEmployeeForm") final DeleteEmployeeForm deleteEmployeeForm
    ) {
        if (errors.hasErrors()) {
            throw new IllegalStateException();
        }

        productService.delete(deleteProductForm.getProductId());

        return new ModelAndView(String.format("redirect:/restaurants/%d/edit", id));
    }

    @RequestMapping(value = "/restaurants/{id:\\d+}/edit/delete_category", method = RequestMethod.POST)
    public ModelAndView deleteCategoryForRestaurant(
            @PathVariable final int id,
            @ModelAttribute("addProductForm") final AddProductForm addProductForm,
            @ModelAttribute("addCategoryForm") final AddCategoryForm addCategoryForm,
            @ModelAttribute("deleteProductForm") final DeleteProductForm deleteProductForm,
            @Valid @ModelAttribute("deleteCategoryForm") final DeleteCategoryForm deleteCategoryForm,
            final BindingResult errors,
            @ModelAttribute("addEmployeeForm") final AddEmployeeForm addEmployeeForm,
            @ModelAttribute("deleteEmployeeForm") final DeleteEmployeeForm deleteEmployeeForm
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
            @ModelAttribute("deleteEmployeeForm") final DeleteEmployeeForm deleteEmployeeForm
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
                    false,
                    false,
                    true
            );
        }

        User user = userService.getByEmail(addEmployeeForm.getEmail()).orElseThrow(UserNotFoundException::new);
        rolesService.setRole(user.getUserId(), id, RestaurantRoleLevel.fromOrdinal(addEmployeeForm.getRole()));

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
            final BindingResult errors
    ) {
        if (errors.hasErrors()) {
            throw new IllegalStateException();
        }

        rolesService.deleteRole(id, deleteEmployeeForm.getUserId());

        return new ModelAndView(String.format("redirect:/restaurants/%d/edit", id));
    }
}
