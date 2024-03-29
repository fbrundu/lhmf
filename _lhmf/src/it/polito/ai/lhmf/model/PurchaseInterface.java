package it.polito.ai.lhmf.model;

import it.polito.ai.lhmf.exceptions.InvalidParametersException;
import it.polito.ai.lhmf.model.constants.MemberTypes;
import it.polito.ai.lhmf.orm.Member;
import it.polito.ai.lhmf.orm.Notify;
import it.polito.ai.lhmf.orm.Order;
import it.polito.ai.lhmf.orm.OrderProduct;
import it.polito.ai.lhmf.orm.Product;
import it.polito.ai.lhmf.orm.Purchase;
import it.polito.ai.lhmf.orm.PurchaseProduct;
import it.polito.ai.lhmf.orm.PurchaseProductId;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
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
	private MemberInterface memberInterface;
	private NotifyInterface notifyInterface;
	private LogInterface logInterface;
	
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
	
	public void setMemberInterface(MemberInterface mi)
	{
		this.memberInterface = mi;
	}
	
	public void setNotifyInterface(NotifyInterface ni)
	{
		this.notifyInterface = ni;
	}
	
	public void setLogInterface(LogInterface logInterface)
	{
		this.logInterface = logInterface;
	}
	
	@Transactional(propagation=Propagation.REQUIRED)
	public Integer newPurchaseInternal(Purchase p, Integer idMember, Integer idOrder)
			throws InvalidParametersException
	{
		if (p == null || idMember == null || idOrder == null)
			throw new InvalidParametersException();

		Order order = orderInterface.getOrder(idOrder);
		Member member = memberInterface.getMember(idMember);

		if (order == null || member == null)
			throw new InvalidParametersException();

		p.setOrder(order);
		p.setMember(member);

		Integer idPurchase = (Integer) sessionFactory.getCurrentSession().save(
				p);

		if (idPurchase > 0)
		{
			logInterface.createLog(
					"Ha creato una scheda con id: " + idPurchase, idMember);
		}
		else
			logInterface.createLog(
					"Ha provato a creare una scheda senza successo", idMember);
	
		return idPurchase;
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
	public List<Purchase> getPurchasesOnDate(String username, Integer when)
	{
		Member m = memberInterface.getMember(username);
		List<Order> os = null;
		
		if (when == 0)
			os = orderInterface.getOrdersNow();
		else
			os = orderInterface.getOrdersPast();

		Query query = sessionFactory.getCurrentSession().createQuery(
				"from Purchase where idMember = :idMember "
						+ "AND idOrder = :idOrder");
		query.setParameter("idMember", m.getIdMember());
	
		List<Purchase> activePurchases = new LinkedList<Purchase>();
		Purchase purTmp = new Purchase();
		for (Order or : os)
		{
			query.setParameter("idOrder", or.getIdOrder());
			if ((purTmp = (Purchase) query.uniqueResult()) != null)
			{
				activePurchases.add(purTmp);
			}
		}
		return activePurchases;
	}

	@Transactional(readOnly = true)
	public Purchase getPurchaseInternal(int idPurchase)
	{
		Query query = sessionFactory.getCurrentSession().createQuery(
				"from Purchase " + "where idPurchase = :idPurchase");
		query.setParameter("idPurchase", idPurchase);
		return (Purchase) query.uniqueResult();
	}
	
	@Transactional(readOnly = true)
	public List<Product> getProducts(int idPurchase, String username)
			throws Exception
	{
		Purchase purchase = getPurchaseInternal(idPurchase);

		if (purchase.getOrder().getMember().getIdMember() != memberInterface
				.getMember(username).getIdMember())
			throw new Exception("Member not authorized");

		List<Product> listProduct = new ArrayList<Product>();
		java.util.Iterator<PurchaseProduct> iter = purchase
				.getPurchaseProducts().iterator();
		while (iter.hasNext())
			listProduct.add(iter.next().getProduct());
		return listProduct;
	}
	
	@SuppressWarnings("unchecked")
	@Transactional(readOnly = true)
	public List<PurchaseProduct> getPurchaseProductsInternal(Integer idPurchase)
			throws InvalidParametersException
	{
		if (idPurchase == null || idPurchase < 0)
			throw new InvalidParametersException();
		Query query = sessionFactory.getCurrentSession().createQuery(
				"from PurchaseProduct " + "where idPurchase = :idPurchase");
		query.setParameter("idPurchase", idPurchase);
		return query.list();
	}
	
	@SuppressWarnings("unchecked")
	@Transactional(readOnly = true)
	public Set<PurchaseProduct> getPurchaseProductsCheckMember(String username,
			Integer idPurchase) throws InvalidParametersException
	{
		if (idPurchase == null || idPurchase < 0)
			throw new InvalidParametersException();
		Purchase p = getPurchaseInternal(idPurchase);
		if (p == null
				|| p.getMember().getIdMember() != memberInterface.getMember(
						username).getIdMember()
				&& p.getOrder().getMember().getIdMember() != memberInterface
						.getMember(username).getIdMember())
			return null;

		Query query = sessionFactory.getCurrentSession().createQuery(
				"from PurchaseProduct " + "where idPurchase = :idPurchase");
		query.setParameter("idPurchase", idPurchase);
		Set<PurchaseProduct> pps = new HashSet<PurchaseProduct>();
		for (PurchaseProduct pp : (List<PurchaseProduct>) query.list())
			pps.add(pp);
		return pps;
	}
	
	@SuppressWarnings("unchecked")
	@Transactional(readOnly = true)
	public List<Purchase> getPurchaseFromOrder(Integer idOrder) 
	{
		Query query = sessionFactory.getCurrentSession().createQuery("from Purchase " + "where idOrder = :idOrder");
		query.setParameter("idOrder", idOrder);
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

	@Transactional(propagation = Propagation.REQUIRED)
	public Integer setPurchaseShipped(String username, Integer idPurchase)
			throws InvalidParametersException
	{
		if (username == null || idPurchase == null)
			throw new InvalidParametersException();

		// il membro che fa la modifica deve essere il responsabile
		// dell'ordine
		if (!getPurchaseInternal(idPurchase).getOrder().getMember().getIdMember()
				.equals(memberInterface.getMember(username).getIdMember()))
			return -1;

		Query query = sessionFactory.getCurrentSession().createQuery(
				"update Purchase " + "set isShipped = true "
						+ "where idPurchase = :idPurchase");
		query.setParameter("idPurchase", idPurchase);

		Integer result = (Integer) query.executeUpdate();
		if(result > 0)
			logInterface.createLog("Ha modificato la scheda con id: " + idPurchase,
				memberInterface.getMember(username).getIdMember());
		else
			logInterface.createLog("Ha provato senza successo a modificare la scheda con id: " + idPurchase,
					memberInterface.getMember(username).getIdMember());
		return result;
	}
	
	@Transactional(propagation = Propagation.REQUIRED)
	public	List<Product> getPurchaseDetails(int idPurchase, String username)
			throws InvalidParametersException
	{
		List<PurchaseProduct> productTmp = getPurchaseProductsInternal(idPurchase);
		List<Product> listProduct = new ArrayList<Product>();
		for (PurchaseProduct product : productTmp)
		{
			listProduct.add(productInterface.getProduct(product.getId()
					.getIdProduct(), username));
		}
		return listProduct;
	}
	
	
	@Transactional(propagation = Propagation.REQUIRED)
	public PurchaseProductId newPurchaseProductInternal(PurchaseProduct purchaseProduct)
			throws InvalidParametersException
	{
		if (purchaseProduct == null)
		{
			throw new InvalidParametersException();
		}
		PurchaseProductId result = (PurchaseProductId) sessionFactory
				.getCurrentSession().save(purchaseProduct);
		return result;
	}

	@Transactional(readOnly = true)
	public PurchaseProduct getPurchaseProductFromIdInternal(Integer idPurchase, Integer idProduct)
	{
		Query query = sessionFactory.getCurrentSession().createQuery(
				"from PurchaseProduct where idPurchase = :idPurchase " + 
						"AND idProduct = :idProduct");
		query.setParameter("idPurchase", idPurchase);
		query.setParameter("idProduct", idProduct);
		return (PurchaseProduct)query.uniqueResult();
	}

	@Transactional(readOnly = true)
	public Integer getAmountPurchaseProductFromId(Integer idPurchase,
			Integer idProduct)
	{
		Query query = sessionFactory.getCurrentSession().createQuery(
				"from PurchaseProduct where idPurchase = :idPurchase "
						+ "AND idProduct = :idProduct");
		query.setParameter("idPurchase", idPurchase);
		query.setParameter("idProduct", idProduct);
		return ((PurchaseProduct) query.uniqueResult()).getAmount();
	}
	
	@Transactional(readOnly = true)
	public Integer getPurchaseProductAmountFromId(Integer idPurchase,
			Integer idProduct)
	{
		return getPurchaseProductFromIdInternal(idPurchase, idProduct).getAmount();
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
	public Integer updatePurchaseProductInternal(PurchaseProduct purchaseProduct) throws InvalidParametersException {
	
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

		Integer result = (Integer) query.executeUpdate();
		
		if (result > 0)
		{
			logInterface.createLog("Ha modificato la scheda con id: "
					+ purchaseProduct.getPurchase().getIdPurchase(),
					purchaseProduct.getPurchase().getMember().getIdMember());
		}
		else 
			logInterface.createLog("Ha provato senza successo a modificare la scheda con id: "
					+ purchaseProduct.getPurchase().getIdPurchase(),
					purchaseProduct.getPurchase().getMember().getIdMember());
				
		return result;
		
	}

	@Transactional(propagation=Propagation.REQUIRED)
	public Integer deletePurchaseProductInternal(Purchase purchase, Product product) throws InvalidParametersException {
		
		if (purchase == null || product == null )
			throw new InvalidParametersException();

		Query query = sessionFactory.getCurrentSession().createQuery(
						  "delete from PurchaseProduct " +
						  "where purchase = :purchase " +
						  "AND product = :product");
		
		query.setParameter("purchase", purchase);
		query.setParameter("product", product);

		Integer result = (Integer) query.executeUpdate();
		if (result > 0)
		{
			logInterface.createLog(
					"Ha cancellato un prodotto dalla scheda con id: " + purchase.getIdPurchase(),
					purchase.getMember().getIdMember());
		}
		else
			logInterface.createLog(
					"Ha provato senza successo a cancellare un prodotto dalla scheda con id: " + purchase.getIdPurchase(),
					purchase.getMember().getIdMember());
		return (Integer) result;
	}

	@Transactional(propagation=Propagation.REQUIRED)
	public Integer deletePurchaseInternal(Purchase purchase) throws InvalidParametersException {
		
		if (purchase == null)
			throw new InvalidParametersException();

		Query query = sessionFactory.getCurrentSession().createQuery(
						  "delete from Purchase " +
						  "where idPurchase = :idPurchase");
		
		query.setParameter("idPurchase", purchase.getIdPurchase());

		Integer result = (Integer) query.executeUpdate();
		if (result > 0)
		{
			logInterface.createLog("Ha cancellato la scheda con id: "
					+ purchase.getIdPurchase(), purchase.getMember()
					.getIdMember());
		}
		else
			logInterface.createLog(
					"Ha provato senza successo a cancellare la scheda con id: " + purchase.getIdPurchase(),
					purchase.getMember().getIdMember());
			
		return result;		
	}
	
	@Transactional(propagation=Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED)
	public Integer createPurchase(String username, Integer idOrder,
			Integer[] ids, Integer[] amounts) throws InvalidParametersException {
		
		Order order = orderInterface.getOrder(idOrder);
		
		if(order == null)
			return -1;
		
		sessionFactory.getCurrentSession().buildLockRequest(new LockOptions(LockMode.PESSIMISTIC_WRITE)).lock(order);
		
		boolean available = false;
		List<Order> availableOrders;
		try
		{
			availableOrders = orderInterface.getAvailableOrders(username);
			//Verifica che l'utente non abbia gia' compilato una scheda per quest'ordine
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
				Product product = productInterface.getProduct(ids[i], username);
				Integer amount = amounts[i];
				
				if(product == null || amount == null || amount <= 0)
					return -1;
				
				Integer maxBuy = product.getMaxBuy();
				
				// Controllo disponibilita' quantita' richiesta
				if (maxBuy != null)
				{
					Integer alreadyBought = orderInterface
							.getTotalAmountOfProduct(order, product);// orderInterface.getBoughtAmounts(order.getIdOrder(),
																		// Collections.singletonList(product.getIdProduct())).get(0);
					if (amount > maxBuy - alreadyBought)
						return -1;
				}
			}
			float progressBefore = computeOrderProgressInternal(order);
			
			Purchase purchase = new Purchase(order, memberInterface.getMember(username));
			
			int result;
			if ((result = newPurchaseInternal(purchase,
					memberInterface.getMember(username).getIdMember(),
					order.getIdOrder())) <= 0)
			{
				return result;
			}
			
			// setto la data odierna
			Calendar calendar = Calendar.getInstance();
			Date insertedTimestamp = calendar.getTime();
			
			for( int i = 0; i < ids.length; i++) 
			{
				Product product = productInterface.getProduct(ids[i], username);
				PurchaseProductId id = new PurchaseProductId(purchase.getIdPurchase(), product.getIdProduct());
				PurchaseProduct purchaseproduct = new PurchaseProduct(id, purchase, product, amounts[i], insertedTimestamp);				
				//In questo caso, dato che l'id non e' generato ma gia' passato, se ci sono errori lancia un'eccezione
				newPurchaseProductInternal(purchaseproduct);
			}
			sessionFactory.getCurrentSession().flush();
			
			sessionFactory.getCurrentSession().refresh(purchase);
			sessionFactory.getCurrentSession().refresh(order);
			
			float progressAfter = computeOrderProgressInternal(order);
			
			sendOrderProgressNotifies(progressBefore, progressAfter, purchase);
			
			return 1;
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return -1;
		}
	}

	@Transactional(propagation=Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED)
	public Integer insertProduct(String username, Integer idPurchase,
			Integer idProduct, Integer amountProduct) throws InvalidParametersException {
		
		Member member = memberInterface.getMember(username);
		Integer disp = null;
		
		Product product = productInterface.getProduct(idProduct,username);
		
		if(product == null)
			return -1;
		
		Integer maxBuy = product.getMaxBuy();
		
		if(maxBuy == null)
			disp = -1;
		
		Purchase purchase = getPurchaseInternal(idPurchase);
		if(purchase == null)
			return -1;
		
		if(purchase.getMember().getIdMember() != member.getIdMember())
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
		
		float progressBefore = computeOrderProgressInternal(order);
		
		// setto la data odierna
		Calendar calendar = Calendar.getInstance();
		Date insertedTimestamp = calendar.getTime();
		
		PurchaseProductId id = new PurchaseProductId(idPurchase, idProduct);
		PurchaseProduct purchaseproduct = new PurchaseProduct(id, purchase, product, amountProduct, insertedTimestamp);	
		newPurchaseProductInternal(purchaseproduct);
		
		sessionFactory.getCurrentSession().flush();
		
		sessionFactory.getCurrentSession().refresh(purchase);
		sessionFactory.getCurrentSession().refresh(order);
		
		float progressAfter = computeOrderProgressInternal(order);
		
		sendOrderProgressNotifies(progressBefore, progressAfter, purchase);
		
		return 1;
	}

	@Transactional(propagation=Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED)
	public Integer updateProduct(String username, Integer idPurchase,
			Integer idProduct, Integer amountProduct) throws InvalidParametersException {
		// 1 = prenotazione aggiornata
		// 0 = prodotto non trovato
		// -1 = Quantita' non disponibile
		// -2 = valore non idoneo
		
		Member member = memberInterface.getMember(username);

		int result = 0;
		
		Integer disp = null;
		
		Product product = productInterface.getProduct(idProduct, username);
		
		if(product == null)
			return -1;
		
		Integer maxBuy = product.getMaxBuy();
		
		if(maxBuy == null)
			disp = -1;
		
		Purchase purchase = getPurchaseInternal(idPurchase);
		if(purchase == null)
			return -1;
		
		if(purchase.getMember().getIdMember() != member.getIdMember())
			return -1;
		
		Order order = purchase.getOrder();
		
		sessionFactory.getCurrentSession().buildLockRequest(new LockOptions(LockMode.PESSIMISTIC_WRITE)).lock(order);
		
		if(disp == null){
			Integer totAmount = (int) (long) orderInterface.getTotalAmountOfProduct(order, product);
			disp = maxBuy - totAmount;
		}
		
		if(amountProduct <= 0)
			return -2;
		
		float progressBefore = computeOrderProgressInternal(order);
			
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
				result = updatePurchaseProductInternal(ppTemp);
				break;
			}
			
		}
		
		if(result > 0){
			sessionFactory.getCurrentSession().refresh(purchase);
			sessionFactory.getCurrentSession().refresh(order);
			
			float progressAfter = computeOrderProgressInternal(order);
			
			sendOrderProgressNotifies(progressBefore, progressAfter, purchase);
		}
		
		return result;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public Integer deletePurchaseProduct(String username,
			Integer idPurchase, Integer idProduct) throws InvalidParametersException {
		// 1 = prenotazione aggiornata
		// 0 = prodotto non trovato

		Member member = memberInterface.getMember(username);
		Product product = productInterface.getProduct(idProduct, username);
		Purchase purchase = getPurchaseInternal(idPurchase);
		
		if(purchase.getMember().getIdMember() != member.getIdMember())
			return -1;
		
		Order order = purchase.getOrder();
		
		sessionFactory.getCurrentSession().buildLockRequest(new LockOptions(LockMode.PESSIMISTIC_WRITE)).lock(order);
		
		float progressBefore = computeOrderProgressInternal(order);
		float progressAfter = -1.0f;
		
		int res = deletePurchaseProductInternal(purchase, product);
		if(res != 1)
			return res;
		else{
			sessionFactory.getCurrentSession().refresh(purchase);
			
			if(purchase.getPurchaseProducts().size() == 0){
				//e' stato eliminato l'utlimo prodotto --> eliminare intera scheda
				
				res = deletePurchaseInternal(purchase);
				if(res == 1){
					sessionFactory.getCurrentSession().refresh(order);
					progressAfter = computeOrderProgressInternal(order);
				}
				else
					throw new InvalidParametersException();
			}
			else{
				sessionFactory.getCurrentSession().refresh(order);
				
				progressAfter = computeOrderProgressInternal(order);
			}
		}
		
		sendOrderProgressNotifies(progressBefore, progressAfter, purchase);
		
		return res;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public Set<Product> getOtherProductsOfOrder(int idPurchase)
	{
		Purchase pu = this.getPurchaseInternal(idPurchase);
		Set<Product> p = new HashSet<Product>();

		for(OrderProduct op : pu.getOrder()
				.getOrderProducts())
			p.add(op.getProduct());
		
		for(PurchaseProduct pp : pu.getPurchaseProducts())
			p.remove(pp.getProduct());
		
		return p;
	}

	@Transactional(readOnly = true)
	public Float getPurchaseCost(String userName, Integer idPurchase, boolean completed) throws InvalidParametersException {
		Member member = memberInterface.getMember(userName);
		
		Purchase purchase = getPurchaseInternal(idPurchase);
		Order order = purchase.getOrder();
		
		if(purchase.getMember().getIdMember() != member.getIdMember() &&
				order.getMember().getIdMember() != member.getIdMember() &&
				order.getSupplier().getIdMember() != member.getIdMember())
			return null;
		
		List<OrderProduct> ops = orderInterface.getOrderProducts(order.getIdOrder());
		
		Float ret = 0.0f;
		List<PurchaseProduct> bought = getPurchaseProductsInternal(idPurchase);
		if(bought.size() == 0)
			return null;
		for(PurchaseProduct pp : bought){
			boolean add = true;
			if(completed){
				Integer idProduct = pp.getProduct().getIdProduct();
				for(OrderProduct op : ops){
					if(op.getProduct().getIdProduct() == idProduct){
						if(op.isFailed())
							add = false;
						break;
					}
				}
			}
			if(add)
				ret += pp.getAmount() * pp.getProduct().getUnitCost();
		}
		return ret;
	}

	@Transactional(readOnly = true)
	public Boolean isFailed(String userName, Integer idPurchase) throws InvalidParametersException {
		Member member = memberInterface.getMember(userName);
		
		Purchase purchase = getPurchaseInternal(idPurchase);
		Order order = purchase.getOrder();
		
		if(purchase.getMember().getIdMember() != member.getIdMember() &&
				order.getMember().getIdMember() != member.getIdMember())
			return null;
		
		List<OrderProduct> ops = orderInterface.getOrderProducts(order.getIdOrder());
		List<PurchaseProduct> bought = getPurchaseProductsInternal(idPurchase);
		
		boolean failed = true;
		for(PurchaseProduct pp : bought){
			Integer idProduct = pp.getProduct().getIdProduct();
			for(OrderProduct op : ops){
				if(op.getProduct().getIdProduct() == idProduct){
					if(!op.isFailed()){
						failed = false;
					}
					break;
				}
			}
			if(!failed)
				break;
		}
		
		return failed;
	}

	@Transactional(readOnly = true)
	public Purchase getMyPurchase(String username, Integer idOrder) {
		Member memberNormal = memberInterface.getMember(username);
		
		if(memberNormal == null)
			return null;
		
		Order order = orderInterface.getOrder(idOrder);
		if(order == null)
			return null;
		
		Purchase ret = null;
		
		for(Purchase p : order.getPurchases()){
			if(p.getMember().getIdMember() == memberNormal.getIdMember()){
				ret = p;
				break;
			}
		}
		return ret;
	}

	@Transactional(readOnly = true)
	public List<Purchase> getShipPurchases(String username) throws InvalidParametersException {
		
		Member member = memberInterface.getMember(username);
		
		List<Purchase> returnList = new ArrayList<Purchase>();
		
		Set<Purchase> purchaseList = member.getPurchases();
		
		for(Purchase pTemp : purchaseList) {
			
			Order oTemp = pTemp.getOrder();
			if(!pTemp.isIsShipped() && oTemp.getDateDelivery() != null && !isFailed(username, pTemp.getIdPurchase())) 
				returnList.add(pTemp);
			
		}
		
		return returnList;
	}
	
	private float computeOrderProgressInternal(Order order){
		Map<Product, Integer> mapProduct = new HashMap<Product, Integer>();
		Integer totMinBuy = 0;

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
		
		Iterator<Entry<Product, Integer>> it = mapProduct.entrySet().iterator();
	    while (it.hasNext()) {
	        Entry<Product, Integer> pairs = it.next();
	        
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
	
	@Transactional(propagation = Propagation.REQUIRED)
	private void sendOrderProgressNotifies(float progressBefore,
			float progressAfter, Purchase purchase) throws InvalidParametersException {
		Integer notifyCat = null;
		
		if (progressBefore < 90 && progressAfter >= 90)
		{
			notifyCat = 9;
		}
		else if (progressBefore < 75 && progressAfter >= 75)
		{
			notifyCat = 8;
		}
		else if (progressBefore < 50 && progressAfter >= 50)
		{
			notifyCat = 7;
		}
		else if (progressAfter < progressBefore){
			if(progressAfter >= 50 && progressAfter < 75 && progressBefore >= 75 ){
				notifyCat = 7;
			}
			else if(progressAfter >= 75 && progressAfter < 90 && progressBefore >= 90){
				notifyCat = 8;
			}
		}
		
		if(notifyCat != null){
			Member purchaseMember = purchase.getMember();
			Integer idOrder = purchase.getOrder().getIdOrder();
			Date ts = new Date();
			
			List<Member> membersNormal = memberInterface.getMembers(MemberTypes.USER_NORMAL);
			List<Member> membersResp = memberInterface.getMembers(MemberTypes.USER_RESP);
			
			for(Member m : membersNormal){
				if(m.getIdMember() != purchaseMember.getIdMember()){
					Notify nn = new Notify();
					nn.setMember(m);
					nn.setIsReaded(false);
					nn.setText(idOrder.toString());
					nn.setNotifyTimestamp(ts);
					nn.setNotifyCategory(notifyCat);
					notifyInterface.newNotify(nn);
				}
			}
			
			for(Member r : membersResp){
				if(r.getIdMember() != purchaseMember.getIdMember()){
					Notify nn = new Notify();
					nn.setMember(r);
					nn.setIsReaded(false);
					nn.setText(idOrder.toString());
					nn.setNotifyTimestamp(ts);
					nn.setNotifyCategory(notifyCat);
					notifyInterface.newNotify(nn);
				}
			}
		}
	}
}