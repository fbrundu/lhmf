package it.polito.ai.lhmf.json;

import java.io.IOException;

import it.polito.ai.lhmf.orm.Message;

import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.JsonSerializer;
import org.codehaus.jackson.map.SerializerProvider;

public class MessageSerializer extends JsonSerializer<Message>
{

	@Override
	public void serialize(Message value, JsonGenerator jgen,
			SerializerProvider provider) throws IOException,
			JsonProcessingException
	{
		jgen.writeStartObject();
		jgen.writeNumberField("idMessage", value.getIdMessage());
		jgen.writeStringField("sender", value.getMemberByIdSender()
				.getUsername());
		jgen.writeStringField("receiver", value.getMemberByIdReceiver()
				.getUsername());
		jgen.writeStringField("text", value.getText());
		jgen.writeBooleanField("isReaded", value.isIsReaded());
		if (value.getOrder() != null)
			jgen.writeNumberField("idOrder", value.getOrder().getIdOrder());
		if (value.getProduct() != null)
			jgen.writeNumberField("idProduct", value.getProduct()
					.getIdProduct());
		jgen.writeStringField("messageTimestamp",
				ISO8601DateParser.toString(value.getMessageTimestamp()));
		jgen.writeNumberField("messageCategory",value.getMessageCategory());
		jgen.writeEndObject();
	}
}
