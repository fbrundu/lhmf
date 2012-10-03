package it.polito.ai.lhmf.controllers.ajax;

import it.polito.ai.lhmf.exceptions.InvalidParametersException;
import it.polito.ai.lhmf.model.MemberInterface;
import it.polito.ai.lhmf.model.MemberStatusInterface;
import it.polito.ai.lhmf.model.MemberTypeInterface;
import it.polito.ai.lhmf.model.constants.MemberStatuses;
import it.polito.ai.lhmf.orm.Member;
import it.polito.ai.lhmf.orm.MemberStatus;
import it.polito.ai.lhmf.orm.MemberType;
import it.polito.ai.lhmf.security.MyUserDetailsService;
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
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class MemberAjaxController
{
	@Autowired
	private MemberInterface memberInterface;
	@Autowired
	private MemberStatusInterface memberStatusInterface;
	@Autowired
	private MemberTypeInterface memberTypeInterface;

	@PreAuthorize("hasRole('" + MyUserDetailsService.UserRoles.ADMIN + "')")
	@RequestMapping(value = "/ajax/newMember", method = RequestMethod.POST)
	public @ResponseBody
	List<String> newMember(HttpServletRequest request,
			@RequestParam(value = "username", required = true) String username,
			@RequestParam(value = "firstname", required = true) String firstname,
			@RequestParam(value = "lastname", required = true) String lastname,
			@RequestParam(value = "email", required = true) String email,
			@RequestParam(value = "address", required = true) String address,
			@RequestParam(value = "city", required = true) String city,
			@RequestParam(value = "state", required = true) String state,
			@RequestParam(value = "cap", required = true) String cap,
			@RequestParam(value = "phone", required = false, defaultValue = "not set") String phone,
			@RequestParam(value = "mType", required = false) int memberType) throws InvalidParametersException, ParseException
	{
		Integer idMember = -1;
		
		ArrayList<String> errors = new ArrayList<String>();
		
		if (username.equals(""))
		{
			errors.add("Username: Formato non Valido");
		}
		else
		{
			if (memberInterface.isMemberPresentByUsername(username))
				errors.add("Username: non disponibile");
		}
		if(firstname.equals("") || CheckNumber.isNumeric(firstname)) {
			errors.add("Nome: Formato non Valido");
		}
		if(lastname.equals("") || CheckNumber.isNumeric(lastname)) {
			errors.add("Cognome: Formato non Valido");
		}
		if(!SendEmail.isValidEmailAddress(email)) {
			errors.add("Email: Formato non Valido");
		} else {
			// Controllo mail gia' in uso
			if(memberInterface.isMemberPresentByEmail(email))
				errors.add("Email: Email gi&agrave utilizzata da un altro account");
		}
		if(address.equals("") || CheckNumber.isNumeric(address)) {
			errors.add("Indirizzo: Formato non Valido");
		}
		if(city.equals("") || CheckNumber.isNumeric(city)) {
			errors.add("Citt&agrave: Formato non Valido");
		}	
		if(state.equals("") || CheckNumber.isNumeric(state)) {
			errors.add("Stato: Formato non Valido");
		}
		if(!phone.equals("") && !phone.equals("not set"))
		{
			if(!CheckNumber.isPhoneNumberValid(phone)) 
				errors.add("Telefono: Formato non Valido");
		}
		if(cap.equals("") || !CheckNumber.isNumeric(cap)) {
			errors.add("Cap: Formato non Valido");
		}

		if (errors.size() > 0)
		{
			// Ci sono errori, rimandare alla pagina mostrandoli
			return errors;

		}
		else
		{
				
			//Registrazione Utente Normale o Responsabile
			
			//Mi ricavo il memberType 
			MemberType mType = memberTypeInterface.getMemberType(memberType);
			//Mi ricavo il memberStatus 
			MemberStatus mStatus = memberStatusInterface.getMemberStatus(MemberStatuses.NOT_VERIFIED);
			
			//genero un regCode
			String regCode = Long.toHexString(Double.doubleToLongBits(Math.random()));
			
			//setto la data odierna
			Calendar calendar = Calendar.getInstance();     
			SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
			String sDate = dateFormat.format(calendar.getTime());
			Date regDate = dateFormat.parse(sDate);
			
			// Creo un nuovo utente 
			
			String alfaString = Long.toHexString(Double.doubleToLongBits(Math.random()));
			String password = alfaString.substring(4, 12);
			
			try {
				
				String md5Password = CreateMD5.MD5(password);
			
				Member member = new Member(mType, mStatus, firstname, lastname, 
											username, md5Password, regCode, regDate, email,
											address, city, state, cap, true);

				if(!phone.equals("") && !phone.equals("not set")) 
					member.setTel(phone);
				
				// Checkmail argument (second parameter) is not used here
				idMember = memberInterface.newMember(member, false, true);
				
				if (idMember < 1)
					errors.add("Errore Interno: la registrazione non &egrave andata a buon fine");
				else
				{

					// Inviare qui la mail con il codice di registrazione e la
					// password generata
					// TODO cambiare link di conferma mail. Togliere la parte
					// relativa a admin, gestito lato server con
					// Member.fromAdmin
					/*
					 * SendEmail emailer = new SendEmail(); boolean isSupplier =
					 * false; emailer.sendAdminRegistration(firstname + " " +
					 * lastname, username, password, regCode, idMember, email,
					 * isSupplier);
					 */
				}
			}
			catch (NoSuchAlgorithmException e)
			{
				e.printStackTrace();
			}
			catch (UnsupportedEncodingException e)
			{
				e.printStackTrace();
			}
		}
		return errors;
	}
	
	// TODO get da chi può essere fatto?
	@RequestMapping(value = "/ajax/getmember", method = RequestMethod.GET)
	public @ResponseBody
	Member getMember(HttpServletRequest request,
			@RequestBody Integer idMember)
	{
		Member member = null;
		member = memberInterface.getMember(idMember);
		return member;
	}
	
	@RequestMapping(value = "/ajax/getmemberurlencoded", method = RequestMethod.GET)
	public @ResponseBody
	Member getMemberUrlEncoded(HttpServletRequest request,
			@RequestParam(value = "idMember", required = true) Integer idMember)
	{
		Member member = null;
		member = memberInterface.getMember(idMember);
		return member;
	}
	
	@RequestMapping(value = "/ajax/getMembersRespString", method = RequestMethod.POST)
	public @ResponseBody
	List<String> getMembersRespString(HttpServletRequest request)
	{
		return memberInterface.getMembersRespString();
	}

	@RequestMapping(value = "/ajax/getmembers", method = RequestMethod.GET)
	public @ResponseBody
	List<Member> getMembers(HttpServletRequest request)
	{
		List<Member> membersList = null;
		membersList = memberInterface.getMembers();
		return membersList;
	}
	
	
	@PreAuthorize("hasRole('" + MyUserDetailsService.UserRoles.ADMIN + "')")
	@RequestMapping(value = "/ajax/getMembersToActivate", method = RequestMethod.POST)
	public @ResponseBody
	List<Member> getMembersToActivate(HttpServletRequest request,
			@RequestParam(value = "memberType", required = true) int memberType,
			@RequestParam(value = "page", required = true) int page,
			@RequestParam(value = "itemsPerPage", required = true) int itemsPerPage)
	{
		if (memberType < 0 || memberType > 4 || page < 0 || itemsPerPage < 1)
			return null;
		if (memberType == 4)
		{
			return memberInterface.getMembersToActivate(null, page,
					itemsPerPage);
		}
		// Richiesta di un tipo utente specifico
		MemberType mType = new MemberType(memberType);
		return memberInterface.getMembersToActivate(mType, page,
				itemsPerPage);
	}
	
	@PreAuthorize("hasRole('" + MyUserDetailsService.UserRoles.ADMIN + "')")
	@RequestMapping(value = "/ajax/getMembersList", method = RequestMethod.POST)
	public @ResponseBody
	List<Member> getMembersList(HttpServletRequest request,
			@RequestParam(value = "memberType", required = true) int memberType,
			@RequestParam(value = "page", required = true) int page,
			@RequestParam(value = "itemsPerPage", required = true) int itemsPerPage)
	{
		if (memberType < 0 || memberType > 4 || page < 0 || itemsPerPage < 1)
			return null;
		if (memberType == 4)
		{
			return memberInterface.getMembers(null, page,
					itemsPerPage);
		}
		// Richiesta di un tipo utente specifico
		MemberType mType = new MemberType(memberType);
		return memberInterface.getMembers(mType, page,
				itemsPerPage);
	}
	
	@PreAuthorize("hasRole('" + MyUserDetailsService.UserRoles.ADMIN + "')")
	@RequestMapping(value = "/ajax/getNumberItemsToActivate", method = RequestMethod.POST)
	public @ResponseBody int getNumberItemsToActivate(HttpServletRequest request,
			@RequestParam(value = "memberType", required = true) int memberType)
	{
		Long resultCall = (long) 0;
		
		if(memberType == 4 ) {
			resultCall = memberInterface.getNumberItemsToActivate();
		} else {
			resultCall = memberInterface.getNumberItemsToActivate(memberType);
		}
		
		int result = (int) (long) resultCall;
		
		return result;
	}
	
	@PreAuthorize("hasRole('" + MyUserDetailsService.UserRoles.ADMIN + "')")
	@RequestMapping(value = "/ajax/getNumberItems", method = RequestMethod.POST)
	public @ResponseBody int getNumberItems(HttpServletRequest request,
			@RequestParam(value = "memberType", required = true) int memberType)
	{
		Long resultCall;

		resultCall = memberInterface.getNumberItems(memberType);
		
		int result = (int) (long) resultCall;
		
		return result;
	}
		
	@PreAuthorize("hasRole('" + MyUserDetailsService.UserRoles.ADMIN + "')")
	@RequestMapping(value = "/ajax/activeMember", method = RequestMethod.POST)
	public @ResponseBody int memberActivation(HttpServletRequest request,
			@RequestParam(value = "idMember", required = true) int idMember)
	{
		try
		{
			if (memberInterface.activateMember(idMember) == null)
				return 0;
			else
				return idMember;
		}
		catch (InvalidParametersException e)
		{
			e.printStackTrace();
			return 0;
		}
	}

	@PreAuthorize("hasRole('" + MyUserDetailsService.UserRoles.ADMIN + "')")
	@RequestMapping(value = "/ajax/updatemember", method = RequestMethod.POST)
	public @ResponseBody
	Integer updateMember(HttpServletRequest request, @RequestBody Member member)
			throws InvalidParametersException
	{
		if (memberInterface.updateMember(member) != null)
			return member.getIdMember();
		else
			return -1;
	}

	@PreAuthorize("hasRole('" + MyUserDetailsService.UserRoles.ADMIN + "')")
	@RequestMapping(value = "/ajax/deletemember", method = RequestMethod.POST)
	public @ResponseBody
	Integer deleteMember(HttpServletRequest request,
			@RequestBody Integer idMember) throws InvalidParametersException
	{
		if (memberInterface.deleteMember(idMember) != null)
			return idMember;
		else
			return -1;
	}
}
