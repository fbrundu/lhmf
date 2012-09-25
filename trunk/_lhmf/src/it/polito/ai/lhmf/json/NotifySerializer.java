package it.polito.ai.lhmf.json;

import java.io.IOException;

import it.polito.ai.lhmf.orm.Notify;

import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.JsonSerializer;
import org.codehaus.jackson.map.SerializerProvider;

public class NotifySerializer extends JsonSerializer<Notify>
{

	@Override
	public void serialize(Notify value, JsonGenerator jgen,
			SerializerProvider provider) throws IOException,
			JsonProcessingException
	{
		jgen.writeStartObject();
		jgen.writeNumberField("idNotify", value.getIdNotify());
		jgen.writeStringField("member", value.getMember()
				.getUsername());
		jgen.writeStringField("text", value.getText());
		jgen.writeBooleanField("isReaded", value.isIsReaded());
		jgen.writeStringField("notifyTimestamp",
				ISO8601DateParser.toString(value.getNotifyTimestamp()));
		jgen.writeNumberField("notifyCategory",value.getNotifyCategory());
		jgen.writeEndObject();
	}
}
