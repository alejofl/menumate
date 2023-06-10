package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.exception.*;
import ar.edu.itba.paw.model.*;
import ar.edu.itba.paw.service.*;
import ar.edu.itba.paw.util.MyBoolean;
import ar.edu.itba.paw.util.PaginatedResult;
import ar.edu.itba.paw.util.Pair;
import ar.edu.itba.paw.webapp.auth.PawAuthUserDetails;
import ar.edu.itba.paw.webapp.form.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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

    @RequestMapping(value = "/user/orders/{status:all|inprogress}", method = RequestMethod.GET)
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

        PaginatedResult<Order> orders = orderService.getByUser(ControllerUtils.getCurrentUserIdOrThrow(), paging.getPageOrDefault(), paging.getSizeOrDefault(ControllerUtils.DEFAULT_ORDERS_PAGE_SIZE), true);
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
        mav.addObject("review", reviewService.getByOrder(id).orElse(null));
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
                true,
                tags
        );

        return new ModelAndView(String.format("redirect:/restaurants/%d/edit", restaurant.getRestaurantId()));
    }

    @RequestMapping(value = "/restaurants/{id:\\d+}/edit", method = RequestMethod.GET)
    public ModelAndView editRestaurant(
            @PathVariable final int id,

            @ModelAttribute("editRestaurantForm") final EditRestaurantForm editRestaurantForm,
            final Boolean editRestaurantErrors,

            @ModelAttribute("addProductForm") final AddProductForm addProductForm,
            final Boolean addProductErrors,
            @ModelAttribute("editProductForm") final EditProductForm editProductForm,
            final Boolean editProductErrors,
            @ModelAttribute("deleteProductForm") final DeleteProductForm deleteProductForm,

            @ModelAttribute("addCategoryForm") final AddCategoryForm addCategoryForm,
            final Boolean addCategoryErrors,
            @ModelAttribute("editCategoryForm") final EditCategoryForm editCategoryForm,
            final Boolean editCategoryErrors,
            @ModelAttribute("editCategoryOrderForm") final EditCategoryOrderForm editCategoryOrderForm,
            @ModelAttribute("deleteCategoryForm") final DeleteCategoryForm deleteCategoryForm,

            @ModelAttribute("addEmployeeForm") final AddEmployeeForm addEmployeeForm,
            @ModelAttribute("addEmployeeErrors") final MyBoolean addEmployeeErrors,
            @ModelAttribute("deleteEmployeeForm") final DeleteEmployeeForm deleteEmployeeForm
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

        mav.addObject("specialties", RestaurantSpecialty.values());
        mav.addObject("tags", RestaurantTags.values());

        editRestaurantForm.setRestaurantId(id);
        editRestaurantForm.setName(restaurant.getName());
        editRestaurantForm.setAddress(restaurant.getAddress());
        editRestaurantForm.setSpecialty(restaurant.getSpecialty().ordinal());
        editRestaurantForm.setDescription(restaurant.getDescription());
        editRestaurantForm.setMaxTables(restaurant.getMaxTables());
        editRestaurantForm.setTags(restaurant.getTags().stream().map(Enum::ordinal).collect(Collectors.toList()));

        // NOTE: This is a workaround to avoid IllegalStateException.
        // The problem is that when this method is called from another method (i.e. when there's an error on a form)
        // the ModelAttributes are not added to the model automatically.
        mav.addObject("editRestaurantForm", editRestaurantForm);
        mav.addObject("addProductForm", addProductForm);
        mav.addObject("editProductForm", editProductForm);
        mav.addObject("deleteProductForm", deleteProductForm);
        mav.addObject("addCategoryForm", addCategoryForm);
        mav.addObject("editCategoryForm", editCategoryForm);
        mav.addObject("editCategoryOrderForm", editCategoryOrderForm);
        mav.addObject("deleteCategoryForm", deleteCategoryForm);
        mav.addObject("addEmployeeForm", addEmployeeForm);
        mav.addObject("deleteEmployeeForm", deleteEmployeeForm);

        mav.addObject("editRestaurantErrors", editRestaurantErrors);
        mav.addObject("addProductErrors", addProductErrors);
        mav.addObject("editProductErrors", editProductErrors);
        mav.addObject("addCategoryErrors", addCategoryErrors);
        mav.addObject("editCategoryErrors", editCategoryErrors);
        mav.addObject("addEmployeeErrors", addEmployeeErrors.get());

        return mav;
    }

    @RequestMapping(value = "/restaurants/{id:\\d+}/edit", method = RequestMethod.POST)
    public ModelAndView editRestaurantInformation(
            @PathVariable final int id,
            @Valid @ModelAttribute("editRestaurantForm") final EditRestaurantForm editRestaurantForm,
            final BindingResult errors
    ) throws IOException {
        if (errors.hasErrors()) {
            return editRestaurant(
                    id,
                    editRestaurantForm,
                    true,
                    new AddProductForm(),
                    false,
                    new EditProductForm(),
                    false,
                    new DeleteProductForm(),
                    new AddCategoryForm(),
                    false,
                    new EditCategoryForm(),
                    false,
                    new EditCategoryOrderForm(),
                    new DeleteCategoryForm(),
                    new AddEmployeeForm(),
                    new MyBoolean(false),
                    new DeleteEmployeeForm()
            );
        }

        restaurantService.update(
                editRestaurantForm.getRestaurantId(),
                editRestaurantForm.getName(),
                RestaurantSpecialty.fromOrdinal(editRestaurantForm.getSpecialty()),
                editRestaurantForm.getAddress(),
                editRestaurantForm.getDescription(),
                editRestaurantForm.getTags().stream().map(RestaurantTags::fromOrdinal).collect(Collectors.toList())
        );
        restaurantService.updateImages(
                editRestaurantForm.getRestaurantId(),
                editRestaurantForm.getLogo().getBytes(),
                editRestaurantForm.getPortrait1().getBytes(),
                editRestaurantForm.getPortrait2().getBytes()
        );

        return new ModelAndView(String.format("redirect:/restaurants/%d/edit", id));
    }

    @RequestMapping(value = "/restaurants/{id:\\d+}/products/add", method = RequestMethod.POST)
    public ModelAndView addProductToRestaurant(
            @PathVariable final int id,
            @Valid @ModelAttribute("addProductForm") final AddProductForm addProductForm,
            final BindingResult errors
    ) throws IOException {
        if (errors.hasErrors()) {
            return editRestaurant(
                    id,
                    new EditRestaurantForm(),
                    false,
                    addProductForm,
                    true,
                    new EditProductForm(),
                    false,
                    new DeleteProductForm(),
                    new AddCategoryForm(),
                    false,
                    new EditCategoryForm(),
                    false,
                    new EditCategoryOrderForm(),
                    new DeleteCategoryForm(),
                    new AddEmployeeForm(),
                    new MyBoolean(false),
                    new DeleteEmployeeForm()
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

    @RequestMapping(value = "/restaurants/{id:\\d+}/products/edit", method = RequestMethod.POST)
    public ModelAndView editProduct(
            @PathVariable final int id,
            @Valid @ModelAttribute("editProductForm") final EditProductForm editProductForm,
            final BindingResult errors
    ) throws IOException {
        if (errors.hasErrors()) {
            return editRestaurant(
                    id,
                    new EditRestaurantForm(),
                    false,
                    new AddProductForm(),
                    false,
                    editProductForm,
                    true,
                    new DeleteProductForm(),
                    new AddCategoryForm(),
                    false,
                    new EditCategoryForm(),
                    false,
                    new EditCategoryOrderForm(),
                    new DeleteCategoryForm(),
                    new AddEmployeeForm(),
                    new MyBoolean(false),
                    new DeleteEmployeeForm()
            );
        }

        Product product = productService.getById(editProductForm.getProductId()).orElseThrow(ProductNotFoundException::new);
        productService.update(editProductForm.getProductId(), editProductForm.getProductName(), editProductForm.getPrice(), editProductForm.getDescription());
        productService.updateImage(editProductForm.getProductId(), editProductForm.getImage().getBytes());
        if (product.getCategoryId() != editProductForm.getCategoryId()) {
            categoryService.moveProduct(editProductForm.getProductId(), editProductForm.getCategoryId());
        }

        return new ModelAndView(String.format("redirect:/restaurants/%d/edit", id));
    }

    @RequestMapping(value = "/restaurants/{id:\\d+}/products/delete", method = RequestMethod.POST)
    public ModelAndView deleteProductForRestaurant(
            @PathVariable final int id,
            @Valid @ModelAttribute("deleteProductForm") final DeleteProductForm deleteProductForm,
            final BindingResult errors
    ) {
        if (errors.hasErrors()) {
            throw new IllegalStateException();
        }

        productService.delete(deleteProductForm.getProductId());

        return new ModelAndView(String.format("redirect:/restaurants/%d/edit", id));
    }


    @RequestMapping(value = "/restaurants/{id:\\d+}/categories/add", method = RequestMethod.POST)
    public ModelAndView addCategoryToRestaurant(
            @PathVariable final int id,
            @Valid @ModelAttribute("addCategoryForm") final AddCategoryForm addCategoryForm,
            final BindingResult errors
    ) {
        if (errors.hasErrors()) {
            return editRestaurant(
                    id,
                    new EditRestaurantForm(),
                    false,
                    new AddProductForm(),
                    false,
                    new EditProductForm(),
                    false,
                    new DeleteProductForm(),
                    addCategoryForm,
                    true,
                    new EditCategoryForm(),
                    false,
                    new EditCategoryOrderForm(),
                    new DeleteCategoryForm(),
                    new AddEmployeeForm(),
                    new MyBoolean(false),
                    new DeleteEmployeeForm()
            );
        }

        categoryService.create(addCategoryForm.getRestaurantId(), addCategoryForm.getName());

        return new ModelAndView(String.format("redirect:/restaurants/%d/edit", id));
    }

    @RequestMapping(value = "/restaurants/{id:\\d+}/categories/edit", method = RequestMethod.POST)
    public ModelAndView editCategory(
            @PathVariable final int id,
            @Valid @ModelAttribute("editCategoryForm") final EditCategoryForm editCategoryForm,
            final BindingResult errors
    ) {
        if (errors.hasErrors()) {
            return editRestaurant(
                    id,
                    new EditRestaurantForm(),
                    false,
                    new AddProductForm(),
                    false,
                    new EditProductForm(),
                    false,
                    new DeleteProductForm(),
                    new AddCategoryForm(),
                    false,
                    editCategoryForm,
                    true,
                    new EditCategoryOrderForm(),
                    new DeleteCategoryForm(),
                    new AddEmployeeForm(),
                    new MyBoolean(false),
                    new DeleteEmployeeForm()
            );
        }

        categoryService.updateName(editCategoryForm.getCategoryId(), editCategoryForm.getName());

        return new ModelAndView(String.format("redirect:/restaurants/%d/edit", id));
    }

    @RequestMapping(value = "/restaurants/{id:\\d+}/categories/move", method = RequestMethod.POST)
    public ModelAndView editCategoryOrder(
            @PathVariable final int id,
            @Valid @ModelAttribute("editCategoryOrderForm") final EditCategoryOrderForm editCategoryOrderForm,
            final BindingResult errors
    ) {
        if (errors.hasErrors()) {
            throw new IllegalStateException();
        }

        categoryService.swapOrder(id, editCategoryOrderForm.getCurrentOrder(), editCategoryOrderForm.getNewOrder());

        return new ModelAndView(String.format("redirect:/restaurants/%d/edit", id));
    }

    @RequestMapping(value = "/restaurants/{id:\\d+}/categories/delete", method = RequestMethod.POST)
    public ModelAndView deleteCategoryForRestaurant(
            @PathVariable final int id,
            @Valid @ModelAttribute("deleteCategoryForm") final DeleteCategoryForm deleteCategoryForm,
            final BindingResult errors
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
            @Valid @ModelAttribute("addEmployeeForm") final AddEmployeeForm addEmployeeForm,
            final BindingResult errors,
            RedirectAttributes redirectAttributes
    ) {
        if (errors.hasErrors()) {
            return editRestaurant(
                    id,
                    new EditRestaurantForm(),
                    false,
                    new AddProductForm(),
                    false,
                    new EditProductForm(),
                    false,
                    new DeleteProductForm(),
                    new AddCategoryForm(),
                    false,
                    new EditCategoryForm(),
                    false,
                    new EditCategoryOrderForm(),
                    new DeleteCategoryForm(),
                    addEmployeeForm,
                    new MyBoolean(true),
                    new DeleteEmployeeForm()
            );
        }

        restaurantRoleService.setRole(addEmployeeForm.getEmail(), id, RestaurantRoleLevel.fromOrdinal(addEmployeeForm.getRole()));

        redirectAttributes.addFlashAttribute("addEmployeeErrors", new MyBoolean(true));
        return new ModelAndView(String.format("redirect:/restaurants/%d/edit", id));
    }

    @RequestMapping(value = "/restaurants/{id:\\d+}/employees/delete", method = RequestMethod.POST)
    public ModelAndView deleteEmployeeForRestaurant(
            @PathVariable final int id,
            @Valid @ModelAttribute("deleteEmployeeForm") final DeleteEmployeeForm deleteEmployeeForm,
            final BindingResult errors,
            RedirectAttributes redirectAttributes
    ) {
        if (errors.hasErrors()) {
            throw new IllegalStateException();
        }

        restaurantRoleService.deleteRole(deleteEmployeeForm.getUserId(), id);

        redirectAttributes.addFlashAttribute("addEmployeeErrors", new MyBoolean(true));
        return new ModelAndView(String.format("redirect:/restaurants/%d/edit", id));
    }

    @RequestMapping(value = "/user", method = RequestMethod.GET)
    public ModelAndView myProfile(
            @ModelAttribute("deleteAddressForm") final DeleteAddressForm deleteAddressForm,
            @ModelAttribute("addAddressForm") final AddAddressForm addAddressForm,
            @Valid final PagingForm paging,
            final BindingResult errors,
            final Boolean addAddressFormError
    ) {

        ModelAndView mav = new ModelAndView("user/myprofile");

        if (errors.hasErrors()) {
            mav.addObject("error", Boolean.TRUE);
            paging.clear();
        }

        PaginatedResult<Review> reviews = reviewService.getByUser(ControllerUtils.getCurrentUserIdOrThrow(), paging.getPageOrDefault(), paging.getSizeOrDefault(ControllerUtils.DEFAULT_ORDERS_PAGE_SIZE));

        mav.addObject("reviews", reviews.getResult());
        mav.addObject("reviewCount", reviews.getTotalCount());
        mav.addObject("pageCount", reviews.getTotalPageCount());

        mav.addObject("addAddressFormError", addAddressFormError);
        return mav;
    }

    @RequestMapping(value = "/user/addresses/delete", method = RequestMethod.POST)
    public ModelAndView deleteAddress(
            @Valid @ModelAttribute("deleteAddressForm") final DeleteAddressForm deleteAddressForm,
            final BindingResult errors
    ) {
        if (errors.hasErrors()) {
            throw new IllegalStateException("Something went wrong");
        }

        userService.deleteAddress(deleteAddressForm.getUserId(), deleteAddressForm.getAddress());

        return new ModelAndView("redirect:/user");
    }

    @RequestMapping(value = "/user/addresses/add", method = RequestMethod.POST)
    public ModelAndView addAddress(
            @Valid @ModelAttribute("addAddressForm") final AddAddressForm addAddressForm,
            final BindingResult errors
    ) {
        if (errors.hasErrors()) {
            PagingForm pagingForm = new PagingForm();
            return myProfile(
                    new DeleteAddressForm(),
                    addAddressForm,
                    pagingForm,
                    new BeanPropertyBindingResult(pagingForm, "pagingForm"),
                    true
            );
        }

        userService.registerAddress(
                addAddressForm.getUserId(),
                addAddressForm.getAddress(),
                addAddressForm.getName()
        );

        return new ModelAndView("redirect:/user");
    }
}
