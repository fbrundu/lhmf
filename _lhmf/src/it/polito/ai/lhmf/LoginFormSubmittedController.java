package it.polito.ai.lhmf;

import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.*;

@Controller
public class LoginFormSubmittedController
{
	@RequestMapping(value = "/loginformsubmitted", method = RequestMethod.POST)
	public ModelAndView loginFormSubmitted(HttpSession session,
			@RequestParam(value = "username") String username,
			@RequestParam(value = "password") String password)
	{
		// tests if username and password are correct
		if (username.equals("") || password.equals(""))
		{
			return new ModelAndView("error");
		}

		return new ModelAndView("error");
	}
}
