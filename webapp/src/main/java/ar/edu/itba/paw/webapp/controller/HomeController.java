package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.RestaurantDetails;
import ar.edu.itba.paw.RestaurantOrderBy;
import ar.edu.itba.paw.model.Restaurant;
import ar.edu.itba.paw.model.RestaurantSpecialty;
import ar.edu.itba.paw.model.RestaurantTags;
import ar.edu.itba.paw.model.util.PaginatedResult;
import ar.edu.itba.paw.service.RestaurantService;
import ar.edu.itba.paw.service.ReviewService;
import ar.edu.itba.paw.webapp.form.FilterForm;
import ar.edu.itba.paw.webapp.form.SearchForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.method.P;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


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
        final PaginatedResult<RestaurantDetails> results = restaurantService.search(null, 1, maxRestaurants, RestaurantOrderBy.RATING, true, null, null);
        mav.addObject("restaurants", results.getResult());

        return mav;
    }

    @RequestMapping(value = "/restaurants", method = RequestMethod.GET)
    public ModelAndView restaurants(
            @Valid @ModelAttribute("searchForm") final FilterForm form,
            final BindingResult errors
    ) {
        ModelAndView mav = new ModelAndView("home/restaurants");

        if (errors.hasErrors()) {
            mav.addObject("error", Boolean.TRUE);
            form.clear();
        }

        mav.addObject("specialties", RestaurantSpecialty.values());
        mav.addObject("tags", RestaurantTags.values());
        mav.addObject("order_by", RestaurantOrderBy.values());

        List<RestaurantTags> tags = null;
        if (form.getTags() != null) {
            tags = form.getTags().stream().map(RestaurantTags::fromOrdinal).collect(Collectors.toList());
        }
        List<RestaurantSpecialty> specialties = null;
        if (form.getSpecialties() != null) {
            specialties = form.getSpecialties().stream().map(RestaurantSpecialty::fromOrdinal).collect(Collectors.toList());
        }

        final PaginatedResult<RestaurantDetails> results = restaurantService.search(
                form.getSearch(),
                form.getPageOrDefault(),
                form.getSizeOrDefault(ControllerUtils.DEFAULT_SEARCH_PAGE_SIZE),
                RestaurantOrderBy.fromOrdinal(form.getOrderByOrDefault()),
                form.getDescendingOrDefault(),
                tags,
                specialties
        );
        mav.addObject("restaurants", results.getResult());
        mav.addObject("restaurantCount", results.getTotalCount());
        mav.addObject("pageCount", results.getTotalPageCount());

        return mav;
    }
}
