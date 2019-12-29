package frc.robot.stateEstimation;

import frc.robot.Robot;

import java.util.ArrayList;
import java.util.Hashtable;

import com.ctre.phoenix.motorcontrol.can.TalonSRX;

import frc.robot.PortMap;
import frc.robot.robotState.*;
import frc.robot.robotState.RobotState.SD;
import frc.robot.sciSensorsActuators.SciPigeon;
import frc.robot.subsystems.DriveSubsystem;
import frc.robot.logging.Logger.DefaultValue;

public class EncoderLocalization implements Updater, Model {

    public static final double ORIGINAL_ANGLE = Math.PI/2, ORIGINAL_X = 0, ORIGINAL_Y = 0;
    public static final double INTERVAL_LENGTH = .02; // Seconds between each tick for commands
    private final String FILENAME = "RobotPosition.java";
    private static final double X_STD_DEV     = 0; // These are meant to be estimates
    private static final double Y_STD_DEV     = 0;
    private static final double ANGLE_STD_DEV = 0;
    private Hashtable<SD, Double> stdDevs;

    private SciPigeon pigeon;
    private TalonSRX pigeonTalon;

    private class WheelChangeInfo{
        public double rotationChange, angle;
        public WheelChangeInfo(double rotationChange, double angle){
            this.rotationChange = rotationChange;
            this.angle = angle;
        }
    }

    public EncoderLocalization(){
        this.pigeonTalon = new TalonSRX(PortMap.PIGEON_TALON);
        this.pigeon      = new SciPigeon(pigeonTalon);

        this.stdDevs = new Hashtable<>();
        this.stdDevs.put(SD.X,     X_STD_DEV);
        this.stdDevs.put(SD.Y,     Y_STD_DEV);
        this.stdDevs.put(SD.Angle, ANGLE_STD_DEV);
        this.stdDevs.put(SD.GearShiftSolenoid, 0.0);
        this.stdDevs.put(SD.LeftWheelAngle, 0.0);
        this.stdDevs.put(SD.RightWheelAngle, 0.0);
    }

    public SciPigeon getPigeon() {return this.pigeon;}

    @Override
    public Hashtable<SD, Double> getStdDevs(){return this.stdDevs;}
    
    public double wheelRotationChange(SD wheelAngleSD, RobotStateHistory stateHistory){
        return StateInfo.getDifference(stateHistory, wheelAngleSD, 1) * DriveSubsystem.WHEEL_RADIUS;
    }

    public WheelChangeInfo newWheelChangeInfo(double rotationChange, double angle){
        return new WheelChangeInfo(rotationChange, angle);
    }

    public RobotState nextPosition(double x, double y, double theta, ArrayList<Double> wheelDistances, double deltaTheta){
        // Works for all forms of drive where the displacement is the average of the movement vectors over the wheels
        double newTheta = theta + deltaTheta;
        RobotState robotState = new RobotState();
        
        double avgTheta = (theta + newTheta)/2;
        for(double wheelDistance : wheelDistances){
            if (wheelDistance != 0){
                ////system.out.println("x change: " + wheelChangeInfo.rotationChange * Math.cos(avgTheta + wheelChangeInfo.angle) / wheelAmount);
            }
            x += wheelDistance * Math.cos(avgTheta) / wheelDistances.size();
            y += wheelDistance * Math.sin(avgTheta) / wheelDistances.size();
        }
        robotState.set(SD.X, x);
        robotState.set(SD.Y, y);
        robotState.set(SD.Angle, newTheta);
        robotState.set(SD.GearShiftSolenoid, 0.0);
        return robotState;
    }

    @Override
    public void updateState(RobotStateHistory stateHistory){
        RobotState state = stateHistory.statesAgo(1);
        // Robot.delayedPrint("left wheel angle: " + Robot.get(SD.LeftWheelAngle));
        // Robot.delayedPrint("" + wheelRotationChange(SD.LeftWheelAngle, pastStates));
        if (Robot.get(SD.X) != 0){
            //system.out.println("robot: " + Robot.get(SD.X));
            //system.out.println("state: " + state.get(SD.X));
        }
        ArrayList<Double> wheelChanges = new ArrayList<>();
        wheelChanges.add(wheelRotationChange(SD.LeftWheelAngle,  stateHistory));
        wheelChanges.add(wheelRotationChange(SD.RightWheelAngle, stateHistory));
        double thetaChange = StateInfo.getDifference(stateHistory, SD.PigeonAngle, 1);
        RobotState newPosition = 
            nextPosition(state.get(SD.X), state.get(SD.Y), state.get(SD.Angle), wheelChanges, thetaChange);
        stateHistory.currentState().incorporateOtherState(newPosition); 
    }

    @Override
    public void updateRobotState(){
        updateState(Robot.stateHistory);
    }

    @Override
    public Iterable<SD> getSDs(){return this.stdDevs.keySet();}
    
	public void periodicLog(){
        Robot.logger.addData(FILENAME, "robot X",     Robot.get(SD.X),     DefaultValue.Previous);
        Robot.logger.addData(FILENAME, "robot y",     Robot.get(SD.Y),     DefaultValue.Previous);
        Robot.logger.addData(FILENAME, "robot angle", Robot.get(SD.Angle), DefaultValue.Previous);
	}

    public void printPosition(){
        //system.out.println("X: " + Robot.get(SD.X));
        //system.out.println("Y: " + Robot.get(SD.Y));
        //system.out.println("Angle: " + Math.toDegrees(Robot.get(SD.Angle)));
    }
}