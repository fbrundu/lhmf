package it.polito.ai.lhmf.model;

import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;

import it.polito.ai.lhmf.exceptions.NoHibernateSessionException;
import it.polito.ai.lhmf.orm.*;

public class HibernateInterface
{
	public static List<Product> getProducts(Session hibernateSession)
			throws NoHibernateSessionException
	{
		if (hibernateSession == null)
			throw new NoHibernateSessionException();
		
		Query query = hibernateSession.createQuery("from Product");
		@SuppressWarnings("unchecked")
		List<Product> rProductList = query.list();
		return rProductList;
	}
}
