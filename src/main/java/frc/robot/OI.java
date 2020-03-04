package frc.robot;

import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.buttons.JoystickButton;
import frc.robot.commands.auto.*;
import frc.robot.commands.intake.*;
import frc.robot.commands.drive.*;
import frc.robot.sciSensorsActuators.SciJoystick;

// FILE HAS NOT BEEN CLEANED UP //
public class OI {
    public SciJoystick leftStick, rightStick;
    public XboxController xboxController;

    public JoystickButton circleControllerButton, pointChangerButton, intakeButton;
    
    public OI() {
        this.leftStick  = new SciJoystick(PortMap.JOYSTICK_LEFT);
        this.rightStick = new SciJoystick(PortMap.JOYSTICK_RIGHT);
        this.xboxController = new XboxController(PortMap.XBOX_CONTROLLER);

        this.intakeButton = new JoystickButton(this.rightStick, PortMap.JOYSTICK_CENTER_BUTTON);
        this.intakeButton.whenPressed(new IntakeSuckCommand());

    }
}

