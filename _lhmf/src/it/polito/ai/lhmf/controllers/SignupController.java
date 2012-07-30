package it.polito.ai.lhmf.controllers;

import it.polito.ai.lhmf.exceptions.InvalidParametersException;
import it.polito.ai.lhmf.model.MemberInterface;
import it.polito.ai.lhmf.model.MemberStatusInterface;
import it.polito.ai.lhmf.model.MemberTypeInterface;
import it.polito.ai.lhmf.model.MessageInterface;
import it.polito.ai.lhmf.model.constants.MemberStatuses;
import it.polito.ai.lhmf.model.constants.MemberTypes;
import it.polito.ai.lhmf.orm.Member;
import it.polito.ai.lhmf.orm.MemberStatus;
import it.polito.ai.lhmf.orm.MemberType;
import it.polito.ai.lhmf.orm.Message;
import it.polito.ai.lhmf.security.FacebookAuthenticationFilter;
import it.polito.ai.lhmf.util.CheckNumber;
import it.polito.ai.lhmf.util.CreateMD5;
import it.polito.ai.lhmf.util.SendEmail;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.node.ObjectNode;
import org.springframework.beans.factory.annotation.Autowired;
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
	@Autowired
	private MemberInterface memberInterface;
	@Autowired
	private MemberStatusInterface memberStatusInterface;
	@Autowired
	private MemberTypeInterface memberTypeInterface;	
	@Autowired
	private MessageInterface messageInterface;	
		
	@RequestMapping(value = "/openid_signup", method = RequestMethod.GET)
	public ModelAndView openIdSignupGet(Model model, HttpSession session)
	{
		session.removeAttribute("checkMail");
		session.removeAttribute("openid_firstname_static");
		session.removeAttribute("openid_lastname_static");
		session.removeAttribute("openid_email_static");
		
		OpenIDAuthenticationToken token = (OpenIDAuthenticationToken) session
				.getAttribute("OPENID_TOKEN");
		session.setAttribute("OPENID_USERID", token.getIdentityUrl());

		List<OpenIDAttribute> attributes = token.getAttributes();
		String firstname = null;
		String lastname = null;
		String email = null;

		for (OpenIDAttribute attribute : attributes)
		{
			String value = attribute.getValues().get(0);
			if (value != null)
			{
				if (attribute.getName().equals("firstname")	&& firstname == null)
				{
					firstname = value;
					session.setAttribute("openid_firstname_static", firstname);
					model.addAttribute("firstname_static", firstname);
				}
				else if (attribute.getName().equals("lastname")	&& lastname == null)
				{
					lastname = value;
					session.setAttribute("openid_lastname_static", lastname);
					model.addAttribute("lastname_static", lastname);
				}
				else if (attribute.getName().equals("email") && email == null)
				{
					email = value;
					session.setAttribute("checkMail", false);
					session.setAttribute("openid_email_static", email);
					model.addAttribute("email_static", email);
				}
			}
		}
		model.addAttribute("actionUrl", "/openid_signup");
		return new ModelAndView("signup");
	}

	@RequestMapping(value = "/openid_signup", method = RequestMethod.POST)
	public ModelAndView openIdSignupPost( Model model, HttpSession session,
			@RequestParam(value = "firstname", required = true) String firstname,
			@RequestParam(value = "lastname", required = true) String lastname,
			@RequestParam(value = "email", required = true) String email,
			@RequestParam(value = "address", required = true) String address,
			@RequestParam(value = "city", required = true) String city,
			@RequestParam(value = "state", required = true) String state,
			@RequestParam(value = "cap", required = true) String cap,
			@RequestParam(value = "phone", required = false, defaultValue = "not set") String phone) throws ParseException, InvalidParametersException
	{
		
		ArrayList<Map<String, String>> errors = new ArrayList<Map<String, String>>();
		String firstname_static = (String) session.getAttribute("openid_firstname_static");
		String lastname_static = (String) session.getAttribute("openid_lastname_static");
		String email_static = (String) session.getAttribute("openid_email_static");
		
		if(firstname_static != null){
			firstname = firstname_static;
			model.addAttribute("firstname_static", firstname_static);
		}
		else
			model.addAttribute("firstname", firstname);
		
		if(lastname_static != null){
			lastname = lastname_static;
			model.addAttribute("lastname_static", lastname_static);
		}
		else
			model.addAttribute("lastname", lastname);
		
		if(email_static != null){
			email = email_static;
			model.addAttribute("email_static", email_static);
		}
		else
			model.addAttribute("email", email);
		
		model.addAttribute("address", address);
		model.addAttribute("city", city);
		model.addAttribute("state", state);
		model.addAttribute("cap", cap);
		model.addAttribute("phone", phone);
		
		if(firstname.equals("") || CheckNumber.isNumeric(firstname)) {
			
			Map<String, String> error = new HashMap<String, String>();
			error.put("id", "Nome");
			error.put("error", "Formato non Valido");
			errors.add(error);
		}
		if(lastname.equals("") || CheckNumber.isNumeric(lastname)) {
			
			Map<String, String> error = new HashMap<String, String>();
			error.put("id", "Cognome");
			error.put("error", "Formato non Valido");
			errors.add(error);
		}
		if(!SendEmail.isValidEmailAddress(email)) {
			
			Map<String, String> error = new HashMap<String, String>();
			error.put("id", "Email");
			error.put("error", "Formato non Valido");
			errors.add(error);
		} else {
			
			//Controllo email già in uso
			
			Member memberControl = memberInterface.getMemberByEmail(email);
			
			if(memberControl != null)
			{
				Map<String, String> error = new HashMap<String, String>();
				error.put("id", "Email");
				error.put("error", "Email già utilizzata da un altro account");
				errors.add(error);
			}
		}
		if(address.equals("") || CheckNumber.isNumeric(address)) {
			
			Map<String, String> error = new HashMap<String, String>();
			error.put("id", "Indirizzo");
			error.put("error", "Formato non Valido");
			errors.add(error);
		}
		if(city.equals("") || CheckNumber.isNumeric(city)) {
			
			Map<String, String> error = new HashMap<String, String>();
			error.put("id", "Cittïà");
			error.put("error", "Formato non Valido");
			errors.add(error);
		}	
		if(state.equals("") || CheckNumber.isNumeric(state)) {
			
			Map<String, String> error = new HashMap<String, String>();
			error.put("id", "Stato");
			error.put("error", "Formato non Valido");
			errors.add(error);
		}
		if(cap.equals("") || !CheckNumber.isNumeric(cap)) {
			
			Map<String, String> error = new HashMap<String, String>();
			error.put("id", "Cap");
			error.put("error", "Formato non Valido");
			errors.add(error);
		}
		if(!phone.equals("") && !phone.equals("not set"))
		{
			if(!CheckNumber.isNumeric(phone)) {
				
				Map<String, String> error = new HashMap<String, String>();
				error.put("id", "Telefono");
				error.put("error", "Formato non Valido");
				errors.add(error);
			}
		}
	
		if(errors.size() > 0) {
		
			// Ci sono errori, rimandare alla pagina mostrandoli
			model.addAttribute("errors", errors);
			model.addAttribute("actionUrl", "/openid_signup");
			return new ModelAndView("signup");
			
		}
		else
		{
			//eseguire la registrazione e mandare alla pagina principale
			
			//Mi ricavo il memberType 
			MemberType mType = memberTypeInterface.getMemberType(MemberTypes.USER_NORMAL);
			//Mi ricavo il memberStatus 
			MemberStatus mStatus;
			
			//genero un regCode
			String regCode = Long.toHexString(Double.doubleToLongBits(Math.random()));
			
			boolean checkMail;
			
			if(session.getAttribute("checkMail") == null)
				checkMail = true;
			else {
				checkMail = false;
				session.removeAttribute("checkMail");
			}			
				
			if(checkMail) {
				mStatus = memberStatusInterface.getMemberStatus(MemberStatuses.NOT_VERIFIED);
				model.addAttribute("checkMail", true);
				
			} else
			{
				mStatus = memberStatusInterface.getMemberStatus(MemberStatuses.VERIFIED_DISABLED);		
			}
			
			//setto la data odierna
			Calendar calendar = Calendar.getInstance();     
			SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
			String sDate = dateFormat.format(calendar.getTime());
			Date regDate = dateFormat.parse(sDate);

			//username
			String username = (String) session.getAttribute("OPENID_USERID");
			
			// Creo un nuovo utente 
			Member member = new Member(	mType, mStatus, firstname, lastname, 
										username, "not set", regCode, regDate, 
										email, address, city, state, cap, false);
			
			if(!phone.equals("")) 
				member.setTel(phone);
			
			@SuppressWarnings("unused")
			int memberId = memberInterface.newMember(member);
			
			if(checkMail) {
				
				//TODO Inviare qui la mail con il codice di registrazione. 
				/*
				SendEmail emailer = new SendEmail();
				boolean isSupplier = false;
				emailer.sendNormalRegistration(firstname + " " + lastname, regCode, memberId, email, isSupplier);
				*/
			}
			else{
				//Mandare messaggio all'admin
				
				//Ricavo il membro Admin
				Member memberAdmin = memberInterface.getMemberAdmin();
				
				//Creo il Current timestamp
				java.util.Date now = calendar.getTime();
				java.sql.Timestamp currentTimestamp = new java.sql.Timestamp(now.getTime());
				
				String text = 	"Utente richiede l'attivazione dell'account\n\n" +
								"Id: " + member.getIdMember() + " - " + member.getName() + " " + member.getSurname() + "\n" +
								"Email: " + member.getEmail() + "\n";  
				
				//Costruisco l'oggetto message	
				Message message = new Message(memberAdmin, currentTimestamp, false, 0);
				
				message.setMemberByIdSender(member);
				message.setText(text);
				
				try {
					messageInterface.newMessage(message);
				} catch (InvalidParametersException e) {
					e.printStackTrace();
				}
			}
		}
		
		// Registrazione avvenuta con successo. Redirigere				
		return new ModelAndView("/signup_confirmed");
	}
	
	@RequestMapping(value = "/facebook_signup", method = RequestMethod.GET)
	public ModelAndView facebookSignupGet(Model model, HttpSession session){
		session.removeAttribute("checkMail");
		session.removeAttribute("fb_firstname_static");
		session.removeAttribute("fb_lastname_static");
		session.removeAttribute("fb_email_static");
		
		ObjectNode values = (ObjectNode) session.getAttribute("FACEBOOK_VALUES");
		JsonNode idNode = values.get("id");
		JsonNode nameNode = values.get("first_name");
		JsonNode surnameNode = values.get("last_name");
		JsonNode emailNode = values.get("email");
		
		session.setAttribute("FACEBOOK_USERID", FacebookAuthenticationFilter.FACEBOOK_USERID_PREFIX + idNode.getTextValue());
		
		if(nameNode != null){
			session.setAttribute("fb_firstname_static", nameNode.getTextValue());
			model.addAttribute("firstname_static", nameNode.getTextValue());
		}
		
		if(surnameNode != null){
			session.setAttribute("fb_lastname_static", surnameNode.getTextValue());
			model.addAttribute("lastname_static", surnameNode.getTextValue());
		}
		
		if(emailNode != null) {
			session.setAttribute("checkMail", false);
			session.setAttribute("fb_email_static", emailNode.getTextValue());
			model.addAttribute("email_static", emailNode.getTextValue());
		}
		
		model.addAttribute("actionUrl", "/facebook_signup");
		return new ModelAndView("signup");
	}
	
	@RequestMapping(value ="/facebook_signup", method = RequestMethod.POST)
	public ModelAndView facebookSignupPost(  Model model, HttpSession session,
			@RequestParam(value = "firstname", required = true) String firstname,
			@RequestParam(value = "lastname", required = true) String lastname,
			@RequestParam(value = "email", required = true) String email,
			@RequestParam(value = "address", required = true) String address,
			@RequestParam(value = "city", required = true) String city,
			@RequestParam(value = "state", required = true) String state,
			@RequestParam(value = "cap", required = true) String cap,
			@RequestParam(value = "phone", required = false, defaultValue = "not set") String phone) throws ParseException, InvalidParametersException
	{
		
		ArrayList<Map<String, String>> errors = new ArrayList<Map<String, String>>();
		String firstname_static = (String) session.getAttribute("fb_firstname_static");
		String lastname_static = (String) session.getAttribute("fb_lastname_static");
		String email_static = (String) session.getAttribute("fb_email_static");
		
		if(firstname_static != null){
			firstname = firstname_static;
			model.addAttribute("firstname_static", firstname_static);
		}
		else
			model.addAttribute("firstname", firstname);
		
		if(lastname_static != null){
			lastname = lastname_static;
			model.addAttribute("lastname_static", lastname_static);
		}
		else
			model.addAttribute("lastname", lastname);
		
		if(email_static != null){
			email = email_static;
			model.addAttribute("email_static", email_static);
		}
		else
			model.addAttribute("email", email);
		
		model.addAttribute("address", address);
		model.addAttribute("city", city);
		model.addAttribute("state", state);
		model.addAttribute("cap", cap);
		model.addAttribute("phone", phone);
		
		if(firstname.equals("") || CheckNumber.isNumeric(firstname)) {
			
			Map<String, String> error = new HashMap<String, String>();
			error.put("id", "Nome");
			error.put("error", "Formato non Valido");
			errors.add(error);
		}
		if(lastname.equals("") || CheckNumber.isNumeric(lastname)) {
			
			Map<String, String> error = new HashMap<String, String>();
			error.put("id", "Cognome");
			error.put("error", "Formato non Valido");
			errors.add(error);
		}
		if(!SendEmail.isValidEmailAddress(email)) {
			
			Map<String, String> error = new HashMap<String, String>();
			error.put("id", "Email");
			error.put("error", "Formato non Valido");
			errors.add(error);
		} else {
			
			//Controllo email già in uso
			Member memberControl = memberInterface.getMemberByEmail(email);
			
			if(memberControl != null)
			{
				Map<String, String> error = new HashMap<String, String>();
				error.put("id", "Email");
				error.put("error", "Email già utilizzata da un altro account");
				errors.add(error);
			}		
		}
		if(address.equals("") || CheckNumber.isNumeric(address)) {
			
			Map<String, String> error = new HashMap<String, String>();
			error.put("id", "Indirizzo");
			error.put("error", "Formato non Valido");
			errors.add(error);
		}
		if(city.equals("") || CheckNumber.isNumeric(city)) {
			
			Map<String, String> error = new HashMap<String, String>();
			error.put("id", "Citt&agrave");
			error.put("error", "Formato non Valido");
			errors.add(error);
		}	
		if(state.equals("") || CheckNumber.isNumeric(state)) {
			
			Map<String, String> error = new HashMap<String, String>();
			error.put("id", "Stato");
			error.put("error", "Formato non Valido");
			errors.add(error);
		}
		if(cap.equals("") || !CheckNumber.isNumeric(cap)) {
			
			Map<String, String> error = new HashMap<String, String>();
			error.put("id", "Cap");
			error.put("error", "Formato non Valido");
			errors.add(error);
		}
		if(!phone.equals("") && !phone.equals("not set"))
		{
			if(!CheckNumber.isNumeric(phone)) {
				
				Map<String, String> error = new HashMap<String, String>();
				error.put("id", "Telefono");
				error.put("error", "Formato non Valido");
				errors.add(error);
			}
		}
	
		if(errors.size() > 0) {
		
			// Ci sono errori, rimandare alla pagina mostrandoli
			model.addAttribute("errors", errors);
			model.addAttribute("actionUrl", "/facebook_signup");
			return new ModelAndView("signup");
			
		}
		else
		{
			//eseguire la registrazione
			
			//Mi ricavo il memberType 
			MemberType mType = memberTypeInterface.getMemberType(MemberTypes.USER_NORMAL);
			
			//Mi ricavo il memberStatus 
			MemberStatus mStatus;
			
			//genero un regCode
			String regCode = Long.toHexString(Double.doubleToLongBits(Math.random()));
			
			boolean checkMail;
			
			if(session.getAttribute("checkMail") == null)
				checkMail = true;
			else {
				checkMail = false;
				session.removeAttribute("checkMail");
			}
			
			if(checkMail) {
				mStatus = memberStatusInterface.getMemberStatus(MemberStatuses.NOT_VERIFIED);
				model.addAttribute("checkMail", true);		
			} else
			{
				mStatus = memberStatusInterface.getMemberStatus(MemberStatuses.VERIFIED_DISABLED);
			}
			
			
			
			//setto la data odierna
			Calendar calendar = Calendar.getInstance();     
			SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
			String sDate = dateFormat.format(calendar.getTime());
			Date regDate = dateFormat.parse(sDate);

			//username
			String username = (String) session.getAttribute("FACEBOOK_USERID");
			
			// Creo un nuovo utente 
			Member member = new Member(	mType, mStatus, firstname, lastname, 
										username, "not set", regCode, regDate, 
										email, address, city, state, cap, false);
			
			if(!phone.equals("")) 
				member.setTel(phone);
			
			@SuppressWarnings("unused")
			int memberId = memberInterface.newMember(member);
			
			if(checkMail) {
				
				//TODO Inviare qui la mail con il codice di registrazione. 
				/*
				SendEmail emailer = new SendEmail();
				boolean isSupplier = false;
				emailer.sendNormalRegistration(firstname + " " + lastname, regCode, memberId, email, isSupplier);
				*/
			} else{
				//Mandare messaggio all'admin
				
				//Ricavo il membro Admin
				Member memberAdmin = memberInterface.getMemberAdmin();
				
				//Creo il Current timestamp
				java.util.Date now = calendar.getTime();
				java.sql.Timestamp currentTimestamp = new java.sql.Timestamp(now.getTime());
				
				String text = 	"Utente richiede l'attivazione dell'account\n\n" +
								"Id: " + member.getIdMember() + " - " + member.getName() + " " + member.getSurname() + "\n" +
								"Email: " + member.getEmail() + "\n";  
				
				//Costruisco l'oggetto message	
				Message message = new Message(memberAdmin, currentTimestamp, false, 0);
				
				message.setMemberByIdSender(member);
				message.setText(text);
				
				try {
					messageInterface.newMessage(message);
				} catch (InvalidParametersException e) {
					e.printStackTrace();
				}
			}
		}
		
		// Registrazione avvenuta con successo. Redirigere 
		return new ModelAndView("/signup_confirmed");
	}
	
	@RequestMapping(value ="/signup")
	public ModelAndView normalSignupGet( Model model )
	{
		model.addAttribute("actionUrl", "/normal_signup");
		model.addAttribute("getUserCredentials", true);
		return new ModelAndView("signup");
		
	}
	
	@RequestMapping(value ="/normal_signup", method = RequestMethod.POST)
	public ModelAndView normalSignupPost( Model model, HttpSession session,
			@RequestParam(value = "firstname", required = true) String firstname,
			@RequestParam(value = "lastname", required = true) String lastname,
			@RequestParam(value = "email", required = true) String email,
			@RequestParam(value = "address", required = true) String address,
			@RequestParam(value = "city", required = true) String city,
			@RequestParam(value = "state", required = true) String state,
			@RequestParam(value = "cap", required = true) String cap,
			@RequestParam(value = "phone", required = false, defaultValue = "not set") String phone,
			@RequestParam(value = "username", required = true) String username,
			@RequestParam(value = "password", required = true) String password,
			@RequestParam(value = "repassword", required = true) String repassword) throws ParseException, InvalidParametersException
	{
		
		ArrayList<Map<String, String>> errors = new ArrayList<Map<String, String>>();
		
		model.addAttribute("firstname", firstname);
		model.addAttribute("lastname", lastname);
		model.addAttribute("email", email);
		model.addAttribute("address", address);
		model.addAttribute("city", city);
		model.addAttribute("state", state);
		model.addAttribute("cap", cap);
		model.addAttribute("phone", phone);
		model.addAttribute("username", phone);
		model.addAttribute("password", phone);
		model.addAttribute("repassword", phone);
		
		if(firstname.equals("") || CheckNumber.isNumeric(firstname)) {
			
			Map<String, String> error = new HashMap<String, String>();
			error.put("id", "Nome");
			error.put("error", "Formato non Valido");
			errors.add(error);
		}
		if(lastname.equals("") || CheckNumber.isNumeric(lastname)) {
			
			Map<String, String> error = new HashMap<String, String>();
			error.put("id", "Cognome");
			error.put("error", "Formato non Valido");
			errors.add(error);
		}
		if(!SendEmail.isValidEmailAddress(email)) {
			
			Map<String, String> error = new HashMap<String, String>();
			error.put("id", "Email");
			error.put("error", "Formato non Valido");
			errors.add(error);
		} else {
			
			//Controllo email già in uso
			Member memberControl = memberInterface.getMemberByEmail(email);
			
			if(memberControl != null)
			{
				Map<String, String> error = new HashMap<String, String>();
				error.put("id", "Email");
				error.put("error", "Email già utilizzata da un altro account");
				errors.add(error);
			}
			
		}
		if(address.equals("") || CheckNumber.isNumeric(address)) {
			
			Map<String, String> error = new HashMap<String, String>();
			error.put("id", "Indirizzo");
			error.put("error", "Formato non Valido");
			errors.add(error);
		}
		if(city.equals("") || CheckNumber.isNumeric(city)) {
			
			Map<String, String> error = new HashMap<String, String>();
			error.put("id", "Citt&agrave");
			error.put("error", "Formato non Valido");
			errors.add(error);
		}	
		if(state.equals("") || CheckNumber.isNumeric(state)) {
			
			Map<String, String> error = new HashMap<String, String>();
			error.put("id", "Stato");
			error.put("error", "Formato non Valido");
			errors.add(error);
		}
		if(cap.equals("") || !CheckNumber.isNumeric(cap)) {
			
			Map<String, String> error = new HashMap<String, String>();
			error.put("id", "Cap");
			error.put("error", "Formato non Valido");
			errors.add(error);
		}
		if(!phone.equals("") && !phone.equals("not set"))
		{
			if(!CheckNumber.isNumeric(phone)) {
				
				Map<String, String> error = new HashMap<String, String>();
				error.put("id", "Telefono");
				error.put("error", "Formato non Valido");
				errors.add(error);
			}
		}
		if(username.equals("")) {
			
			Map<String, String> error = new HashMap<String, String>();
			error.put("id", "Username");
			error.put("error", "Formato non Valido");
			errors.add(error);
		} else {
						
			Member memberControl = memberInterface.getMember(username);
			
			if(memberControl != null)
			{
				Map<String, String> error = new HashMap<String, String>();
				error.put("id", "Username");
				error.put("error", "Username non disponibile");
				errors.add(error);
			}		
		}
		if(password.equals("")) {
					
			Map<String, String> error = new HashMap<String, String>();
			error.put("id", "Password");
			error.put("error", "Formato non Valido");
			errors.add(error);
		}
		if(repassword.equals("")) {
			
			Map<String, String> error = new HashMap<String, String>();
			error.put("id", "RePassword");
			error.put("error", "Formato non Valido");
			errors.add(error);
		}
	
		if(errors.size() > 0) {
		
			// Ci sono errori, rimandare alla pagina mostrandoli
			model.addAttribute("errors", errors);
			model.addAttribute("actionUrl", "/normal_signup");
			model.addAttribute("getUserCredentials", true);
			return new ModelAndView("signup");
			
		}
		else
		{
			//eseguire la registrazione
			
			//Mi ricavo il memberType 
			MemberType mType = memberTypeInterface.getMemberType(MemberTypes.USER_NORMAL);
			
			//Mi ricavo il memberStatus 
			MemberStatus mStatus = memberStatusInterface.getMemberStatus(MemberStatuses.NOT_VERIFIED);
			model.addAttribute("checkMail", true);	
			
			//genero un regCode
			String regCode = Long.toHexString(Double.doubleToLongBits(Math.random()));					
			
			//setto la data odierna
			Calendar calendar = Calendar.getInstance();     
			SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
			String sDate = dateFormat.format(calendar.getTime());
			Date regDate = dateFormat.parse(sDate);
			
			//genero l'md5 della password
			String md5Password;
			@SuppressWarnings("unused")
			int memberId = 0;
			
			try {
				md5Password = CreateMD5.MD5(password);
				
				// Creo un nuovo utente 
				Member member = new Member(	mType, mStatus, firstname, lastname, 
						username, md5Password, regCode, regDate, 
						email, address, city, state, cap, false);
				
				if(!phone.equals("") && !phone.equals("not set")) 
					member.setTel(phone);
				
				memberId = memberInterface.newMember(member);
				
			} catch (NoSuchAlgorithmException e) {
				e.printStackTrace();
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			
			//TODO Inviare qui la mail con il codice di registrazione. 
			/*
			SendEmail emailer = new SendEmail();
			boolean isSupplier = false;
			emailer.sendNormalRegistration(firstname + " " + lastname, regCode, memberId, email, isSupplier);
			*/
			
					
			
		}
		
		// Registrazione avvenuta con successo. Redirigere 
		return new ModelAndView("/signup_confirmed");
		
	}
	
	@RequestMapping(value ="/authMail", method = RequestMethod.GET)
	public ModelAndView authMail( Model model,
			@RequestParam(value = "id", required = true) String id,
			@RequestParam(value = "regCode", required = true) String regCode)
	{
		ArrayList<Map<String, String>> errors = new ArrayList<Map<String, String>>();
		
		int idMember = Integer.parseInt(id);
		
		boolean fromAdmin = false;
	
		Member member = memberInterface.getMember(idMember);
		
		if(member == null) {
			
			Map<String, String> error = new HashMap<String, String>();
			error.put("id", "Account");
			error.put("error", "Account non esistente");
			errors.add(error);
			
		} else if(!member.getRegCode().equals(regCode)) {
			
				Map<String, String> error = new HashMap<String, String>();
				error.put("id", "Code");
				error.put("error", "Codice non corretto");
				errors.add(error);
			
		} else {
			
			//Account esistente e codice corretto.
			fromAdmin = member.isFromAdmin();
			
			MemberStatus mStatus;
			if(fromAdmin)
				mStatus = memberStatusInterface.getMemberStatus(MemberStatuses.ENABLED);
			else
				mStatus = memberStatusInterface.getMemberStatus(MemberStatuses.VERIFIED_DISABLED);
			
			member.setMemberStatus(mStatus);
			
			model.addAttribute("firstname", member.getName());
			
			try {
				memberInterface.updateMember(member);
			} catch (InvalidParametersException e) {
				
				Map<String, String> error = new HashMap<String, String>();
				error.put("id", "InternalError");
				error.put("error", "Non è stato possibile verificare l'email.");
				errors.add(error);
			}
			
		}
		
		// Ci sono errori, rimandare alla pagina mostrandoli
		if(errors.size() <= 0) {
			
			if(!fromAdmin) {
					
				//Mandare messaggio all'admin
				
				//Ricavo il membro Admin
				Member memberAdmin = memberInterface.getMemberAdmin();
				
				//Creo il Current timestamp
				Calendar calendar = Calendar.getInstance();
				java.util.Date now = calendar.getTime();
				java.sql.Timestamp currentTimestamp = new java.sql.Timestamp(now.getTime());
				
				String text = 	"Utente richiede l'attivazione dell'account\n\n" +
								"Id: " + member.getIdMember() + " - " + member.getName() + " " + member.getSurname() + "\n" +
								"Email: " + member.getEmail() + "\n";  
				
				//Costruisco l'oggetto message	
				Message message = new Message(memberAdmin, currentTimestamp, false, 0);
				
				message.setMemberByIdSender(member);
				message.setText(text);
				
				try {
					messageInterface.newMessage(message);
				} catch (InvalidParametersException e) {
					e.printStackTrace();
				}
			}		
		}	
		return new ModelAndView("/authMail_confirmed");
	}

}
