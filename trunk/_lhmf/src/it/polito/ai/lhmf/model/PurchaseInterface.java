package it.polito.ai.lhmf.model;

import java.util.Calendar;
import java.util.List;

import it.polito.ai.lhmf.exceptions.InvalidParametersException;
import it.polito.ai.lhmf.orm.Order;
import it.polito.ai.lhmf.orm.Purchase;

import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

public class PurchaseInterface 
{

	private SessionFactory sessionFactory;
	
	public void setSessionFactory(SessionFactory sf)
	{
		this.sessionFactory = sf;
	}
	
	@Transactional(propagation=Propagation.REQUIRED)
	public Integer newPurchase(Purchase purchase)
			throws InvalidParametersException
	{
		if (purchase == null)
		{
			throw new InvalidParametersException();
		}
		return (Integer) sessionFactory.getCurrentSession().save(purchase);
	}
	
	@SuppressWarnings("unchecked")
	@Transactional(readOnly=true)
	public List<Purchase> getPastPurchase()
	{ 
		Query queryOrders = sessionFactory.getCurrentSession().createQuery("from Order " + "where date_close <= :date_close"); 
		String dateToQuery = new String(Calendar.YEAR+""+Calendar.MONTH+""+Calendar.DAY_OF_MONTH);
		List<Order> orders = queryOrders.setParameter("date_close", dateToQuery).list();
		Query queryPurchase = sessionFactory.getCurrentSession().createQuery("from Purchase " + "where idPurchase = :idPurchase");
		return queryPurchase.setParameterList("idPurchase", orders).list();
	}
	
	@SuppressWarnings("unchecked")
	@Transactional(readOnly=true)
	public List<Purchase> getActivePurchase()
	{
		List<Order> orders = sessionFactory.getCurrentSession().createQuery("from Order " + "where date_close = 0").list();
		Query queryPurchase = sessionFactory.getCurrentSession().createQuery("from Purchase " + "where idPurchase = :idPurchase");
		return queryPurchase.setParameterList("idPurchase", orders).list();
	}	
}
