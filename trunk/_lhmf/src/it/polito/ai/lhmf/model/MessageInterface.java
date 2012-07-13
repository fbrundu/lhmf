package it.polito.ai.lhmf.model;

import it.polito.ai.lhmf.exceptions.InvalidParametersException;
import it.polito.ai.lhmf.orm.Message;

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
				"from Message" + "where idMessage = :idMessage");
		query.setParameter("idMessage", idMessage);
		return (Message) query.uniqueResult();
	}

	@SuppressWarnings("unchecked")
	@Transactional(readOnly = true)
	public List<Message> getMessages()
	{
		return sessionFactory.getCurrentSession().createQuery("from Message")
				.list();
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public Integer updateMessage(Message message)
			throws InvalidParametersException
	{
		if (message == null)
			throw new InvalidParametersException();

		// FIXME : ci vuole la virgola alla fine delle string?
		Query query = sessionFactory.getCurrentSession().createQuery(
				"update Message" + "set text = :text,"
						+ "message_timestamp = :message_timestamp,"
						+ "idSender = :idSender," + "idReceiver = :idReceiver"
						+ "where idMessage = :idMessage");
		query.setParameter("idMessage", message.getIdMessage());
		query.setParameter("text", message.getText());
		query.setParameter("message_timestamp", new java.sql.Timestamp(message
				.getMessageTimestamp().getTime()));
		query.setParameter("idSender", message.getMemberByIdSender()
				.getIdMember());
		query.setParameter("idReceiver", message.getMemberByIdReceiver()
				.getIdMember());

		return (Integer) query.executeUpdate();
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public Integer deleteMessage(Integer idMessage)
			throws InvalidParametersException
	{
		if (idMessage == null)
			throw new InvalidParametersException();

		Query query = sessionFactory.getCurrentSession().createQuery(
				"delete from Message" + "where idMessage = :idMessage");

		query.setParameter("idMember", idMessage);

		return (Integer) query.executeUpdate();
	}
}
