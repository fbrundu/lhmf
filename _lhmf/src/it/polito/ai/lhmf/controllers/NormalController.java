package it.polito.ai.lhmf.controllers;

import it.polito.ai.lhmf.security.MyUserDetailsService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class NormalController {
	
	@RequestMapping("/purchase")
	@PreAuthorize("hasRole('" + MyUserDetailsService.UserRoles.NORMAL + "')")
	public ModelAndView purchasePage(Model model, HttpServletRequest request, HttpServletResponse response)
	{
		model.addAttribute("user", request.getSession().getAttribute("user"));

		return new ModelAndView("purchase_normal");
	}
	
	@RequestMapping("/statNormal")
	@PreAuthorize("hasRole('" + MyUserDetailsService.UserRoles.NORMAL + "')")
	public ModelAndView statPageNormal(Model model, HttpServletRequest request, HttpServletResponse response)
	{
		model.addAttribute("user", request.getSession().getAttribute("user"));

		return new ModelAndView("statistics_normal");
	}

}
