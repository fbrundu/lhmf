package it.polito.ai.lhmf.json;

import it.polito.ai.lhmf.orm.Member;
import it.polito.ai.lhmf.orm.Message;
import it.polito.ai.lhmf.orm.Order;
import it.polito.ai.lhmf.orm.Product;

import java.io.IOException;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.ObjectCodec;
import org.codehaus.jackson.map.DeserializationContext;
import org.codehaus.jackson.map.JsonDeserializer;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

public class MessageDeserializer extends JsonDeserializer<Message>
{
	private SessionFactory sessionFactory;

	public MessageDeserializer(SessionFactory sf)
	{
		this.sessionFactory = sf;
	}

	@Override
	public Message deserialize(JsonParser jpar, DeserializationContext context)
			throws IOException, JsonProcessingException
	{
		Message newMessage = new Message();

		try
		{
			Session hibernateSession = sessionFactory.getCurrentSession();

			ObjectCodec oc = jpar.getCodec();
			JsonNode node = oc.readTree(jpar);

			Query query = hibernateSession.createQuery("from Member "
					+ "where username = :username");
			query.setParameter("username", node.get("sender").getTextValue());
			Member sender = (Member) query.uniqueResult();
			newMessage.setMemberByIdSender(sender);

			query = hibernateSession.createQuery("from Member "
					+ "where username = :username");
			query.setParameter("username", node.get("receiver").getTextValue());
			Member receiver = (Member) query.uniqueResult();
			newMessage.setMemberByIdReceiver(receiver);

			newMessage.setIdMessage(node.get("idMessage").getNumberValue()
					.intValue());
			newMessage.setText(node.get("text").getTextValue());
			newMessage.setIsReaded(node.get("isReaded").getBooleanValue());
			if (node.get("idOrder") != null)
			{
				query = hibernateSession.createQuery("from Order "
						+ "where idOrder = :idOrder");
				query.setParameter("idOrder", node.get("idOrder").getIntValue());
				Order order = (Order) query.uniqueResult();
				newMessage.setOrder(order);
			}
			if (node.get("idProduct") != null)
			{
				query = hibernateSession.createQuery("from Product "
						+ "where idProduct = :idProduct");
				query.setParameter("idProduct", node.get("idProduct")
						.getIntValue());
				Product product = (Product) query.uniqueResult();
				newMessage.setProduct(product);
			}
			newMessage.setMessageTimestamp(ISO8601DateParser.parse(node.get(
					"messageTimestamp").getTextValue()));
			newMessage.setMessageCategory(node.get("messageCategory")
					.getIntValue());
		}
		catch (Exception e)
		{
			// TODO cosa fa mappingException??? perch� nella console non si vede
			// nulla e lato client nemmeno..
			throw context.mappingException(e.getMessage());
		}
		return newMessage;
	}
}
