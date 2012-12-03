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

public class NaoIceSkating extends Robot
{
  public static final int STEP_TIME = 64;
  public static final int MILLISECONDS_IN_SECOND = 1000;
  
  public static final double GRAVITY = -9.81;
  
  public static final String DATA_FILE_PATH = "../../../../data/accelerometer.csv";
  
  private Motion motion;
  private Accelerometer accelerometer;
  
  private double timeElapsed;
  
  /**
   * Constructor.
   **/
  public NaoIceSkating()
  {
    super();
    
    this.motion = new Motion( "../../../../data/generations/ForwardsEvolved.motion" );
    
    // Enable accelerometer.
    this.accelerometer = this.getAccelerometer( "Accelerometer" ); 
    this.accelerometer.enable( NaoIceSkating.STEP_TIME );
  }
  public void run()
  {
    // Create file for writing.
    try
    {
      FileWriter fileWriter = new FileWriter( new File( NaoIceSkating.DATA_FILE_PATH ) );
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
                            values[ 0 ],
                            values[ 1 ] - NaoIceSkating.GRAVITY,
                            values[ 2 ] );
        
        timeElapsed += (double)NaoIceSkating.STEP_TIME / NaoIceSkating.MILLISECONDS_IN_SECOND;
      }
      while( this.step( NaoIceSkating.STEP_TIME ) != -1 );
      
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
    NaoIceSkating controller = new NaoIceSkating();
    controller.run();
  }
}
