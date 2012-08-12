package it.polito.ai.lhmf.model;

import it.polito.ai.lhmf.exceptions.InvalidParametersException;
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
	public Integer newNotify(Notify notify) throws InvalidParametersException
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
	public List<Notify> getNotifiesByIdMember(Integer idMember)
			throws InvalidParametersException
	{
		if (idMember == null)
			throw new InvalidParametersException();

		Query query = sessionFactory.getCurrentSession().createQuery(
				"from Notify " + "where idMember = :idMember");
		query.setParameter("idMember", idMember);
		return query.list();
	}

	@SuppressWarnings("unchecked")
	@Transactional(readOnly = true)
	public List<Notify> getNotifiesByIdMemberAfter(Integer idMember,
			Integer idNotify) throws InvalidParametersException
	{
		if (idMember == null || idNotify == null)
			throw new InvalidParametersException();

		// FIXME : testare
		Query query = sessionFactory.getCurrentSession().createQuery(
				"from Notify " + "where idMember = :idMember"
						+ "and idNotify > :idNotify");
		query.setParameter("idMember", idMember);
		query.setParameter("idNotify", idNotify);

		return query.list();
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public Integer setReadBefore(Integer idNotify)
			throws InvalidParametersException
	{
		if (idNotify == null)
			throw new InvalidParametersException();

		// FIXME : testare
		Query query = sessionFactory.getCurrentSession().createQuery(
				"update Notify" + " set isReaded = true"
						+ " where idNotify <= :idNotify and isReaded = false");
		query.setParameter("idNotify", idNotify);

		return (Integer) query.executeUpdate();
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
