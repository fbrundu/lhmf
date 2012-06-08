package it.polito.ai.lhmf;

import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.*;

@Controller
public class IndexController
{
	@RequestMapping("/")
	public ModelAndView helloSpring(Model model, HttpSession session)
	{
		// TODO : test if user is logged in, otherwise
		// redirect to login; if user is logged in test role
		// and return its view (i.e. for role "supplier" return
		// ModelAndView("supplier")
		if (session.getAttribute("username") != null)
			return new ModelAndView("index");
		else
			return new ModelAndView("login");
	}
}
