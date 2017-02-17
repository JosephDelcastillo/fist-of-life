package org.teamresistance.frc;

import edu.wpi.first.wpilibj.RobotDrive;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import org.strongback.Strongback;
import org.strongback.SwitchReactor;
import org.strongback.components.ui.ContinuousRange;
import org.strongback.components.ui.FlightStick;
import org.strongback.drive.MecanumDrive;
import org.strongback.hardware.Hardware;
import org.teamresistance.frc.subsystem.drive.Drive;
import org.teamresistance.frc.subsystem.snorfler.Snorfler;
import org.teamresistance.frc.util.testing.DriveTesting;
import org.teamresistance.frc.util.testing.SnorflerTesting;

import edu.wpi.first.wpilibj.IterativeRobot;

/**
 * Main robot class. Override methods from {@link IterativeRobot} to define behavior.
 *
 * @author Shreya Ravi
 * @author Rothanak So
 * @author Tarik C. Brown
 */
public class Robot extends IterativeRobot {
  private final FlightStick leftJoystick = Hardware.HumanInterfaceDevices.logitechAttack3D(0);
  private final FlightStick rightJoystick = Hardware.HumanInterfaceDevices.logitechAttack3D(1);
  private final FlightStick coJoystick = Hardware.HumanInterfaceDevices.logitechAttack3D(2);

  private final Drive drive = new Drive(
      new RobotDrive(IO.lfMotor, IO.lrMotor, IO.rfMotor, IO.rrMotor),
      IO.navX,
      () -> spy(leftJoystick.getRoll(), "Left X").read(),
      () -> spy(leftJoystick.getPitch(), "Left Y").read(),
      () -> spy(rightJoystick.getRoll(), "Right X").read());

  private final Snorfler snorfler = new Snorfler(IO.snorflerMotor);

  private static ContinuousRange spy(ContinuousRange range, String key) {
    SmartDashboard.putNumber(key, range.read());
    return range;
  }

  @Override
  public void robotInit() {
    Strongback.configure().recordNoEvents().recordNoData();
    DriveTesting driveTesting = new DriveTesting(drive, IO.navX, leftJoystick, rightJoystick);
    SnorflerTesting snorflerTesting = new SnorflerTesting(snorfler, leftJoystick, rightJoystick);

    driveTesting.enableAngleHold();
    driveTesting.enableAngleHoldTests();
    driveTesting.enableCancelling();
    driveTesting.enableNavXReset();

//    SwitchReactor reactor = Strongback.switchReactor();
//
//    reactor.whileTriggered(rightJoystick.getButton(6), () -> IO.lfMotor.setSpeed(0.2));
//    reactor.whileUntriggered(rightJoystick.getButton(6), () -> IO.lfMotor.setSpeed(0.0));
//
//    reactor.whileTriggered(rightJoystick.getButton(7), () -> IO.lrMotor.setSpeed(0.2));
//    reactor.whileUntriggered(rightJoystick.getButton(7), () -> IO.lrMotor.setSpeed(0.0));
//
//    reactor.whileTriggered(rightJoystick.getButton(11), () -> IO.rfMotor.setSpeed(0.2));
//    reactor.whileUntriggered(rightJoystick.getButton(11), () -> IO.rfMotor.setSpeed(0.0));
//
//    reactor.whileTriggered(rightJoystick.getButton(10), () -> IO.rrMotor.setSpeed(0.2));
//    reactor.whileUntriggered(rightJoystick.getButton(10), () -> IO.rrMotor.setSpeed(0.0));
  }

  @Override
  public void autonomousInit() {
    Strongback.start();
  }

  @Override
  public void teleopInit() {
    Strongback.start();
  }

  @Override
  public void teleopPeriodic() {
    double orientation = IO.navX.getAngle();
    SmartDashboard.putNumber("Gyro", orientation);

    Feedback feedback = new Feedback(orientation);
    drive.onUpdate(feedback);
  }

  @Override
  public void disabledInit() {
    Strongback.disable();
  }
}
