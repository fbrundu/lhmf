package it.polito.ai.lhmf;

import it.polito.ai.lhmf.model.constants.MemberTypes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

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
		model.addAttribute("user", session.getAttribute("user"));
		
		switch ((Integer) session.getAttribute("member_type"))
		{
		case MemberTypes.USER_NORMAL:
			return new ModelAndView("index_normal");
		case MemberTypes.USER_RESP:
			return new ModelAndView("index_resp");
		case MemberTypes.USER_SUPPLIER:
			return new ModelAndView("index_supplier");
		case MemberTypes.USER_ADMIN:
			return new ModelAndView("index_admin");
		default:
			return new ModelAndView("error");
		}
	}
}
