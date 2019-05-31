package org.firstinspires.ftc.teamcode;

import org.firstinspires.ftc.teamcode.RobotCommands.RobotCommand;

import java.util.ArrayList;

public class CommandQueue {

	private RobotControlSystemOpMode opMode;
	private ArrayList<SynchronousCommandGroup> synchronousCommandGroups = new ArrayList<>();
	private ArrayList<SynchronousCommandGroup> finishedGroups = new ArrayList<>();

	CommandQueue(RobotControlSystemOpMode opMode) {
		this.opMode = opMode;
	}

	boolean commandLoop () {
		//for loop going in the reverse direction so it can remove elements w/o freaking out
		for (int i = synchronousCommandGroups.size() - 1; i >= 0; i--) {
			if (!synchronousCommandGroups.get(i).commandLoop(opMode)) {
				finishedGroups.add(synchronousCommandGroups.remove(i));
			}
		}
		return synchronousCommandGroups.isEmpty();
	}

	void endAllCommands () {
		while (!synchronousCommandGroups.isEmpty()) {
			synchronousCommandGroups.remove(0).endAllCommands();
		}
	}

	public void add (RobotCommand command) {
		SynchronousCommandGroup grp = new SynchronousCommandGroup();
		grp.addNewCommand(command);
		add(grp);
	}

	public void add (SynchronousCommandGroup group) {
		synchronousCommandGroups.add(group);
	}

	public void add (ArrayList<SynchronousCommandGroup> list) {
		synchronousCommandGroups.addAll(list);
	}


}
