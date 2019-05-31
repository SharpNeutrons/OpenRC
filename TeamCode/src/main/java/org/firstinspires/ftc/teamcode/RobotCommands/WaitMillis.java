package org.firstinspires.ftc.teamcode.RobotCommands;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.util.ElapsedTime;

public class WaitMillis implements RobotCommand {

	private double waitTime;
	private ElapsedTime elapsedTime = new ElapsedTime();

	public WaitMillis(double waitTime) {
		this.waitTime = waitTime;
	}

	@Override
	public void runCommand () {
		elapsedTime.reset();
	}

	@Override
	public boolean commandLoop (LinearOpMode opMode) {
		return (elapsedTime.milliseconds() < waitTime) && opMode.opModeIsActive();
	}

	@Override
	public void endCommand (){}
}
