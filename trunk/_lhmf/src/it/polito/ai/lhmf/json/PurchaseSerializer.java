package it.polito.ai.lhmf.json;

import java.io.IOException;

import it.polito.ai.lhmf.orm.Purchase;

import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.JsonSerializer;
import org.codehaus.jackson.map.SerializerProvider;

/**
 * Serializer for the Hibernate class {@link Member}
 * 
 * @author Hassan Metwalley
 * 
 */

public class PurchaseSerializer extends JsonSerializer<Purchase>{
	
	@Override
	public void serialize(Purchase value, JsonGenerator jgen,
			SerializerProvider provider) throws IOException,
			JsonProcessingException
	{
		jgen.writeStartObject();
		jgen.writeNumberField("idPurchase", value.getIdPurchase());
		jgen.writeNumberField("idMember", value.getMember().getIdMember());
		jgen.writeNumberField("idOrder", value.getOrder().getIdOrder());
		jgen.writeEndObject();
	}

}
