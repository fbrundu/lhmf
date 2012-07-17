package it.polito.ai.lhmf.controllers.ajax;

import it.polito.ai.lhmf.exceptions.InvalidParametersException;
import it.polito.ai.lhmf.model.MemberInterface;
import it.polito.ai.lhmf.model.MemberStatusInterface;
import it.polito.ai.lhmf.model.SupplierInterface;
import it.polito.ai.lhmf.model.constants.MemberStatuses;
import it.polito.ai.lhmf.orm.Member;
import it.polito.ai.lhmf.orm.MemberStatus;
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

	@PreAuthorize("hasRole('" + MyUserDetailsService.UserRoles.ADMIN + "')")
	@RequestMapping(value = "/ajax/newMember", method = RequestMethod.POST)
	public @ResponseBody
	List<String> newMember(HttpServletRequest request,
			@RequestBody Member member) throws InvalidParametersException, ParseException
	{
		Integer idMember = -1;
		
		ArrayList<String> errors = new ArrayList<String>();
		
		if(member.getName().equals("") || CheckNumber.isNumeric(member.getName())) {
			errors.add("Nome: Formato non Valido");
		}
		if(member.getSurname().equals("") || CheckNumber.isNumeric(member.getSurname())) {
			errors.add("Cognome: Formato non Valido");
		}
		if(!SendEmail.isValidEmailAddress(member.getEmail())) {
			errors.add("Email: Formato non Valido");
		} else {
			
			//Controllo email già in uso
			
			Member memberControl = memberInterface.getMemberByEmail(member.getEmail());
			boolean checkSupplier = true;
			
			if(memberControl != null)
			{
				checkSupplier = false;
				errors.add("Email: Email già utilizzata da un altro account");
			}
						
			if(checkSupplier) {
				Supplier supplierControl = supplierInterface.getSupplierByMail(member.getEmail());
				
				if(supplierControl != null)
					errors.add("Email: Email già utilizzata da un altro account");
			}
			
		}
		if(member.getAddress().equals("") || CheckNumber.isNumeric(member.getAddress())) {
			errors.add("Indirizzo: Formato non Valido");
		}
		if(member.getCity().equals("") || CheckNumber.isNumeric(member.getCity())) {
			errors.add("Città: Formato non Valido");
		}	
		if(member.getState().equals("") || CheckNumber.isNumeric(member.getState())) {
			errors.add("Stato: Formato non Valido");
		}
		if(!member.getTel().equals(""))
		{
			if(!CheckNumber.isNumeric(member.getTel())) 
				errors.add("Telefono: Formato non Valido");
		}
		
		if(errors.size() > 0) {
			
			// Ci sono errori, rimandare alla pagina mostrandoli
			return errors;
			
		}
		else
		{
			//eseguire la registrazione 
			
			//genero un regCode
			String regCode = Long.toHexString(Double.doubleToLongBits(Math.random()));
			member.setRegCode(regCode);
			
			//Inserisco lo stato		
			MemberStatus mStatus = memberStatusInterface.getMemberStatus(MemberStatuses.NOT_VERIFIED);
			member.setMemberStatus(mStatus);
			
			//setto la data odierna
			Calendar calendar = Calendar.getInstance();     
			SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
			String sDate = dateFormat.format(calendar.getTime());
			Date regDate = dateFormat.parse(sDate);
			member.setRegDate(regDate);
			
			idMember = memberInterface.newMember(member);
			
			if(idMember < 1)
				errors.add("Errore Interno: la registrazione non è andata a buon fine");
			else {
				
				//Inviare qui la mail con il codice di registrazione.
				SendEmail emailer = new SendEmail();
				
				boolean fromAdmin = true;
				
				String mailTo = member.getEmail();
				String subject = "Conferma mail per GasProject.net";
				String body = emailer.getBodyForAuth(member.getName(), member.getSurname(), regCode, idMember, fromAdmin);
				SendEmail.send(mailTo, subject, body);	
				
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

	@RequestMapping(value = "/ajax/getmembers", method = RequestMethod.GET)
	public @ResponseBody
	List<Member> getMembers(HttpServletRequest request)
	{
		List<Member> membersList = null;
		membersList = memberInterface.getMembers();
		return membersList;
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
