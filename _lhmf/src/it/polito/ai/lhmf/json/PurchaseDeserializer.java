package it.polito.ai.lhmf.json;

import java.io.IOException;

import it.polito.ai.lhmf.orm.Member;
import it.polito.ai.lhmf.orm.Order;
import it.polito.ai.lhmf.orm.Purchase;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.ObjectCodec;
import org.codehaus.jackson.map.DeserializationContext;
import org.codehaus.jackson.map.JsonDeserializer;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;

public class PurchaseDeserializer extends JsonDeserializer<Purchase>{

	@Autowired
	private SessionFactory sessionFactory;

	public void setSessionFactory(SessionFactory sessionFactory)
	{
		this.sessionFactory = sessionFactory;
	}
	
	@Override
	public Purchase deserialize(JsonParser jPar, DeserializationContext context)
			throws IOException, JsonProcessingException {
		Purchase newPurchase = new Purchase();
		try
		{
			Session hibernateSession = sessionFactory.getCurrentSession();
			ObjectCodec oc = jPar.getCodec();
			JsonNode node = oc.readTree(jPar);
			
			newPurchase.setIdPurchase(node.get("idPurchase").getIntValue());
			
			Query queryMember = hibernateSession.createQuery("from Member where idMember = :idMember");
			queryMember.setParameter("idMember", node.get("idMember").getNumberValue());
			Member member = (Member) queryMember.uniqueResult();
			newPurchase.setMember(member);
			
			Query queryOrder = hibernateSession.createQuery("from Order where idOrder = :idOrder");
			queryOrder.setParameter("idOrder", node.get("idOrder").getNumberValue());
			Order order = (Order) queryOrder.uniqueResult();
			newPurchase.setOrder(order);
		}
		catch (Exception e)
		{
			throw context.mappingException(e.getMessage());
		}
		return newPurchase;
	}	
}
