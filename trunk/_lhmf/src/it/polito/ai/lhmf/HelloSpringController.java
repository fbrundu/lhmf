package it.polito.ai.lhmf;

import javax.servlet.http.HttpSession;

import org.apache.catalina.Session;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.*;

@Controller
public class HelloSpringController
{
	@RequestMapping("/hello")
	public ModelAndView helloSpring(Model model, HttpSession s)
	{
		String message = "HelloSpring";
		model.addAttribute("message", message);
		return new ModelAndView("hello");
	}
}
