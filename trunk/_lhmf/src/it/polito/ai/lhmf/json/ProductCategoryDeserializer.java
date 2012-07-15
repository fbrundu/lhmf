package it.polito.ai.lhmf.json;

import it.polito.ai.lhmf.orm.ProductCategory;

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

public class ProductCategoryDeserializer extends
		JsonDeserializer<ProductCategory>
{
	private SessionFactory sessionFactory;

	public ProductCategoryDeserializer(SessionFactory sf)
	{
		this.sessionFactory = sf;
	}

	@Override
	public ProductCategory deserialize(JsonParser jpar,
			DeserializationContext context) throws IOException,
			JsonProcessingException
	{
		ProductCategory newProductCategory = new ProductCategory();

		try
		{
//			Session hibernateSession = sessionFactory.getCurrentSession();

			ObjectCodec oc = jpar.getCodec();
			JsonNode node = oc.readTree(jpar);

			newProductCategory.setIdProductCategory(node.get(
					"idProductCategory").getIntValue());
			newProductCategory.setDescription(node.get("description")
					.getTextValue());

			// Query query = hibernateSession
			// .createQuery("from Supplier where idMember = :idMember");
			// query.setParameter("idMember", node.get("idMember")
			// .getNumberValue());
			// Supplier supplier = (Supplier) query.uniqueResult();
			// newProductCategory.setSupplier(supplier);
		}
		catch (Exception e)
		{
			throw context.mappingException(e.getMessage());
		}
		return newProductCategory;
	}
}
