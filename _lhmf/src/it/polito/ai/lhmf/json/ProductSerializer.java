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
		jgen.writeNumberField("unitBlock", value.getUnitBlock());
		jgen.writeBooleanField("availability", value.isAvailability());
		jgen.writeNumberField("transportCost", value.getTransportCost());
		jgen.writeNumberField("unitCost", value.getUnitCost());
		
		if(value.getMinBuy() == null)
			jgen.writeStringField("minBuy", "No");
		else
			jgen.writeNumberField("minBuy", value.getMinBuy());
		
		if(value.getMaxBuy() == null)
			jgen.writeStringField("maxBuy", "No");
		else
			jgen.writeNumberField("maxBuy", value.getMaxBuy());
		
		if(value.getImgPath() == null)
			jgen.writeStringField("imgPath", "img/noproduct.jpg");
		else
			jgen.writeStringField("imgPath", value.getImgPath());
		
		jgen.writeNumberField("idMemberSupplier", value.getSupplier()
				.getIdMember());
		jgen.writeObjectField("category", value.getProductCategory());
		jgen.writeEndObject();
	}

}
