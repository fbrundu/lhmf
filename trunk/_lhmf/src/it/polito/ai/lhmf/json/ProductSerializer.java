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
		jgen.writeNumberField("id_product", value.getIdProduct());
		jgen.writeStringField("name", value.getName());
		jgen.writeStringField("description", value.getDescription());
		jgen.writeNumberField("dimension", value.getDimension());
		jgen.writeStringField("measure_unit", value.getMeasureUnit());
		jgen.writeNumberField("unit_block", value.getUnitBlock());
		jgen.writeBooleanField("availability", value.isAvailability());
		jgen.writeNumberField("transport_cost", value.getTransportCost());
		jgen.writeNumberField("unit_cost", value.getUnitCost());
		jgen.writeNumberField("min_buy", value.getMinBuy());
		jgen.writeNumberField("max_buy", value.getMaxBuy());
		jgen.writeNumberField("id_member_supplier", value.getSupplier()
				.getIdMember());
		jgen.writeNumberField("id_product_category", value.getProductCategory()
				.getIdProductCategory());
		jgen.writeEndObject();
	}

}
