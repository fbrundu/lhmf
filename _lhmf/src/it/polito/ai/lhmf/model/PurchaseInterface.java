package it.polito.ai.lhmf.model;

import java.util.List;

import it.polito.ai.lhmf.exceptions.InvalidParametersException;
import it.polito.ai.lhmf.orm.Purchase;

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
		//Query query = sessionFactory.getCurrentSession().createQuery("from Order" + "where date_close <= :date_close"); 
		//String dateToQuery = new String(Calendar.YEAR+""+Calendar.MONTH+""+Calendar.DAY_OF_MONTH);
		//query.setParameter("date_close", dateToQuery);
		//return query.list();
		return sessionFactory.getCurrentSession().createQuery("from Purchase").list();
	}
	
	@SuppressWarnings("unchecked")
	@Transactional(readOnly=true)
	public List<Purchase> getActivePurchase()
	{
		return sessionFactory.getCurrentSession().createQuery("from Purchase").list();
	}
	
	
}
