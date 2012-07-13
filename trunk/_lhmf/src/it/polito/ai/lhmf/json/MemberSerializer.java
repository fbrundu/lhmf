package it.polito.ai.lhmf.json;

import java.io.IOException;

import it.polito.ai.lhmf.orm.Member;

import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.JsonSerializer;
import org.codehaus.jackson.map.SerializerProvider;

/**
 * Serializer for the Hibernate class {@link Member}
 * 
 * @author Luca Moretto
 * 
 */
public class MemberSerializer extends JsonSerializer<Member>
{

	@Override
	public void serialize(Member value, JsonGenerator jgen,
			SerializerProvider provider) throws IOException,
			JsonProcessingException
	{
		jgen.writeStartObject();
		jgen.writeNumberField("idMember", value.getIdMember());
		jgen.writeStringField("name", value.getName());
		jgen.writeStringField("surname", value.getSurname());
		jgen.writeStringField("username", value.getUsername());
		// FIXME E' utile? questo campo può essere richiesto solo dall'admin
		// credo; nel caso serva lo rimettiamo
		// jgen.writeFieldName("regDate");
		// provider.defaultSerializeDateValue(value.getRegDate(), jgen);
		jgen.writeStringField("email", value.getEmail());
		jgen.writeStringField("address", value.getAddress());
		jgen.writeStringField("city", value.getCity());
		jgen.writeStringField("state", value.getState());
		jgen.writeNumberField("cap", value.getCap());
		jgen.writeStringField("tel", value.getTel());
		jgen.writeStringField("memberType", value.getMemberType()
				.getDescription());
		jgen.writeStringField("memberStatus", value.getMemberStatus()
				.getDescription());
		jgen.writeEndObject();
	}

}
