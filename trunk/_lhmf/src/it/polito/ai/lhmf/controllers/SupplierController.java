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
public class SupplierController
{
	@RequestMapping("/productsMgmt")
	@PreAuthorize("hasRole('" + MyUserDetailsService.UserRoles.SUPPLIER + "')")
	public ModelAndView productsPage(Model model, HttpServletRequest request,
			HttpServletResponse response)
	{
		// TODO: bisogna aggiungere i parametri e reinserirli nel model.
		model.addAttribute("user", request.getSession().getAttribute("user"));

		//model.addAttribute("firstPage", "products");

		return new ModelAndView("productsMgmt_supplier");
	}

	@RequestMapping("/orderSup")
	@PreAuthorize("hasRole('" + MyUserDetailsService.UserRoles.SUPPLIER + "')")
	public ModelAndView orderPage(Model model, HttpServletRequest request,
			HttpServletResponse response)
	{
		// TODO: bisogna aggiungere i parametri e reinserirli nel model.
		model.addAttribute("user", request.getSession().getAttribute("user"));

		//model.addAttribute("firstPage", "products");

		return new ModelAndView("order_supplier");
	}

}
