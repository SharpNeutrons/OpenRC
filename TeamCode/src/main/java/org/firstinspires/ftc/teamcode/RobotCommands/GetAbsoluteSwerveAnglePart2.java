package org.firstinspires.ftc.teamcode.RobotCommands;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DigitalChannel;
import com.qualcomm.robotcore.hardware.TouchSensor;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.RobotHardware.MotorWrapper;
import org.firstinspires.ftc.teamcode.RobotHardware.SwerveAssembly;
import org.firstinspires.ftc.teamcode.RobotHardware.TouchSensorWrapper;

/**
 * This RobotCommand is used to find the absolute angle of an individual swerve assembly
 * it is only the first of 2 commands. This one runs a motor at a very slow set speed until a touch
 * sensor is no longer activated. Then the motor's encoder will be reset.
 */
public class GetAbsoluteSwerveAnglePart2 implements RobotCommand {

	private MotorWrapper motor;
	private TouchSensorWrapper touchSensor;
	private double speed;
	private SwerveAssembly swerve;

	public GetAbsoluteSwerveAnglePart2(MotorWrapper motor, TouchSensorWrapper touchSensor,
	                                   SwerveAssembly swerve, double speed) {
		this.motor = motor;
		this.touchSensor = touchSensor;
		this.swerve = swerve;
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
			//if the sensor is not pressed, stop (return false)
			//if the sensor is pressed, keep going (return true)
			return touchSensor.isPressed();
		}
		return false;
	}

	@Override
	public void endCommand () {
		motor.setPowerOverride(0);
		motor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
		motor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
		motor.autoControlActive = false;
		swerve.setZeroed(true);
	}

}
