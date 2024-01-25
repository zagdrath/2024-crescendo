/*
 * Copyright (C) 2024, FRC Team 3602. All rights reserved. This work
 * is licensed under the terms of the MIT license which can be found
 * in the root directory of this project.
 */

package frc.team3602.robot;

import edu.wpi.first.wpilibj.PowerDistribution;
import edu.wpi.first.wpilibj.PowerDistribution.ModuleType;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Commands;
import edu.wpi.first.wpilibj2.command.button.CommandXboxController;

import frc.team3602.robot.subsystems.IntakeSubsystem;
import frc.team3602.robot.subsystems.ShooterSubsystem;
import static frc.team3602.robot.Constants.OperatorInterfaceConstants.*;

public class RobotContainer {
  // Subsystems
  private final ShooterSubsystem shooterSubsys = new ShooterSubsystem();
  public final IntakeSubsystem intakeSubsys = new IntakeSubsystem();

  // Operator interfaces
  private final CommandXboxController xboxController = new CommandXboxController(kXboxControllerPort);
  private double driveSpeed = 1.0;

  // Autonomous
  private final SendableChooser<Command> sendableChooser = new SendableChooser<>();

  public final PowerDistribution powerDistribution = new PowerDistribution(1, ModuleType.kRev);

  public RobotContainer() {
    configDefaultCommands();
    configButtonBindings();
    configAutonomous();
  }

  private void configDefaultCommands() {
  }

  private void configButtonBindings() {
    // While holding b button, run the intake at 75% duty cycle
    xboxController.b().whileTrue(intakeSubsys.runIntake(() -> 0.75))
        .whileFalse(intakeSubsys.run(() -> intakeSubsys.stopIntake()));

    // While holding x button, run the intake at 15% duty cycle
    xboxController.x().whileTrue(intakeSubsys.runIntake(() -> 0.15))
        .whileFalse(intakeSubsys.run(() -> intakeSubsys.stopIntake()));

    // While holding b button, reverse the intake at 15% duty cycle
    xboxController.a().whileTrue(intakeSubsys.runIntake(() -> -0.15))
        .whileFalse(intakeSubsys.run(() -> intakeSubsys.stopIntake()));

    // While holding right bumper, run the shooter at 75% duty cycle
    xboxController.rightBumper().whileTrue(shooterSubsys.runShooter(() -> -0.75))
        .whileFalse(shooterSubsys.run(() -> shooterSubsys.stopShooter()));

    // While holding left bumper, run the shooter at 15% duty cycle
    xboxController.leftBumper().whileTrue(shooterSubsys.runShooter(() -> -0.15))
        .whileFalse(shooterSubsys.run(() -> shooterSubsys.stopShooter()));

    // While holding d-pad up, reverse the shooter at 15% duty cycle
    xboxController.pov(0).whileTrue(shooterSubsys.runShooter(() -> 0.15))
        .whileFalse(shooterSubsys.run(() -> shooterSubsys.stopShooter()));
  }

  private void configAutonomous() {
    SmartDashboard.putData(sendableChooser);
  }

  public Command getAutonomousCommand() {
    return sendableChooser.getSelected();
  }
}
