package it.polito.ai.lhmf.controllers;

import it.polito.ai.lhmf.security.FacebookAuthenticationFilter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.servlet.http.HttpSession;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.node.ObjectNode;
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
		String cap = null;
		String phone = null;

		// TODO implementare altri attributi (vedere
		// applicationContext-security.xml dove ci sono quelli richiesti)
		for (OpenIDAttribute attribute : attributes)
		{
			String value = attribute.getValues().get(0);
			if (value != null)
			{
				if (attribute.getName().equals("firstname")	&& firstname == null)
				{
					firstname = value;
					model.addAttribute("firstname", firstname);
				}
				else if (attribute.getName().equals("lastname")	&& lastname == null)
				{
					lastname = value;
					model.addAttribute("lastname", lastname);
				}
				else if (attribute.getName().equals("email") && email == null)
				{
					email = value;
					model.addAttribute("email", email);
				}
				else if (attribute.getName().equals("address") && address == null)
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
		model.addAttribute("fromOpenID", true);
		return new ModelAndView("signup");
	}

	@RequestMapping(value = "/openid_signup", method = RequestMethod.POST)
	public ModelAndView openIdSignupPost( Model model,
			@RequestParam(value = "firstname", required = true) String firstname,
			@RequestParam(value = "lastname", required = true) String lastname,
			@RequestParam(value = "email", required = true) String email,
			@RequestParam(value = "address", required = true) String address,
			@RequestParam(value = "city", required = true) String city,
			@RequestParam(value = "state", required = true) String state,
			//@RequestParam(value = "country", required = true) String country,
			@RequestParam(value = "cap", required = true) String cap,
			@RequestParam(value = "phone", required = false, defaultValue = "not set") String phone)
	{
		// TODO registrazione, settaggio attributi e reindirizzamento alla view
		// apposita
		
		ArrayList<Map<String, String>> errors = new ArrayList<Map<String, String>>();
		
		model.addAttribute("firstname", firstname);
		model.addAttribute("lastname", lastname);
		model.addAttribute("email", email);
		model.addAttribute("address", address);
		model.addAttribute("city", city);
		model.addAttribute("state", state);
		model.addAttribute("cap", cap);
		model.addAttribute("phone", phone);
		
		if(firstname.equals("") || isNumeric(firstname)) {
			
			Map<String, String> error = new HashMap<String, String>();
			error.put("id", "Nome");
			error.put("error", "Formato non Valido");
			errors.add(error);
		}
		if(lastname.equals("") || isNumeric(lastname)) {
			
			Map<String, String> error = new HashMap<String, String>();
			error.put("id", "Cognome");
			error.put("error", "Formato non Valido");
			errors.add(error);
		}
		if(!isValidEmailAddress(email)) {
			
			Map<String, String> error = new HashMap<String, String>();
			error.put("id", "Email");
			error.put("error", "Formato non Valido");
			errors.add(error);
		}
		if(address.equals("") || isNumeric(address)) {
			
			Map<String, String> error = new HashMap<String, String>();
			error.put("id", "Indirizzo");
			error.put("error", "Formato non Valido");
			errors.add(error);
		}
		if(city.equals("") || isNumeric(city)) {
			
			Map<String, String> error = new HashMap<String, String>();
			error.put("id", "Citt�");
			error.put("error", "Formato non Valido");
			errors.add(error);
		}	
		if(state.equals("") || isNumeric(state)) {
			
			Map<String, String> error = new HashMap<String, String>();
			error.put("id", "Stato");
			error.put("error", "Formato non Valido");
			errors.add(error);
		}
		if(cap.equals("") || !isNumeric(cap)) {
			
			Map<String, String> error = new HashMap<String, String>();
			error.put("id", "Cap");
			error.put("error", "Formato non Valido");
			errors.add(error);
		}
		if(!phone.equals("not set")) {
			
			if(!isNumeric(phone)) {
				
				Map<String, String> error = new HashMap<String, String>();
				error.put("id", "Telefono");
				error.put("error", "Formato non Valido");
				errors.add(error);
			}
		}
	
		if(errors.size() > 0) {
		
			// Ci sono errori, rimandare alla pagina mostrandoli
			model.addAttribute("errors", errors);
			model.addAttribute("fromOpenID", false);
			model.addAttribute("actionUrl", "/openid_signup");
			return new ModelAndView("signup");
			
		}
		else
		{
			//eseguire la registrazione e mandare alla pagina principale
		}
		return new ModelAndView("/");
	}
	
	@RequestMapping(value = "/facebook_signup", method = RequestMethod.GET)
	public ModelAndView facebookSignupGet(Model model, HttpSession session){
		ObjectNode values = (ObjectNode) session.getAttribute("FACEBOOK_VALUES");
		JsonNode idNode = values.get("id");
		JsonNode nameNode = values.get("first_name");
		JsonNode surnameNode = values.get("last_name");
		JsonNode emailNode = values.get("email");
		
		session.setAttribute("FACEBOOK_USERID", FacebookAuthenticationFilter.FACEBOOK_USERID_PREFIX + idNode.getTextValue());
		
		//TODO implementare altri attributti
		if(nameNode != null)
			model.addAttribute("firstname", nameNode.getTextValue());
		
		if(surnameNode != null)
			model.addAttribute("lastname", surnameNode.getTextValue());
		
		if(emailNode != null)
			model.addAttribute("email", emailNode.getTextValue());
		
		model.addAttribute("actionUrl", "/facebook_signup");
		return new ModelAndView("signup");
	}
	
	@RequestMapping(value ="/facebook_signup", method = RequestMethod.POST)
	public ModelAndView facebookSignupPost( @RequestParam(value="firstname", required=true) String firstname,
			@RequestParam(value="lastname", required=true) String lastname, 
			@RequestParam(value="email", required=true)String email){
		return null;
	}
	
	public static boolean isValidEmailAddress(String email) {
		   boolean result = true;
		   
		   try {
		      InternetAddress emailAddr = new InternetAddress(email);
		      emailAddr.validate();
		   } catch (AddressException ex) {
		      result = false;
		   }
		   return result;
	}
	
	public static boolean isNumeric(String args) {
			boolean result = true;
		
			try {	
				Integer.parseInt(args);
			} catch (NumberFormatException e) {
				result = false;
			}
			
			return result;
			
	}

}
