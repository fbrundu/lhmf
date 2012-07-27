package it.polito.ai.lhmf.json;

import java.io.IOException;

import it.polito.ai.lhmf.orm.Member;
import it.polito.ai.lhmf.orm.Product;

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
public class ProductSerializer extends JsonSerializer<Product>
{

	@Override
	public void serialize(Product value, JsonGenerator jgen,
			SerializerProvider provider) throws IOException,
			JsonProcessingException
	{
		jgen.writeStartObject();
		jgen.writeNumberField("idProduct", value.getIdProduct());
		jgen.writeStringField("name", value.getName());
		jgen.writeStringField("description", value.getDescription());
		jgen.writeNumberField("dimension", value.getDimension());
		jgen.writeStringField("measureUnit", value.getMeasureUnit());
		
		if(value.getUnitBlock() == null)
			jgen.writeStringField("unitBlock", "null");
		else
			jgen.writeNumberField("unitBlock", value.getUnitBlock());
		
		jgen.writeBooleanField("availability", value.isAvailability());
		jgen.writeNumberField("transportCost", value.getTransportCost());
		jgen.writeNumberField("unitCost", value.getUnitCost());
		
		if(value.getMinBuy() == null)
			jgen.writeStringField("minBuy", "no limit");
		else
			jgen.writeNumberField("minBuy", value.getMinBuy());
		
		if(value.getMaxBuy() == null)
			jgen.writeStringField("maxBuy", "no limit");
		else
			jgen.writeNumberField("maxBuy", value.getMaxBuy());
		
		//TODO: aggiungere attributo path immagine
		
		jgen.writeNumberField("idMemberSupplier", value.getSupplier()
				.getIdMember());
		jgen.writeStringField("category", value.getProductCategory()
				.getDescription());
		jgen.writeEndObject();
	}

}
