package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.model.UserRole;
import ar.edu.itba.paw.model.UserRoleLevel;
import ar.edu.itba.paw.service.UserRoleService;
import ar.edu.itba.paw.webapp.form.AddModeratorForm;
import ar.edu.itba.paw.webapp.form.PagingForm;
import ar.edu.itba.paw.webapp.form.RemoveModeratorForm;
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
public class ModeratorController {

    @Autowired
    private UserRoleService userRoleService;

    @RequestMapping(value = "moderators", method = RequestMethod.GET)
    public ModelAndView moderators(
            @ModelAttribute("addModeratorForm") final AddModeratorForm addModeratorForm
    ) {
        ModelAndView mav = new ModelAndView("user/moderators");

        List<UserRole> roles = userRoleService.getByRole(UserRoleLevel.MODERATOR);

        mav.addObject("userRoles", roles);
        mav.addObject("userRoleCount", roles.size());

        return mav;
    }

    @RequestMapping(value = "moderators/add", method = RequestMethod.POST)
    public ModelAndView add(
            @Valid final AddModeratorForm form,
            final BindingResult errors
    ) {
        if (errors.hasErrors()) {
            PagingForm pagingForm = new PagingForm();
            return moderators(form);
        }

        userRoleService.setRole(form.getEmail(), UserRoleLevel.MODERATOR);
        return new ModelAndView("redirect:/moderators");
    }

    @RequestMapping(value = "moderators/remove", method = RequestMethod.POST)
    public ModelAndView remove(
            @Valid final RemoveModeratorForm form,
            final BindingResult errors
    ) {
        if (errors.hasErrors()) {
            // TODO: Forward to site where the form is at
            throw new IllegalStateException("Invalid form");
        }

        userRoleService.deleteRole(form.getUserId());
        return new ModelAndView("redirect:/moderators");
    }
}
