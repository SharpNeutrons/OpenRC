package com.sharpneutrons.pcinterface.message;

import android.util.Log;

import com.qualcomm.robotcore.hardware.KinectAngles;
import com.qualcomm.robotcore.hardware.LogitechJoystick;

public class Message {

	private MessageType type;
	private Object data;

	public Message(byte[] bytes) {
		this.type = MessageType.valueOf(bytes[0]);
		switch (type) {
			case JOYSTICK_DATA:
				this.data = new LogitechJoystick(bytes);
				break;
			case KINECT_SKELETON:
				this.data = new KinectAngles(bytes);
			default:
				this.data = new String("Unhandled message type");
		}
	}

	public MessageType getType() {
		return type;
	}

	public Object getData() {
		return data;
	}

}
