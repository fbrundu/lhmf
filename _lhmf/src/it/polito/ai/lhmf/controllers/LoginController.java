package it.polito.ai.lhmf.controllers;

import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.*;

@Controller
public class LoginController
{
	@RequestMapping("/login")
	public ModelAndView login(Model model, HttpSession session)
	{
		return new ModelAndView("login");
	}
	
	@RequestMapping("/mobile/login")
	public ModelAndView mobileLogin(Model model, HttpSession session)
	{
		model.addAttribute("client", "mobile_browser");
		return new ModelAndView("mobile/login");
	}
	
	@RequestMapping("/android/login")
	public ModelAndView androidLogin(Model model, HttpSession session)
	{
		model.addAttribute("client", "android");
		return new ModelAndView("mobile/login");
	}
}
