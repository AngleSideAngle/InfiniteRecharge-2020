package frc.robot.controllers;

import frc.robot.shapes.Point;

public class Waypoint {
    
    public Point point;
    public double heading;

    public Waypoint (Point point, double heading) {
        this.point   = new Point(point.x, point.y);
        this.heading = heading;
    }

    public String toString () {
        return "Point: " + this.point + "\t Heading: " + this.heading;
    }

    public boolean equals (Waypoint waypoint) { 
        return this.point.equals(waypoint.point) && this.heading == waypoint.heading;
    }
}