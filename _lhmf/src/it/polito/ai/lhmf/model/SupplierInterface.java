package it.polito.ai.lhmf.model;

import it.polito.ai.lhmf.exceptions.InvalidParametersException;
import it.polito.ai.lhmf.model.constants.MemberStatuses;
import it.polito.ai.lhmf.model.constants.MemberTypes;
import it.polito.ai.lhmf.orm.Member;
import it.polito.ai.lhmf.orm.MemberStatus;
import it.polito.ai.lhmf.orm.MemberType;
import it.polito.ai.lhmf.orm.Supplier;
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

import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

public class SupplierInterface
{
	// The session factory will be automatically injected by spring
	private SessionFactory sessionFactory;
	private MemberInterface memberInterface;
	
	public void setSessionFactory(SessionFactory sf)
	{
		this.sessionFactory = sf;
	}
	
	public void setMemberInterface(MemberInterface memberInterface)
	{
		this.memberInterface = memberInterface;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public Integer newSupplier(Supplier supplier)
			throws InvalidParametersException
	{
		if (supplier == null)
			throw new InvalidParametersException();

		return (Integer) sessionFactory.getCurrentSession().save(supplier);
	}
	
	@Transactional(propagation = Propagation.REQUIRED)
	public List<String> newSupplier(String username, String firstname,
			String lastname, String email, String address, String city,
			String state, String cap, String phone, int memberType,
			String company, String description, String contactName, String fax,
			String website, String payMethod, int idResp)
	{
		ArrayList<String> errors = new ArrayList<String>();
		try
		{
			Integer idMember = -1;

	
			if (username.equals(""))
			{
				errors.add("Username: Formato non Valido");
			}
			else
			{
				if (memberInterface.isMemberPresentByUsername(username))
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
				// Controllo email gia' in uso
				if (memberInterface.isMemberPresentByEmail(email))
					errors.add("Email: Email gi&agrave; utilizzata da un altro account");
			}
			if (address.equals("") || CheckNumber.isNumeric(address))
			{
				errors.add("Indirizzo: Formato non Valido");
			}
			if (city.equals("") || CheckNumber.isNumeric(city))
			{
				errors.add("Citt&agrave;: Formato non Valido");
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

				// Genero la password
				String alfaString = Long.toHexString(Double
						.doubleToLongBits(Math.random()));
				String password = alfaString.substring(4, 12);

				// Creo un nuovo membro-fornitore

				String md5Password = CreateMD5.MD5(password);

				MemberType mType = new MemberType(MemberTypes.USER_SUPPLIER);
				MemberStatus mStatus = new MemberStatus(
						MemberStatuses.NOT_VERIFIED);

				Member memberSupplier = new Member(mType, mStatus, firstname,
						lastname, username, md5Password, regCode, regDate,
						email, address, city, state, cap, true);

				Supplier supplier = new Supplier(memberSupplier, memberResp,
						payMethod);

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

				// second argument is not used here
				idMember = memberInterface.newMember(memberSupplier, false,
						true);
				if (idMember < 0)
				{
					errors.add("Errore Interno: la registrazione non &egrave andata a buon fine");
				}
				else
				{
					idMember = -1;
					idMember = newSupplier(supplier);

					if (idMember < 0)
						errors.add("Errore Interno: la registrazione non &egrave andata a buon fine");
					else
					{
						// Inviare qui la mail con il codice di registrazione e
						// la
						// password generata
						 SendEmail emailer = new SendEmail(); 
						 emailer.sendAdminRegistration(firstname + " " + lastname, username, password, regCode, idMember, email);

					}
				}
			}
		}
		catch (NoSuchAlgorithmException e)
		{
			e.printStackTrace();
			errors.add("No such algorithm exception");
		}
		catch (UnsupportedEncodingException e)
		{
			e.printStackTrace();
			errors.add("UnsupportedEncodingException");
		}
		catch (ParseException e)
		{
			e.printStackTrace();
			errors.add("ParseException");
		}
		catch (InvalidParametersException e)
		{
			e.printStackTrace();
			errors.add("InvalidParametersException");
		}
		catch(Exception e)
		{
			e.printStackTrace();
			errors.add("Generic exception");
		}
		return errors;
	}

	@Transactional(readOnly = true)
	public Supplier getSupplier(Integer idSupplier)
	{
		Query query = sessionFactory.getCurrentSession().createQuery(
				"from Supplier " + "where idMember = :idSupplier");
		query.setParameter("idSupplier", idSupplier);
		return (Supplier) query.uniqueResult();
	}

	@Transactional(readOnly = true)
	public Supplier getSupplier(String username)
	{
		if (username == null)
			return null;

		Query query = sessionFactory.getCurrentSession().createQuery(
				"select idMember " + "from Member "
						+ "where username = :username");
		query.setParameter("username", username);
		Integer idMember = (Integer) query.uniqueResult();
		if (idMember <= 0)
			return null;
		query = sessionFactory.getCurrentSession().createQuery(
				"from Supplier " + "where idMember = :idMember");
		query.setParameter("idMember", idMember);
		return (Supplier) query.uniqueResult();
	}

	@Transactional(readOnly = true)
	public Supplier getSupplierByMail(String email)
	{
		Query query = sessionFactory.getCurrentSession().createQuery(
				"from Supplier " + "where email = :email");
		query.setParameter("email", email);
		return (Supplier) query.uniqueResult();
	}

	@SuppressWarnings("unchecked")
	@Transactional(readOnly = true)
	public List<Supplier> getSuppliers()
	{
		return sessionFactory.getCurrentSession()
				.createQuery("from Supplier order by idMember").list();
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public Integer updateSupplier(Supplier supplier)
			throws InvalidParametersException
	{
		if (supplier == null)
			throw new InvalidParametersException();

		Query query = sessionFactory.getCurrentSession().createQuery(
				"update Supplier " + "set companyName = :companyName,"
						+ "description = :description,"
						+ "contactName = :contactName," + "fax = :fax,"
						+ "website = :website,"
						+ "paymentMethod = :paymentMethod,"
						+ "idMember_resp = :idMember_resp "
						+ "where idMember = :idSupplier");
		query.setParameter("companyName", supplier.getCompanyName());
		query.setParameter("description", supplier.getDescription());
		query.setParameter("contactName", supplier.getContactName());
		query.setParameter("fax", supplier.getFax());
		query.setParameter("website", supplier.getWebsite());
		query.setParameter("paymentMethod", supplier.getPaymentMethod());
		query.setParameter("idMember_resp", supplier.getMemberByIdMemberResp()
				.getIdMember());
		query.setParameter("idSupplier", supplier.getIdMember());

		return (Integer) query.executeUpdate();
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public Integer deleteSupplier(Integer idSupplier)
			throws InvalidParametersException
	{
		if (idSupplier == null)
			throw new InvalidParametersException();

		Query query = sessionFactory.getCurrentSession().createQuery(
				"delete from Supplier " + "where idMember = :idSupplier");

		query.setParameter("idSupplier", idSupplier);

		return (Integer) query.executeUpdate();
	}

	@SuppressWarnings("unchecked")
	@Transactional(readOnly = true)
	public List<Supplier> getSuppliersToActivate()
	{

		Query query = sessionFactory.getCurrentSession().createQuery(
				"from Supplier where active = :active order by idMember");

		query.setParameter("active", false);
		return (List<Supplier>) query.list();
	}

	@Transactional(readOnly = true)
	public Integer getIdSupplier(String username)
	{
		if (username == null)
			return -1;

		Query query = sessionFactory.getCurrentSession().createQuery(
				"from Supplier where username = :username");
		query.setString("username", username);
		return ((Supplier) query.uniqueResult()).getIdMember();
	}
}
