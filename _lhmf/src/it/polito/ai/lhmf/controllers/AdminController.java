package it.polito.ai.lhmf.controllers;

import it.polito.ai.lhmf.security.MyUserDetailsService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class AdminController {
	
	@RequestMapping("/log")
	@PreAuthorize("hasRole('" + MyUserDetailsService.UserRoles.ADMIN + "')")
	public ModelAndView logPage(Model model, HttpServletRequest request, HttpServletResponse response,
			@RequestParam(value="min", required=false) Long minTs, @RequestParam(value="max", required=false) Long maxTs)
	{
		model.addAttribute("user", request.getSession().getAttribute("user"));
		
		model.addAttribute("firstPage", "log");
		if(minTs != null && maxTs != null){
			model.addAttribute("minLog", minTs);
			model.addAttribute("maxLog", maxTs);
		}
		return new ModelAndView("log_admin");
	}
	
	@RequestMapping("/userMgmt")
	@PreAuthorize("hasRole('" + MyUserDetailsService.UserRoles.ADMIN + "')")
	public ModelAndView userPage(Model model, HttpServletRequest request, HttpServletResponse response)
	{
		//TODO: bisogna aggiungere i parametri e reinserirli nel model.
		model.addAttribute("user", request.getSession().getAttribute("user"));
		
		model.addAttribute("firstPage", "user");
		
		return new ModelAndView("userMgmt_admin");
	}
	
	@RequestMapping("/prodMgmt")
	@PreAuthorize("hasRole('" + MyUserDetailsService.UserRoles.ADMIN + "')")
	public ModelAndView productPage(Model model, HttpServletRequest request, HttpServletResponse response)
	{
		//TODO: bisogna aggiungere i parametri e reinserirli nel model.
		model.addAttribute("user", request.getSession().getAttribute("user"));
		
		model.addAttribute("firstPage", "user");
		
		return new ModelAndView("productsMgmt_admin");
	}
}
