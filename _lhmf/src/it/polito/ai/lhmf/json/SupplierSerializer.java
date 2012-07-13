package it.polito.ai.lhmf.json;

import java.io.IOException;

import it.polito.ai.lhmf.orm.Member;
import it.polito.ai.lhmf.orm.Supplier;

import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.JsonSerializer;
import org.codehaus.jackson.map.SerializerProvider;

/**
 * Serializer for the Hibernate class {@link Member}
 * 
 * @author Francesco Brundu
 * 
 */
public class SupplierSerializer extends JsonSerializer<Supplier>
{

	@Override
	public void serialize(Supplier value, JsonGenerator jgen,
			SerializerProvider provider) throws IOException,
			JsonProcessingException
	{
		jgen.writeStartObject();

		jgen.writeNumberField("idMember", value.getIdMember());
		jgen.writeStringField("name", value.getName());
		jgen.writeStringField("surname", value.getSurname());
		jgen.writeStringField("username", value.getUsername());
//		jgen.writeStringField("password", value.getPassword());
		jgen.writeStringField("regCode", value.getRegCode());
		jgen.writeFieldName("regDate");
		provider.defaultSerializeDateValue(value.getRegDate(), jgen);
		jgen.writeStringField("email", value.getEmail());
		jgen.writeStringField("address", value.getAddress());
		jgen.writeStringField("city", value.getCity());
		jgen.writeStringField("state", value.getState());
		jgen.writeNumberField("cap", value.getCap());
		jgen.writeStringField("tel", value.getTel());
		jgen.writeBooleanField("active", value.isActive());
		jgen.writeNumberField("memberType", value.getMemberType());
		jgen.writeStringField("companyName", value.getCompanyName());
		jgen.writeStringField("description", value.getDescription());
		jgen.writeStringField("contactName", value.getContactName());
		jgen.writeStringField("fax", value.getFax());
		jgen.writeStringField("website", value.getWebsite());
		jgen.writeStringField("paymentMethod", value.getPaymentMethod());
		jgen.writeNumberField("idMember_resp", value.getMember().getIdMember());

		jgen.writeEndObject();
	}
}
