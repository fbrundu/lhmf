package it.polito.ai.lhmf.model;

import it.polito.ai.lhmf.orm.Log;
import it.polito.ai.lhmf.security.MyUserDetailsService;

import java.security.InvalidParameterException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;

public class LogInterface
{
	//The session factory will be automatically injected by spring
	private SessionFactory sessionFactory;
	private MemberInterface memberInterface;
	
	public void setSessionFactory(SessionFactory sf){
		this.sessionFactory = sf;
	}
	
	public void setMemberInterface(MemberInterface memberInterface)
	{
		this.memberInterface = memberInterface;
	}
	
	@Transactional
	public Integer createLog(String logtext, Integer idMember)
	{
		if (logtext == null || logtext == "" || idMember < 0)
			throw new InvalidParameterException();
		Log l = new Log();
		l.setLogtext(logtext);
		l.setLogTimestamp(new Date());
		l.setMember(memberInterface.getMember(idMember));
		return (Integer) sessionFactory.getCurrentSession().save(l);
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

	public Long getLogsAmount(long start, long end)
	{
		Query query = sessionFactory
				.getCurrentSession()
				.createQuery(
						"select count(*) from Log where logTimestamp between :startDate and :endDate");
		Timestamp startDate = new Timestamp(start);
		Timestamp endDate = new Timestamp(end);
		query.setTimestamp("startDate", startDate);
		query.setTimestamp("endDate", endDate);
		return (Long) query.uniqueResult();
	}
}
