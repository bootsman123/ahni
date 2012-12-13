// File:          NaoIceSkating.java
// Date:          
// Description:   
// Author:        
// Modifications: 

import com.cyberbotics.webots.controller.Robot;
import com.cyberbotics.webots.controller.Motion;
import com.cyberbotics.webots.controller.Accelerometer;

import java.io.*;
import java.lang.*;
import java.util.Locale;

public class NaoArmMoving extends Robot
{
  public static final int STEP_TIME = 64;
  public static final int MILLISECONDS_IN_SECOND = 1000;
    
  public static final String DATA_FILE_PATH = "../../../../data/accelerometer.csv";
  
  private Motion motion;
  private Accelerometer accelerometer;
  
  private double timeElapsed;
  
  /**
   * Constructor.
   **/
  public NaoArmMoving()
  {
    super();
    
    this.motion = new Motion( "../../../../data/generations/ForwardsEvolved.motion" );
    
    // Enable accelerometer.
    this.accelerometer = this.getAccelerometer( "Accelerometer" ); 
    this.accelerometer.enable( NaoArmMoving.STEP_TIME );
  }
  public void run()
  {
    // Create file for writing.
    try
    {
      FileWriter fileWriter = new FileWriter( new File( NaoArmMoving.DATA_FILE_PATH ) );
      PrintWriter printWriter = new PrintWriter( fileWriter );
      
      // Write header.
      //printWriter.printf( "time;x;y;z\n" );
      
      // Main loop.
      do
      {
        this.motion.play();
        
        // Write accelerometer values.
        double[] values = this.accelerometer.getValues();
        
        printWriter.printf( Locale.US,
                            "%f;%f;%f;%f\n",
                            timeElapsed,
                            XXX );
        
        timeElapsed += (double)NaoArmMoving.STEP_TIME / NaoArmMoving.MILLISECONDS_IN_SECOND;
      }
      while( this.step( NaoArmMoving.STEP_TIME ) != -1 );
      
      // Close writer.
      printWriter.close();
      fileWriter.close();
    }
    catch( Exception e )
    {
    }
  }

  /**
   * Main function.
   * @param args
   */
  public static void main( String[] args )
  {
    NaoArmMoving controller = new NaoArmMoving();
    controller.run();
  }
}
