package frc.robot.helpers;

import frc.robot.Robot;

public class CircleController { 

    private static final double FINAL_HEADING_P = 0;
    private static final double DESIRED_HEADING_P = 0;
    private PID finalHeadingPID   = new PID(FINAL_HEADING_P, 0, 0);
    private PID desiredHeadingPID = new PID(DESIRED_HEADING_P, 0, 0);

    public CircleController () {}

    public void update (Point currPos, double currHeading, Point finalPos, double finalHeading) {
        Circle currCircle  = Circle.makeCircleWithTangentAnd2Points(currPos, currHeading, finalPos);
        Line finalHeadingLine = Geo.pointAngleForm(finalPos, finalHeading);
        double angleOfFinalHeadingLine = Geo.thetaOf(finalHeadingLine);
        Line tangentOnCircleToFinalPos = Geo.getTangentToCircle(currCircle, finalPos);
        double angleOfTangentOnCircleToFinalPos = Geo.thetaOf(tangentOnCircleToFinalPos);

        double finalHeadingError = angleOfFinalHeadingLine - angleOfTangentOnCircleToFinalPos;
        finalHeadingPID.addMeasurement(finalHeadingError);

        double desiredHeadingError = finalHeading - currHeading;
        desiredHeadingPID.addMeasurement(desiredHeadingError);

        double turnMagnitude = desiredHeadingPID.getOutput() + finalHeadingPID.getOutput();  
        Robot.driveSubsystem.setSpeedTankTurningPercentage(turnMagnitude);
    }
}