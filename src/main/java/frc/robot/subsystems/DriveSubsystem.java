package frc.robot.subsystems;

import frc.robot.PortMap;
import frc.robot.Robot;
import frc.robot.Utils;
import frc.robot.RobotState.RS;
import frc.robot.helpers.PID;
import frc.robot.helpers.RobotStateInference;
import frc.robot.logging.Logger.DefaultValue;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.command.Subsystem;

import com.revrobotics.CANSparkMax;

import java.util.Hashtable;
import java.util.Map;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;

public class DriveSubsystem extends Subsystem {
    // Define tested error values here
    double TANK_ANGLE_P = .075, TANK_ANGLE_D = 0.0, TANK_ANGLE_I = 0;
    double GOAL_OMEGA_CONSTANT = 8; // Change this to change angle
    private double MAX_OMEGA_GOAL = 1 * GOAL_OMEGA_CONSTANT;
    public CANSparkMax l, l1, l2, r, r1, r2;
	private final String FILENAME = "DriveSubsystem.java";

    // deadzones by Alejandro at Chris' request. Graph them with the joystick function to understand the math.
    // https://www.desmos.com/calculator/ch19ahiwol
    private static final double INPUT_DEADZONE = 0.11; // deadzone because the joysticks are bad and they detect input when there is none
    private static final double DEFAULT_MAX_JERK = 0.1; // Doesn't allow a motor's output to change by more than this in one tick
    private static final double STRAIGHT_DEADZONE = 0.15;
    private static final double STRAIGHT_EQUAL_INPUT_DEADZONE = 0; // If goal Omega is 0 and our regular input diff magnitude is less than this, the input diff goes to 0
    private PID tankAnglePID;
    public boolean assisted = false;
    public double driveMultiplier = 1;
    public Hashtable<CANSparkMax, RS> sparkToWheelAngleRS;
    public Hashtable<CANSparkMax, RS> sparkToValueRS;
    public Hashtable<CANSparkMax, RS> sparkToVoltageRS;
    public Hashtable<CANSparkMax, RS> sparkToCurrentRS;

    private CANSparkMax newMotorObject(int port){
        return new CANSparkMax(port, MotorType.kBrushless);
    }

    public PID getTankAnglePID()   {return this.tankAnglePID;}
    public double getMaxOmegaGoal(){return MAX_OMEGA_GOAL;}

    public DriveSubsystem(){

		this.l  = newMotorObject(PortMap.LEFT_FRONT_SPARK);
		this.l1 = newMotorObject(PortMap.LEFT_MIDDLE_SPARK);
        this.l2 = newMotorObject(PortMap.LEFT_BACK_SPARK);
        
		this.r  = newMotorObject(PortMap.RIGHT_FRONT_SPARK);
		this.r1 = newMotorObject(PortMap.RIGHT_MIDDLE_SPARK);
        this.r2 = newMotorObject(PortMap.RIGHT_BACK_SPARK);

        this.l .setInverted(true);
        this.l1.setInverted(true);
        this.l2.setInverted(true);

        this.l1.follow(this.l);
        this.l2.follow(this.l);

        this.r1.follow(this.r);
        this.r2.follow(this.r);

        setRSMappings(this.l, RS.LeftWheelAngle,  RS.LeftSparkVal,  RS.LeftSparkVoltage,  RS.LeftSparkCurrent);
        setRSMappings(this.r, RS.RightWheelAngle, RS.RightSparkVal, RS.RightSparkVoltage, RS.RightSparkCurrent);
        
        setRSMappings(this.l1, RS.L1WheelAngle, RS.L1SparkVal, RS.L1SparkVoltage, RS.L1SparkCurrent);
        setRSMappings(this.r1, RS.R1WheelAngle, RS.R1SparkVal, RS.R1SparkVoltage, RS.R1SparkCurrent);
        setRSMappings(this.l2, RS.L2WheelAngle, RS.L2SparkVal, RS.L2SparkVoltage, RS.L2SparkCurrent);
        setRSMappings(this.r2, RS.R2WheelAngle, RS.R2SparkVal, RS.R2SparkVoltage, RS.R2SparkCurrent);

        this.tankAnglePID = new PID(TANK_ANGLE_P, TANK_ANGLE_I, TANK_ANGLE_D);
        Robot.logger.logFinalPIDConstants(FILENAME, "tank angle PID", this.tankAnglePID);
        Robot.logger.logFinalField(FILENAME, "input deadzone", INPUT_DEADZONE);
    }
    
    public void setRSMappings(CANSparkMax spark, RS wheelAngleRS, RS valueRS, RS volatageRS, RS currentRS){
        this.sparkToWheelAngleRS.put(spark, wheelAngleRS);
        this.sparkToValueRS     .put(spark, valueRS);
        this.sparkToVoltageRS   .put(spark, volatageRS);
        this.sparkToCurrentRS   .put(spark, currentRS);
    }
    
	public void periodicLog(){
    }
    public void updateRobotState(){
        for(CANSparkMax spark : getSparks()){updateSparkState(spark);}
    }
    public void updateSparkState(CANSparkMax spark){
        Robot.getCurrentState().set(this.sparkToWheelAngleRS.get(spark), Robot.encoderSubsystem.getSparkAngle(spark));
        Robot.getCurrentState().set(this.sparkToValueRS.get(spark),   spark.get());
        Robot.getCurrentState().set(this.sparkToVoltageRS.get(spark), spark.getBusVoltage());
        Robot.getCurrentState().set(this.sparkToCurrentRS.get(spark), spark.getOutputCurrent());
    }

	public CANSparkMax[] getSparks() {
        return new CANSparkMax[]{this.l, this.l1, this.l2, this.r, this.r1, this.r2};
    }

    public double deadzone(double output){
        if (Math.abs(output) < INPUT_DEADZONE){
            return 0;
        } else {
            return output;
        }
    }
    
    public double processStick(Joystick stick){
        return deadzone(-stick.getY());
    }

    public void assistedDriveMode(){this.assisted = true;}
    public void manualDriveMode()  {this.assisted = false;}

    public void setSpeed(Joystick leftStick, Joystick rightStick) {
        if (!this.assisted) {
            double leftInput  = processStick(leftStick);
            double rightInput = processStick(rightStick);
            setSpeedTankAngularControl(leftInput, rightInput);
        }
    }
	
	public void setSpeedRaw(Joystick leftStick, Joystick rightStick){
		setSpeedTank(processStick(leftStick),processStick(rightStick));
    }

    public double limitJerk(double oldSpeed, double newSpeed, double maxJerk){
        // Makes sure that the change in input for a motor is not more than maxJerk
        if (oldSpeed - newSpeed > maxJerk){
            return oldSpeed - maxJerk;
        } else if (newSpeed - oldSpeed > maxJerk){
            return oldSpeed + maxJerk;
        } else {
            return newSpeed;
        }
    }

    public void setMotorSpeed(CANSparkMax motor, double speed){setMotorSpeed(motor, speed, DEFAULT_MAX_JERK);}
    public void setMotorSpeed(TalonSRX motor, double speed)   {setMotorSpeed(motor, speed, DEFAULT_MAX_JERK);}

    public void setMotorSpeed(CANSparkMax motor, double speed, double maxJerk){
        speed = limitJerk(motor.get(), speed, maxJerk);
        //System.out.println("setting spark " + motor.getDeviceId() + " to " + speed);
        motor.set(speed);
    }
    public void setMotorSpeed(TalonSRX motor, double speed, double maxJerk){
        speed = limitJerk(motor.getMotorOutputPercent(), speed, maxJerk);
        //System.out.println("setting talon to " + speed);
        motor.set(ControlMode.PercentOutput, speed);
    }

    public void defaultDriveMultilpier(){this.driveMultiplier = 1;}
    public void setDriveMultiplier(double driveMultiplier){
        this.driveMultiplier = driveMultiplier;
    }
        	
	public void setSpeedTank(double leftSpeed, double rightSpeed) {
        setMotorSpeed(this.l, leftSpeed  * this.driveMultiplier);
        setMotorSpeed(this.r, rightSpeed * this.driveMultiplier);
    }
	
	public void setSpeedTankAngularControl(double leftSpeed, double rightSpeed) {
		double averageOutput = (leftSpeed + rightSpeed) / 2;
        double goalOmega = GOAL_OMEGA_CONSTANT * (rightSpeed - leftSpeed);
        // Makes it so if the two joysticks are close enough, it will try to go straight
        if (Math.abs(goalOmega) < STRAIGHT_DEADZONE){
            goalOmega = 0;
        } else {
            goalOmega -= Utils.signOf(goalOmega) * STRAIGHT_DEADZONE;
        }
        goalOmega = Utils.limitOutput(goalOmega, MAX_OMEGA_GOAL);
        double error = goalOmega - RobotStateInference.getAngularVelocity();
        tankAnglePID.addMeasurement(error);
        double inputDiff = tankAnglePID.getOutput();
        // If you are going almost straight and goalOmega is 0, it will simply give the same input to both wheels
        // We should test if this is beneficial
        if (goalOmega == 0 && (Math.abs(inputDiff) < STRAIGHT_EQUAL_INPUT_DEADZONE)){
            inputDiff = 0;
        }
        Robot.logger.addData(FILENAME, "input diff", inputDiff, DefaultValue.Empty);
        Robot.logger.addData(FILENAME, "error", error, DefaultValue.Empty);
		setSpeedTank(averageOutput - inputDiff, averageOutput + inputDiff); 
	}
	
	public void setSpeedTankForwardTurningPercentage(double forward, double turnMagnitude) {
        // Note: this controls dtheta/dx rather than dtheta/dt
		setSpeedTank(forward * (1 + turnMagnitude), forward * (1 - turnMagnitude));
    }
    
    public void setSpeedTankTurningPercentage(double turnMagnitude){
        double forward = (processStick(Robot.oi.leftStick) + processStick(Robot.oi.rightStick)) / 2;
        setSpeedTankForwardTurningPercentage(forward, turnMagnitude);
	}

    @Override
    protected void initDefaultCommand() {
		//IGNORE THIS METHOD
    }
}
