package org.firstinspires.ftc.teamcode.RobotHardware;

import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.Range;

import java.util.HashMap;

public class ServoWrapper {

	private Servo servo;

	public boolean autoControlActive;
	private HashMap<String, Double> importantPositions = new HashMap<>();

	public ServoWrapper(Servo servo, double startPos) {
		this.servo = servo;
		importantPositions.put("start", Range.clip(startPos, 0, 1));
	}




	/**MODIFIED and ADDED METHODS
	 * methods I've added to streamline the coding process**/

	public void init () {
		servo.setPosition(importantPositions.get("start"));
	}

	/**
	 * A "smart" set position.
	 * Also allows the class to see if auto movement is active, and if it is, does not set the pos
	 * Additionally does not set position if the requested is equal to the last
	 * @param pos - the position
	 */
	public void setPosition (double pos) {
		if (servo.getPosition() != pos && !autoControlActive) {
			servo.setPosition(Range.clip(pos, 0, 1));
		}
	}

	public void setPosition (String key) {
		if (importantPositions.get(key) != null) {
			setPosition(importantPositions.get(key));
		}
	}

	/**
	 * add a single key-value pair to the encoder hashMap
	 * @param key the string that the value is mapped to
	 * @param val a number from 0 to 1 that the servo will move to
	 */
	public void addPositionToMap (String key, double val) {
		importantPositions.put(key, Range.clip(val, 0, 1));
	}

	/**
	 * Add a full hashMap to the encoderValue map
	 * @param map a hashMap of keys and servo position pairs to be added to the map
	 */
	public void addPositionsToMap (HashMap<String, Double> map) {
		importantPositions.putAll(map);
	}

	public double getPosFromMap (String key) {
		return importantPositions.get(key);
	}

	public void changeByPos (double deltaPos) {
		setPosition(servo.getPosition() + deltaPos);
	}



	/**DEFAULT METHODS
	 * used to let the DcMotor field remain private,
	 * I've added some basic ones, and add others as I need them**/

	public double getPosition () {
		return servo.getPosition();
	}

	/**
	 * Sets the motor power to the given value no matter what
	 * @param position - the position to set it to
	 */
	public void setPositionOverride (double position) {
		servo.setPosition(position);
	}

}
