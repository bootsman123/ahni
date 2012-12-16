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
      
  private Motion motion;
  
  private double timeElapsed;
  
  /**
   * Constructor.
   **/
  public NaoArmMoving()
  {
    super();
    
    this.motion = new Motion( "../../../../data/generations/ArmMoving.motion" );
  }
  public void run()
  {
    // Create file for writing.
    try
    {
      // Main loop.
      do
      {
        //this.motion.play();
        
        timeElapsed += (double)NaoArmMoving.STEP_TIME / NaoArmMoving.MILLISECONDS_IN_SECOND;
      }
      while( this.step( NaoArmMoving.STEP_TIME ) != -1 );
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
