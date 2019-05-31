package org.firstinspires.ftc.teamcode.RobotHardware;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

import org.firstinspires.ftc.teamcode.RobotCommands.RunMotorToPosition;

import java.util.HashMap;

public class MotorWrapper {

	private DcMotor motor;

	public boolean autoControlActive;
	private HashMap<String, Integer> importantEncoderValues = new HashMap<>();

	MotorWrapper(DcMotor motor) {
		this.motor = motor;
		importantEncoderValues.put("min", null);
		importantEncoderValues.put("max", null);
	}

	MotorWrapper(DcMotor motor, int min, int max) {
		this.motor = motor;
		importantEncoderValues.put("min", min);
		importantEncoderValues.put("max", max);
	}


	/**MODIFIED and ADDED METHODS
	 * methods I've added to streamline the coding process**/

	public void init () {
		motor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
		motor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
		motor.setPower(0);
		motor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
	}

	/**
	 * A "smart" set power.
	 * Allows the class to see if the motor is outside of a set range of encoder values
	 * If it is outside the range, the method does not allow the motor to get further away from the end
	 * values, only closer to the range
	 * Also allows the class to see if encoder movement is active, and if it is, does not set the power
	 * Additionally does not set power if the requested is equal to the last
	 * @param power the power to set the motor
	 * @return whether or not the program set the motor power to the requested input
	 */
	public boolean setPower (double power) {
		if (importantEncoderValues.get("min") != null && importantEncoderValues.get("max") != null) {
			if (importantEncoderValues.get("min") < importantEncoderValues.get("max")) {
				int encoderVal = motor.getCurrentPosition();
				if (encoderVal < importantEncoderValues.get("min") && power < 0) {
					return false;
				}
				if (encoderVal > importantEncoderValues.get("max") && power > 0) {
					return false;
				}
			}
		}
		if (autoControlActive) {
			return false;
		}
		if (power == motor.getPower()) {
			return false;
		}
		setPowerOverride(power);
		return true;
	}

	/**
	 * add a single key-value pair to the encoder hashMap
	 * @param key a string that links to the value given
	 * @param val an encoder count that the motor can move to
	 */
	public void addEncoderValToMap (String key, int val) {
		importantEncoderValues.put(key, val);
	}

	/**
	 * Add a full hashMap to the encoderValue map
	 * @param map a hashmap containing strings and encoder values that the motor can move to
	 */
	public void addEncoderValuesToMap (HashMap<String, Integer> map) {
		importantEncoderValues.putAll(map);
	}

	public int getPosFromMap(String key) {
		return importantEncoderValues.get(key);
	}

	/**
	 * Note this does not actually execute the command, it just returns it so it can be used later
	 * @param position the encoder counts the motor should run to
	 * @param speed the speed the motor should run with
	 * @return the execute command, used by adding to a SynchronousCommandQueue
	 */
	RunMotorToPosition runMotorToPosition (int position, double speed) {
		return new RunMotorToPosition(this, position, speed);
	}

	RunMotorToPosition runMotorToPosition (String key, double speed) {
		if (importantEncoderValues.get(key) != null) {
			return new RunMotorToPosition(this, importantEncoderValues.get(key), speed);
		}
		return new RunMotorToPosition(this, getCurrentPosition(), speed);
	}

	/**
	 * Note this does not actually execute the command, it just returns it so it can be used later
	 * @param position the encoder counts the motor should run to
	 * @param speed the speed the motor should run with
	 * @param timeoutMillis how long the command should run if the motors are unable to finish
	 * @return the execute command, used by adding to a SynchronousCommandQueue
	 */
	RunMotorToPosition runMotorToPosition (int position, double speed, double timeoutMillis) {
		return new RunMotorToPosition(this, position, speed, timeoutMillis);
	}

	RunMotorToPosition runMotorToPosition (String key, double speed, double timeoutMillis) {
		if (importantEncoderValues.get(key) != null) {
			return new RunMotorToPosition(this, importantEncoderValues.get(key), speed, timeoutMillis);
		}
		return new RunMotorToPosition(this, getCurrentPosition(), speed, timeoutMillis);
	}

//	public void setMinMaxEncoder (int min, int max) {
//		importantEncoderValues.replace("min", min);
//		importantEncoderValues.replace("max", max);
//	}

	/**END Modified and Added Methods**/





	/**DEFAULT METHODS
	 * used to let the DcMotor field remain private,
	 * I've added some basic ones, and add others as I need them**/

	public double getPower () {
		return motor.getPower();
	}

	public void setTargetPostion (int targetPostion) {
		motor.setTargetPosition(targetPostion);
	}

	public int getTargetPosition () {
		return motor.getTargetPosition();
	}

	public int getCurrentPosition () {
		return motor.getCurrentPosition();
	}

	public void setMode (DcMotor.RunMode mode) {
		motor.setMode(mode);
	}

	public void setDirection (DcMotorSimple.Direction direction) {
		motor.setDirection(direction);
	}

	public void setZeroPowerBehavior (DcMotor.ZeroPowerBehavior behavior) {
		motor.setZeroPowerBehavior(behavior);
	}

	public boolean isBusy () {
		return motor.isBusy();
	}

	/**
	 * Sets the motor power to the given value no matter what
	 * @param power the power between -1 and 1 that will be set to the motor
	 */
	public void setPowerOverride (double power) {
		motor.setPower(power);
	}

}
