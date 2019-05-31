package org.firstinspires.ftc.teamcode.RobotCommands;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.robotcore.external.Telemetry;

public class TelemetryUpdate implements RobotCommand {

	private String message;
	private Telemetry telemetry;

	public TelemetryUpdate(String message, Telemetry telemetry) {
		this.message = message;
		this.telemetry = telemetry;
	}

	@Override
	public void runCommand () {
		telemetry.addLine(message);
		telemetry.update();
	}

	@Override
	public boolean commandLoop (LinearOpMode opMode) {
		return false;
	}

	@Override
	public void endCommand () {}

}
