package frc.robot.subsystems;

import frc.robot.PortMap;
import frc.robot.Robot;
import frc.robot.controllers.PID;
import frc.robot.robotState.RobotState.SD;
import frc.robot.sciSensorsActuators.*;
import edu.wpi.first.wpilibj.command.Subsystem;


public class ClimberSubsystem extends Subsystem {
    private SciSpark liftRight;
    private SciSpark liftLeft;
    private SciSpark shiftMotor;
    private final double CASCADE_GEAR_RATIO = 1; // CHANGE
    private final double CASCADE_WHEEL_RADIUS = 3; //CHANGE
    private final double MAXIMUM_TILT_ANGLE = Math.toRadians(14.5);
    private PID tiltPID;
    private double P = 1/MAXIMUM_TILT_ANGLE;
    public ClimberSubsystem() {
        this.liftRight  = new SciSpark(PortMap.LIFT_RIGHT_TALON, CASCADE_GEAR_RATIO);
        this.liftLeft   = new SciSpark(PortMap.LIFT_LEFT_TALON,  CASCADE_GEAR_RATIO);
        this.shiftMotor = new SciSpark(PortMap.SHIFTING_TALON);
        Robot.addSDToLog(SD.ClimberHeight);
        tiltPID = new PID(P, 0, 0);

    }

    public void setShiftMotorSpeed(double speed) {this.shiftMotor.set(speed);}

    public void setLiftSpeed(double speed){ 
        this.liftRight.set(speed);
        this.liftLeft.set(speed);
    }

    public double getCascadeHeight(){
        return Robot.get(SD.ClimberTalonAngle) * CASCADE_WHEEL_RADIUS;
    }

    public void moveToBalance(){
        this.shiftMotor.set(tiltPID.getOutput());
    }

    public void updateRobotState(){
        Robot.set(SD.ClimberTalonAngle,  this.liftLeft.getWheelAngle());
        Robot.set(SD.ShiftTalonAngle, this.shiftMotor.getWheelAngle());
        Robot.set(SD.ClimberHeight, getCascadeHeight());
    }
    @Override
    protected void initDefaultCommand() {
		//IGNORE THIS METHOD
    }
}