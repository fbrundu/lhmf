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
		serializers.addSerializer(Message.class, new MessageSerializer());
		serializers.addSerializer(Notify.class, new NotifySerializer());
		serializers.addSerializer(Order.class, new OrderSerializer());
		serializers.addSerializer(Product.class, new ProductSerializer());
		serializers.addSerializer(ProductCategory.class,
				new ProductCategorySerializer());
		serializers.addSerializer(Purchase.class, new PurchaseSerializer());
		serializers.addSerializer(Supplier.class, new SupplierSerializer());

		serializers.addDeserializer(Member.class, new MemberDeserializer(sf));
		serializers.addDeserializer(Message.class, new MessageDeserializer(sf));
		serializers.addDeserializer(Notify.class, new NotifyDeserializer(sf));
		serializers.addDeserializer(Order.class, new OrderDeserializer(sf));
		serializers.addDeserializer(Product.class, new ProductDeserializer(sf));
		serializers.addDeserializer(ProductCategory.class,
				new ProductCategoryDeserializer(sf));
		serializers.addDeserializer(Purchase.class, new PurchaseDeserializer(sf));
		serializers.addDeserializer(Supplier.class,
				new SupplierDeserializer(sf));

		registerModule(serializers);
	}
}
