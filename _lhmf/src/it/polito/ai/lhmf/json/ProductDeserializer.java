package it.polito.ai.lhmf.json;

import java.io.IOException;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;

import it.polito.ai.lhmf.orm.Product;
import it.polito.ai.lhmf.orm.ProductCategory;
import it.polito.ai.lhmf.orm.Supplier;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.JsonToken;
import org.codehaus.jackson.ObjectCodec;
import org.codehaus.jackson.map.DeserializationContext;
import org.codehaus.jackson.map.JsonDeserializer;
import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

public class ProductDeserializer extends JsonDeserializer<Product>
{

	@Override
	public Product deserialize(JsonParser jpar, DeserializationContext context)
			throws IOException, JsonProcessingException
	{
		Product newProduct = new Product();

		try
		{
			Session hibernateSession = (Session) ((ServletRequestAttributes) RequestContextHolder
					.getRequestAttributes()).getRequest().getAttribute(
					"hibernate_session");

			ObjectCodec oc = jpar.getCodec();
			JsonNode node = oc.readTree(jpar);
			
			newProduct.setIdProduct(node.get("idProduct").getIntValue());
			newProduct.setName(node.get("name").getTextValue());
			newProduct.setDescription(node.get("description").getTextValue());
			newProduct.setDimension(node.get("dimension").getIntValue());
			newProduct.setMeasureUnit(node.get("measureUnit").getTextValue());
			newProduct.setUnitBlock(node.get("unitBlock").getIntValue());
			newProduct.setAvailability(node.get("availability")
					.getBooleanValue());
			newProduct.setTransportCost(node.get(
					"transportCost").getNumberValue().floatValue());
			newProduct.setUnitCost(node.get("unitCost")
					.getNumberValue().floatValue());
			newProduct.setMinBuy(node.get("minBuy").getIntValue());
			newProduct.setMaxBuy(node.get("maxBuy").getIntValue());

			Query query = hibernateSession
					.createQuery("from Supplier where idMember = :idMember");
			query.setParameter("idMember", node.get("idMember").getNumberValue());
			Supplier supplier = (Supplier) query.uniqueResult();
			newProduct.setSupplier(supplier);

			query = hibernateSession
					.createQuery("from ProductCategory where idProduct_Category = :idProductCategory");
			query.setParameter("idProductCategory",
					node.get("idProductCategory").getNumberValue());
			ProductCategory productCategory = (ProductCategory) query
					.uniqueResult();
			newProduct.setProductCategory(productCategory);
		}
		catch (Exception e)
		{
			throw context.mappingException(e.getMessage());
		}
		return newProduct;
	}
}
