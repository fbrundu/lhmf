package it.polito.ai.lhmf.model;

import it.polito.ai.lhmf.exceptions.InvalidParametersException;
import it.polito.ai.lhmf.orm.Member;
import it.polito.ai.lhmf.orm.Message;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

public class MessageInterface
{
	// The session factory will be automatically injected by spring
	private SessionFactory sessionFactory;

	public void setSessionFactory(SessionFactory sf)
	{
		this.sessionFactory = sf;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public Integer newMessageToAdmin(Member admin, Member sender, String text)
			throws InvalidParametersException
	{
		if (admin == null || sender == null || text == null)
			throw new InvalidParametersException();

		Calendar calendar = Calendar.getInstance();
		java.util.Date now = calendar.getTime();
		Timestamp currentTimestamp = new Timestamp(now.getTime());

		Message m = new Message(admin, currentTimestamp, false, 0);
		m.setMemberByIdSender(sender);
		m.setText(text);
		return (Integer) sessionFactory.getCurrentSession().save(m);
	}
	
	@Transactional(propagation = Propagation.REQUIRED)
	public Integer newMessage(Message message)
			throws InvalidParametersException
	{
		if (message == null)
			throw new InvalidParametersException();

		return (Integer) sessionFactory.getCurrentSession().save(message);
	}

	@Transactional(readOnly = true)
	public Message getMessage(Integer idMessage)
	{
		Query query = sessionFactory.getCurrentSession().createQuery(
				"from Message " + "where idMessage = :idMessage");
		query.setParameter("idMessage", idMessage);
		return (Message) query.uniqueResult();
	}

	@SuppressWarnings("unchecked")
	public List<Message> getMessagesByIdMember(Integer idMember)
			throws InvalidParametersException
	{
		if (idMember == null)
			throw new InvalidParametersException();

		Query query = sessionFactory
				.getCurrentSession()
				.createQuery(
						"from Message"
								+ " where id_receiver = :idMember order by messageTimestamp desc");
		query.setParameter("idMember", idMember);
		List<Message> rMessagesList = query.list();
		// setAllRead(idMember);
		return rMessagesList;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public Integer setRead(Integer idMember, Integer idMessage)
			throws InvalidParametersException
	{
		if (idMember == null || idMessage == null)
			throw new InvalidParametersException();

		// FIXME : testare
		Query query = sessionFactory.getCurrentSession().createQuery(
				"update Message" + " set isReaded = true"
						+ " where isReaded = false and id_receiver = :idMember"
						+ " and idMessage = :idMessage");
		query.setParameter("idMember", idMember);
		query.setParameter("idMessage", idMessage);

		return (Integer) query.executeUpdate();
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public Integer setAllRead(Integer idMember)
			throws InvalidParametersException
	{
		if (idMember == null)
			throw new InvalidParametersException();

		// FIXME : testare
		Query query = sessionFactory
				.getCurrentSession()
				.createQuery(
						"update Message"
								+ " set isReaded = true"
								+ " where isReaded = false and id_receiver = :idMember");
		query.setParameter("idMember", idMember);

		return (Integer) query.executeUpdate();
	}

	/*
	 * @Transactional(propagation = Propagation.REQUIRED) public Integer
	 * updateMessage(Message message) throws InvalidParametersException { if
	 * (message == null) throw new InvalidParametersException();
	 * 
	 * Query query = sessionFactory.getCurrentSession().createQuery(
	 * "update Message " + "set text = :text," +
	 * "message_timestamp = :message_timestamp," + "idSender = :idSender," +
	 * "idReceiver = :idReceiver" + " where idMessage = :idMessage");
	 * query.setParameter("idMessage", message.getIdMessage());
	 * query.setParameter("text", message.getText());
	 * query.setParameter("message_timestamp", new java.sql.Timestamp(message
	 * .getMessageTimestamp().getTime())); query.setParameter("idSender",
	 * message.getMemberByIdSender() .getIdMember());
	 * query.setParameter("idReceiver", message.getMemberByIdReceiver()
	 * .getIdMember());
	 * 
	 * return (Integer) query.executeUpdate(); }
	 */
	@Transactional(readOnly = true)
	public Long getUnreadCount(Integer idMember)
			throws InvalidParametersException
	{
		if (idMember == null)
			throw new InvalidParametersException();

		Query query = sessionFactory
				.getCurrentSession()
				.createQuery(
						"select count(*) from Message"
								+ " where isReaded = false and id_receiver = :idMember");
		query.setParameter("idMember", idMember);

		return (Long) query.uniqueResult();
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public Integer deleteMessage(Integer idMessage)
			throws InvalidParametersException
	{
		if (idMessage == null)
			throw new InvalidParametersException();

		Query query = sessionFactory.getCurrentSession().createQuery(
				"delete from Message " + "where idMessage = :idMessage");

		query.setParameter("idMessage", idMessage);

		return (Integer) query.executeUpdate();
	}
}
