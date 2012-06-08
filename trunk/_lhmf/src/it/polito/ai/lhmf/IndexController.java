package it.polito.ai.lhmf;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.*;

@Controller
public class IndexController
{
	@RequestMapping("/")
	public ModelAndView helloSpring(Model model)
	{
		// TODO : test if user is logged in, otherwise
		// redirect to login
		return new ModelAndView("index");
	}
}
