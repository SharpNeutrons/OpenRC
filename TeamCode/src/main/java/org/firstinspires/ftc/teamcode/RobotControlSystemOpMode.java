package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.LogitechJoystick;

import org.firstinspires.ftc.teamcode.RobotHardware.RobotHardware;

//import org.firstinspires.ftc.teamcode.HardwareClasses.RobotHardware;

public class RobotControlSystemOpMode extends LinearOpMode {

	CommandQueue commandQueue;
	RobotHardware robot;

	@Override
	public void runOpMode () {
		commandQueue = new CommandQueue(this);
		robot = new RobotHardware(hardwareMap);

		telemetry.addData("Status", "Initializing");
		telemetry.update();

		robot.setAbsoluteSwerveAngles(commandQueue, telemetry);

		while (!isStarted()) {
			opModeLoop();
			telemetry.addLine("Waiting to start");
			robot.getIfSwervesReady(telemetry);
			telemetry.update();
		}

		waitForStart();
	}

	void opModeLoop () {
		commandQueue.commandLoop();


		// as a fail safe, set the joystick to its default values
		// if there hasn't been an update for .2 seconds(the amt of time can be changed)
		if (getJoystickTimeout()) {
			joystick = new LogitechJoystick(new byte[]{0, 126, 126, 126, -1, 0, 0});
		}
	}

}
