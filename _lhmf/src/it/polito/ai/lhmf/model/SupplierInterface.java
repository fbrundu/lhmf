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
	public Long getNumberItemsToActivate()
	{

		Query query = sessionFactory.getCurrentSession().createQuery(
				"select count(*) from Supplier where active = :active");

		query.setParameter("active", false);
		return (Long) query.uniqueResult();
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
