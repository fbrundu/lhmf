package it.polito.ai.lhmf.json;

import it.polito.ai.lhmf.orm.*;

import org.codehaus.jackson.Version;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.module.SimpleModule;


/**
 * Use this class to register custom serializers to use when generating JSON responses.
 * 
 * To do so, use the method addSerializer passing as arguments a Class T and an extension of class JsonSerializer<T>.
 * For an example see {@link MemberSerializer}
 * @author Luca Moretto
 *
 */
public class CustomObjectMapper extends ObjectMapper {
	public CustomObjectMapper() {
		SimpleModule serializers = new SimpleModule("CustomSerializers", new Version(1, 0, 0, null));
		serializers.addSerializer(Member.class, new MemberSerializer());
		serializers.addSerializer(Product.class, new ProductSerializer());
		serializers.addSerializer(ProductCategory.class, new ProductCategorySerializer());
		serializers.addDeserializer(Product.class, new ProductDeserializer());
		registerModule(serializers);
	}
}
