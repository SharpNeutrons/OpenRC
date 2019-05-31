package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.teamcode.RobotCommands.RobotCommand;

import java.util.concurrent.ConcurrentLinkedQueue;

public class SynchronousCommandGroup {

	private ConcurrentLinkedQueue<RobotCommand> commandQueue = new ConcurrentLinkedQueue<>();
	private ConcurrentLinkedQueue<RobotCommand> finishedCommands = new ConcurrentLinkedQueue<>();
	RobotCommand currentCommand;

	public SynchronousCommandGroup() {}

	public void addNewCommand (RobotCommand cmd) {
		commandQueue.add(cmd);
	}

	public void addAllToQueue (ConcurrentLinkedQueue<RobotCommand> queue) {
		commandQueue.addAll(queue);
	}

	public void concat (SynchronousCommandGroup cmdGroup) {
		this.addAllToQueue(cmdGroup.commandQueue);
	}

	/**Runs command loop on the currentCommand, or starts the next command if it is finished
	 * @param opMode the opmode that is running the command group,
	 *               used so the loop can check whether or not the opmode is active
	 * @return false only when there are no more commands to execute
	 */
	boolean commandLoop (LinearOpMode opMode) {
		if (currentCommand == null) {
			return startNewCommand();
		}
		if (!currentCommand.commandLoop(opMode)) {
			currentCommand.endCommand();
			//there might be some problems with pointer vs. value reference in the next 2 lines
			//but i think this is ok right now. The object current command is pointing to is passed into add,
			//add puts the object into the queue
			//currentCommand is made so it no longer points to the object
			finishedCommands.add(currentCommand);
			currentCommand = null;
		}
		return true;
	}

	private boolean startNewCommand () {
		if (commandQueue.isEmpty()) {
			return false;
		}
		//idk why it's called poll instead of dequeue, but it returns and removes the first element
		currentCommand = commandQueue.poll();
		currentCommand.runCommand();
		return true;
	}

	void endAllCommands () {
		while (!commandQueue.isEmpty()) {
			commandQueue.poll().endCommand();
		}
	}

}
