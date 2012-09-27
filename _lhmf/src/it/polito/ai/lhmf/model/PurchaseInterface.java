package it.polito.ai.lhmf.model;

import it.polito.ai.lhmf.exceptions.InvalidParametersException;
import it.polito.ai.lhmf.orm.Member;
import it.polito.ai.lhmf.orm.Order;
import it.polito.ai.lhmf.orm.Product;
import it.polito.ai.lhmf.orm.Purchase;
import it.polito.ai.lhmf.orm.PurchaseProduct;
import it.polito.ai.lhmf.orm.PurchaseProductId;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

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
			System.out.println("Ordine " + or.getIdOrder());
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
		
		Query query = sessionFactory.getCurrentSession().createQuery("from Purchase " + "where idPurchase = :idPurchase");
		query.setParameter("idPurchase", idPurchase);
		purchase = (Purchase) query.uniqueResult();
		
		Set<PurchaseProduct> purchaseProducts = purchase.getPurchaseProducts();
		
		List<Product> listProduct = new ArrayList<Product>();
		
		java.util.Iterator<PurchaseProduct> iter = purchaseProducts.iterator();
	    while (iter.hasNext())
	      listProduct.add(iter.next().getProduct());
		
		return listProduct;
	}
	
	@SuppressWarnings("unchecked")
	@Transactional(readOnly = true)
	public List<PurchaseProduct> getPurchaseProduct(Integer idPurchase) throws InvalidParametersException 
	{
		if (idPurchase == null || idPurchase < 0)
			throw new InvalidParametersException();
		Query query = sessionFactory.getCurrentSession().createQuery("from PurchaseProduct " + "where idPurchase = :idPurchase");
		query.setParameter("idPurchase", idPurchase);
		return query.list();
	}

	
	@Transactional(readOnly = true)
	public Integer getAmount(int idPurchase, int idProduct) 
	{	
	
		Query query = sessionFactory.getCurrentSession().createQuery(
				"select amount from PurchaseProduct " + "where idPurchase = :idPurchase AND idProduct = :idProduct");
			query.setParameter("idPurchase", idPurchase);
			query.setParameter("idProduct", idProduct);
			
		Integer result = (Integer) query.uniqueResult();
		return result;	
	}

	public Integer updatePurchase(Purchase purchase) throws InvalidParametersException 
	{
		
		if (purchase == null)
			throw new InvalidParametersException();

		Query query = sessionFactory.getCurrentSession().createQuery(
				"update Purchase "
						+ "set isShipped = :isShipped "
						+ "where idPurchase = :idPurchase");
		query.setParameter("isShipped", purchase.isIsShipped());
		query.setParameter("idPurchase", purchase.getIdPurchase());

		return (Integer) query.executeUpdate();
	}
	
	@Transactional(propagation=Propagation.REQUIRED)
	public PurchaseProductId newPurchaseProduct(PurchaseProduct purchaseProduct)
			throws InvalidParametersException
	{
		if (purchaseProduct == null)
		{
			throw new InvalidParametersException();
		}
		return (PurchaseProductId) sessionFactory.getCurrentSession().save(purchaseProduct); //TODO Non cast a Integer ma a PurchaseProductId!!!
	}
	
	@Transactional(readOnly = true)
	public PurchaseProduct getPurchaseProductFromId(Integer idPurchase, Integer idProduct)
	{
		Query query = sessionFactory.getCurrentSession().createQuery(
				"from PurchaseProduct where idPurchase = :idPurchase " + 
						"AND idProduct = :idProduct");
		query.setParameter("idPurchase", idPurchase);
		query.setParameter("idProduct", idProduct);
		return (PurchaseProduct)query.uniqueResult();
	}

	//@SuppressWarnings("rawtypes")
	@SuppressWarnings("rawtypes")
	@Transactional(readOnly=true)
	public ArrayList<Double> getSumAndAvgOfPurchasePerMonth(Member memberNormal, int year, int month) {
		
		ArrayList<Double> mList = new ArrayList<Double>();
		
		Calendar cal = Calendar.getInstance();
		cal.set(year, month, 1);
		Date startDateD = cal.getTime();
		cal.set(year, month, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
		Date endDateD = cal.getTime();
		
		Timestamp startDate = new Timestamp(startDateD.getTime());
		Timestamp endDate = new Timestamp(endDateD.getTime());
		
		Query query = sessionFactory.getCurrentSession().createQuery(
				"select p.idPurchase, sum(pp.amount*(prod.unitBlock * prod.unitCost)) " +
				"from Purchase as p " +
				"join p.purchaseProducts as pp " +
				"join p.member as m " +
				"join p.order as o " +
				"join pp.product as prod " +
			    "where m.idMember = :memberNormal " +
				" AND o.dateClose between :startDate and :endDate " +
				" AND o.dateDelivery is NOT NULL " +
				"group by p.idPurchase");
		
		query.setParameter("memberNormal", memberNormal.getIdMember());
		query.setTimestamp("startDate", startDate);
		query.setDate("endDate", endDate);
		
		int count = 0;
		Double tempSum = (double) 0;
		
		for (Iterator it = query.iterate(); it.hasNext();) {
			Object[] row = (Object[]) it.next();
			
			count++;
			tempSum += (Double) row[1];
			
		}
		
		if(tempSum == 0)
			mList.add(0.1); 
		else
			mList.add(tempSum);
		
		if(count == 0)
			mList.add(0.1);
		else
			mList.add(tempSum/count);
		
		return mList;
	}

}