/* Copyright (c) 2017 FIRST. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted (subject to the limitations in the disclaimer below) provided that
 * the following conditions are met:
 *
 * Redistributions of source code must retain the above copyright notice, this list
 * of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice, this
 * list of conditions and the following disclaimer in the documentation and/or
 * other materials provided with the distribution.
 *
 * Neither the name of FIRST nor the names of its contributors may be used to endorse or
 * promote products derived from this software without specific prior written permission.
 *
 * NO EXPRESS OR IMPLIED LICENSES TO ANY PARTY'S PATENT RIGHTS ARE GRANTED BY THIS
 * LICENSE. THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.robot.Robot;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.RobotCommands.AddCommandToSeparateQueue;
import org.firstinspires.ftc.teamcode.RobotCommands.TelemetryUpdate;
import org.firstinspires.ftc.teamcode.RobotCommands.WaitMillis;
import org.firstinspires.ftc.teamcode.RobotHardware.RobotHardware;
import org.joml.Vector3d;

import java.util.Vector;

@TeleOp(name = "Swerve Drive")
public class SeniorProjectOpmode extends RobotControlSystemOpMode {

	private ElapsedTime runtime = new ElapsedTime();

	@Override
	public void runOpMode() {
		super.runOpMode();

		runtime.reset();

		while (opModeIsActive()) {
			opModeLoop();

//			robot.addWheelAngles(telemetry);

			double throttle = joystick.throttle;
			double v_x = joystick.x_pos;
			double v_y = -joystick.y_pos;
			double w = joystick.twist * throttle;


//			robot.driveWithSwerve(v_x, v_y, w);
			double angle = RobotHardware.getAngle(v_x, v_y);
			telemetry.addLine(Math.toDegrees(angle) + "");

			double pow = Math.hypot(v_x, v_y) * throttle;

			if (!joystick.buttons[0]) {
				pow = 0;
				if (v_x == 0 && v_y == 0) {
					angle = robot.averageWheelAngle();
				}
			}

			if (joystick.buttons[1]) {
				robot.spin(w, 0, 0);
			}else {
				robot.swerveAngleAndSpeed(angle, pow);
			}


			telemetry.update();
			idle();

		}
	}
}
