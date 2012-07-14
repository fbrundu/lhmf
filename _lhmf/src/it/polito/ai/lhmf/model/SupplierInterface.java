package it.polito.ai.lhmf.model;

import it.polito.ai.lhmf.exceptions.InvalidParametersException;
import it.polito.ai.lhmf.orm.Supplier;

import java.util.List;

import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

public class SupplierInterface
{
	// The session factory will be automatically injected by spring
	private SessionFactory sessionFactory;

	public void setSessionFactory(SessionFactory sf)
	{
		this.sessionFactory = sf;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public Integer newSupplier(Supplier supplier)
			throws InvalidParametersException
	{
		if (supplier == null)
			throw new InvalidParametersException();

		return (Integer) sessionFactory.getCurrentSession().save(supplier);
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
		Query query = sessionFactory.getCurrentSession().createQuery(
				"from Supplier " + "where username = :username");
		query.setParameter("username", username);
		return (Supplier) query.uniqueResult();
	}

	@SuppressWarnings("unchecked")
	@Transactional(readOnly = true)
	public List<Supplier> getSuppliers()
	{
		return sessionFactory.getCurrentSession().createQuery("from Supplier")
				.list();
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public Integer updateSupplier(Supplier supplier)
			throws InvalidParametersException
	{
		if (supplier == null)
			throw new InvalidParametersException();

		Query query = sessionFactory.getCurrentSession().createQuery(
				"update Supplier"
						+ "set name = :name,"
						+ "surname = :surname,"
						+ "username = :username"
						// + "password = :password"
						// + "regCode = :regCode" + "regDate = :regDate"
						+ "email = :email" + "address = :address"
						+ "city = :city" + "state = :state" + "cap = :cap"
						+ "tel = :tel" + "active = :active"
						+ "memberType = :memberType"
						+ "companyName = :companyName"
						+ "description = :description"
						+ "contactName = :contactName" + "fax = :fax"
						+ "website = :website"
						+ "paymentMethod = :paymentMethod"
						+ "idMember_resp = :idMember_resp"
						+ "where idMember = :idSupplier");
		query.setParameter("name", supplier.getName());
		query.setParameter("surname", supplier.getSurname());
		query.setParameter("username", supplier.getUsername());
		// query.setParameter("password", supplier.getPassword());
		// query.setParameter("regCode", supplier.getRegCode());
		// query.setParameter("regDate", supplier.getRegDate());
		query.setParameter("email", supplier.getEmail());
		query.setParameter("address", supplier.getAddress());
		query.setParameter("city", supplier.getCity());
		query.setParameter("state", supplier.getState());
		query.setParameter("cap", supplier.getCap());
		query.setParameter("tel", supplier.getTel());
		query.setParameter("active", supplier.isActive());
		//FIXME non dovrebbe essere di tipo MemberType?
		query.setParameter("memberType", supplier.getMemberType());
		query.setParameter("companyName", supplier.getCompanyName());
		query.setParameter("description", supplier.getDescription());
		query.setParameter("contactName", supplier.getContactName());
		query.setParameter("fax", supplier.getFax());
		query.setParameter("website", supplier.getWebsite());
		query.setParameter("paymentMethod", supplier.getPaymentMethod());
		query.setParameter("idMember_resp", supplier.getMember().getIdMember());
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
				"delete from Supplier" + "where idMember = :idSupplier");

		query.setParameter("idSupplier", idSupplier);

		return (Integer) query.executeUpdate();
	}
}
