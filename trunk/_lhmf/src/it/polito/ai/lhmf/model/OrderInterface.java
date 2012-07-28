package it.polito.ai.lhmf.model;

import it.polito.ai.lhmf.exceptions.InvalidParametersException;
import it.polito.ai.lhmf.orm.Member;
import it.polito.ai.lhmf.orm.Order;

import java.sql.Timestamp;
import java.util.Calendar;
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
	public Order getOrder(Integer idOrder)
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
	
}