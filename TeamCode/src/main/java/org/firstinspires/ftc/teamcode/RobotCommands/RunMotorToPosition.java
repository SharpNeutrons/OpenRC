package org.firstinspires.ftc.teamcode.RobotCommands;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.RobotCommands.RobotCommand;
import org.firstinspires.ftc.teamcode.RobotHardware.MotorWrapper;

public class RunMotorToPosition implements RobotCommand {

	private MotorWrapper motor;
	private int targetPos;
	private double speed;
	private boolean shouldTimeout;
	private double timeoutMillis;
	private ElapsedTime elapsedTime = new ElapsedTime();

	public RunMotorToPosition(MotorWrapper motor, int targetPos, double speed) {
		this.motor = motor;
		this.targetPos = targetPos;
		this.speed = speed;
		this.shouldTimeout = false;
	}

	public RunMotorToPosition(MotorWrapper motor, int targetPos, double speed, double timeoutMillis) {
		this.motor = motor;
		this.targetPos = targetPos;
		this.speed = speed;
		this.shouldTimeout = true;
		this.timeoutMillis = timeoutMillis;
	}

	@Override
	public void runCommand () {
		motor.autoControlActive = true;
		motor.setTargetPostion(targetPos);
		motor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
		motor.setPowerOverride(speed);
		if (shouldTimeout) {
			elapsedTime.reset();
		}
	}

	@Override
	public boolean commandLoop (LinearOpMode opMode) {
		if (opMode.opModeIsActive()) {
			//if the motor is still running, check the timeout
			//if the motor is not still running, it will return false
			if (motor.isBusy()) {
				return elapsedTime.milliseconds() < timeoutMillis || !shouldTimeout;
			}
		}
		return false;
	}

	@Override
	public void endCommand () {
		motor.setPowerOverride(0);
		motor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
		motor.autoControlActive = false;
	}

}
