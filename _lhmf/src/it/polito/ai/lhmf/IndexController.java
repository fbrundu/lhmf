package it.polito.ai.lhmf;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class IndexController
{
	@RequestMapping("/")
	public ModelAndView index(Model model, HttpSession session,
			HttpServletRequest request)
	{
		String user = SecurityContextHolder.getContext().getAuthentication().getName();
		model.addAttribute("user", user);
		
//		switch ((Integer) session.getAttribute("member_type"))
//		{
		// FIXME: use enum instead of numeric values for member_type
//		case 0:
//			return new ModelAndView("index_normal");
//		case 1:
//			return new ModelAndView("index_resp");
//		case 2:
			return new ModelAndView("index_supplier");
//		case 3:
//			return new ModelAndView("index_admin");
//		default:
//			return new ModelAndView("error");
//		}
	}
}