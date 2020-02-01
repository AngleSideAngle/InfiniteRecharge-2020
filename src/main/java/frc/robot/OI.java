package frc.robot;

import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.buttons.JoystickButton;
import frc.robot.sciSensorsActuators.SciJoystick;

// FILE HAS NOT BEEN CLEANED UP //
public class OI {
    public SciJoystick leftStick, rightStick;

    public JoystickButton increment, decrement;

    public XboxController xboxController;
    
    public OI() {
        this.leftStick = new SciJoystick(PortMap.JOYSTICK_LEFT);
        this.rightStick = new SciJoystick(PortMap.JOYSTICK_RIGHT);
        this.xboxController = new XboxController(PortMap.XBOX_CONTROLLER);
    }
}

