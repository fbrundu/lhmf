package it.polito.ai.lhmf.json;

import java.io.IOException;

import it.polito.ai.lhmf.orm.OrderProduct;

import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.JsonSerializer;
import org.codehaus.jackson.map.SerializerProvider;

public class OrderProductSerializer extends JsonSerializer<OrderProduct> {

	@Override
	public void serialize(OrderProduct value, JsonGenerator jgen,
			SerializerProvider provider) throws IOException,
			JsonProcessingException {
		jgen.writeStartObject();
		jgen.writeObjectField("order", value.getOrder());
		jgen.writeObjectField("product", value.getProduct());
		jgen.writeBooleanField("failed", value.isFailed());
		jgen.writeEndObject();
	}

}
