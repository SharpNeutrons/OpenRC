package org.firstinspires.ftc.teamcode.RobotCommands;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

public interface RobotCommand {

	boolean isRunning = false;

	/**
	 * The code to be run when the command starts.
	 * i.e. DcMotor.setMode(RUN_TO_POSITION) for a move by encoder command
	 */
	void runCommand();

	/**
	 * The code to be run every loop when the opmode and command are running.
	 * used to determine if the command should stop, and to give telemetry data.
	 * @param opMode used to reference if opmode is still running and to give telemetry
	 * @return if the command is still running (true), or if it should be stopped (false)
	 */
	boolean commandLoop(LinearOpMode opMode);

	/**
	 * The code to be run when the command is finished
	 * i.e. setPower(0) etc.
	 */
	void endCommand();

}
