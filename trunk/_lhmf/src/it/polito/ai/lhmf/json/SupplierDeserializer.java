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
import org.springframework.beans.factory.annotation.Autowired;

public class SupplierDeserializer extends JsonDeserializer<Supplier>
{
	@Autowired
	private SessionFactory sessionFactory;

	public void setSessionFactory(SessionFactory sessionFactory)
	{
		this.sessionFactory = sessionFactory;
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
			newSupplier.setName(node.get("name").getTextValue());
			newSupplier.setSurname(node.get("surname").getTextValue());
			newSupplier.setUsername(node.get("username").getTextValue());
			// newSupplier.setPassword(node.get("password").getTextValue());
//			newSupplier.setRegCode(node.get("regCode").getTextValue());
//			String regDateString = node.get("regDate").getTextValue();
//			newSupplier.setRegDate(ISO8601DateParser.parse(regDateString));
			newSupplier.setEmail(node.get("email").getTextValue());
			newSupplier.setAddress(node.get("address").getTextValue());
			newSupplier.setCity(node.get("city").getTextValue());
			newSupplier.setState(node.get("state").getTextValue());
			newSupplier.setCap(node.get("cap").getIntValue());
			newSupplier.setTel(node.get("tel").getTextValue());
			newSupplier.setActive(node.get("state").getBooleanValue());
//			newSupplier.setMemberType(node.get("memberType").getNumberValue()
//					.byteValue());
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
			newSupplier.setMember(memberResp);
		}
		catch (Exception e)
		{
			throw context.mappingException(e.getMessage());
		}
		return newSupplier;
	}
}
