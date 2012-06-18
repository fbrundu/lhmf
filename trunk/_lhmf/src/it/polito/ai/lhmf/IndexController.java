package it.polito.ai.lhmf;

import java.util.List;

import it.polito.ai.lhmf.exceptions.*;
import it.polito.ai.lhmf.model.*;
import it.polito.ai.lhmf.orm.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.*;

@Controller
public class IndexController
{
	@RequestMapping("/")
	public ModelAndView index(Model model, HttpSession session,
			HttpServletRequest request)
	{
		// redirects to appropriate view or error
		switch ((Integer) session.getAttribute("member_type"))
		{
		// FIXME: use enum instead of numeric values for member_type
		case 0:
			return new ModelAndView("index_normal");
		case 1:
			return new ModelAndView("index_resp");
		case 2:
			try
			{
				List<Product> productList = HibernateInterface
						.getProducts((org.hibernate.Session) request
								.getAttribute("hibernate_session"));
				model.addAttribute("productList", productList);
			}
			catch (NoHibernateSessionException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return new ModelAndView("index_supplier");
		case 3:
			return new ModelAndView("index_admin");
		default:
			return new ModelAndView("error");
		}
	}
}
