package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.model.Restaurant;
import ar.edu.itba.paw.model.util.PaginatedResult;
import ar.edu.itba.paw.service.RestaurantService;
import ar.edu.itba.paw.service.ReviewService;
import ar.edu.itba.paw.webapp.form.SearchForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;


@Controller
public class HomeController {
    @Autowired
    private RestaurantService restaurantService;

    @Autowired
    private ReviewService reviewService;

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public ModelAndView index(
            @ModelAttribute("searchForm") final SearchForm form
    ) {
        ModelAndView mav = new ModelAndView("home/index");

        final int maxRestaurants = 4;
        final PaginatedResult<Restaurant> results = restaurantService.getActive(1, maxRestaurants);
        mav.addObject("restaurants", restaurantService.getAverageRatingForRestaurants(results.getResult()));

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
            form.clear();
        }

        final PaginatedResult<Restaurant> results = restaurantService.getSearchResults(form.getSearch(), form.getPageOrDefault(), form.getSizeOrDefault(ControllerUtils.DEFAULT_SEARCH_PAGE_SIZE));
        mav.addObject("restaurants", restaurantService.getAverageRatingForRestaurants(results.getResult()));
        mav.addObject("restaurantCount", results.getTotalCount());
        mav.addObject("pageCount", results.getTotalPageCount());

        return mav;
    }
}
