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
		jgen.writeStringField("companyName", value.getCompanyName());
		jgen.writeStringField("description", value.getDescription());
		jgen.writeStringField("contactName", value.getContactName());
		jgen.writeStringField("fax", value.getFax());
		jgen.writeStringField("website", value.getWebsite());
		jgen.writeStringField("paymentMethod", value.getPaymentMethod());
		jgen.writeNumberField("idMemberResp", value.getMemberByIdMemberResp()
				.getIdMember());

		jgen.writeEndObject();
	}
}
