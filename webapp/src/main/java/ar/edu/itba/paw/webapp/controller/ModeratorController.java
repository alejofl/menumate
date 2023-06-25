package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.model.UserRole;
import ar.edu.itba.paw.model.Restaurant;
import ar.edu.itba.paw.model.UserRoleLevel;
import ar.edu.itba.paw.service.ReportService;
import ar.edu.itba.paw.service.UserRoleService;
import ar.edu.itba.paw.util.PaginatedResult;
import ar.edu.itba.paw.util.Pair;
import ar.edu.itba.paw.webapp.form.AddModeratorForm;
import ar.edu.itba.paw.webapp.form.PagingForm;
import ar.edu.itba.paw.webapp.form.DeleteModeratorForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;
import java.util.List;

@Controller
public class ModeratorController {

    @Autowired
    private UserRoleService userRoleService;

    @Autowired
    private ReportService reportService;

    @RequestMapping(value = "/moderators", method = RequestMethod.GET)
    public ModelAndView moderators(
            @ModelAttribute("addModeratorForm") final AddModeratorForm addModeratorForm,
            final Boolean addModeratorFormErrors,
            @ModelAttribute("deleteModeratorForm") final DeleteModeratorForm deleteModeratorForm,
            @Valid final PagingForm paging,
            final BindingResult errors
    ) {
        ModelAndView mav = new ModelAndView("moderator/dashboard");

        if (errors.hasErrors()) {
            mav.addObject("error", Boolean.TRUE);
            paging.clear();
        }

        List<UserRole> roles = userRoleService.getByRole(UserRoleLevel.MODERATOR);
        mav.addObject("userRoles", roles);

        PaginatedResult<Pair<Restaurant, Integer>> restaurantsWithReports = reportService.getCountByRestaurant(paging.getPageOrDefault(), paging.getSizeOrDefault(ControllerUtils.DEFAULT_MYRESTAURANTS_PAGE_SIZE));

        mav.addObject("addModeratorFormErrors", addModeratorFormErrors);

        mav.addObject("restaurants", restaurantsWithReports.getResult());
        mav.addObject("restaurantCount", restaurantsWithReports.getTotalCount());
        mav.addObject("pageCount", restaurantsWithReports.getTotalPageCount());

        return mav;
    }

    @RequestMapping(value = "moderators/add", method = RequestMethod.POST)
    public ModelAndView add(
            @Valid final AddModeratorForm form,
            final BindingResult errors
    ) {
        if (errors.hasErrors()) {
            PagingForm pagingForm = new PagingForm();
            return moderators(
                    form,
                    true,
                    new DeleteModeratorForm(),
                    pagingForm,
                    new BeanPropertyBindingResult(pagingForm, "pagingForm")
            );
        }

        userRoleService.setRole(form.getEmail(), UserRoleLevel.MODERATOR);
        return new ModelAndView("redirect:/moderators");
    }

    @RequestMapping(value = "/moderators/delete", method = RequestMethod.POST)
    public ModelAndView delete(
            @Valid final DeleteModeratorForm form,
            final BindingResult errors
    ) {
        if (errors.hasErrors()) {
            throw new IllegalStateException("Invalid form");
        }

        userRoleService.deleteRole(form.getUserId());
        return new ModelAndView("redirect:/moderators");
    }
}
