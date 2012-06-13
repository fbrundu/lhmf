package it.polito.ai.lhmf;

import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.*;

@Controller
public class IndexController
{
	@RequestMapping(value={"/","/login"})
	public ModelAndView index(Model model, HttpSession session)
	{
		// if user is not logged in, redirect to view 'login'
		if (session.getAttribute("member_type") == null)
			return new ModelAndView("login");
		else {
			// gets member type
			int member_type = (Integer) session.getAttribute("member_type");
			// FIXME: è una prova?
			session.setAttribute("lol", member_type);
			// redirects to appropriate view or error
			switch(member_type) {
				case 0:
					return new ModelAndView("index_normal");
				case 1:
					return new ModelAndView("index_resp");
				case 2:
					return new ModelAndView("index_supplier");
				case 3:
					return new ModelAndView("index_admin");
				default:
					return new ModelAndView("error");
			}
		}
	}
}
