package it.polito.ai.lhmf.json;

import it.polito.ai.lhmf.orm.*;

import org.codehaus.jackson.Version;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.module.SimpleModule;
import org.hibernate.SessionFactory;

/**
 * Use this class to register custom serializers to use when generating JSON
 * responses.
 * 
 * To do so, use the method addSerializer passing as arguments a Class T and an
 * extension of class JsonSerializer<T>. For an example see
 * {@link MemberSerializer}
 * 
 * @author Luca Moretto, Francesco Brundu
 * 
 */
public class CustomObjectMapper extends ObjectMapper
{
	public CustomObjectMapper(SessionFactory sf)
	{
		SimpleModule serializers = new SimpleModule("CustomSerializers",
				new Version(1, 0, 0, null));

		serializers.addSerializer(Member.class, new MemberSerializer());
		serializers.addSerializer(Order.class, new OrderSerializer());
		serializers.addSerializer(Product.class, new ProductSerializer());
		serializers.addSerializer(ProductCategory.class,
				new ProductCategorySerializer());
		serializers.addSerializer(Purchase.class, new PurchaseSerializer());
		serializers.addSerializer(Supplier.class, new SupplierSerializer());

		serializers.addDeserializer(Member.class, new MemberDeserializer());
		serializers.addDeserializer(Order.class, new OrderDeserializer());
		serializers.addDeserializer(Product.class, new ProductDeserializer(sf));
		serializers.addDeserializer(ProductCategory.class,
				new ProductCategoryDeserializer(sf));
		serializers.addDeserializer(Purchase.class, new PurchaseDeserializer());
		serializers.addDeserializer(Supplier.class,
				new SupplierDeserializer());

		registerModule(serializers);
	}
}
