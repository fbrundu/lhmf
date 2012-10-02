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

import org.hibernate.LockMode;
import org.hibernate.LockOptions;
import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

public class PurchaseInterface 
{
	
	private SessionFactory sessionFactory;
	
	
	private ProductInterface productInterface;
	private OrderInterface orderInterface;
	
	public void setSessionFactory(SessionFactory sf)
	{
		this.sessionFactory = sf;
	}
	
	public void setProductInterface(ProductInterface pi)
	{
		this.productInterface = pi;
	}
	
	public void setOrderInterface(OrderInterface oi)
	{
		this.orderInterface = oi;
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

	@Transactional(propagation=Propagation.REQUIRED)
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
		return (PurchaseProductId) sessionFactory.getCurrentSession().save(purchaseProduct);
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
				"select p.idPurchase, sum(pp.amount*(prod.unitCost)) " +
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

	@Transactional(propagation=Propagation.REQUIRED)
	public Integer updatePurchaseProduct(PurchaseProduct purchaseProduct) throws InvalidParametersException {
	
		if (purchaseProduct == null)
			throw new InvalidParametersException();

		Query query = sessionFactory.getCurrentSession().createQuery(
				"update PurchaseProduct "
						+ "set amount = :amount, " +
						  "insertedTimestamp = :insertedTimestamp "
						+ "where purchase = :purchase " +
						  "AND product = :product");
		
		query.setParameter("amount", purchaseProduct.getAmount());
		query.setParameter("insertedTimestamp", purchaseProduct.getInsertedTimestamp());
		query.setParameter("purchase", purchaseProduct.getPurchase());
		query.setParameter("product", purchaseProduct.getProduct());


		return (Integer) query.executeUpdate();
		
	}

	@Transactional(propagation=Propagation.REQUIRED)
	public Integer deletePurchaseProduct(Purchase purchase, Product product) throws InvalidParametersException {
		
		if (purchase == null || product == null )
			throw new InvalidParametersException();

		Query query = sessionFactory.getCurrentSession().createQuery(
						  "delete from PurchaseProduct " +
						  "where purchase = :purchase " +
						  "AND product = :product");
		
		query.setParameter("purchase", purchase);
		query.setParameter("product", product);

		return (Integer) query.executeUpdate();
	}

	@Transactional(propagation=Propagation.REQUIRED)
	public Integer deletePurchase(Purchase purchase) throws InvalidParametersException {
		
		if (purchase == null)
			throw new InvalidParametersException();

		Query query = sessionFactory.getCurrentSession().createQuery(
						  "delete from Purchase " +
						  "where idPurchase = :idPurchase");
		
		query.setParameter("idPurchase", purchase.getIdPurchase());

		return (Integer) query.executeUpdate();
		
	}

	@Transactional(propagation=Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED)
	public Integer createPurchase(Member memberNormal, Integer idOrder,
			Integer[] ids, Integer[] amounts) throws InvalidParametersException {
		Order order = orderInterface.getOrder(idOrder);
		
		if(order == null)
			return -1;
		
		sessionFactory.getCurrentSession().buildLockRequest(new LockOptions(LockMode.PESSIMISTIC_WRITE)).lock(order);
		
		boolean available = false;
		List<Order> availableOrders = orderInterface.getAvailableOrders(memberNormal);
		
		//Verifica che l'utente non abbia già compilato una scheda per quest'ordine
		for(Order tmp : availableOrders){
			if(tmp.getIdOrder() == order.getIdOrder()){
				available = true;
				break;
			}
		}
		
		if(!available)
			return -1;
		
		for( int i = 0; i < ids.length; i++) 
		{	
			Product product = productInterface.getProduct(ids[i]);
			Integer amount = amounts[i];
			
			if(product == null || amount == null || amount <= 0)
				return -1;
			
			Integer maxBuy = product.getMaxBuy();
			
			//Controllo disponibilità quantità richiesta
			if(maxBuy != null){
				Integer alreadyBought = orderInterface.getTotalAmountOfProduct(order, product);//orderInterface.getBoughtAmounts(order.getIdOrder(), Collections.singletonList(product.getIdProduct())).get(0);
				if(amount > maxBuy - alreadyBought)
					return -1;
			}
		}
		
		Purchase purchase = new Purchase(order, memberNormal);
		
		int result;
		if((result = newPurchase(purchase)) <= 0)
		{
			return result;
		}
		
		// setto la data odierna
		Calendar calendar = Calendar.getInstance();
		Date insertedTimestamp = calendar.getTime();
		
		for( int i = 0; i < ids.length; i++) 
		{
			Product product = productInterface.getProduct(ids[i]);
			PurchaseProductId id = new PurchaseProductId(purchase.getIdPurchase(), product.getIdProduct());
			PurchaseProduct purchaseproduct = new PurchaseProduct(id, purchase, product, amounts[i], insertedTimestamp);				
			//In questo caso, dato che l'id non è generato ma già passato, se ci sono errori lancia un'eccezione
			newPurchaseProduct(purchaseproduct);
		}		
		return 1;
	}

	@Transactional(propagation=Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED)
	public Integer insertProduct(Member memberNormal, Integer idPurchase,
			Integer idProduct, Integer amountProduct) throws InvalidParametersException {
		Integer disp = null;
		
		Product product = productInterface.getProduct(idProduct);
		
		if(product == null)
			return -1;
		
		Integer maxBuy = product.getMaxBuy();
		
		if(maxBuy == null)
			disp = -1;
		
		Purchase purchase = getPurchase(idPurchase);
		if(purchase == null)
			return -1;
		
		if(purchase.getMember().getIdMember() != memberNormal.getIdMember())
			return -1;
		
		Order order = purchase.getOrder();
		
		sessionFactory.getCurrentSession().buildLockRequest(new LockOptions(LockMode.PESSIMISTIC_WRITE)).lock(order);
		
		if(disp == null){
			Integer totAmount = (int) (long) orderInterface.getTotalAmountOfProduct(order, product);
			disp = maxBuy - totAmount;
		}
		
		int error = 0;
		
		if(disp == -1) {
			if(amountProduct <= 0)
				error = 1;
		} else if(amountProduct > disp || amountProduct <= 0)
				error = 1;
			
		if(error == 1)	
			return -2;
		
		// setto la data odierna
		Calendar calendar = Calendar.getInstance();
		Date insertedTimestamp = calendar.getTime();
		
		PurchaseProductId id = new PurchaseProductId(idPurchase, idProduct);
		PurchaseProduct purchaseproduct = new PurchaseProduct(id, purchase, product, amountProduct, insertedTimestamp);	
		newPurchaseProduct(purchaseproduct);
		
		return 1;
	}

	@Transactional(propagation=Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED)
	public Integer updateProduct(Member memberNormal, Integer idPurchase,
			Integer idProduct, Integer amountProduct) throws InvalidParametersException {
		// 1 = prenotazione aggiornata
		// 0 = prodotto non trovato
		// -1 = Quantità non disponibile
		// -2 = valore non idoneo
		
		int result = 0;
		
		Integer disp = null;
		
		Product product = productInterface.getProduct(idProduct);
		
		if(product == null)
			return -1;
		
		Integer maxBuy = product.getMaxBuy();
		
		if(maxBuy == null)
			disp = -1;
		
		Purchase purchase = getPurchase(idPurchase);
		if(purchase == null)
			return -1;
		
		if(purchase.getMember().getIdMember() != memberNormal.getIdMember())
			return -1;
		
		Order order = purchase.getOrder();
		
		sessionFactory.getCurrentSession().buildLockRequest(new LockOptions(LockMode.PESSIMISTIC_WRITE)).lock(order);
		
		if(disp == null){
			Integer totAmount = (int) (long) orderInterface.getTotalAmountOfProduct(order, product);
			disp = maxBuy - totAmount;
		}
		
		if(amountProduct <= 0)
			return -2;
			
		Set<PurchaseProduct> ppSet = purchase.getPurchaseProducts();
		Product pTemp;
		
		for (PurchaseProduct ppTemp : ppSet) {
			
			pTemp = ppTemp.getProduct();
			
			if(pTemp.equals(product)) {
				
				Integer actualAmount = ppTemp.getAmount();
				
				if (disp != -1)
					if(amountProduct - actualAmount > disp)
						return -1;

				ppTemp.setAmount(amountProduct);
				result = updatePurchaseProduct(ppTemp);
				break;
			}
			
		}
		
		return result;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public Integer deletePurchaseProduct(Member memberNormal,
			Integer idPurchase, Integer idProduct) throws InvalidParametersException {
		// 1 = prenotazione aggiornata
		// 0 = prodotto non trovato
		Product product = productInterface.getProduct(idProduct);
		Purchase purchase = getPurchase(idPurchase);
		
		if(purchase.getMember().getIdMember() != memberNormal.getIdMember())
			return -1;
		
		Order order = purchase.getOrder();
		
		sessionFactory.getCurrentSession().buildLockRequest(new LockOptions(LockMode.PESSIMISTIC_WRITE)).lock(order);
		
		int res = deletePurchaseProduct(purchase, product);
		if(res != 1)
			return res;
		else{
			if(purchase.getPurchaseProducts().size() == 0){
				//è stato eliminato l'utlimo prodotto --> eliminare intera scheda
				
				res = deletePurchase(purchase);
				if(res == 1)
					return res;
				else
					throw new InvalidParametersException();
			}
			else
				return res;
		}
	}

}