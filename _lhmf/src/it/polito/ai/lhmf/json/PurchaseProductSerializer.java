package it.polito.ai.lhmf.json;

import java.io.IOException;

import it.polito.ai.lhmf.orm.PurchaseProduct;

import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.JsonSerializer;
import org.codehaus.jackson.map.SerializerProvider;


public class PurchaseProductSerializer extends JsonSerializer<PurchaseProduct>
{

	@Override
	public void serialize(PurchaseProduct value, JsonGenerator jgen,
			SerializerProvider provider) throws IOException,
			JsonProcessingException 
	{
		jgen.writeStartObject();
		jgen.writeNumberField("idPurchase", value.getId().getIdPurchase());
		jgen.writeNumberField("idProduct", value.getId().getIdProduct());
		jgen.writeNumberField("amount", value.getAmount());
		jgen.writeEndObject();
	}

}
