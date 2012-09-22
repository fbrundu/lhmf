package it.polito.ai.lhmf.model;

import it.polito.ai.lhmf.exceptions.InvalidParametersException;
import it.polito.ai.lhmf.orm.Member;
import it.polito.ai.lhmf.orm.Order;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

public class OrderInterface
{
	
	private SessionFactory sessionFactory;
		
	public void setSessionFactory(SessionFactory sf)
	{
		this.sessionFactory = sf;
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
	public List<Order> getActiveOrders(long start, Member memberResp) {
		
		//Creo il Current timestamp
		Calendar calendar = Calendar.getInstance();  
		java.util.Date now = calendar.getTime();
		java.sql.Timestamp currentTimestamp = new java.sql.Timestamp(now.getTime());
		Timestamp startDate = new Timestamp(start);
		
		Query query = sessionFactory.getCurrentSession()
				.createQuery("from Order where idMember_resp = :id " +
										  "AND dateClose > :dateNow " +
										  "AND dateOpen > :startDate");
		
		query.setParameter("id", memberResp.getIdMember());
		query.setTimestamp("dateNow", currentTimestamp);
		query.setTimestamp("startDate", startDate);
	
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
	public List<Order> getOldOrders(Member memberResp, long start, long end,
			boolean settedDeliveryDate) {
		
		Timestamp startDate = new Timestamp(start);
		Timestamp endDate = new Timestamp(end);
		Query query;
		if(settedDeliveryDate) {
			query = sessionFactory.getCurrentSession()
					.createQuery("from Order where idMember_resp = :id " +
											  "AND dateClose < :endDate " +
											  "AND dateOpen > :startDate " +
											  "AND dateDelivery is not null");
		} else {
			
			query = sessionFactory.getCurrentSession()
					.createQuery("from Order where idMember_resp = :id " +
											  "AND dateClose < :endDate " +
											  "AND dateOpen > :startDate " +
											  "AND dateDelivery is null");
		}
		
				
		query.setParameter("id", memberResp.getIdMember());
		query.setTimestamp("startDate", startDate);
		query.setTimestamp("endDate", endDate);
	
		return query.list();
	}

	@SuppressWarnings("unchecked")
	@Transactional(readOnly=true)
	public List<Order> getOrdersToDelivery(Member memberResp, long start, long end) {
		
		Timestamp startDate = new Timestamp(start);
		Timestamp endDate = new Timestamp(end);
		
		Query query = sessionFactory.getCurrentSession()
					.createQuery("from Order where idMember_resp = :id " +
											  "AND dateDelivery between :startDate and :endDate");
		
		query.setParameter("id", memberResp.getIdMember());
		query.setTimestamp("startDate", startDate);
		query.setTimestamp("endDate", endDate);

		return query.list();
	}
	
	@SuppressWarnings("unchecked")
	@Transactional(readOnly=true)
	public List<Order> getProducts(int idOrder) 
	{			
		Query query = sessionFactory.getCurrentSession().createQuery("from OrderProduct where idOrder = :idOrder");
		query.setParameter("idOrder", idOrder);
		return query.list();
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
}