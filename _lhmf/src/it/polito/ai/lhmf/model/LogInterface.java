package it.polito.ai.lhmf.model;

import it.polito.ai.lhmf.exceptions.NoHibernateSessionException;
import it.polito.ai.lhmf.orm.Log;

import java.sql.Timestamp;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;

public abstract class LogInterface
{
	@SuppressWarnings("unchecked")
	public static List<Log> getLogs(Session hibernateSession, long start,
			long end) throws NoHibernateSessionException
	{
		if (hibernateSession == null)
			throw new NoHibernateSessionException();

		Query query = hibernateSession
				.createQuery("from Log where logTimestamp between :startDate and :endDate");
		Timestamp startDate = new Timestamp(start);
		Timestamp endDate = new Timestamp(end);
		query.setTimestamp("startDate", startDate);
		query.setTimestamp("endDate", endDate);
		return query.list();
	}
}
