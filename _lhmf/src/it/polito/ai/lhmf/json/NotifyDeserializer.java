package it.polito.ai.lhmf.json;

import it.polito.ai.lhmf.orm.Member;
import it.polito.ai.lhmf.orm.Notify;

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

public class NotifyDeserializer extends JsonDeserializer<Notify>
{
	private SessionFactory sessionFactory;

	public NotifyDeserializer(SessionFactory sf)
	{
		this.sessionFactory = sf;
	}

	@Override
	public Notify deserialize(JsonParser jpar, DeserializationContext context)
			throws IOException, JsonProcessingException
	{
		Notify newNotify = new Notify();

		try
		{
			Session hibernateSession = sessionFactory.getCurrentSession();

			ObjectCodec oc = jpar.getCodec();
			JsonNode node = oc.readTree(jpar);

			Query query = hibernateSession.createQuery("from Member "
					+ "where username = :username");
			query.setParameter("username", node.get("member").getTextValue());
			Member member = (Member) query.uniqueResult();
			newNotify.setMember(member);

			newNotify.setIdNotify(node.get("idNotify").getNumberValue()
					.intValue());
			newNotify.setText(node.get("text").getTextValue());
			newNotify.setIsReaded(node.get("isReaded").getBooleanValue());
			newNotify.setNotifyTimestamp(ISO8601DateParser.parse(node.get(
					"notifyTimestamp").getTextValue()));
			newNotify.setNotifyCategory(node.get("notifyCategory")
					.getIntValue());
		}
		catch (Exception e)
		{
			// TODO cosa fa mappingException??? perch� nella console non si vede
			// nulla e lato client nemmeno..
			throw context.mappingException(e.getMessage());
		}
		return newNotify;
	}
}
