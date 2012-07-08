package it.polito.ai.lhmf.model;

import it.polito.ai.lhmf.exceptions.InvalidParametersException;
import it.polito.ai.lhmf.orm.Supplier;

import java.util.List;

import org.hibernate.SessionFactory;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

public class SupplierInterface
{
	//The session factory will be automatically injected by spring
	private SessionFactory sessionFactory;
	
	public void setSessionFactory(SessionFactory sf){
		this.sessionFactory = sf;
	}
	
	@Transactional(propagation=Propagation.REQUIRED)
	public Integer newSupplier(Supplier supplier) throws InvalidParametersException
	{
		if (supplier == null)
			throw new InvalidParametersException();

		return (Integer) sessionFactory.getCurrentSession().save(supplier);
	}

	@SuppressWarnings("unchecked")
	@Transactional(readOnly=true)
	public List<Supplier> getSuppliers()
	{
		return sessionFactory.getCurrentSession().createQuery("from Supplier").list();
	}
}
