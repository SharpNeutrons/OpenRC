package com.sharpneutrons.pcinterface.message;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.sharpneutrons.pcinterface.message.MessageType;

import java.lang.reflect.Type;

public class MessageDeserializer implements JsonDeserializer<Message> {

	@Override
	public Message deserialize(JsonElement jsonElement, Type type,
							   JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
		JsonObject messageObj = jsonElement.getAsJsonObject();
		String messageTypeString = messageObj.get("type").getAsString();
		MessageType messageType = MessageType.valueOf(messageTypeString);
		JsonElement data = messageObj.get("data");
		return new Message(messageType, data);
	}

}
