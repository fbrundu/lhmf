package it.polito.ai.lhmf.json;

import java.io.IOException;

import it.polito.ai.lhmf.orm.Order;

import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.JsonSerializer;
import org.codehaus.jackson.map.SerializerProvider;

import com.sun.corba.se.impl.orbutil.graph.Node;

public class OrderSerializer extends JsonSerializer<Order>
{
	@Override
	public void serialize(Order value, JsonGenerator jgen,
			SerializerProvider provider) throws IOException,
			JsonProcessingException
	{
		jgen.writeStartObject();
		jgen.writeNumberField("idOrder", value.getIdOrder());
		jgen.writeStringField("dateOpen",
				ISO8601DateParser.toString(value.getDateOpen()));
		jgen.writeStringField("dateClose",
				ISO8601DateParser.toString(value.getDateClose()));
		jgen.writeStringField("dateDelivery",
				ISO8601DateParser.toString(value.getDateDelivery()));
		jgen.writeNumberField("idMemberResp", value.getMember().getIdMember());
		jgen.writeNumberField("idMemberSupplier", value.getSupplier()
				.getIdMember());
		jgen.writeEndObject();
	}
}
