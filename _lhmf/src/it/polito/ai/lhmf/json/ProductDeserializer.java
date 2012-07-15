package it.polito.ai.lhmf.json;

import it.polito.ai.lhmf.orm.Product;
import it.polito.ai.lhmf.orm.ProductCategory;
import it.polito.ai.lhmf.orm.Supplier;

import java.io.IOException;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.ObjectCodec;
import org.codehaus.jackson.map.DeserializationContext;
import org.codehaus.jackson.map.JsonDeserializer;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

public class ProductDeserializer extends JsonDeserializer<Product>
{
	private SessionFactory sessionFactory;

	public ProductDeserializer(SessionFactory sf)
	{
		this.sessionFactory = sf;
	}

	@Override
	public Product deserialize(JsonParser jpar, DeserializationContext context)
			throws IOException, JsonProcessingException
	{
		Product newProduct = new Product();

		try
		{
			Session hibernateSession = sessionFactory.getCurrentSession();

			ObjectCodec oc = jpar.getCodec();
			JsonNode node = oc.readTree(jpar);

			newProduct.setIdProduct(node.get("id_product").getIntValue());
			newProduct.setName(node.get("name").getTextValue());
			newProduct.setDescription(node.get("description").getTextValue());
			newProduct.setDimension(node.get("dimension").getIntValue());
			newProduct.setMeasureUnit(node.get("measure_unit").getTextValue());
			newProduct.setUnitBlock(node.get("unit_block").getIntValue());
			newProduct.setAvailability(node.get("availability")
					.getBooleanValue());
			newProduct.setTransportCost(node.get("transport_cost")
					.getNumberValue().floatValue());
			newProduct.setUnitCost(node.get("unit_cost").getNumberValue()
					.floatValue());
			newProduct.setMinBuy(node.get("min_buy").getIntValue());
			newProduct.setMaxBuy(node.get("max_buy").getIntValue());

			Query query = hibernateSession
					.createQuery("from Supplier where idMember = :idMember");
			query.setParameter("idMember", node.get("id_member_supplier")
					.getNumberValue());
			Supplier supplier = (Supplier) query.uniqueResult();
			newProduct.setSupplier(supplier);

			query = hibernateSession.createQuery("from ProductCategory "
					+ "where idProduct_Category = :idProductCategory");
			query.setParameter("idProductCategory",
					node.get("id_product_category").getNumberValue());
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
