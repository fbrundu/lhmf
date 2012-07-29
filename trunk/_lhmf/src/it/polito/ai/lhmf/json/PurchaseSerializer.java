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
		
		if(value.isIsShipped() == true)
			jgen.writeStringField("isShipped", "Spedizione Effettuata");
		else
			jgen.writeStringField("isShipped", "Spedizione Non Effettuata");
		
		jgen.writeObjectField("member", value.getMember());
		jgen.writeObjectField("order", value.getOrder());
		jgen.writeEndObject();
	}

}
