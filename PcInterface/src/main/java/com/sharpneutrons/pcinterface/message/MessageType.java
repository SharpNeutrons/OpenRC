package com.sharpneutrons.pcinterface.message;

import java.util.Arrays;

public enum MessageType {

	JOYSTICK_DATA(0),
	KINECT_SKELETON(1);

	private final int value;

	MessageType (int value) {
		this.value = value;
	}

	public static MessageType valueOf (int value) {
		return values().clone()[value];
	}

}
