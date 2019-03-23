package com.sharpneutrons.pcinterface.message;

import com.sharpneutrons.pcinterface.message.MessageType;

public class Message {

	private MessageType type;
	private Object data;

	public Message(MessageType type) {
		this(type, null);
	}

	public Message(MessageType type, Object data) {
		this.type = type;
		this.data = data;
	}

	public MessageType getType() {
		return type;
	}

	public Object getData() {
		return data;
	}

}
