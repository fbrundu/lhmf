package it.polito.ai.lhmf.model;

import it.polito.ai.lhmf.exceptions.InvalidParametersException;
import it.polito.ai.lhmf.exceptions.NoHibernateSessionException;
import it.polito.ai.lhmf.orm.Product;

import java.util.List;

import org.hibernate.Session;

public abstract class ProductInterface
{
	public static Integer newProduct(Session hibernateSession, Product product)
			throws NoHibernateSessionException, InvalidParametersException
	{
		if (hibernateSession == null)
			throw new NoHibernateSessionException();

		if (product == null)
			throw new InvalidParametersException();

		return (Integer) hibernateSession.save(product);
	}

	@SuppressWarnings("unchecked")
	public static List<Product> getProducts(Session hibernateSession)
			throws NoHibernateSessionException
	{
		if (hibernateSession == null)
			throw new NoHibernateSessionException();

		return hibernateSession.createQuery("from Product").list();
	}
}
