package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.model.Restaurant;
import ar.edu.itba.paw.service.RestaurantService;
import ar.edu.itba.paw.webapp.form.SearchForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;
import java.util.List;


@Controller
public class HomeController {

    @Autowired
    private RestaurantService restaurantService;

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public ModelAndView index() {
        ModelAndView mav = new ModelAndView("home/index");

        final int maxRestaurants = 4;
        final List<Restaurant> restaurants = restaurantService.getActive(1, maxRestaurants);
        mav.addObject("restaurants", restaurants);

        return mav;
    }

    @RequestMapping(value = "/restaurants", method = RequestMethod.GET)
    public ModelAndView restaurants(
            @Valid @ModelAttribute("searchForm") final SearchForm form,
            final BindingResult errors
    ) {
        ModelAndView mav = new ModelAndView("home/restaurants");

        if (errors.hasErrors()) {
            mav.addObject("error", Boolean.TRUE);
            form.setSearch(null);
            form.setPage(1);
            form.setSize(12);
        }

        final List<Restaurant> restaurants = restaurantService.getSearchResults(form.getSearch(), form.getPageOrDefault(), form.getSizeOrDefault());
        mav.addObject("restaurantCount", restaurantService.getSearchResultsCount(form.getSearch()));
        mav.addObject("restaurants", restaurants);

        return mav;
    }

    // TODO: Remove or move to a separate controller
    @RequestMapping(value = "/error", method = RequestMethod.GET)
    public ModelAndView errorView() {
        return new ModelAndView("errors/error");
    }

    @RequestMapping("/restaurants/order")
    public ModelAndView restaurantOrder() {
        return new ModelAndView("menu/restaurant_orders");
    }
}
