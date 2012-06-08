package it.polito.ai.lhmf;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.*;

@Controller
public class HelloSpringController
{
	@RequestMapping("/hello")
	public ModelAndView helloSpring(Model model)
	{
		String message = "HelloSpring";
		model.addAttribute("message", message);
		return new ModelAndView("hello");
	}
}
