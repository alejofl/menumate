package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.model.Category;
import ar.edu.itba.paw.model.Product;
import ar.edu.itba.paw.model.Restaurant;
import ar.edu.itba.paw.service.*;
import ar.edu.itba.paw.webapp.exception.RestaurantNotFoundException;
import ar.edu.itba.paw.webapp.form.CheckoutForm;
import javafx.util.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;
import java.util.List;

@Controller
public class RestaurantsController {
    @Autowired
    private RestaurantService restaurantService;
    @Autowired
    private OrderService orderService;

    @RequestMapping(value = "/restaurants/{id:\\d+}", method = RequestMethod.GET)
    public ModelAndView restaurantMenu(@PathVariable final long id, @ModelAttribute("checkoutForm") final CheckoutForm form) {
        final ModelAndView mav = new ModelAndView("menu/restaurant_menu");

        final Restaurant restaurant = restaurantService.getById(id).orElseThrow(RestaurantNotFoundException::new);
        mav.addObject("restaurant", restaurant);

        final List<Pair<Category, List<Product>>> menu = restaurantService.getMenu(id);
        mav.addObject("menu", menu);

        return mav;
    }

    @RequestMapping(value = "/restaurants/{id:\\d+}", method = RequestMethod.POST)
    public ModelAndView restaurantMenu(
        @PathVariable final long id,
        @Valid @ModelAttribute("checkoutForm") final CheckoutForm form,
        final BindingResult errors
    ) {
        if (errors.hasErrors()) {
            return restaurantMenu(id, form);
        }

//        orderService.create()

        return new ModelAndView("redirect:/");
    }
}
