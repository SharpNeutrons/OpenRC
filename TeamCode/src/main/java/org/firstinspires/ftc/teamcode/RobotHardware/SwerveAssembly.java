package org.firstinspires.ftc.teamcode.RobotHardware;

import android.opengl.Matrix;
import android.text.method.Touch;

import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.DigitalChannel;
import com.qualcomm.robotcore.hardware.TouchSensor;
import com.qualcomm.robotcore.util.Range;

import org.firstinspires.ftc.teamcode.RobotCommands.GetAbsoluteSwerveAnglePart1;
import org.firstinspires.ftc.teamcode.RobotCommands.GetAbsoluteSwerveAnglePart2;
import org.firstinspires.ftc.teamcode.RobotCommands.WaitMillis;
import org.firstinspires.ftc.teamcode.SynchronousCommandGroup;
import org.joml.Vector3d;

import java.util.Vector;

public class SwerveAssembly {

	/**
	 * Begin Hardware References
	 */
	MotorWrapper[] driveMotors = new MotorWrapper[2];
	MotorWrapper orientationMotor;
	TouchSensorWrapper touchSensor;



	/**
	 * Begin Declarations of Finals
	 */
	private final double O_COUNTS_PER_MOTOR_REV = 1120;
	private final double DRIVE_COUNTS_PER_MOTOR_REV = 537.6;
	private final double O_MOTOR_REVS_PER_ASSEMBLY_REV = 9.6;
	private final double D_MOTOR_REVS_PER_WHEEL_REV = 1;
	private final double WHEEL_RADIUS_MM = 2 * 25.4;//2 in. radius --> mm
	private final double D_MOTOR_FREE_SPEED_RPM = 340;
	final double DRIVE_SPEED_MAX =  D_MOTOR_FREE_SPEED_RPM / D_MOTOR_REVS_PER_WHEEL_REV
									* 2 * Math.PI * WHEEL_RADIUS_MM / 1000 / 60;//mm/min
	final double D_SPEED_MAX_SQ = Math.pow(DRIVE_SPEED_MAX, 2);
	private final double O_MOTOR_FREE_SPEED_RPM = 150;
	private final double ASSEMBLY_MAX_RPM = O_MOTOR_FREE_SPEED_RPM / O_MOTOR_REVS_PER_ASSEMBLY_REV;
	private final double ASSEMBLY_MAX_RAD_PER_SEC = ASSEMBLY_MAX_RPM * 2 * Math.PI / 60;
	private final double JOYSTICK_UPDATE_FREQUENCY = 20;//how many times per second is the joystick data updated
	private final double ABSOLUTE_ORIENTATION_SPEED = 0.2;
	private final double ABSOLUTE_ORIENTATION_SPEED_2 = 0.0375;

	private boolean isZeroed = false;

	Vector3d WHEEL_POS;
	private double WHEEL_ANGLE_OFFSET;//TODO in radians the starting angle of the wheel
									// when it is zeroed by the program, counterclockwise from x-axis



	//TODO maybe add something to track if the swerve has been zeroed


	/**
	 * Sets up the hardware references to the motors and sensors, as well as initializing the motors
	 * and setting the position of the wheel
	 * @param drive1 Reference to the first drive motor
	 * @param drive2 Reference to the second drive motor
	 * @param orientationMotor Reference to the orientation motor
	 * @param touchSensor Reference to the color sensor that allows for absolute positioning of the wheel
	 * @param x X coordinate of the center of the wheel, in mm
	 * @param y Y coordinate of the center of the wheel, in mm
	 */
	SwerveAssembly (MotorWrapper drive1, MotorWrapper drive2, MotorWrapper orientationMotor,
	                       TouchSensorWrapper touchSensor, double x, double y, double angleOffset) {
		driveMotors[0] = drive1;
		driveMotors[1] = drive2;
		this.orientationMotor = orientationMotor;
		this.touchSensor = touchSensor;

		for (MotorWrapper motor:driveMotors) {
			motor.init();
			motor.init();
		}

		orientationMotor.init();
		orientationMotor.setDirection(DcMotorSimple.Direction.REVERSE);

		WHEEL_POS = new Vector3d(x, y, 0);
		WHEEL_ANGLE_OFFSET = angleOffset;
	}

	SynchronousCommandGroup getAbsoluteOrientation () {
		SynchronousCommandGroup grp = new SynchronousCommandGroup();
		grp.addNewCommand(
				new GetAbsoluteSwerveAnglePart1(orientationMotor, touchSensor,
						ABSOLUTE_ORIENTATION_SPEED));
		grp.addNewCommand(
				new WaitMillis(500)
		);
		grp.addNewCommand(
				new GetAbsoluteSwerveAnglePart2(orientationMotor, touchSensor,
						this, ABSOLUTE_ORIENTATION_SPEED_2));
		return grp;
	}

	Vector3d calcAngleAndSpeed (double v_x, double v_y, double w, double imuHeading) {
		Vector3d v_t = new Vector3d(v_x, v_y, 0);
		Vector3d v_w = new Vector3d(0, 0, w);

		//TODO  this is where the center of rotation would be changed by adding (or subtracting, depending
		//      on how it is defined) the offset to the wheel position. I am not yet sure if it should
		//      be before or after the vector is rotated

		//rotates the wheel's position around the z axis by the IMU calculated heading to allow for
		//easier control as it rotates and translates at the same time
		Vector3d posRotated = new Vector3d(0, 0, 0);
		WHEEL_POS.rotateZ(imuHeading, posRotated);

		//Gets the cross product of the requested angular velocity and the wheel's position
		//this is the direction the wheel will point if there is no translational velocity
		Vector3d wr = new Vector3d(0, 0, 0);
		v_w.cross(posRotated, wr);

		//Adds the translational and rotational wheel velocities together to get the resultant
		//direction. This is where the wheel will point, and how fast it will move
		Vector3d wheelTarget = new Vector3d(0, 0, 0);
		wr.add(v_t, wheelTarget);

		//from here the hardware class will compare all of the vectors to see if any of them have magnitudes
		//that are greater than the max speed.
		//If they to have a speed that is too fast, the vectors will all be scaled down, and then passed into
		//each assembly's driveFromVector method where the motor powers and such are actually set
		return wheelTarget;
	}

	void driveFromVector(Vector3d vec, double imuHeading) {
		//gets the fraction of the maximum speed that the requested v is, so the motor power can be set
		double wheelSpeed = vec.length();
		double drivePower = wheelSpeed / DRIVE_SPEED_MAX;
		//don't set the motor powers yet because it might be negated

		//gets the angle between the requested v and the x axis
		//it is an absolute value, so that needs to be adjusted
		double targetAngle = RobotHardware.getAngle(vec.x, vec.y);


		double angleDifference = targetAngle - getWheelAngle(imuHeading);

		alignWheel(angleDifference);
		accelerateDrive(drivePower, angleDifference);

	}

	public void swerveAngle (double angle, double speed, double heading) {
		angle %= 2 * Math.PI;
		double angleDifference = angle - getWheelAngle(heading);

		alignWheel(angleDifference);

		accelerateDrive(speed, angleDifference);

	}

	private void alignWheel (double angleDifference) {
		//if the wheel would turn more than 180 degrees, make it turn the other way
		if (Math.abs(angleDifference) > Math.PI) {
			angleDifference = -(2 * Math.PI - angleDifference);
		}

		//if the wheel would turn more than 90 degrees, it would be faster to reverse the motor direction
		//and have the wheel in the direction opposite the supplement of the difference
		//TODO some more intelligent decision making could be helpful
//		if (Math.abs(angleDifference) > Math.PI/2) {
//			angleDifference = -(Math.PI - angleDifference);
//			drivePower *= -1;//TODO if we end up doing this, figure out a way to change power
//		}

		//this is the rotational speed that the assembly would need to have to get to the desired angle
		//by the time the angle is next updated
		double neededRotationalSpeed = angleDifference * JOYSTICK_UPDATE_FREQUENCY;

		//this is the ratio between the needed rotational speed, and the max possible speed
		double rotationSpeedRatio = neededRotationalSpeed / ASSEMBLY_MAX_RAD_PER_SEC;


		double clipRange = 1;
		if (Math.abs(angleDifference) < 0.25) {
			clipRange = 0.1;
		}
		if (Math.abs(angleDifference) < 0.1) {
			clipRange = 0.05;
		}
		if (Math.abs(angleDifference) < 0.05) {
			clipRange = 0.01;
		}
		//clip the ratio between -1 and 1 because that is the maximum range of motor powers
		rotationSpeedRatio = Range.clip(rotationSpeedRatio, -clipRange, clipRange);

		orientationMotor.setPower(rotationSpeedRatio);
	}

	private void accelerateDrive (double speed, double angleDifference) {
		double currSpeed = avgDrivePower();
		if (Math.abs(angleDifference) > 0.1 && currSpeed == 0) {//~5 degrees
			speed = 0;//don't drive the motors unless the wheel is close enough to target
		}

		double maxDriveChange = 0.05;
		double driveChange = speed - currSpeed;

		if (Math.abs(driveChange) > maxDriveChange) {
			speed = currSpeed + Math.copySign(maxDriveChange, driveChange);
		}

		for (MotorWrapper driveMotor : driveMotors) {
			driveMotor.setPower(speed);
		}
	}




	public Vector3d getWheelUVector (double heading) {
		double angle = getWheelAngle(heading);
		return new Vector3d(1,0,0).rotateZ(angle);
	}

	public double getWheelAngle (double heading) {
		int posCounts = orientationMotor.getCurrentPosition();
		double oMotorRevs = posCounts/O_COUNTS_PER_MOTOR_REV;

		//motor revs / ratio % 1 =
		double swerveRevs = oMotorRevs / O_MOTOR_REVS_PER_ASSEMBLY_REV % 1;
		double relativeAngle = swerveRevs * 2 * Math.PI;

		return relativeAngle + WHEEL_ANGLE_OFFSET - heading;
	}

	public boolean isZeroed () {
		return isZeroed;
	}

	public void setZeroed (boolean val) {
		isZeroed = val;
	}

	double avgDrivePower () {
		return RobotHardware.arrAvg(new double[]{driveMotors[0].getPower(), driveMotors[1].getPower()});
	}

}

