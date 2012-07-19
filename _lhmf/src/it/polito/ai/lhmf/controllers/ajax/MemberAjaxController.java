package it.polito.ai.lhmf.controllers.ajax;

import it.polito.ai.lhmf.exceptions.InvalidParametersException;
import it.polito.ai.lhmf.model.MemberInterface;
import it.polito.ai.lhmf.model.MemberStatusInterface;
import it.polito.ai.lhmf.model.MemberTypeInterface;
import it.polito.ai.lhmf.model.SupplierInterface;
import it.polito.ai.lhmf.model.constants.MemberStatuses;
import it.polito.ai.lhmf.orm.Member;
import it.polito.ai.lhmf.orm.MemberStatus;
import it.polito.ai.lhmf.orm.MemberType;
import it.polito.ai.lhmf.orm.Supplier;
import it.polito.ai.lhmf.security.MyUserDetailsService;
import it.polito.ai.lhmf.util.CheckNumber;
import it.polito.ai.lhmf.util.SendEmail;

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
	private SupplierInterface supplierInterface;
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
			@RequestParam(value = "mType", required = false) int memberType,
			@RequestParam(value = "company", required = false) String company,
			@RequestParam(value = "description", required = false) String description,
			@RequestParam(value = "contactName", required = false) String contactName,
			@RequestParam(value = "fax", required = false) String fax,
			@RequestParam(value = "website", required = false) String website,
			@RequestParam(value = "payMethod", required = false) String payMethod,
			@RequestParam(value = "idResp", required = false) int idResp) throws InvalidParametersException, ParseException
	{
		Integer idMember = -1;
		
		ArrayList<String> errors = new ArrayList<String>();
		
		if(username.equals("")) {
			errors.add("Username: Formato non Valido");
		} else {
						
			Member memberControl = memberInterface.getMember(username);
			boolean checkSupplier = true;
			
			if(memberControl != null)
			{
				checkSupplier = false;
				errors.add("Username: non disponibile");
			}
						
			if(checkSupplier) {
				Supplier supplierControl = supplierInterface.getSupplier(username);
				
				if(supplierControl != null)
					errors.add("Username: non disponibile");
			}	
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
			
			//Controllo email già in uso
			
			Member memberControl = memberInterface.getMemberByEmail(email);
			boolean checkSupplier = true;
			
			if(memberControl != null)
			{
				checkSupplier = false;
				errors.add("Email: Email già utilizzata da un altro account");
			}
						
			if(checkSupplier) {
				Supplier supplierControl = supplierInterface.getSupplierByMail(email);
				
				if(supplierControl != null)
					errors.add("Email: Email già utilizzata da un altro account");
			}
			
		}
		if(address.equals("") || CheckNumber.isNumeric(address)) {
			errors.add("Indirizzo: Formato non Valido");
		}
		if(city.equals("") || CheckNumber.isNumeric(city)) {
			errors.add("Città: Formato non Valido");
		}	
		if(state.equals("") || CheckNumber.isNumeric(state)) {
			errors.add("Stato: Formato non Valido");
		}
		if(!phone.equals("") && !phone.equals("not set"))
		{
			if(!CheckNumber.isNumeric(phone)) 
				errors.add("Telefono: Formato non Valido");
		}
		if(cap.equals("") || !CheckNumber.isNumeric(cap)) {
			errors.add("Cap: Formato non Valido");
		}
		
		if(memberType == 3) {
			
			//Controllo campi fornitore
			
			if(CheckNumber.isNumeric(company)) {
				errors.add("Compagnia: Formato non Valido");
			}
			if(CheckNumber.isNumeric(description)) {
				errors.add("Descrizione: Formato non Valido");
			}	
			if(CheckNumber.isNumeric(contactName)) {
				errors.add("Stato: Formato non Valido");
			}
			if(!fax.equals(""))
			{
				if(!CheckNumber.isNumeric(fax)) 
					errors.add("Fax: Formato non Valido");
			}
			if(CheckNumber.isNumeric(website)) {
				errors.add("Web Site: Formato non Valido");
			}
			if(CheckNumber.isNumeric(payMethod)) {
				errors.add("Metodo Pagamento: Formato non Valido");
			}			
		}
		
		if(errors.size() > 0) {
			
			// Ci sono errori, rimandare alla pagina mostrandoli
			return errors;
			
		}
		else
		{
			
			if(memberType != 3) {
				
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
				
				//trasformo il cap in un numero
				int capNumeric = Integer.parseInt(cap);
				
				// Creo un nuovo utente 
				
				//TODO: implementare generazione password
				Member member = new Member(	mType, mStatus, firstname, lastname, 
											username, "prova", regCode, regDate, 
											email, address, city, state, capNumeric);
				
				if(!phone.equals("") && !phone.equals("not set")) 
					member.setTel(phone);
				
				idMember = memberInterface.newMember(member);
				
				if(idMember < 1)
					errors.add("Errore Interno: la registrazione non è andata a buon fine");
				else {
					
					//Inviare qui la mail con il codice di registrazione.
					/*SendEmail emailer = new SendEmail();
					
					boolean fromAdmin = true;
					
					String mailTo = member.getEmail();
					String subject = "Conferma mail per GasProject.net";
					String body = emailer.getBodyForAuth(member.getName(), member.getSurname(), regCode, idMember, fromAdmin);
					SendEmail.send(mailTo, subject, body);	*/
				}
				
			} else {
				
				//Registrazione Fornitore
				
				//genero un regCode
				String regCode = Long.toHexString(Double.doubleToLongBits(Math.random()));
				
				//setto la data odierna
				Calendar calendar = Calendar.getInstance();     
				SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
				String sDate = dateFormat.format(calendar.getTime());
				Date regDate = dateFormat.parse(sDate);
				
				//trasformo il cap in un numero
				int capNumeric = Integer.parseInt(cap);
				
				//Mi ricavo il membro responsabile 
				Member memberResp = memberInterface.getMember(idResp);
				
				// Creo un nuovo fornitore
				
				//TODO: implementare generazione password
				
				byte mt = 0;
				
				Supplier supplier = new Supplier(memberResp, firstname, lastname, username, "password", regCode, regDate, email, address, city, state, capNumeric, false, mt, payMethod);
								
				if(!phone.equals("") && !phone.equals("not set")) 
					supplier.setTel(phone);
				if(!company.equals("")) 
					supplier.setCompanyName(company);
				if(!description.equals("")) 
					supplier.setDescription(description);
				if(!contactName.equals("")) 
					supplier.setContactName(contactName);
				if(!fax.equals("")) 
					supplier.setFax(fax);
				if(!website.equals("")) 
					supplier.setWebsite(website);
				if(!payMethod.equals("")) 
					supplier.setPaymentMethod(payMethod);
				
				idMember = supplierInterface.newSupplier(supplier);
				
				if(idMember < 1)
					errors.add("Errore Interno: la registrazione non è andata a buon fine");
				else {
					
					//Inviare qui la mail con il codice di registrazione.
					/*SendEmail emailer = new SendEmail();
					
					boolean fromAdmin = true;
					
					String mailTo = member.getEmail();
					String subject = "Conferma mail per GasProject.net";
					String body = emailer.getBodyForAuth(member.getName(), member.getSurname(), regCode, idMember, fromAdmin);
					SendEmail.send(mailTo, subject, body);	*/
					
				}
			}
		}
		return errors;
	}
	
	// TODO get da chi puÃ² essere fatto?
	@RequestMapping(value = "/ajax/getmember", method = RequestMethod.GET)
	public @ResponseBody
	Member getMember(HttpServletRequest request,
			@RequestBody Integer idMember)
	{
		
		Member member = null;
		member = memberInterface.getMember(idMember);
		return member;
	}
	
	@RequestMapping(value = "/ajax/getMembersRespString", method = RequestMethod.POST)
	public @ResponseBody
	ArrayList<String> getMembersRespString(HttpServletRequest request)
	{
		ArrayList<String> respString = new ArrayList<String>();
		
		List<Member> listMember = new ArrayList<Member>();
		listMember = memberInterface.getMembersResp();
		
		for (Member m : listMember) {
		  
			String temp = m.getIdMember() + "," + m.getName() + " " + m.getSurname();
			respString.add(temp);	
		}
		
		return respString;
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
	@RequestMapping(value = "/ajax/getMemberToActivate", method = RequestMethod.POST)
	public @ResponseBody
	List<Member> getMembersToActivate(HttpServletRequest request,
			@RequestParam(value = "memberType", required = true) int memberType,
			@RequestParam(value = "page", required = true) int page,
			@RequestParam(value = "itemsPerPage", required = true) int itemsPerPage)
	{
		List<Member> membersList = null;
		
		if(memberType == 4) {
			//Prendo tutti gli utenti da attivare
			membersList = memberInterface.getMembersToActivate();
		} else
		{
			//Richiesta di un tipo utente specifico
			
			MemberType mType = new MemberType(memberType);
			membersList = memberInterface.getMembersToActivate(mType);
		}
		
		List<Member> returnList = new ArrayList<Member>();
		
		int startIndex = page * itemsPerPage;
		int endIndex = startIndex + itemsPerPage;
		
		if(endIndex > membersList.size())
			endIndex = membersList.size();
		
		returnList.addAll(membersList.subList(startIndex, endIndex));
		
		return returnList;
	}
		
	@PreAuthorize("hasRole('" + MyUserDetailsService.UserRoles.ADMIN + "')")
	@RequestMapping(value = "/ajax/activeMember", method = RequestMethod.POST)
	public Integer memberActivation(HttpServletRequest request,
			@RequestParam(value = "idMember", required = true) Integer idMember)
	{
		Member member = memberInterface.getMember(idMember);
		
		MemberStatus mStatus = new MemberStatus(MemberStatuses.ENABLED);
		member.setMemberStatus(mStatus);
		
		Integer result;
		try {
			result = memberInterface.updateMember(member);
		} catch (InvalidParametersException e) {
			result = 0;
			e.printStackTrace();
		}
		
		if(result == 0)
			return result;
		else 
			return idMember;
	}

	@PreAuthorize("hasRole('" + MyUserDetailsService.UserRoles.ADMIN + "')")
	@RequestMapping(value = "/ajax/updatemember", method = RequestMethod.POST)
	public @ResponseBody
	Integer updateMember(HttpServletRequest request,
			@RequestBody Member member) throws InvalidParametersException
	{
		Integer rowsAffected = -1;
		rowsAffected = memberInterface.updateMember(member);
		return rowsAffected;
	}

	@PreAuthorize("hasRole('" + MyUserDetailsService.UserRoles.ADMIN + "')")
	@RequestMapping(value = "/ajax/deletemember", method = RequestMethod.POST)
	public @ResponseBody
	Integer deleteMember(HttpServletRequest request,
			@RequestBody Integer idMember) throws InvalidParametersException
	{
		Integer rowsAffected = -1;
		rowsAffected = memberInterface.deleteMember(idMember);
		return rowsAffected;
	}
}
