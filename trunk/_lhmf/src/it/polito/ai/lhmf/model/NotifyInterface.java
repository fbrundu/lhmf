package it.polito.ai.lhmf.model;

import it.polito.ai.lhmf.exceptions.InvalidParametersException;
import it.polito.ai.lhmf.orm.Message;
import it.polito.ai.lhmf.orm.Notify;

import java.util.List;

import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

public class NotifyInterface
{
	// The session factory will be automatically injected by spring
	private SessionFactory sessionFactory;

	public void setSessionFactory(SessionFactory sf)
	{
		this.sessionFactory = sf;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public Integer newNotify(Notify notify)
			throws InvalidParametersException
	{
		if (notify == null)
			throw new InvalidParametersException();

		return (Integer) sessionFactory.getCurrentSession().save(notify);
	}

	@Transactional(readOnly = true)
	public Notify getNotify(Integer idNotify)
	{
		Query query = sessionFactory.getCurrentSession().createQuery(
				"from Notify " + "where idNotify = :idNotify");
		query.setParameter("idNotify", idNotify);
		return (Notify) query.uniqueResult();
	}

	@SuppressWarnings("unchecked")
	@Transactional(readOnly = true)
	public List<Message> getNotifies()
	{
		return sessionFactory.getCurrentSession().createQuery("from Notify")
				.list();
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public Integer deleteNotify(Integer idNotify)
			throws InvalidParametersException
	{
		if (idNotify == null)
			throw new InvalidParametersException();

		Query query = sessionFactory.getCurrentSession().createQuery(
				"delete from Notify" + " where idNotify = :idNotify");

		query.setParameter("idNotify", idNotify);

		return (Integer) query.executeUpdate();
	}
}
