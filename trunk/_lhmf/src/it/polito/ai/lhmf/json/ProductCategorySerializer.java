package it.polito.ai.lhmf.json;

import java.io.IOException;

import it.polito.ai.lhmf.orm.Member;
import it.polito.ai.lhmf.orm.ProductCategory;

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
public class ProductCategorySerializer extends JsonSerializer<ProductCategory>
{

	@Override
	public void serialize(ProductCategory value, JsonGenerator jgen,
			SerializerProvider provider) throws IOException,
			JsonProcessingException
	{
		jgen.writeStartObject();
		jgen.writeNumberField("idProductCategory",
				value.getIdProductCategory());
		jgen.writeStringField("description", value.getDescription());
		jgen.writeEndObject();
	}

}
