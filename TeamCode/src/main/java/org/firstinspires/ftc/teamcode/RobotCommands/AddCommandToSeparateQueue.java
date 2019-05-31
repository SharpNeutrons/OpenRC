package org.firstinspires.ftc.teamcode.RobotCommands;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.teamcode.CommandQueue;
import org.firstinspires.ftc.teamcode.SynchronousCommandGroup;

/**
 * This command creates a new SynchronousCommandGroup with the given command in it
 * When run, it adds the synchronousCommandGroup to the given command queue so it will be run
 * makes it easier to have multiple commands running asynchronously
 */
public class AddCommandToSeparateQueue implements RobotCommand {

	private SynchronousCommandGroup group;
	private CommandQueue queue;

	/**
	 *
	 * @param queue the CommandQueue that the synchronous group will be added to
	 * @param commands the list of commands in order that will be added to the synchronous group
	 */
	public AddCommandToSeparateQueue(CommandQueue queue, RobotCommand... commands) {
		group = new SynchronousCommandGroup();
		for (RobotCommand command:commands) {
			group.addNewCommand(command);
		}
		this.queue = queue;
	}

	public AddCommandToSeparateQueue(CommandQueue queue, SynchronousCommandGroup group) {
		this.group = group;
		this.queue = queue;
	}

	@Override
	public void runCommand () {
		queue.add(group);
	}

	@Override
	public boolean commandLoop (LinearOpMode opMode) {
		return false;
	}

	@Override
	public void endCommand (){}
}
