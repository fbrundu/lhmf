package it.polito.ai.lhmf.controllers;

import java.io.IOException;
import java.io.PrintWriter;

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
public class AdminController
{

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
		// TODO: bisogna aggiungere i parametri e reinserirli nel model.
		model.addAttribute("user", request.getSession().getAttribute("user"));

		model.addAttribute("firstPage", "user");

		return new ModelAndView("userMgmt_admin");
	}

	@RequestMapping("/productsMgmtAdmin")
	@PreAuthorize("hasRole('" + MyUserDetailsService.UserRoles.ADMIN + "')")
	public ModelAndView productPage(Model model, HttpServletRequest request,
			HttpServletResponse response)
	{
		// TODO: bisogna aggiungere i parametri e reinserirli nel model.
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
		response.setHeader("connection" , "keep-alive");
		PrintWriter pw;
		try
		{
			pw = response.getWriter();
			int i = 0;
			while (true)
			{

				i++;
				pw.write("event: server-time\n\n"); // take note of the 2 \n 's,
													// also on the next line.
				pw.write("data: " + i + "\n\n");
				pw.flush();
				System.out.println("Data Sent!!!" + i);
				if (i > 10)
					break;
				Thread.sleep(2000);
			}
			pw.close();
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (InterruptedException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}
