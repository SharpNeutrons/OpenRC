package com.qualcomm.robotcore.hardware;

public class KinectAngles {

	/**
	 * the angle the shadow of the arm makes with the x axis, going ccw from the x axis
	 */
	public double theta;

	/**
	 * the angle of elevation that the "bicep" makes with the horizontal;
	 */
	public double inclination;

	/**
	 * the angle of depression that the "forearm" makes with the line extended from the "bicep"
	 */
	public double alpha;

	public KinectAngles (double theta, double inclination, double alpha) {
		this.theta = theta;
		this.inclination = inclination;
		this.alpha = alpha;
	}

	public KinectAngles (byte[] bytes) {

		//first shift the byte left to give it 9 bits, to allow for the range from 0 to 360
		//then use bitwise and to convert it to the unsigned value
		this.theta = ((int)bytes[1] << 1) & 0xff;

		//use the bitwise and with 0xff to convert to the unsigned value
		this.inclination = bytes[2] & 0xff;
		this.alpha = bytes[3] & 0xff;
	}

}
