package it.polito.ai.lhmf.model;

import it.polito.ai.lhmf.exceptions.InvalidParametersException;
import it.polito.ai.lhmf.exceptions.NoHibernateSessionException;
import it.polito.ai.lhmf.orm.Supplier;

import java.util.List;

import org.hibernate.Session;

public abstract class SupplierInterface
{
	public static Integer newSupplier(Session hibernateSession,
			Supplier supplier) throws NoHibernateSessionException,
			InvalidParametersException
	{
		if (hibernateSession == null)
			throw new NoHibernateSessionException();

		if (supplier == null)
			throw new InvalidParametersException();

		return (Integer) hibernateSession.save(supplier);
	}

	@SuppressWarnings("unchecked")
	public static List<Supplier> getSuppliers(Session hibernateSession)
			throws NoHibernateSessionException
	{
		if (hibernateSession == null)
			throw new NoHibernateSessionException();

		return hibernateSession.createQuery("from Supplier").list();
	}
}
