package it.polito.ai.lhmf;

import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.security.openid.OpenIDAttribute;
import org.springframework.security.openid.OpenIDAuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class SignupController {
	
	@RequestMapping(value = "/openid_signup", method = RequestMethod.GET)
	public ModelAndView openIdSignupGet(Model model, HttpSession session){
		OpenIDAuthenticationToken token = (OpenIDAuthenticationToken) session.getAttribute("OPENID_TOKEN");
		session.setAttribute("OPENID_USERID", token.getIdentityUrl());
		
		List<OpenIDAttribute> attributes = token.getAttributes();
		String email = null;
		String firstname = null;
		String lastname = null;
		
		//TODO implementare altri attributti (vedere applicationContext-security.xml dove ci sono quelli richiesti)
		for (OpenIDAttribute attribute : attributes) {
			String value = attribute.getValues().get(0);
	    	if(value != null){
			    if (attribute.getName().equals("email") && email == null) {
				    email = value;
			    	model.addAttribute("email", email);
				}
				
			    else if (attribute.getName().equals("firstname") && firstname == null) {
			    	firstname = value;
			    	model.addAttribute("firstname", firstname);
				}
				
			    else if (attribute.getName().equals("lastname") && lastname == null) {
			    	lastname = value;
			    	model.addAttribute("lastname", lastname);
				}
	    	}
		}
		model.addAttribute("actionUrl", "/openid_signup");
		return new ModelAndView("signup");
	}
	
	@RequestMapping(value ="/openid_signup", method = RequestMethod.POST)
	public ModelAndView openIdSignupPost( @RequestParam(value="firstname", required=true) String firstname,
			@RequestParam(value="lastname", required=true) String lastname, 
			@RequestParam(value="email", required=true)String email){
		return null;
	}
}
