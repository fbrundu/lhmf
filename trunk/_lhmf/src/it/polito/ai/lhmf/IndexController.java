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
	public ModelAndView helloSpring(Model model, HttpSession session)
	{
		// TODO : test if user is logged in, otherwise
		// redirect to login; if user is logged in test role
		// and return its view (i.e. for role "supplier" return
		// ModelAndView("supplier")
		if (session.getAttribute("member_type") == null)
			return new ModelAndView("login");
		else {
			int member_type = (Integer) session.getAttribute("member_type");
			session.setAttribute("lol", member_type);
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
