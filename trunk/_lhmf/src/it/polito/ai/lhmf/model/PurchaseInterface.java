package it.polito.ai.lhmf.model;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import it.polito.ai.lhmf.exceptions.InvalidParametersException;
import it.polito.ai.lhmf.orm.Member;
import it.polito.ai.lhmf.orm.Order;
import it.polito.ai.lhmf.orm.Product;
import it.polito.ai.lhmf.orm.Purchase;
import it.polito.ai.lhmf.orm.PurchaseProduct;

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
		List<Order> orders = sessionFactory.getCurrentSession().createQuery("from Order " + "where date_delivery = 0").list();
		Query queryPurchase = sessionFactory.getCurrentSession().createQuery("from Purchase " + "where idPurchase = :idPurchase");
		return queryPurchase.setParameterList("idPurchase", orders).list();
	}
	
	@SuppressWarnings("unchecked")
	@Transactional(readOnly = true)
	public List<Purchase> getPurchasesByMember(String username)
	{
		Query query = sessionFactory.getCurrentSession().createQuery(
				"from Member where username = :username");
		query.setParameter("username", username);
		Member member = (Member) query.uniqueResult();
		if (member != null)
		{
			query = sessionFactory.getCurrentSession().createQuery(
					"from Purchase "
							+ "where idMember = :idMember");
			query.setParameter("idMember", member.getIdMember());
			return query.list();
		}
		else
			return null;
	}
	
	@Transactional(readOnly = true)
	public List<Purchase> getPurchasesOnDate(Integer idMember, List<Order> orderTmp)
	{
		
		Query query = sessionFactory.getCurrentSession().createQuery("from Purchase where idMember = :idMember " + 
																	 "AND idOrder = :idOrder");
		query.setParameter("idMember", idMember.intValue());
		System.out.println("Member "+idMember.intValue());
		List<Purchase> activePurchases = new LinkedList<Purchase>();
		Purchase purTmp = new Purchase();
		for(Order or : orderTmp)
		{
			System.out.println("Ordine "+or.getIdOrder());
			query.setParameter("idOrder", or.getIdOrder());
			if((purTmp = (Purchase)query.uniqueResult()) != null)
			{
				activePurchases.add(purTmp);
			}
		}		
		return activePurchases;
	}

	@Transactional(readOnly = true)
	public Purchase getPurchase(int idPurchase) {
		
		Query query = sessionFactory.getCurrentSession().createQuery(
				"from Purchase " + "where idPurchase = :idPurchase");
		query.setParameter("idPurchase", idPurchase);
		return (Purchase) query.uniqueResult();
	}
	
	@Transactional(readOnly = true)
	public List<Product> getProducts(int idPurchase) {
		
		Purchase purchase;
		
		Query query = sessionFactory.getCurrentSession().createQuery(
				"from Purchase " + "where idPurchase = :idPurchase");
			query.setParameter("idPurchase", idPurchase);
		purchase = (Purchase) query.uniqueResult();
		
		Set<PurchaseProduct> purchaseProducts = purchase.getPurchaseProducts();
		
		List<Product> listProduct = new ArrayList<Product>();
		
		java.util.Iterator<PurchaseProduct> iter = purchaseProducts.iterator();
	    while (iter.hasNext())
	      listProduct.add(iter.next().getProduct());
		
		return listProduct;
	}

	@Transactional(readOnly = true)
	public Integer getAmount(int idPurchase, int idProduct) {
		
		
		Query query = sessionFactory.getCurrentSession().createQuery(
				"select amount from PurchaseProduct " + "where idPurchase = :idPurchase AND idProduct = :idProduct");
			query.setParameter("idPurchase", idPurchase);
			query.setParameter("idProduct", idProduct);
			
		Integer result = (Integer) query.uniqueResult();
		return result;
		
	}

	public Integer updatePurchase(Purchase purchase) throws InvalidParametersException {
		
		if (purchase == null)
			throw new InvalidParametersException();

		Query query = sessionFactory.getCurrentSession().createQuery(
				"update Purchase "
						+ "set isShipped = :isShipped "
						+ "where idPurchase = :idPurchase");
		query.setParameter("isShipped", purchase.getIsShipped());
		query.setParameter("idPurchase", purchase.getIdPurchase());

		return (Integer) query.executeUpdate();
	}
}