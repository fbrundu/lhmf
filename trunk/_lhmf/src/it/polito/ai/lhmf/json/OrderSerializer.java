package it.polito.ai.lhmf.json;

import java.io.IOException;

import it.polito.ai.lhmf.orm.Order;

import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.JsonSerializer;
import org.codehaus.jackson.map.SerializerProvider;

public class OrderSerializer extends JsonSerializer<Order>
{
	@Override
	public void serialize(Order value, JsonGenerator jgen,
			SerializerProvider provider) throws IOException,
			JsonProcessingException
	{
		jgen.writeStartObject();
		jgen.writeNumberField("idOrder", value.getIdOrder());
		jgen.writeStringField("date_open", value.getDateOpen().toString());
		jgen.writeStringField("date_close", value.getDateClose().toString());
		jgen.writeStringField("date_delivery", value.getDateDelivery().toString());
		jgen.writeNumberField("idMember_resp", value.getMember().getIdMember());
		jgen.writeNumberField("idMember_supplier", value.getSupplier().getIdMember());
		jgen.writeEndObject();
	}
}