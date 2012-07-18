package it.polito.ai.lhmf.json;

import it.polito.ai.lhmf.orm.Member;
import it.polito.ai.lhmf.orm.Order;

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

public class OrderDeserializer extends JsonDeserializer<Order>{

	private SessionFactory sessionFactory;

	public OrderDeserializer(SessionFactory sf) {
		this.sessionFactory = sf;
	}
	
	@Override
	public Order deserialize(JsonParser jPar, DeserializationContext context)
			throws IOException, JsonProcessingException {
		Order newOrder = new Order();
		try
		{
			Session hibernateSession = sessionFactory.getCurrentSession();
			ObjectCodec oc = jPar.getCodec();
			JsonNode node = oc.readTree(jPar);
			
			newOrder.setIdOrder(node.get("idOrder").getIntValue());
			newOrder.setDateOpen(ISO8601DateParser.parse(node.get("dateOpen").getTextValue()));
			newOrder.setDateClose(ISO8601DateParser.parse(node.get("dateClose").getTextValue()));
			newOrder.setDateDelivery(ISO8601DateParser.parse(node.get("dateDelivery").getTextValue()));
			
			Query queryResp = hibernateSession.createQuery("from Member where idMember = :idMember");
			queryResp.setParameter("idMember", node.get("idMemberResp").getNumberValue());
			Member memberResp = (Member) queryResp.uniqueResult();
			newOrder.setMember(memberResp);
			
			Query querySupplier = hibernateSession.createQuery("from Supplier where idMember = :idMember");
			querySupplier.setParameter("idMember", node.get("idMemberSupplier").getNumberValue());
			Member memberSupplier = (Member) querySupplier.uniqueResult();
			newOrder.setMember(memberSupplier);
		}
		catch (Exception e)
		{
			throw context.mappingException(e.getMessage());
		}
		return newOrder;
	}
}
