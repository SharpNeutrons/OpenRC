package org.firstinspires.ftc.teamcode.RobotHardware;

import com.qualcomm.hardware.bosch.BNO055IMU;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DigitalChannel;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.TouchSensor;
import com.qualcomm.robotcore.util.Hardware;
import com.qualcomm.robotcore.util.Range;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.CommandQueue;
import org.firstinspires.ftc.teamcode.RobotCommands.TelemetryUpdate;
import org.firstinspires.ftc.teamcode.SynchronousCommandGroup;
import org.joml.Vector3d;

public class RobotHardware {

	SwerveAssembly[] swerveDrive = new SwerveAssembly[4];
	ImuWrapper imu;

	private final double wP = 207.175;//the x and y values of each wheel
	//a 2d array of the positions of the axis of rotation of each swerve assembly relative to the
	//centroid of the octagon. The values are: x, y, t; where t is the angle offset of the swerve
	//when the button is pressed
	private final double[][] swervePositions = new double[][] {
			{wP, wP, toRadians(293 - 34.4)},
			{-wP, wP, toRadians(203 - 34.4)},
			{-wP, -wP, toRadians(109 - 34.4)},
			{wP, -wP, toRadians(19 - 34.4)}};

	public RobotHardware (HardwareMap hwMap) {
		for (int i = 0; i < swerveDrive.length; i++) {
			swerveDrive[i] = new SwerveAssembly(
					new MotorWrapper(hwMap.get(DcMotor.class, "swerve_" + i + "_drive_1")),
					new MotorWrapper(hwMap.get(DcMotor.class, "swerve_" + i + "_drive_2")),
					new MotorWrapper(hwMap.get(DcMotor.class, "swerve_" + i + "_orientation_1")),
					new TouchSensorWrapper(
							hwMap.get(DigitalChannel.class, "swerve_" + i + "_touch")),
					swervePositions[i][0], swervePositions[i][1], swervePositions[i][2]);


		}

		imu = new ImuWrapper(hwMap.get(BNO055IMU.class, "imu"));

	}

	public void setAbsoluteSwerveAngles (CommandQueue queue, Telemetry telemetry) {
		for (int i = 0; i < swerveDrive.length; i++) {
			SynchronousCommandGroup grp = swerveDrive[i].getAbsoluteOrientation();
			grp.addNewCommand(new TelemetryUpdate(
					"Swerve " + i + " has found its absolute position", telemetry));
			queue.add(grp);
		}
	}

	public void driveWithSwerve (double v_x, double v_y, double w) {

		double imu_heading = imu.getHeading();

		double maxSpeedSq = 0;

		Vector3d[] wheelVecs = new Vector3d[4];

		//this populates the wheelVecs array with the speeds that each
		for(int i = 0; i < swerveDrive.length; i++) {
			wheelVecs[i] = swerveDrive[i].calcAngleAndSpeed(v_x, v_y, w, imu_heading);
			double vecSpeedSq = wheelVecs[i].lengthSquared();
			if (vecSpeedSq > maxSpeedSq) {
				maxSpeedSq = vecSpeedSq;
			}
		}

		//see if the maximum speed that a wheel goes at is greater than the maximum possible speed
		//if it is, scale down all of the vectors by that maximum speed, and then scale them up by
		//the maximum possible speed. This makes it so that the largest vector has a magnitude of the
		//largest possible speed, and every other vector is proportionally smaller.
		if (maxSpeedSq > swerveDrive[0].D_SPEED_MAX_SQ) {
			double maxSpeed = Math.sqrt(maxSpeedSq);
			for (Vector3d wheelVec:wheelVecs) {
				wheelVec.div(maxSpeed);
				wheelVec.mul(swerveDrive[0].DRIVE_SPEED_MAX);
			}
		}

		//once the wheel vectors have been finalized, send them back to the swerve to set the motor speeds
		for (int i = 0; i < swerveDrive.length; i++) {
			swerveDrive[i].driveFromVector(wheelVecs[i], imu_heading);
		}

	}

	public void swerveAngleAndSpeed (double angle, double speed) {
		for (SwerveAssembly swerve:swerveDrive) {
			swerve.swerveAngle(angle, speed, imu.getHeading());
		}
	}

	public void addWheelAngles (Telemetry telemetry) {
		for (SwerveAssembly swerve:swerveDrive) {
			telemetry.addLine(swerve.orientationMotor.getCurrentPosition() + "");
			telemetry.addLine(swerve.getWheelAngle(imu.getHeading()) + "");
		}
	}

	public boolean getSwerveTouch(int i) {
		return swerveDrive[i].touchSensor.isPressed();
	}

	public void getIfSwervesReady(Telemetry telemetry) {
		for (SwerveAssembly swerve:swerveDrive) {
			telemetry.addLine(swerve.isZeroed() + "");
		}
	}

	private double toRadians(double deg) {
		return deg * Math.PI/180;
	}

	public double averageWheelAngle () {
		double heading = imu.getHeading();
		double[] angles = new double[swerveDrive.length];
		for (int i = 0; i < angles.length; i++) {
			angles[i] = swerveDrive[i].getWheelAngle(heading);
		}
		return arrAvg(angles);
	}

	public void spin (double speed, double x_off, double y_off) {
		speed = Range.clip(speed, -1, 1);
		Vector3d spinVec = new Vector3d(0, 0, speed);
		Vector3d offVec = new Vector3d(x_off, y_off, 0);
		double heading = imu.getHeading();
		for (SwerveAssembly swerve:swerveDrive) {
			Vector3d temp = new Vector3d();//used to store the rotated, offset, and crossed wheel pos
			swerve.WHEEL_POS.rotateZ(heading, temp);
			temp.sub(offVec);
			spinVec.cross(temp, temp);
			double angle  = getAngle(temp.x, temp.y);
			swerve.swerveAngle(angle, speed, heading);
		}
	}

	static double arrAvg (double[] arr) {
		double sum = 0;
		for (double num:arr) {
			sum += num;
		}
		return sum/arr.length;
	}

	public static double getAngle (double x, double y) {
		double angle = Math.atan2(y, x);
		if (angle < 0) {
			angle += 2 * Math.PI;
//				simply makes the angle from 0 to 2pi instead of -pi to pi
		}
		return angle;
	}

}
