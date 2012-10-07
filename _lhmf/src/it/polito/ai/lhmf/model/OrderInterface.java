package it.polito.ai.lhmf.model;

import it.polito.ai.lhmf.exceptions.InvalidParametersException;
import it.polito.ai.lhmf.model.constants.MemberTypes;
import it.polito.ai.lhmf.orm.Member;
import it.polito.ai.lhmf.orm.Notify;
import it.polito.ai.lhmf.orm.Order;
import it.polito.ai.lhmf.orm.OrderProduct;
import it.polito.ai.lhmf.orm.OrderProductId;
import it.polito.ai.lhmf.orm.Product;
import it.polito.ai.lhmf.orm.Purchase;
import it.polito.ai.lhmf.orm.PurchaseProduct;
import it.polito.ai.lhmf.orm.Supplier;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

public class OrderInterface
{
	private SessionFactory sessionFactory;
	private SupplierInterface supplierInterface;
	private ProductInterface productInterface;
	private MemberInterface memberInterface;
	private PurchaseInterface purchaseInterface;
	private NotifyInterface notifyInterface;
	private LogInterface logInterface;
	
	public void setSessionFactory(SessionFactory sf)
	{
		this.sessionFactory = sf;
	}

	public void setSupplierInterface(SupplierInterface supplierInterface) {
		this.supplierInterface = supplierInterface;
	}

	public void setProductInterface(ProductInterface productInterface) {
		this.productInterface = productInterface;
	}
	
	public void setMemberInterface(MemberInterface memberInterface) {
		this.memberInterface = memberInterface;
	}

	public void setPurchaseInterface(PurchaseInterface purchaseInterface)
	{
		this.purchaseInterface = purchaseInterface;
	}
	
	public void setNotifyInterface(NotifyInterface notifyInterface)
	{
		this.notifyInterface = notifyInterface;
	}
	
	public void setLogInterface(LogInterface logInterface)
	{
		this.logInterface = logInterface;
	}
	
	@Transactional(propagation=Propagation.REQUIRED)
	public Integer newOrder(Order order)
			throws InvalidParametersException
	{
		if (order == null)
			throw new InvalidParametersException();
		Integer newOrderId = (Integer) sessionFactory.getCurrentSession().save(order);

		// Invia la notifica agli utenti normali
		for (Member m : memberInterface.getMembers(MemberTypes.USER_NORMAL))
		{
			Notify n = new Notify();
			n.setMember(m);
			n.setIsReaded(false);
			// FIXME mettere costanti
			n.setNotifyCategory(2);
			n.setText(newOrderId.toString());
			n.setNotifyTimestamp(new Date());
			notifyInterface.newNotify(n);
		}

		// Invia la notifica agli utenti responsabili (che possono partecipare
		// all'ordine) eccetto il creatore dell'ordine
		for (Member m : memberInterface.getMembers(MemberTypes.USER_RESP))
		{
			if (order.getMember().getIdMember() != m.getIdMember())
			{
				Notify n = new Notify();
				n.setMember(m);
				n.setIsReaded(false);
				// FIXME mettere costanti
				n.setNotifyCategory(2);
				n.setText(newOrderId.toString());
				n.setNotifyTimestamp(new Date());
				notifyInterface.newNotify(n);
			}
		}
		
		logInterface.createLog("Ha creato l'ordine con id: " + newOrderId,
				order.getMember().getIdMember());

		return newOrderId;
	}

	@Transactional(readOnly = true)
	public Order getOrder(int idOrder)
	{
		Query query = sessionFactory.getCurrentSession().createQuery("from Order " + "where idOrder = :idOrder");
		query.setParameter("idOrder", idOrder);
		return (Order) query.uniqueResult();
	}
	
	@Transactional(readOnly = true)
	public String getOrderName(int idOrder)
	{
		Query query = sessionFactory.getCurrentSession().createQuery(
				"from Order " + "where idOrder = :idOrder");
		query.setParameter("idOrder", idOrder);
		return ((Order) query.uniqueResult()).getOrderName();
	}
	
	@SuppressWarnings("unchecked")
	@Transactional(readOnly = true)
	public List<Order> getAllOrders()
	{
		Query query = sessionFactory.getCurrentSession().createQuery(
				"from Order");
		return query.list();
	}

	@SuppressWarnings("unchecked")
	@Transactional(readOnly=true)
	public List<Order> getPastOrders()
	{ 
		Query query = sessionFactory.getCurrentSession().createQuery("from Order " + "where date_close <= :date_close"); 
		String dateToQuery = new String(Calendar.YEAR+""+Calendar.MONTH+""+Calendar.DAY_OF_MONTH);
		query.setParameter("date_close", dateToQuery);
		return query.list();
	}
	
	@SuppressWarnings("unchecked")
	@Transactional(readOnly=true)
	public List<Order> getActiveOrders()
	{
		return sessionFactory.getCurrentSession().createQuery("from Order " + "where date_delivery = 0").list();
	}

	@SuppressWarnings("unchecked")
	@Transactional(readOnly = true)
	public List<Order> getActiveOrders(String username)
			throws InvalidParametersException
	{
		if (username == null)
			throw new InvalidParametersException();
		Member memberResp = memberInterface.getMember(username);
		if (memberResp == null)
			throw new InvalidParametersException();
		// Creo il Current timestamp
		Calendar calendar = Calendar.getInstance();
		Date now = calendar.getTime();
		Timestamp currentTimestamp = new Timestamp(now.getTime());

		Query query = sessionFactory.getCurrentSession().createQuery(
				"from Order where idMember_resp = :id "
						+ "AND dateClose > :dateNow");

		query.setParameter("id", memberResp.getIdMember());
		query.setTimestamp("dateNow", currentTimestamp);

		return query.list();
	}
	
	@SuppressWarnings("unchecked")
	@Transactional(readOnly=true)
	public List<Order> getOrdersNow() 
	{
		Calendar calendar = Calendar.getInstance();  
		java.util.Date now = calendar.getTime();
		java.sql.Timestamp currentTimestamp = new java.sql.Timestamp(now.getTime());
		
		Query query = sessionFactory.getCurrentSession().createQuery("from Order where dateClose > :dateNow");
		query.setTimestamp("dateNow", currentTimestamp);
	
		return query.list();
	}
	
	@SuppressWarnings("unchecked")
	@Transactional(readOnly = true)
	public List<String> getOrdersNowString()
	{
		Calendar calendar = Calendar.getInstance();
		java.util.Date now = calendar.getTime();
		java.sql.Timestamp currentTimestamp = new java.sql.Timestamp(
				now.getTime());

		Query query = sessionFactory.getCurrentSession().createQuery(
				"from Order where dateClose > :dateNow");
		query.setTimestamp("dateNow", currentTimestamp);

		ArrayList<String> orderString = new ArrayList<String>();

		for (Order or : (List<Order>) query.list())
		{
			String temp = or.getIdOrder() + ", " + or.getOrderName() + " - "
					+ or.getDateClose() + "," + or.getDateDelivery();
			orderString.add(temp);
		}
		return orderString;
	}
	
	@SuppressWarnings("unchecked")
	@Transactional(readOnly=true)
	public List<Order> getOrdersPast() 
	{
		Calendar calendar = Calendar.getInstance();  
		java.util.Date now = calendar.getTime();
		java.sql.Timestamp currentTimestamp = new java.sql.Timestamp(now.getTime());
		
		Query query = sessionFactory.getCurrentSession().createQuery("from Order where dateClose < :dateNow");
		query.setTimestamp("dateNow", currentTimestamp);
	
		return query.list();
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public Integer setDeliveryDate(Integer idOrder, String username, Date date)
			throws InvalidParametersException
	{
		if (idOrder == null || username == null || date == null)
			throw new InvalidParametersException();

		Query query = sessionFactory.getCurrentSession().createQuery(
						"update Order "
						+ "set dateDelivery = :dateDelivery " +
						"where idOrder = :idOrder");
		query.setParameter("dateDelivery", date);
		query.setParameter("idOrder", idOrder);
		
		Integer result = (Integer) query.executeUpdate();

		// Invio notifica ai membri partecipanti
		for (Purchase p : getOrder(idOrder).getPurchases())
		{
			Notify n = new Notify();
			n.setMember(p.getMember());
			n.setIsReaded(false);
			// FIXME mettere costanti
			n.setNotifyCategory(5);
			n.setText(idOrder.toString());
			n.setNotifyTimestamp(new Date());
			notifyInterface.newNotify(n);
		}
		
		return result;
	}
	
	@Transactional(propagation = Propagation.REQUIRED)
	public Integer updateOrder(Order order)
			throws InvalidParametersException
	{
		if (order == null)
			throw new InvalidParametersException();

		Query query = sessionFactory.getCurrentSession().createQuery(
						"update Order "
						+ "set dateDelivery = :dateDelivery " +
						"where idOrder = :idOrder");
		query.setParameter("dateDelivery", order.getDateDelivery());
		query.setParameter("idOrder", order.getIdOrder());

		return (Integer) query.executeUpdate();
	}


	@SuppressWarnings("unchecked")
	@Transactional(readOnly=true)
	public List<Order> getOldOrders(String respUsername, long end, int dateDeliveryType) throws InvalidParametersException {
		
		//dateDeliveryType
		// 0 = Impostata
		// 1 = Non Impostata
		// 2 = Entrambe
		Member memberResp = memberInterface.getMember(respUsername);
		if (memberResp == null)
			throw new InvalidParametersException();
		
		Timestamp endDate = new Timestamp(end);
		Date now = new Date();
		Timestamp nowT = new Timestamp(now.getTime());
		
		Query query = null;
		
		switch(dateDeliveryType) 
		{
			case 0:
				
				query = sessionFactory.getCurrentSession()
				.createQuery("from Order where idMember_resp = :id " +
										  "AND dateClose between :endDate and :now " +
										  "AND dateDelivery is not null");
				
				break;
			case 1: 
				
				query = sessionFactory.getCurrentSession()
				.createQuery("from Order where idMember_resp = :id " +
										  "AND dateClose between :endDate and :now " +
										  "AND dateDelivery is null");
				
				break;
			case 2:
				
				query = sessionFactory.getCurrentSession()
				.createQuery("from Order where idMember_resp = :id " +
										  "AND dateClose between :endDate and :now ");
				
				break;
		
		}
		
		query.setParameter("id", memberResp.getIdMember());
		query.setTimestamp("endDate", endDate);
		query.setTimestamp("now", nowT);
	
		return query.list();
	}
	
	@SuppressWarnings("unchecked")
	@Transactional(readOnly = true)
	public List<Order> getOrdersToDelivery(String username)
			throws InvalidParametersException
	{
		if (username == null)
			throw new InvalidParametersException();
		Date now = new Date();
		Timestamp nowT = new Timestamp(now.getTime());

		Query query = sessionFactory.getCurrentSession().createQuery(
				"from Order as o " + "where o.idOrder in ( select o2.idOrder "
						+ "from Order as o2 " + "left join o2.purchases as p "
						+ "where o2.member = :memberResp "
						+ "AND o2.dateDelivery is not NULL "
						+ "AND o2.dateDelivery <= :now "
						+ "AND p.isShipped = false " + "group by o2.idOrder )");

		query.setParameter("memberResp", memberInterface.getMember(username));
		query.setParameter("now", nowT);

		List<Order> orderList = query.list();
		List<Order> returnList = new ArrayList<Order>();
				
		for(Order orderTemp : orderList) {
			Set<Purchase> purchaseList = orderTemp.getPurchases();
			int count = purchaseList.size();
			
			for(Purchase purchaseTemp : purchaseList) 
				if( purchaseInterface.isFailed(username, purchaseTemp.getIdPurchase()) || purchaseTemp.isIsShipped() )
					count--;
		
			if(count != 0)
				returnList.add(orderTemp);
		}
		return returnList;
	}

	@Transactional(readOnly = true)
	public List<Product> getProducts(Integer idOrder, String username)
			throws InvalidParametersException
	{
		if (idOrder == null || username == null)
			throw new InvalidParametersException();

		Member member = memberInterface.getMember(username);
		Order order = getOrder(idOrder);

		if (member == null || order == null)
			throw new InvalidParametersException();

		if (member.getMemberType().getIdMemberType() == MemberTypes.USER_NORMAL
				|| order.getSupplier().getIdMember() == member.getIdMember()
				|| order.getMember().getIdMember() == member.getIdMember())
		{
			List<Product> ret = new ArrayList<Product>();
			for (OrderProduct op : order.getOrderProducts())
				ret.add(op.getProduct());
			return ret;
		}
		return null;
	}

	// FIXME messo solo per legacy con vecchio metodo
	@Transactional(readOnly = true)
	public List<Product> getProducts(Integer idOrder)
			throws InvalidParametersException
	{
		if (idOrder == null)
			throw new InvalidParametersException();

		Order order = getOrder(idOrder);

		if (order == null)
			throw new InvalidParametersException();

		List<Product> ret = new ArrayList<Product>();
		for (OrderProduct op : order.getOrderProducts())
			ret.add(op.getProduct());
		return ret;
	}
	
	@Transactional(readOnly=true)
	public List<OrderProduct> getOrderProducts(Integer idOrder){
		Order order = getOrder(idOrder);
		if(order != null){
			List<OrderProduct> ret = new ArrayList<OrderProduct>(order.getOrderProducts());
			return ret;
		}
		return null;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public List<Purchase> getPurchasesFromOrder(Integer idOrder, String username)
			throws InvalidParametersException
	{
		if (idOrder == null || idOrder <= 0 || username == null)
			throw new InvalidParametersException();
		
		Order order = getOrder(idOrder);
		if(order == null)
			return null;
		
		if (order.getMember().getIdMember() == memberInterface
				.getMember(username).getIdMember())
			return new ArrayList<Purchase>(order.getPurchases());
		else
			return null;
	}
	
	@SuppressWarnings("unchecked")
	@Transactional(readOnly=true)
	public List<Order> getOldOrdersShippedBySupplier(Member memberSupplier, int year) {
		
		
		Calendar cal = Calendar.getInstance();
		cal.set(year, 0, 1);
		Date startDateD = cal.getTime();
		cal.set(year, 11, 31);
		Date endDateD = cal.getTime();
	
		Timestamp startDate = new Timestamp(startDateD.getTime());
		Timestamp endDate = new Timestamp(endDateD.getTime());
		
		Query query = sessionFactory.getCurrentSession()
					.createQuery("from Order where idSupplier = :id " +
													"AND dateClose between :startDate and :endDate " +
													"AND dateDelivery is NOT NULL");
			
		query.setParameter("id", memberSupplier.getIdMember());
		query.setTimestamp("startDate", startDate);
		query.setTimestamp("endDate", endDate);
	
		return query.list();
	}
	
	@Transactional(readOnly=true)
	public Long getNumberOldOrdersShippedBySupplier(Member memberSupplier, int year) {
		
		
		Calendar cal = Calendar.getInstance();
		cal.set(year, 0, 1);
		Date startDateD = cal.getTime();
		cal.set(year, 11, 31);
		Date endDateD = cal.getTime();
	
		Timestamp startDate = new Timestamp(startDateD.getTime());
		Timestamp endDate = new Timestamp(endDateD.getTime());
		
		Query query = sessionFactory.getCurrentSession()
					.createQuery("select count(*) from Order where idSupplier = :id " +
													"AND dateClose between :startDate and :endDate " +
													"AND dateDelivery is NOT NULL");
			
		query.setParameter("id", memberSupplier.getIdMember());
		query.setTimestamp("startDate", startDate);
		query.setTimestamp("endDate", endDate);
	
		return (Long) query.uniqueResult();
	}
	
	@Transactional(readOnly=true)
	public Long getNumberOldOrdersShippedBySupplier(Member memberSupplier, int year, int month) {
		
		
		Calendar cal = Calendar.getInstance();
		cal.set(year, month, 1);
		Date startDateD = cal.getTime();
		cal.set(year, month, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
		Date endDateD = cal.getTime();
	
		Timestamp startDate = new Timestamp(startDateD.getTime());
		Timestamp endDate = new Timestamp(endDateD.getTime());
		
		Query query = sessionFactory.getCurrentSession()
					.createQuery("select count(*) from Order where idSupplier = :id " +
													"AND dateClose between :startDate and :endDate " +
													"AND dateDelivery is NOT NULL");
			
		query.setParameter("id", memberSupplier.getIdMember());
		query.setTimestamp("startDate", startDate);
		query.setTimestamp("endDate", endDate);
	
		return (Long) query.uniqueResult();
	}
	
	@Transactional(readOnly=true)
	public Long getNumberOldOrdersBySupplier(Member memberSupplier, int year) {
		
		
		Calendar cal = Calendar.getInstance();
		cal.set(year, 0, 1);
		Date startDateD = cal.getTime();
		cal.set(year, 11, 31);
		Date endDateD = cal.getTime();
	
		Timestamp startDate = new Timestamp(startDateD.getTime());
		Timestamp endDate = new Timestamp(endDateD.getTime());
		
		Query query = sessionFactory.getCurrentSession()
					.createQuery("select count(*) from Order where idSupplier = :id " +
													"AND dateClose  between :startDate and :endDate ");
			
		query.setParameter("id", memberSupplier.getIdMember());
		query.setTimestamp("startDate", startDate);
		query.setTimestamp("endDate", endDate);
	
		return (Long) query.uniqueResult();
	}
	
	@Transactional(readOnly=true)
	public Long getNumberOldOrdersBySupplier(Member memberSupplier, int year, int month) {
		
		
		Calendar cal = Calendar.getInstance();
		cal.set(year, month, 1);
		Date startDateD = cal.getTime();
		cal.set(year, month, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
		Date endDateD = cal.getTime();
	
		Timestamp startDate = new Timestamp(startDateD.getTime());
		Timestamp endDate = new Timestamp(endDateD.getTime());
		
		Query query = sessionFactory.getCurrentSession()
					.createQuery("select count(*) from Order where idSupplier = :id " +
													"AND dateClose between :startDate and :endDate ");
			
		query.setParameter("id", memberSupplier.getIdMember());
		query.setTimestamp("startDate", startDate);
		query.setTimestamp("endDate", endDate);
	
		return (Long) query.uniqueResult();
	}

	@Transactional(readOnly=true)
	public List<Integer> getBoughtAmounts(Integer idOrder, List<Integer> productIds) {
		// retrieve bought amounts. idOrder � l'ordine, productIds sono gli id dei prodotti di quell'ordine di cui si vuole ottenere la quantit� acquistata
		// Prendere tutte le schede dell'ordine, guardare se contengono i prodotti, e aumentare la quantit� acquistata dei singoli prodotti, messa in un array nello stesso
		// ordine degli id.
		Order order = getOrder(idOrder);
		if(order != null){
			List<Integer> ret = new ArrayList<Integer>(productIds.size());
			for(int i = 0; i < productIds.size(); i++)
				ret.add(Integer.valueOf(0));
			
			for(Purchase purchase : order.getPurchases()){
				for(PurchaseProduct purchaseProduct : purchase.getPurchaseProducts()){
					Integer idProduct = purchaseProduct.getProduct().getIdProduct();
					if(productIds.contains(idProduct)){
						int amount = purchaseProduct.getAmount();
						int index = productIds.indexOf(idProduct);
						
						int oldAmount = ret.get(index);
						ret.remove(index);
						ret.add(index, Integer.valueOf(oldAmount + amount));
					}
				}
			}
			return ret;
		}
		return null;
	}

	// Limited Visibility: can be used only by interfaces
	@Transactional(readOnly=true)
	Integer getTotalAmountOfProduct(Order order, Product product) {
		
		Query query = sessionFactory.getCurrentSession()
				.createQuery("select pp.product, sum(pp.amount) " +
						     "from Order as o " +
						     "join o.purchases as pur " +
						     "join pur.purchaseProducts as pp " +
						     "join pp.product as prod " +
						     "where prod.idProduct = :product " +
						     "AND o.idOrder = :id " +
							 "group by pp.product");
		
		query.setParameter("id", order.getIdOrder());
		query.setParameter("product", product.getIdProduct());
		
		Object[] row = (Object[]) query.uniqueResult();
		
		Integer sumAmount;
		
		if (row == null)
			sumAmount = 0;
		else 
			sumAmount = (int) (long) row[1];
		
		return sumAmount;
	}

	@Transactional(readOnly = true)
	public Integer getDispProduct(Integer idPurchase, Integer idProduct,
			String username) throws InvalidParametersException
	{
		Product p = productInterface.getProduct(idProduct, username);
		Integer maxBuy = p.getMaxBuy();
		if (maxBuy != null)
		{
			Integer totAmount = (int) (long) getTotalAmountOfProduct(
					purchaseInterface.getPurchaseInternal(idPurchase).getOrder(), p);

			return maxBuy - totAmount;
		}
		else
			return -1;
	}

	@Transactional(readOnly = true)
	public Integer getDispProductO(Integer idOrder, Integer idProduct,
			String username) throws InvalidParametersException
	{
		Product p = productInterface.getProduct(idProduct, username);
		Integer maxBuy = p.getMaxBuy();
		if (maxBuy != null)
		{
			Integer totAmount = (int) (long) getTotalAmountOfProduct(
					getOrder(idOrder), p);

			return maxBuy - totAmount;
		}
		else
			return -1;
	}

	//TODO usare anche per creazione scheda sul sito. Restituisce gli ordini attivi per cui l'utente normale richiedente non ha ancora compilato schede
	@Transactional(readOnly = true)
	public List<Order> getAvailableOrders(String username) throws Exception
	{
		Member m = memberInterface.getMember(username);
		if (m == null)
			throw new Exception("No such member");
		List<Order> ret = new ArrayList<Order>();

		for (Order order : getOrdersNow())
		{
			Set<Purchase> purchases = order.getPurchases();
			boolean alreadyPurchased = false;
			for (Purchase purchase : purchases)
			{
				if (purchase.getMember().getIdMember() == m.getIdMember())
				{
					alreadyPurchased = true;
					break;
				}
			}
			if (!alreadyPurchased)
				ret.add(order);
		}
		return ret;
	}

	@SuppressWarnings("rawtypes")
	@Transactional(readOnly=true)
	public Float getProgress(int idOrder) {

		Map<Product, Integer> mapProduct = new HashMap<Product, Integer>();
		
		Integer totMinBuy = 0;
		
		Order order = getOrder(idOrder);

		for(Purchase pTemp : order.getPurchases()) 
			for(PurchaseProduct ppTemp : pTemp.getPurchaseProducts()) {
				
				Product productTemp = ppTemp.getProduct();
				Integer amount = ppTemp.getAmount();
				Integer minBuy = productTemp.getMinBuy();
				
				if(minBuy == null)
					minBuy = 1;
				
				if (mapProduct.containsKey((Product) productTemp)) 
					amount += mapProduct.get((Product) productTemp);
				else
					totMinBuy += minBuy;
				
				mapProduct.put(productTemp, amount);
				
			}
		
		if(mapProduct.size() == 0)
			return 0.0f;
		
		// Controllare ogni prodotto
		
		Integer totBought = 0;
		
		Iterator it = mapProduct.entrySet().iterator();
	    while (it.hasNext()) {
	        Map.Entry pairs = (Map.Entry)it.next();
	        
	        Product product = (Product) pairs.getKey();
	        Integer totAmount = (Integer) pairs.getValue();
	        Integer minBuy = product.getMinBuy();
	        
	        if(minBuy == null)
				minBuy = 1;
	        
	        if(totAmount >= minBuy) 
	        	totBought += minBuy;
	        else 
	        	totBought += totAmount;
	    }
		
		return ((float) totBought) / totMinBuy * 100;
		
	}

	@Transactional(readOnly = true)
	public List<Float> getProgresses(List<Integer> orderIds) {
		if(orderIds.size() > 0){
			List<Float> ret = new ArrayList<Float>(orderIds.size());
			for(Integer id : orderIds)
				ret.add(getProgress(id));
			return ret;
		}
		return null;
	}

	@SuppressWarnings("rawtypes")
	@Transactional(readOnly = true)
	public List<String> getProgressProduct(int idOrder) {
		
		Map<Product, Integer> mapProduct = new HashMap<Product, Integer>();
		List<String> returnString = new ArrayList<String>();
		Order order = getOrder(idOrder);
		
		for(Purchase pTemp : order.getPurchases()) {
			for(PurchaseProduct ppTemp : pTemp.getPurchaseProducts()) {
				
				Product productTemp = ppTemp.getProduct();
				Integer amount = ppTemp.getAmount();
				
				if (mapProduct.containsKey((Product) productTemp)) 
					amount += mapProduct.get((Product) productTemp);
				
				mapProduct.put(productTemp, amount);
				
			}
		}
			
		
		for(OrderProduct opTemp : order.getOrderProducts()) {
			Product productTemp = opTemp.getProduct();
			
			if(!mapProduct.containsKey(productTemp))
				mapProduct.put(productTemp, 0);
		}
		
		Iterator it = mapProduct.entrySet().iterator();
	    while (it.hasNext()) {
	        Map.Entry pairs = (Map.Entry)it.next();
	        
	        Product product = (Product) pairs.getKey();
	        Integer totAmount = (Integer) pairs.getValue();
	        Integer minBuy = product.getMinBuy();
	        Float temp;
	        
	        if(minBuy == null)
				minBuy = 1;
	        
	        if(totAmount >= minBuy)
	        	temp = 100f;
	        else
	        	temp = ((float) totAmount) / minBuy * 100;
	        
	       returnString.add(product.getIdProduct() + "," + temp.toString());
	    }
	    
	    return returnString;
	}

	@Transactional(readOnly = true)
	public List<Order> getOrdersBySupplier(long start, Member supplier)
	{
		// Creo il Current timestamp
		Calendar calendar = Calendar.getInstance();
		Date now = calendar.getTime();
		Date startDate = new Date(start);

		List<Order> orders = new LinkedList<Order>();
		for (Order o : getAllOrders())
			if (o.getSupplier().getIdMember() == supplier.getIdMember()
					&& o.getDateClose().after(now)
					&& o.getDateOpen().after(startDate))
				orders.add(o);
		return orders;
	}

	@Transactional(readOnly = true)
	public List<Order> getOrdersBySupplier(long start, String username)
	{
		Member supplier = memberInterface.getMember(username);
		// Creo il Current timestamp
		Calendar calendar = Calendar.getInstance();
		Date now = calendar.getTime();
		Date startDate = new Date(start);

		List<Order> orders = new LinkedList<Order>();
		for (Order o : getAllOrders())
			if (o.getSupplier().getIdMember() == supplier.getIdMember()
					&& o.getDateClose().after(now)
					&& o.getDateOpen().after(startDate))
				orders.add(o);
		return orders;
	}
	
	@Transactional(propagation=Propagation.REQUIRED)
	public OrderProductId newOrderProduct(OrderProduct orderProduct)
			throws InvalidParametersException
	{
		if (orderProduct == null)
		{
			throw new InvalidParametersException();
		}
		return (OrderProductId) sessionFactory.getCurrentSession().save(orderProduct);
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public Integer createOrder(Member resp, int idSupplier,
			List<Integer> productIds, String orderName, Date dateOpen,
			Date dateClose) throws InvalidParametersException
	{
		Supplier supplier = supplierInterface.getSupplier(idSupplier);
		if (supplier == null)
			return -1;

		if (supplier.getMemberByIdMemberResp().getIdMember() != resp
				.getIdMember())
			return -1;

		Order order = new Order(supplier, resp, orderName, dateOpen, dateClose);

		int result = -1;
		if ((result = newOrder(order)) <= 0)
		{
			return result;
		}

		for (Integer productId : productIds)
		{
			Product p = productInterface.getProduct(productId,
					resp.getUsername());
			if (p == null
					|| p.getSupplier().getIdMember() != supplier.getIdMember())
				// Lancio eccezione cosi' viene fato il rollback e viene
				// eliminato l'ordine
				throw new InvalidParametersException();

			OrderProductId id = new OrderProductId(order.getIdOrder(),
					p.getIdProduct());
			OrderProduct orderproduct = new OrderProduct(id, order, p);

			// In questo caso, dato che l'id non e' generato ma gia' passato, se
			// ci sono errori lancia un'eccezione
			newOrderProduct(orderproduct);
		}
		return 1;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public Integer createOrder(String respUsername, int idSupplier,
			List<Integer> productIds, String orderName, Date dateOpen,
			Date dateClose) throws InvalidParametersException
	{
		Supplier supplier = supplierInterface.getSupplier(idSupplier);
		if (supplier == null)
			return -1;

		Member resp = memberInterface.getMember(respUsername);
		
		if (supplier.getMemberByIdMemberResp().getIdMember() != memberInterface
				.getMember(respUsername).getIdMember())
			return -1;

		Order order = new Order(supplier, resp, orderName, dateOpen, dateClose);

		int result = -1;
		if ((result = newOrder(order)) <= 0)
		{
			return result;
		}

		for (Integer productId : productIds)
		{
			Product p = productInterface.getProduct(productId,
					resp.getUsername());
			if (p == null
					|| p.getSupplier().getIdMember() != supplier.getIdMember())
				// Lancio eccezione cosi' viene fato il rollback e viene
				// eliminato l'ordine
				throw new InvalidParametersException();

			OrderProductId id = new OrderProductId(order.getIdOrder(),
					p.getIdProduct());
			OrderProduct orderproduct = new OrderProduct(id, order, p);

			// In questo caso, dato che l'id non e' generato ma gia' passato, se
			// ci sono errori lancia un'eccezione
			newOrderProduct(orderproduct);
		}
		return 1;
	}
	
	@Transactional(readOnly=true)
	public boolean isFailed(Integer idOrder){
		Order order = getOrder(idOrder);
		if(order == null)
			return true;
		
		Set<OrderProduct> ops = order.getOrderProducts();
		for(OrderProduct op : ops)
			if(op.isFailed() == false)
				return false;
		
		return true;
	}
	
	@Transactional(readOnly=true)
	public List<Order> getCompletedOrders(Integer idMemberResp) throws InvalidParametersException {
		//dateDeliveryType
		// 0 = Impostata
		// 1 = Non Impostata
		// 2 = Entrambe
		
		Member member = memberInterface.getMember(idMemberResp);
		
		if(member == null)
			return null;
		
		List<Order> tmp = getOldOrders(member.getUsername(), 0, 1); //0 = "The epoch", 1 = data consegna non settata
		
		List<Order> ret = new ArrayList<Order>();
		
		for(Order o : tmp){
			if(!isFailed(o.getIdOrder()))
				ret.add(o);
		}
		
		return ret;
	}
	
	@Transactional(readOnly=true)
	public List<Order> getCompletedOrders(String username) throws InvalidParametersException {
		Member member = memberInterface.getMember(username);
		
		return getCompletedOrders(member.getIdMember());
		
	}
	
	
	@SuppressWarnings("unchecked")
	@Transactional(readOnly=true)
	public List<Order> getOrdersClosedYesterday()
	{
		Calendar now = Calendar.getInstance();
		Date dateNowD = now.getTime();
		Timestamp dateNow = new Timestamp(dateNowD.getTime());
		
		Calendar past = Calendar.getInstance();
		past.add(Calendar.DATE, -1);
		Date dateYesterdayD = past.getTime();
		Timestamp dateYesterday = new Timestamp(dateYesterdayD.getTime());
		
		Query query = sessionFactory.getCurrentSession()
				.createQuery("from Order where dateClose <= :dateNow " +
										  "and dateClose > :dateYesterday");
		
		query.setTimestamp("dateNow", dateNow);
		query.setTimestamp("dateYesterday", dateYesterday);
	
		return query.list();
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public void checkClosedOrders()
	{
		List<Order> listOrders = getOrdersClosedYesterday();

		for (Order orderTmp : listOrders)
		{
			boolean orderFailed = true;
			List<OrderProduct> ops = getOrderProducts(orderTmp.getIdOrder());

			List<Integer> productIds = new ArrayList<Integer>();

			for (OrderProduct op : ops)
				productIds.add(op.getProduct().getIdProduct());

			List<Integer> amounts = getBoughtAmounts(orderTmp.getIdOrder(),
					productIds);

			for (int i = 0; i < ops.size(); i++)
			{
				Integer amount = amounts.get(i);
				OrderProduct op = ops.get(i);
				if (amount <= 0)
					op.setFailed(true);
				else
				{
					Product p = op.getProduct();
					Integer minBuy = p.getMinBuy();
					if (minBuy != null && amount < minBuy)
						op.setFailed(true);
					else
					{
						op.setFailed(false);
						orderFailed = false;
					}
				}
				if (!orderFailed)
				{
					// Manda notifica al responsabile
					Notify n = new Notify();
					n.setMember(orderTmp.getMember());
					n.setIsReaded(false);
					// FIXME mettere costanti
					n.setNotifyCategory(4);
					n.setText(orderTmp.getIdOrder().toString());
					n.setNotifyTimestamp(new Date());
					try
					{
						notifyInterface.newNotify(n);
					}
					catch (Exception e)
					{
						e.printStackTrace();
					}
					
					// Manda notifica agli utenti partecipanti
//					for (Purchase p : orderTmp.getPurchases())
//					{
//						Notify nn = new Notify();
//						nn.setMember(p.getMember());
//						nn.setIsReaded(false);
//						// FIXME mettere costanti
//						nn.setNotifyCategory(4);
//						nn.setText(orderTmp.getIdOrder().toString());
//						nn.setNotifyTimestamp(new Date());
//						try
//						{
//							notifyInterface.newNotify(nn);
//						}
//						catch (Exception e)
//						{
//							e.printStackTrace();
//						}
//					}
				}
			}
		}
	}
	
	@Transactional(propagation = Propagation.REQUIRED)
	public Integer setNewOrder(String username, int idSupplier,
			String orderName, String idString, long dataCloseTime)
			throws InvalidParametersException
	{
		List<Integer> productIds = new ArrayList<Integer>();
		Member resp = memberInterface.getMember(username);

		if (resp == null)
			return -1;

		// setto la data odierna
		Calendar calendar = Calendar.getInstance();
		Date dateOpen = calendar.getTime();

		Date dateClose = new Date(dataCloseTime);

		String[] temp = idString.split(",");
		if (temp.length > 0)
		{
			for (int i = 0; i < temp.length; i++)
			{
				try
				{
					Integer id = Integer.valueOf(temp[i]);
					productIds.add(id);
				}
				catch (NumberFormatException e)
				{
					e.printStackTrace();
					throw new InvalidParametersException();
				}

			}
			return createOrder(resp, idSupplier, productIds, orderName,
					dateOpen, dateClose);
		}
		return -1;
	}

	@SuppressWarnings("unchecked")
	@Transactional(readOnly = true)
	public List<Order> getActiveOrdersForSupplier(String username) throws InvalidParametersException {
		if (username == null)
			throw new InvalidParametersException();
		Member memberSuppier = memberInterface.getMember(username);
		if (memberSuppier == null)
			throw new InvalidParametersException();
		// Creo il Current timestamp
		Calendar calendar = Calendar.getInstance();
		Date now = calendar.getTime();
		Timestamp currentTimestamp = new Timestamp(now.getTime());

		Query query = sessionFactory.getCurrentSession().createQuery(
				"from Order where idSupplier = :id "
						+ "AND dateClose > :dateNow");

		query.setParameter("id", memberSuppier.getIdMember());
		query.setTimestamp("dateNow", currentTimestamp);

		return query.list();
	}
	
	@SuppressWarnings("unchecked")
	@Transactional(readOnly = true)
	public List<Order> getCompletedOrdersForSupplier(String username) throws InvalidParametersException {
		if (username == null)
			throw new InvalidParametersException();
		Member memberSuppier = memberInterface.getMember(username);
		if (memberSuppier == null)
			throw new InvalidParametersException();
		// Creo il Current timestamp
		Calendar calendar = Calendar.getInstance();
		Date now = calendar.getTime();
		Timestamp currentTimestamp = new Timestamp(now.getTime());

		Query query = sessionFactory.getCurrentSession().createQuery(
				"from Order where idSupplier = :id "
						+ "AND dateClose < :dateNow and (dateDelivery is NULL or dateDelivery > :dateNow)");

		query.setParameter("id", memberSuppier.getIdMember());
		query.setTimestamp("dateNow", currentTimestamp);
		
		List<Order> tmp = query.list();
		
		List<Order> ret = new ArrayList<Order>();
		
		for(Order o : tmp){
			if(!isFailed(o.getIdOrder()))
				ret.add(o);
		}

		return ret;
	}
}