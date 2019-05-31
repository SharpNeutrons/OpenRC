package com.qualcomm.robotcore.hardware;

import java.util.Locale;

public class LogitechJoystick {

	public double x_pos;

	public double y_pos;

	public double twist;

	public double throttle;

	private final double AXIS_DEAD_ZONE = 0.075;

	public boolean[] buttons = new boolean[12];

	public boolean switch_left = false;

	public boolean switch_right = false;

	public boolean switch_up = false;

	public boolean switch_down = false;

	public LogitechJoystick(byte[] bytes) {
		//it starts at 1 because 0 is the byte dedicated for message type
		//the & 0xFF is to convert from unsigned to signed values
		x_pos = (bytes[1] & 0xff) * 2/255d - 1;
		y_pos = (bytes[2] & 0xff) * 2/255d - 1;
		twist = (bytes[3] & 0xff) * 2/255d - 1;
		throttle = ((bytes[4] & 0xff) / 255d - 1) * -1;

		x_pos = applyDeadzone(x_pos);
		y_pos = applyDeadzone(y_pos);
		twist = applyDeadzone(twist);

//		buttons[0] = (bytes[5] & -128) == 1;

		for (int i = 0; i < buttons.length; i++) {
			byte b = bytes[i/8 + 5];
			byte comp = (byte)(0b10000000 >>> i%8);
			//buttons[i] = (b & (byte)Math.pow(2, i%8)) == 1;
			buttons[i] = (b & comp) == comp;
		}

//		buttons[8] = ((bytes[6] & -128 >>> 4)) == -128 >>> 4;
//		buttons[9] = (bytes[6] >>> 2 & 1) == 1;
//		buttons[10] = (bytes[6] >>> 1 & 1) == 1;
//		buttons[11] = (bytes[6] & 1) == 1;

		int switchPos = (bytes[6] & 15);
		switch (switchPos) {
			case 1:
				switch_up = true;
				break;
			case 2:
				switch_up = true;
				switch_right = true;
				break;
			case 3:
				switch_right = true;
				break;
			case 4:
				switch_down = true;
				switch_right = true;
				break;
			case 5:
				switch_down = true;
				break;
			case 6:
				switch_down = true;
				switch_left = true;
				break;
			case 7:
				switch_left = true;
				break;
			case 8:
				switch_up = true;
				switch_left = true;
				break;
			default:
				switch_down = false;
				switch_left = false;
				switch_up = false;
				switch_right = false;
		}
	}


	private double applyDeadzone (double d) {
		if (Math.abs(d) < AXIS_DEAD_ZONE) {
			return 0;
		}

		return d;
	}

	public String toString () {
		StringBuilder builder = new StringBuilder();
		String buttonString = "buttons:";
		for (boolean button:buttons) {
			buttonString += "\n" + button;
		}

		String s = String.format(Locale.US,"x: %f %n" +
									"y: %f %n" +
									"twist: %f %n" +
									"throttle: %f %n" +
									"switch: %n" +
									"%b %n" +
									"%b %n" +
									"%b %n" +
									"%b %n" +
									"%s",
				x_pos, y_pos, twist, throttle,
				switch_up, switch_right, switch_down, switch_left, buttonString);
		return s;
	}
}
