package it.polito.ai.lhmf.controllers;

import it.polito.ai.lhmf.model.constants.MemberTypes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
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
	
	@RequestMapping("/notifiche")
	public ModelAndView notifiesPage(Model model, HttpSession session, 
			HttpServletRequest request)
	{
		// TODO: bisogna aggiungere i parametri e reinserirli nel model.
		model.addAttribute("user", request.getSession().getAttribute("user"));

		switch ((Integer) session.getAttribute("member_type"))
		{
		case MemberTypes.USER_NORMAL:
			return new ModelAndView("notifies_normal");
		case MemberTypes.USER_RESP:
			return new ModelAndView("notifies_resp");
		case MemberTypes.USER_SUPPLIER:
			return new ModelAndView("notifies_supplier");
		case MemberTypes.USER_ADMIN:
			return new ModelAndView("notifies_admin");
		default:
			return new ModelAndView("error");
		}
		
		//return new ModelAndView("notifies");
	}
	
	@RequestMapping("/messaggi")
	public ModelAndView messagesPage(Model model, HttpSession session, 
			HttpServletRequest request)
	{
		// TODO: bisogna aggiungere i parametri e reinserirli nel model.
		model.addAttribute("user", request.getSession().getAttribute("user"));
		
		switch ((Integer) session.getAttribute("member_type"))
		{
		case MemberTypes.USER_NORMAL:
			return new ModelAndView("messages_normal");
		case MemberTypes.USER_RESP:
			return new ModelAndView("messages_resp");
		case MemberTypes.USER_SUPPLIER:
			return new ModelAndView("messages_supplier");
		case MemberTypes.USER_ADMIN:
			return new ModelAndView("messages_admin");
		default:
			return new ModelAndView("error");
		}

		//return new ModelAndView("messages");
	}
}
