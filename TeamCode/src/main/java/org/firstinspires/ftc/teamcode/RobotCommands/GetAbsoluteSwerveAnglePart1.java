package org.firstinspires.ftc.teamcode.RobotCommands;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DigitalChannel;
import com.qualcomm.robotcore.hardware.TouchSensor;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.RobotHardware.MotorWrapper;
import org.firstinspires.ftc.teamcode.RobotHardware.TouchSensorWrapper;

/**
 * This RobotCommand is used to find the absolute angle of an individual swerve assembly
 * it is only the first of 2 commands. This one runs a motor at a set speed until a touch sensor
 * is activated. Then Part 2 will run.
 */
public class GetAbsoluteSwerveAnglePart1 implements RobotCommand {

	private MotorWrapper motor;
	private TouchSensorWrapper touchSensor;
	private double speed;

	public GetAbsoluteSwerveAnglePart1(MotorWrapper motor, TouchSensorWrapper touchSensor, double speed) {
		this.motor = motor;
		this.touchSensor = touchSensor;
		this.speed = speed;
	}

	@Override
	public void runCommand () {
		motor.autoControlActive = true;
		motor.setPowerOverride(speed);
	}

	@Override
	public boolean commandLoop (LinearOpMode opMode) {
		if (!opMode.isStopRequested()) {
			//if the sensor is pressed, stop (return false)
			//if the sensor is not pressed, keep going (return true)
			return !touchSensor.isPressed();

		}
		return false;
	}

	@Override
	public void endCommand () {
		motor.setPowerOverride(0);
	}

}
