package org.firstinspires.ftc.teamcode.RobotHardware;

import com.qualcomm.robotcore.hardware.DigitalChannel;

public class TouchSensorWrapper {

	private DigitalChannel sensor;

	public TouchSensorWrapper (DigitalChannel sensor) {
		this.sensor = sensor;
		this.sensor.setMode(DigitalChannel.Mode.INPUT);
	}

	public boolean isPressed () {
		return !sensor.getState();
	}

	public DigitalChannel.Mode getMode () {
		return sensor.getMode();
	}



}
