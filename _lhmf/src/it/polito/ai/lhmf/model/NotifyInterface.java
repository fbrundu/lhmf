package it.polito.ai.lhmf.model;

import it.polito.ai.lhmf.exceptions.InvalidParametersException;
import it.polito.ai.lhmf.orm.Notify;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

public class NotifyInterface
{
	// The session factory will be automatically injected by spring
	private SessionFactory sessionFactory;

	private MemberInterface memberInterface;
	
	public void setSessionFactory(SessionFactory sf)
	{
		this.sessionFactory = sf;
	}
	
	public void setMemberInterface(MemberInterface memberInterface)
	{
		this.memberInterface = memberInterface;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public Integer newNotify(Notify notify) throws InvalidParametersException
	{
		if (notify == null)
			throw new InvalidParametersException();

		return (Integer) sessionFactory.getCurrentSession().save(notify);
	}

	@Transactional(readOnly = true)
	public Notify getNotify(Integer idNotify) throws InvalidParametersException
	{
		if (idNotify == null)
			throw new InvalidParametersException();

		Query query = sessionFactory.getCurrentSession().createQuery(
				"from Notify " + "where idNotify = :idNotify");
		query.setParameter("idNotify", idNotify);
		return (Notify) query.uniqueResult();
	}

	@Transactional(readOnly = true)
	public List<Notify> getNotifiesByUsername(String username)
			throws InvalidParametersException
	{
		if (username == null)
			throw new InvalidParametersException();

		return getNotifiesByIdMember(memberInterface.getMember(username)
				.getIdMember());
	}
	
	@SuppressWarnings("unchecked")
	@Transactional(readOnly = true)
	public List<Notify> getNotifiesByIdMember(Integer idMember)
			throws InvalidParametersException
	{
		if (idMember == null)
			throw new InvalidParametersException();

		Query query = sessionFactory
				.getCurrentSession()
				.createQuery(
						"from Notify "
								+ "where idMember = :idMember order by notifyTimestamp desc");
		query.setParameter("idMember", idMember);

		List<Notify> rNotifiesList = query.list();
		//setAllRead(idMember);
		return rNotifiesList;
	}

	@SuppressWarnings("unchecked")
	@Transactional(readOnly = true)
	public List<Notify> getNotifiesByIdMemberAfter(Integer idMember,
			Integer idNotify) throws InvalidParametersException
	{
		if (idMember == null || idNotify == null)
			throw new InvalidParametersException();

		Query query = sessionFactory
				.getCurrentSession()
				.createQuery(
						"from Notify "
								+ "where idMember = :idMember"
								+ "and idNotify > :idNotify order by notifyTimestamp desc");
		query.setParameter("idMember", idMember);
		query.setParameter("idNotify", idNotify);

		List<Notify> rNotifiesList = query.list();
		//setAllRead(idMember);
		return rNotifiesList;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public Integer setRead(String username, Integer idNotify)
			throws InvalidParametersException
	{
		if (username == null || idNotify == null)
			throw new InvalidParametersException();

		Query query = sessionFactory.getCurrentSession().createQuery(
				"update Notify" + " set isReaded = true"
						+ " where isReaded = false and idMember = :idMember"
						+ " and idNotify = :idNotify");
		query.setParameter("idMember", memberInterface.getMember(username)
				.getIdMember());
		query.setParameter("idNotify", idNotify);

		return (Integer) query.executeUpdate();
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public Integer setAllRead(Integer idMember)
			throws InvalidParametersException
	{
		if (idMember == null)
			throw new InvalidParametersException();

		Query query = sessionFactory.getCurrentSession().createQuery(
				"update Notify" + " set isReaded = true"
						+ " where isReaded = false and idMember = :idMember");
		query.setParameter("idMember", idMember);

		return (Integer) query.executeUpdate();
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public Integer setReadBefore(Integer idNotify, Integer idMember)
			throws InvalidParametersException
	{
		if (idNotify == null || idMember == null)
			throw new InvalidParametersException();

		Query query = sessionFactory
				.getCurrentSession()
				.createQuery(
						"update Notify"
								+ " set isReaded = true"
								+ " where idNotify <= :idNotify and isReaded = false and idMember = :idMember");
		query.setParameter("idNotify", idNotify);
		query.setParameter("idMember", idMember);

		return (Integer) query.executeUpdate();
	}

	@Transactional(readOnly = true)
	public Long getUnreadCount(String username)
			throws InvalidParametersException
	{
		if (username == null)
			throw new InvalidParametersException();

		Query query = sessionFactory.getCurrentSession().createQuery(
				"select count(*) from Notify"
						+ " where isReaded = false and idMember = :idMember");
		query.setParameter("idMember", memberInterface.getMember(username)
				.getIdMember());

		return (Long) query.uniqueResult();
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

	@Transactional(propagation = Propagation.REQUIRED)
	public List<Notify> getNewNotifiesByUsername(String username) throws InvalidParametersException {
		List<Notify> notifies = getNotifiesByUsername(username);
		
		if(notifies == null)
			return null;
		
		List<Notify> ret = new ArrayList<Notify>();
		
		for(Notify n : notifies){
			if(!n.isIsReaded()){
				ret.add(n);
				//Importante settarle come lette per evitare che android riceva continuamente le stesse notifiche!
				n.setIsReaded(true);
			}
		}
		return ret;
	}
}
