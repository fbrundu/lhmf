package it.polito.ai.lhmf.controllers.ajax;

import it.polito.ai.lhmf.exceptions.InvalidParametersException;
import it.polito.ai.lhmf.model.MemberInterface;
import it.polito.ai.lhmf.model.OrderInterface;
import it.polito.ai.lhmf.model.SupplierInterface;
import it.polito.ai.lhmf.model.constants.MemberStatuses;
import it.polito.ai.lhmf.model.constants.MemberTypes;
import it.polito.ai.lhmf.orm.Member;
import it.polito.ai.lhmf.orm.MemberStatus;
import it.polito.ai.lhmf.orm.MemberType;
import it.polito.ai.lhmf.orm.Order;
import it.polito.ai.lhmf.orm.Supplier;
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
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class SupplierAjaxController
{
	@Autowired
	private SupplierInterface supplierInterface;
	@Autowired
	private MemberInterface memberInterface;
	@Autowired
	private OrderInterface orderInterface;
	
	@PreAuthorize("hasRole('" + MyUserDetailsService.UserRoles.ADMIN + "')")
	@RequestMapping(value = "/ajax/newSupplier", method = RequestMethod.POST)
	public @ResponseBody
	List<String> newSupplier(
			HttpServletRequest request,
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
			@RequestParam(value = "payMethod", required = true) String payMethod,
			@RequestParam(value = "idResp", required = false) int idResp)
			throws InvalidParametersException, ParseException
	{
		Integer idMember = -1;

		ArrayList<String> errors = new ArrayList<String>();

		if (username.equals(""))
		{
			errors.add("Username: Formato non Valido");
		}
		else
		{
			Member memberControl = memberInterface.getMember(username);

			if (memberControl != null)
				errors.add("Username: non disponibile");
		}
		if (firstname.equals("") || CheckNumber.isNumeric(firstname))
		{
			errors.add("Nome: Formato non Valido");
		}
		if (lastname.equals("") || CheckNumber.isNumeric(lastname))
		{
			errors.add("Cognome: Formato non Valido");
		}
		if (!SendEmail.isValidEmailAddress(email))
		{
			errors.add("Email: Formato non Valido");
		}
		else
		{
			// Controllo email gi� in uso

			Member memberControl = memberInterface.getMemberByEmail(email);

			if (memberControl != null)
				errors.add("Email: Email gi&agrave utilizzata da un altro account");
		}
		if (address.equals("") || CheckNumber.isNumeric(address))
		{
			errors.add("Indirizzo: Formato non Valido");
		}
		if (city.equals("") || CheckNumber.isNumeric(city))
		{
			errors.add("Citt&agrave: Formato non Valido");
		}
		if (state.equals("") || CheckNumber.isNumeric(state))
		{
			errors.add("Stato: Formato non Valido");
		}
		if (!phone.equals("") && !phone.equals("not set"))
		{
			if (!CheckNumber.isPhoneNumberValid(phone))
				errors.add("Telefono: Formato non Valido");
		}
		if (cap.equals("") || !CheckNumber.isNumeric(cap))
		{
			errors.add("Cap: Formato non Valido");
		}
		if (!company.equals("") && CheckNumber.isNumeric(company))
		{
			errors.add("Compagnia: Formato non Valido");
		}
		if (!description.equals("") && CheckNumber.isNumeric(description))
		{
			errors.add("Descrizione: Formato non Valido");
		}
		if (!contactName.equals("") && CheckNumber.isNumeric(contactName))
		{
			errors.add("Nome Contatto: Formato non Valido");
		}
		if (!fax.equals(""))
		{
			if (!CheckNumber.isNumeric(fax))
				errors.add("Fax: Formato non Valido");
		}
		if (!website.equals("") && CheckNumber.isNumeric(website))
		{
			errors.add("Web Site: Formato non Valido");
		}
		if (CheckNumber.isNumeric(payMethod))
		{
			errors.add("Metodo Pagamento: Formato non Valido");
		}

		if (errors.size() > 0)
		{
			// Ci sono errori, rimandare alla pagina mostrandoli
			return errors;
		}
		else
		{

			// Registrazione Fornitore

			// genero un regCode
			String regCode = Long.toHexString(Double.doubleToLongBits(Math
					.random()));

			// setto la data odierna
			Calendar calendar = Calendar.getInstance();
			SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
			String sDate = dateFormat.format(calendar.getTime());
			Date regDate = dateFormat.parse(sDate);

			// Mi ricavo il membro responsabile
			Member memberResp = memberInterface.getMember(idResp);

			//Genero la password
			String alfaString = Long.toHexString(Double.doubleToLongBits(Math
					.random()));
			String password = alfaString.substring(4, 12);
			
			// Creo un nuovo membro-fornitore

			try
			{
				String md5Password = CreateMD5.MD5(password);
				
				MemberType mType = new MemberType(MemberTypes.USER_SUPPLIER);
				MemberStatus mStatus = new MemberStatus(MemberStatuses.NOT_VERIFIED);

				Member memberSupplier = new Member(	mType, mStatus, firstname, lastname, username,
													md5Password, regCode, regDate, email, address, 
													city, state, cap, true);

				Supplier supplier = new Supplier(memberSupplier, memberResp, payMethod);

				if (!phone.equals("") && !phone.equals("not set"))
					memberSupplier.setTel(phone);
				if (!company.equals(""))
					supplier.setCompanyName(company);
				if (!description.equals(""))
					supplier.setDescription(description);
				if (!contactName.equals(""))
					supplier.setContactName(contactName);
				if (!fax.equals(""))
					supplier.setFax(fax);
				if (!website.equals(""))
					supplier.setWebsite(website);
				
				idMember = memberInterface.newMember(memberSupplier);
				if(idMember < 0) {
					errors.add("Errore Interno: la registrazione non &egrave andata a buon fine");
				} else {
					idMember = -1;
					idMember = supplierInterface.newSupplier(supplier);
					
					if (idMember < 0)
						errors.add("Errore Interno: la registrazione non &egrave andata a buon fine");
					else
					{

						// Inviare qui la mail con il codice di registrazione e la
						// password generata
						/*
						 * SendEmail emailer = new SendEmail(); boolean isSupplier =
						 * true; emailer.sendAdminRegistration(firstname + " " +
						 * lastname, username, password, regCode, idMember, email,
						 * isSupplier);
						 */
					}
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

	@PreAuthorize("hasRole('" + MyUserDetailsService.UserRoles.SUPPLIER + "')")
	@RequestMapping(value = "/ajax/getOrderSupplier", method = RequestMethod.POST)
	public @ResponseBody
	List<Order> getOrderSupplier(HttpServletRequest request, HttpSession session,
			@RequestParam(value = "start") long start)
			throws InvalidParametersException
	{
		String username = (String) session.getAttribute("username");

		Member supplier = memberInterface.getMember(username);

		List<Order> listOrder = null;
		listOrder = orderInterface.getOrdersBySupplier(start, supplier);
		return listOrder;
	}
	
	@PreAuthorize("hasRole('" + MyUserDetailsService.UserRoles.SUPPLIER + "')")
	@RequestMapping(value = "/ajax/getmyidsupplier", method = RequestMethod.GET)
	public @ResponseBody
	Integer getMyIdSupplier(HttpServletRequest request)
	{
		Integer idSupplier = -1;
		idSupplier = supplierInterface.getIdSupplier((String) request
				.getAttribute("user"));
		return idSupplier;
	}

	@PreAuthorize("hasRole('" + MyUserDetailsService.UserRoles.SUPPLIER + "')")
	@RequestMapping(value = "/ajax/getmesupplier", method = RequestMethod.GET)
	public @ResponseBody
	Supplier getMeSupplier(HttpServletRequest request)
	{
		Supplier supplier = null;
		supplier = supplierInterface.getSupplier((String) request
				.getAttribute("user"));
		return supplier;
	}
	
	// TODO get da chi può essere fatto?
	@PreAuthorize("hasRole('" + MyUserDetailsService.UserRoles.ADMIN + "')")
	@RequestMapping(value = "/ajax/getsupplier", method = RequestMethod.GET)
	public @ResponseBody
	Supplier getSupplier(HttpServletRequest request,
			@RequestBody Integer idSupplier)
	{
		Supplier supplier = null;
		supplier = supplierInterface.getSupplier(idSupplier);
		return supplier;
	}

	// TODO get da chi può essere fatto?
	@PreAuthorize("hasRole('" + MyUserDetailsService.UserRoles.ADMIN + "')")
	@RequestMapping(value = "/ajax/getsuppliers", method = RequestMethod.GET)
	public @ResponseBody
	List<Supplier> getSuppliers(HttpServletRequest request)
	{
		List<Supplier> suppliersList = null;
		suppliersList = supplierInterface.getSuppliers();
		return suppliersList;
	}

	@PreAuthorize("hasRole('" + MyUserDetailsService.UserRoles.ADMIN + "')")
	@RequestMapping(value = "/ajax/updatesupplier", method = RequestMethod.POST)
	public @ResponseBody
	Integer updateSupplier(HttpServletRequest request,
			@RequestBody Supplier supplier) throws InvalidParametersException
	{
		Integer rowsAffected = -1;
		rowsAffected = supplierInterface.updateSupplier(supplier);
		return rowsAffected;
	}

	@PreAuthorize("hasRole('" + MyUserDetailsService.UserRoles.ADMIN + "')")
	@RequestMapping(value = "/ajax/deletesupplier", method = RequestMethod.POST)
	public @ResponseBody
	Integer deleteSupplier(HttpServletRequest request,
			@RequestBody Integer idSupplier) throws InvalidParametersException
	{
		Integer rowsAffected = -1;
		rowsAffected = supplierInterface.deleteSupplier(idSupplier);
		return rowsAffected;
	}
}
