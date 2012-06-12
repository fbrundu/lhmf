package it.polito.ai.lhmf;

import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.*;

@Controller
public class LoginFormSubmittedController {
	
	@RequestMapping(value="/loginformsubmitted", method = RequestMethod.POST)
	
	public ModelAndView loginSpring(HttpSession session,
									@RequestParam(value = "username") String username,
									@RequestParam(value = "password") String password )
	{
		// TODO : test if user is logged in, otherwise
		// redirect to login; if user is logged in test role
		// and return its view (i.e. for role "supplier" return
		// ModelAndView("supplier")
		
		if(username.equals("") || password.equals(""))
		{
			
			return new ModelAndView("error");
		}
		
		
		
		
		
		return new ModelAndView("error");
			
	}

}
