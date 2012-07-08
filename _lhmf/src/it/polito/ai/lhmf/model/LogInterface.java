package it.polito.ai.lhmf.model;

import it.polito.ai.lhmf.orm.Log;
import it.polito.ai.lhmf.security.MyUserDetailsService;

import java.sql.Timestamp;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;

public class LogInterface
{
	//The session factory will be automatically injected by spring
	private SessionFactory sessionFactory;
	
	public void setSessionFactory(SessionFactory sf){
		this.sessionFactory = sf;
	}
	
	@SuppressWarnings("unchecked")
	@Transactional(readOnly=true)
	@PreAuthorize("hasRole('" + MyUserDetailsService.UserRoles.ADMIN + "')")
	public List<Log> getLogs(long start, long end)
	{
		Query query = sessionFactory.getCurrentSession().createQuery("from Log where logTimestamp between :startDate and :endDate");
		Timestamp startDate = new Timestamp(start);
		Timestamp endDate = new Timestamp(end);
		query.setTimestamp("startDate", startDate);
		query.setTimestamp("endDate", endDate);
		return query.list();
	}
}
