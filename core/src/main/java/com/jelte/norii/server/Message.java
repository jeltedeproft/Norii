package com.jelte.norii.server;

import com.github.czyzby.websocket.serialization.SerializationException;
import com.github.czyzby.websocket.serialization.Transferable;
import com.github.czyzby.websocket.serialization.impl.Deserializer;
import com.github.czyzby.websocket.serialization.impl.Serializer;

public class Message implements Transferable<Message> {
	public String message;

	public Message(String message) {
		this.message = message;
	}

	public Message() {
	}

	@Override
	public void serialize(Serializer serializer) throws SerializationException {
		serializer.serializeString(message);
	}

	@Override
	public Message deserialize(Deserializer deserializer) throws SerializationException {
		return new Message(deserializer.deserializeString());
	}
}
