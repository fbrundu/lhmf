package it.polito.ai.lhmf.model;

import it.polito.ai.lhmf.exceptions.InvalidParametersException;
import it.polito.ai.lhmf.orm.Member;
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
	
	@Transactional(propagation=Propagation.REQUIRED)
	public Integer newOrder(Order order)
			throws InvalidParametersException
	{
		if (order == null)
		{
			throw new InvalidParametersException();
		}
		return (Integer) sessionFactory.getCurrentSession().save(order);
	}

	@Transactional(readOnly = true)
	public Order getOrder(int idOrder)
	{
		Query query = sessionFactory.getCurrentSession().createQuery("from Order " + "where idOrder = :idOrder");
		query.setParameter("idOrder", idOrder);
		return (Order) query.uniqueResult();
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
	@Transactional(readOnly=true)
	public List<Order> getActiveOrders(Member memberResp) {
		
		//Creo il Current timestamp
		Calendar calendar = Calendar.getInstance();  
		Date now = calendar.getTime();
		Timestamp currentTimestamp = new Timestamp(now.getTime());
		
		Query query = sessionFactory.getCurrentSession()
				.createQuery("from Order where idMember_resp = :id " +
										  "AND dateClose > :dateNow");
		
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
	public List<Order> getOldOrders(Member memberResp, long start, long end) {
		
		Timestamp startDate = new Timestamp(start);
		Timestamp endDate = new Timestamp(end);
		
		Query query = sessionFactory.getCurrentSession()
				.createQuery("from Order where idMember_resp = :id " +
										  "AND dateClose < :endDate " +
										  "AND dateOpen > :startDate");
		
		query.setParameter("id", memberResp.getIdMember());
		query.setTimestamp("startDate", startDate);
		query.setTimestamp("endDate", endDate);
	
		return query.list();
	}

	@SuppressWarnings("unchecked")
	@Transactional(readOnly=true)
	public List<Order> getOldOrders(Member memberResp, long end, int dateDeliveryType) {
		
		//dateDeliveryType
		// 0 = Impostata
		// 1 = Non Impostata
		// 2 = Entrambe
		
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
	@Transactional(readOnly=true)
	public List<Order> getOrdersToDelivery(Member memberResp) {
				
		Query query = sessionFactory.getCurrentSession()
					.createQuery("from Order as o " +
							     "where o.idOrder in ( select o2.idOrder " +
							     					 "from Order as o2 " +
												     "left join o2.purchases as p " +
												     "where o2.member = :memberResp " +
												     "AND o2.dateDelivery is not NULL " +
												     "AND p.isShipped = false " +
												     "group by o2.idOrder )");
		
		query.setParameter("memberResp", memberResp);

		return query.list();
	}
	
	@Transactional(readOnly=true)
	public List<Product> getProducts(Integer idOrder) 
	{
		Order order = getOrder(idOrder);
		if(order != null){
			List<Product> ret = new ArrayList<Product>();
			for(OrderProduct op : order.getOrderProducts())
				ret.add(op.getProduct());
			return ret;
		}
		return null;
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
	public Integer getDispProduct(Integer idPurchase, Integer idProduct)
	{
		Product p = productInterface.getProduct(idProduct);
		Integer maxBuy = p.getMaxBuy();
		if (maxBuy != null)
		{
			Integer totAmount = (int) (long) getTotalAmountOfProduct(
					purchaseInterface.getPurchase(idPurchase).getOrder(), p);

			return maxBuy - totAmount;
		}
		else
			return -1;
	}

	@Transactional(readOnly = true)
	public Integer getDispProductO(Integer idOrder, Integer idProduct)
	{
		Product p = productInterface.getProduct(idProduct);
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
		
		for(Purchase pTemp : order.getPurchases()) 
			for(PurchaseProduct ppTemp : pTemp.getPurchaseProducts()) {
				
				Product productTemp = ppTemp.getProduct();
				Integer amount = ppTemp.getAmount();
				
				if (mapProduct.containsKey((Product) productTemp)) 
					amount += mapProduct.get((Product) productTemp);
				
				mapProduct.put(productTemp, amount);
				
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

	@Transactional(propagation=Propagation.REQUIRED)
	public Integer createOrder(Member resp, int idSupplier,
			List<Integer> productIds, String orderName, Date dateOpen,
			Date dateClose) throws InvalidParametersException {
		Supplier supplier = supplierInterface.getSupplier(idSupplier);
		if(supplier == null)
			return -1;
		
		if(supplier.getMemberByIdMemberResp().getIdMember() != resp.getIdMember())
			return -1;
		
		Order order = new Order(supplier, resp, orderName, dateOpen, dateClose);
		
		int result = -1;
		if((result = newOrder(order)) <= 0)
		{
			return result;
		}
	
		
		
		for(Integer productId : productIds){
			Product p = productInterface.getProduct(productId);
			if(p == null || p.getSupplier().getIdMember() != supplier.getIdMember())
				//Lancio eccezione cos� viene fato il rollback e viene eliminato l'ordine
				throw new InvalidParametersException();
			
			OrderProductId id = new OrderProductId(order.getIdOrder(), p.getIdProduct());
			OrderProduct orderproduct = new OrderProduct(id, order, p);
			
			//In questo caso, dato che l'id non � generato ma gi� passato, se ci sono errori lancia un'eccezione
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
	public List<Order> getCompletedOrders(Integer idMemberResp) {
		//dateDeliveryType
		// 0 = Impostata
		// 1 = Non Impostata
		// 2 = Entrambe
		
		Member member = memberInterface.getMember(idMemberResp);
		
		if(member == null)
			return null;
		
		List<Order> tmp = getOldOrders(member, 0, 1); //0 = "The epoch", 1 = data consegna non settata
		
		List<Order> ret = new ArrayList<Order>();
		
		for(Order o : tmp){
			if(!isFailed(o.getIdOrder()))
				ret.add(o);
		}
		
		return ret;
	}
	
	@Transactional(readOnly=true)
	public List<Order> getCompletedOrders(String username){
		Member member = memberInterface.getMember(username);
		
		return getCompletedOrders(member.getIdMember());
	}
	
	
	@SuppressWarnings("unchecked")
	@Transactional(readOnly=true)
	public List<Order> getOrdersNotFailed()
	{
				
		Calendar now = Calendar.getInstance();
		Date dateNowD = now.getTime();
		Timestamp dateNow = new Timestamp(dateNowD.getTime());
		
		Calendar past = Calendar.getInstance();
		past.set(Calendar.YEAR, Calendar.MONTH, Calendar.DAY_OF_MONTH - 1);
		Date dateYesterdayD = past.getTime();
		Timestamp dateYesterday = new Timestamp(dateYesterdayD.getTime());
		
		Query query = sessionFactory.getCurrentSession()
				.createQuery("from Order where dateClose <= :dateNow " +
										  "OR dateClose > :dateYesterday");
		
		query.setTimestamp("dateNow", dateNow);
		query.setTimestamp("dateYesterday", dateYesterday);
	
		return query.list();
	}
	
	@Transactional(propagation = Propagation.REQUIRED)
	public Integer updateFailedWithOrder(int idOrder, int idProduct)
	{		
		Query query = sessionFactory.getCurrentSession().createQuery(
						"update OrderProduct "
						+ "set failed = true " +
						"where idOrder = :idOrder AND idProduct = :idProduct");
		query.setParameter("idOrder", idOrder);
		query.setParameter("idProduct", idProduct);
	
		return (Integer) query.executeUpdate();
	}
	
	@Transactional(propagation = Propagation.REQUIRED)
	public void updateFailedWithNoOrder(int idProduct)
	{		
		Query query = sessionFactory.getCurrentSession().createQuery(
						"update OrderProduct "
						+ "set failed = true " +
						"where idProduct = :idProduct");
		query.setParameter("idProduct", idProduct);
	
		query.executeUpdate();
	}
}