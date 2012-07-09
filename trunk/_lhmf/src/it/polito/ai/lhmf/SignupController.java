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
public class SignupController
{
	@RequestMapping(value = "/openid_signup", method = RequestMethod.GET)
	public ModelAndView openIdSignupGet(Model model, HttpSession session)
	{
		OpenIDAuthenticationToken token = (OpenIDAuthenticationToken) session
				.getAttribute("OPENID_TOKEN");
		session.setAttribute("OPENID_USERID", token.getIdentityUrl());

		List<OpenIDAttribute> attributes = token.getAttributes();
		String firstname = null;
		String lastname = null;
		String email = null;
		String address = null;
		String city = null;
		String state = null;
		String country = null;
		String cap = null;
		String phone = null;

		// TODO implementare altri attributi (vedere
		// applicationContext-security.xml dove ci sono quelli richiesti)
		for (OpenIDAttribute attribute : attributes)
		{
			String value = attribute.getValues().get(0);
			if (value != null)
			{
				if (attribute.getName().equals("firstname")
						&& firstname == null)
				{
					firstname = value;
					model.addAttribute("firstname", firstname);
				}
				else if (attribute.getName().equals("lastname")
						&& lastname == null)
				{
					lastname = value;
					model.addAttribute("lastname", lastname);
				}
				else if (attribute.getName().equals("email") && email == null)
				{
					email = value;
					model.addAttribute("email", email);
				}
				else if (attribute.getName().equals("address")
						&& address == null)
				{
					address = value;
					model.addAttribute("address", address);
				}
				else if (attribute.getName().equals("city") && city == null)
				{
					city = value;
					model.addAttribute("city", city);
				}
				else if (attribute.getName().equals("state") && state == null)
				{
					state = value;
					model.addAttribute("state", state);
				}
				else if (attribute.getName().equals("country")
						&& country == null)
				{
					country = value;
					model.addAttribute("country", country);
				}
				else if (attribute.getName().equals("cap") && cap == null)
				{
					cap = value;
					model.addAttribute("cap", cap);
				}
				else if (attribute.getName().equals("phone") && phone == null)
				{
					phone = value;
					model.addAttribute("phone", phone);
				}
			}
		}
		model.addAttribute("actionUrl", "/openid_signup");
		return new ModelAndView("signup");
	}

	@RequestMapping(value = "/openid_signup", method = RequestMethod.POST)
	public ModelAndView openIdSignupPost(
			@RequestParam(value = "firstname", required = true) String firstname,
			@RequestParam(value = "lastname", required = true) String lastname,
			@RequestParam(value = "email", required = true) String email,
			@RequestParam(value = "address", required = true) String address,
			@RequestParam(value = "city", required = true) String city,
			@RequestParam(value = "state", required = true) String state,
			@RequestParam(value = "country", required = true) String country,
			@RequestParam(value = "cap", required = true) String cap,
			@RequestParam(value = "phone", required = true) String phone)
	{
		// TODO registrazione, settaggio attributi e reindirizzamento alla view
		// apposita
		return new ModelAndView("/");
	}
}
