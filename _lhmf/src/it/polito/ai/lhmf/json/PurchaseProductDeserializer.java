package it.polito.ai.lhmf.json;

import it.polito.ai.lhmf.orm.Product;
import it.polito.ai.lhmf.orm.Purchase;
import it.polito.ai.lhmf.orm.PurchaseProduct;
import it.polito.ai.lhmf.orm.PurchaseProductId;

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

public class PurchaseProductDeserializer extends JsonDeserializer<PurchaseProduct>{

	private SessionFactory sessionFactory;

	public PurchaseProductDeserializer(SessionFactory sf) {
		this.sessionFactory = sf;
	}
	
	@Override
	public PurchaseProduct deserialize(JsonParser jPar, DeserializationContext context)
			throws IOException, JsonProcessingException 
	{
		PurchaseProduct newPurchaseP = new PurchaseProduct();
		try
		{
			Session hibernateSession = sessionFactory.getCurrentSession();
			ObjectCodec oc = jPar.getCodec();
			JsonNode node = oc.readTree(jPar);
			
			PurchaseProductId id = new PurchaseProductId(node.get("idPurchase").getIntValue(), node.get("idProduct").getIntValue());
			newPurchaseP.setId(id);
			
			Query queryPurchase = hibernateSession.createQuery("from Purchase where idPurchase = :idPurchase");
			queryPurchase.setParameter("idPurchase", node.get("idPurchase").getNumberValue());
			Purchase purchase = (Purchase) queryPurchase.uniqueResult();
			newPurchaseP.setPurchase(purchase);
			
			newPurchaseP.setAmount(node.get("amount").getIntValue());
			
			Query queryProduct = hibernateSession.createQuery("from Product where idProduct = :idProduct");
			queryProduct.setParameter("idProduct", node.get("idProduct").getNumberValue());
			Product product = (Product) queryPurchase.uniqueResult();
			newPurchaseP.setProduct(product);
			
			newPurchaseP.setInsertedTimestamp(ISO8601DateParser.parse(node.get("insertedTimestamp").getTextValue()));
			
		}
		catch (Exception e)
		{
			throw context.mappingException(e.getMessage());
		}
		return newPurchaseP;
	}	
}
