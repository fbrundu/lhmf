package it.polito.ai.lhmf.json;

import it.polito.ai.lhmf.orm.Member;
import it.polito.ai.lhmf.orm.Supplier;

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

public class SupplierDeserializer extends JsonDeserializer<Supplier>
{
	private SessionFactory sessionFactory;

	public SupplierDeserializer(SessionFactory sf)
	{
		this.sessionFactory = sf;
	}

	@Override
	public Supplier deserialize(JsonParser jpar, DeserializationContext context)
			throws IOException, JsonProcessingException
	{
		Supplier newSupplier = new Supplier();

		try
		{
			Session hibernateSession = sessionFactory.getCurrentSession();

			ObjectCodec oc = jpar.getCodec();
			JsonNode node = oc.readTree(jpar);

			newSupplier.setIdMember(node.get("idMember").getIntValue());
			newSupplier.setCompanyName(node.get("companyName").getTextValue());
			newSupplier.setDescription(node.get("description").getTextValue());
			newSupplier.setContactName(node.get("contactName").getTextValue());
			newSupplier.setFax(node.get("fax").getTextValue());
			newSupplier.setWebsite(node.get("website").getTextValue());
			newSupplier.setPaymentMethod(node.get("paymentMethod")
					.getTextValue());

			Query query = hibernateSession
					.createQuery("from Member where idMember = :idMember");
			query.setParameter("idMember", node.get("idMemberResp")
					.getNumberValue());
			Member memberResp = (Member) query.uniqueResult();
			newSupplier.setMemberByIdMemberResp(memberResp);
		}
		catch (Exception e)
		{
			throw context.mappingException(e.getMessage());
		}
		return newSupplier;
	}
}
