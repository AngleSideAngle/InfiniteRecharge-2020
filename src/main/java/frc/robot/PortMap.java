package frc.robot;

public class PortMap {

    //*****************JOYSTICKS*****************//

    public static final int JOYSTICK_LEFT = 0;
    public static final int JOYSTICK_RIGHT = 1;

    public static final int JOYSTICK_TRIGGER = 1;
    public static final int JOYSTICK_CENTER_BUTTON = 2;
    public static final int JOYSTICK_LEFT_BUTTON = 3;
    public static final int JOYSTICK_RIGHT_BUTTON = 4;

    public static final int[][] JOYSTICK_BUTTON_MATRIX_LEFT = { { 5, 6, 7 }, { 10, 9, 8 } };
    public static final int[][] JOYSTICK_BUTTON_MATRIX_RIGHT = { { 13, 12, 11 }, { 14, 15, 16 } };

    // *****************XBOX*****************//
  
    public static final int XBOX_CONTROLLER = 2;
  
    public static final int XBOX_A = 1;
    public static final int XBOX_B = 2;
    public static final int XBOX_X = 3;
    public static final int XBOX_Y = 4;
  
    public static final int XBOX_BUMPER_LEFT = 5;
    public static final int XBOX_BUMPER_RIGHT = 6;
  
    public static final int XBOX_BACK = 7;
    public static final int XBOX_START = 8;
  
    public static final int XBOX_STICK_LEFT_BUTTON = 9;
    public static final int XBOX_STICK_RIGHT_BUTTON = 10;
  
    public static final int XBOX_TRIGGER_LEFT = 2;
    public static final int XBOX_TRIGGER_RIGHT = 3;
  
    public static final int XBOX_LEFT_JOY_X = 0;
    public static final int XBOX_LEFT_JOY_Y = 1;
  
    public static final int XBOX_RIGHT_JOY_X = 4;
    public static final int XBOX_RIGHT_JOY_Y = 5;
    
    //*******************SPARKS******************//

    public static final int SHOOTER_MOTOR_TOP = 9;
    public static final int SHOOTER_MOTOR_BOTTOM = 8;
    public static final int SHOOTER_ROTATE_SPARK = 7;
  
    public static final int LEFT_FRONT_SPARK = 6; 
    public static final int LEFT_MIDDLE_SPARK = 5; 
    public static final int LEFT_BACK_SPARK = 4;

    public static final int RIGHT_FRONT_SPARK = 3;
    public static final int RIGHT_MIDDLE_SPARK = 2;
    public static final int RIGHT_BACK_SPARK = 69;

    public static final int HOOD_SPARK = 16;
    public static final int SHOOTER_SPARK = 33;

    //*******************TALONS******************//

    public static final int PIGEON_TALON = 9;
    public static final int INTAKE_TALON = 12; // Temporary
    
    //******************SOLENOIDS****************//

    public static final int INTAKE_SOLENOID_FORWARD = 1; // Temporary
    public static final int INTAKE_SOLENOID_REVERSE = 2; // Temporary

    public static final int TILT_PIGEON = 11;      // CHANGE TO ACTUAL INPUT
    public static final int SHIFTING_TALON = 10;   // CHANGE TO ACTUAL INPUT
    public static final int LIFT_LEFT_TALON = 12;  // CHANGE TO ACTUAL INPUT
    public static final int LIFT_RIGHT_TALON = 13; // CHANGE TO ACTUAL INPUT

    //*******************MISC********************//
    public static final int PRESSURE_SENSOR = 0;
    public static final int TARGETING_LIGHT_DIGITAL_OUTPUT = 5;
    public static final int THROUGH_BORE_ENCODER = 0;
}
