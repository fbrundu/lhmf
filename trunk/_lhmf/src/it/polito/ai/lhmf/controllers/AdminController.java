package it.polito.ai.lhmf.controllers;

import java.io.IOException;
import java.io.PrintWriter;

import it.polito.ai.lhmf.security.MyUserDetailsService;
import it.polito.ai.lhmf.util.ModelState;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class AdminController
{
	@Autowired
	private ModelState modelState;

	@RequestMapping("/log")
	@PreAuthorize("hasRole('" + MyUserDetailsService.UserRoles.ADMIN + "')")
	public ModelAndView logPage(Model model, HttpServletRequest request,
			HttpServletResponse response,
			@RequestParam(value = "min", required = false) Long minTs,
			@RequestParam(value = "max", required = false) Long maxTs)
	{
		model.addAttribute("user", request.getSession().getAttribute("user"));

		model.addAttribute("firstPage", "log");
		if (minTs != null && maxTs != null)
		{
			model.addAttribute("minLog", minTs);
			model.addAttribute("maxLog", maxTs);
		}
		return new ModelAndView("log_admin");
	}

	@RequestMapping("/userMgmt")
	@PreAuthorize("hasRole('" + MyUserDetailsService.UserRoles.ADMIN + "')")
	public ModelAndView userPage(Model model, HttpServletRequest request,
			HttpServletResponse response)
	{
		model.addAttribute("user", request.getSession().getAttribute("user"));

		model.addAttribute("firstPage", "user");

		return new ModelAndView("userMgmt_admin");
	}

	@RequestMapping("/productsMgmtAdmin")
	@PreAuthorize("hasRole('" + MyUserDetailsService.UserRoles.ADMIN + "')")
	public ModelAndView productPage(Model model, HttpServletRequest request,
			HttpServletResponse response)
	{
		model.addAttribute("user", request.getSession().getAttribute("user"));

		model.addAttribute("firstPage", "user");

		return new ModelAndView("productsMgmt_admin");
	}

	@RequestMapping("/productsUpdatesAdmin")
	@PreAuthorize("hasRole('" + MyUserDetailsService.UserRoles.ADMIN + "')")
	public void productsUpdatesAdmin(Model model, HttpServletRequest request,
			HttpServletResponse response)
	{
		response.setContentType("text/event-stream; charset=utf-8");
		response.setHeader("cache-control", "no-cache");
		response.setHeader("connection", "keep-alive");
		PrintWriter pw;
		try
		{
			pw = response.getWriter();
			// se ci sono nuovi prodotti scrive una notifica
			if (modelState.isToReloadProducts())
			{
				pw.write("retry: 10000\n");
				pw.write("data: products updated\n\n");
				pw.flush();
				System.out.println("Products updated sent");
				modelState.setToReloadProducts(false);
			}
			pw.close();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
	
	@RequestMapping("/statAdmin")
	@PreAuthorize("hasRole('" + MyUserDetailsService.UserRoles.ADMIN + "')")
	public ModelAndView statPage(Model model, HttpServletRequest request,
			HttpServletResponse response)
	{
		model.addAttribute("user", request.getSession().getAttribute("user"));

		model.addAttribute("firstPage", "user");

		return new ModelAndView("statistics_admin");
	}

}
