package it.polito.ai.lhmf.json;

import it.polito.ai.lhmf.orm.Member;
import it.polito.ai.lhmf.orm.MemberStatus;
import it.polito.ai.lhmf.orm.MemberType;

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

public class MemberDeserializer extends JsonDeserializer<Member>
{
	@Autowired
	private SessionFactory sessionFactory;

	public void setSessionFactory(SessionFactory sessionFactory)
	{
		this.sessionFactory = sessionFactory;
	}

	@Override
	public Member deserialize(JsonParser jpar, DeserializationContext context)
			throws IOException, JsonProcessingException
	{
		Member newMember = new Member();

		try
		{
			Session hibernateSession = sessionFactory.getCurrentSession();

			ObjectCodec oc = jpar.getCodec();
			JsonNode node = oc.readTree(jpar);

			newMember.setIdMember(node.get("idMember").getIntValue());
			newMember.setName(node.get("name").getTextValue());
			newMember.setSurname(node.get("surname").getTextValue());
			newMember.setUsername(node.get("username").getTextValue());
			// newSupplier.setPassword(node.get("password").getTextValue());
			// newSupplier.setRegCode(node.get("regCode").getTextValue());
			// String regDateString = node.get("regDate").getTextValue();
			// newSupplier.setRegDate(ISO8601DateParser.parse(regDateString));
			newMember.setEmail(node.get("email").getTextValue());
			newMember.setAddress(node.get("address").getTextValue());
			newMember.setCity(node.get("city").getTextValue());
			newMember.setState(node.get("state").getTextValue());
			newMember.setCap(node.get("cap").getIntValue());
			newMember.setTel(node.get("tel").getTextValue());

			Query query = hibernateSession.createQuery("from MemberType "
					+ "where description = :typeDescription");
			query.setParameter("typeDescription", node.get("memberType")
					.getTextValue());
			MemberType memberType = (MemberType) query.uniqueResult();
			newMember.setMemberType(memberType);
			
			query = hibernateSession.createQuery("from MemberStatus "
					+ "where description = :statusDescription");
			query.setParameter("statusDescription", node.get("memberStatus")
					.getTextValue());
			MemberStatus memberStatus = (MemberStatus) query.uniqueResult();
			newMember.setMemberStatus(memberStatus);
		}
		catch (Exception e)
		{
			throw context.mappingException(e.getMessage());
		}
		return newMember;
	}
}
