package it.polito.ai.lhmf.json;

import it.polito.ai.lhmf.orm.ProductCategory;

import java.io.IOException;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.ObjectCodec;
import org.codehaus.jackson.map.DeserializationContext;
import org.codehaus.jackson.map.JsonDeserializer;

public class ProductCategoryDeserializer extends
		JsonDeserializer<ProductCategory>
{
	@Override
	public ProductCategory deserialize(JsonParser jpar,
			DeserializationContext context) throws IOException,
			JsonProcessingException
	{
		ProductCategory newProductCategory = new ProductCategory();

		try
		{
			ObjectCodec oc = jpar.getCodec();
			JsonNode node = oc.readTree(jpar);

			newProductCategory.setIdProductCategory(node.get(
					"idProductCategory").getIntValue());
			newProductCategory.setDescription(node.get("description")
					.getTextValue());
		}
		catch (Exception e)
		{
			throw context.mappingException(e.getMessage());
		}
		return newProductCategory;
	}
}
