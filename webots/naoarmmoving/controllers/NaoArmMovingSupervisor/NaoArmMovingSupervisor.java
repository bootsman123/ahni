// File:          NaoArmMovingSupervisor.java
// Date:          
// Description:   
// Author:        
// Modifications: 

import com.cyberbotics.webots.controller.Supervisor;
import com.cyberbotics.webots.controller.Node;
import com.cyberbotics.webots.controller.Field;

import java.io.*;
import java.lang.*;
import java.util.Locale;

public class NaoArmMovingSupervisor extends Supervisor
{
  public static final int STEP_TIME = 64;
  public static final int MILLISECONDS_IN_SECOND = 1000;
  public static final double QUIT_TIME = 2.0;
  
  // Displaying debug info.
  public static final boolean DEBUG = true;
  public static final int DEBUG_UID = 0;
  public static final double DEBUG_X = 0.02;
  public static final double DEBUG_Y = 0.02;
  public static final double DEBUG_FONT_SIZE = 0.12;
  public static final int DEBUG_FONT_COLOR = 0x000000;
  public static final double DEBUG_FONT_TRANSPARANCY = 0.0;
  
  public static final String DATA_FILE_PATH = "../../../../data/armmovingheight.csv";

  private double timeElapsed;
  
  
  /**
   * Constructor.
   **/
  public NaoArmMovingSupervisor()
  {
    super();
  }
  public void run()
  {
    try
    {
      // Create file for writing.
      FileWriter fileWriter = new FileWriter( new File( NaoArmMovingSupervisor.DATA_FILE_PATH ) );
      PrintWriter printWriter = new PrintWriter( fileWriter );
  
  
      this.timeElapsed = 0.0;
      
      Node RWristYaw = this.getFromDef( "RWristYaw" );
      double[] originalPosition = RWristYaw.getPosition();
    
      // Main loop.
      do
      {
        double position[] = RWristYaw.getPosition();
        double distance = position[ 1 ] - originalPosition[ 1 ];
        
        /*
        // Create debug message.
        String message = String.format( "RWristYaw: %.03f\t%.03f\t%.03f\nDistance: %.03f",
                                        position[ 0 ],
                                        position[ 1 ],
                                        position[ 2 ],
                                        distance );
        this.showDebugLabel( message );
        */
        
        printWriter.printf( Locale.US,
                            "%f;%f\n",
                            timeElapsed,
                            distance );
      
        if( this.timeElapsed > NaoArmMovingSupervisor.QUIT_TIME )
        {
          // Revert simulation.
          this.simulationQuit( 1 );
        }
        
        // Update time.
        this.timeElapsed += (double)NaoArmMovingSupervisor.STEP_TIME / NaoArmMovingSupervisor.MILLISECONDS_IN_SECOND;
      }
      while( this.step( NaoArmMovingSupervisor.STEP_TIME ) != -1 );
      
      // Close file.
      fileWriter.close();
      printWriter.close();
    }
    catch( Exception e )
    {
    }
  }
  
  /**
   * Shows a debug label.
   * @param message
   */
  private void showDebugLabel( String message )
  {
      this.setLabel( NaoArmMovingSupervisor.DEBUG_UID,
                    message,
                    NaoArmMovingSupervisor.DEBUG_X,
                    NaoArmMovingSupervisor.DEBUG_Y,
                    NaoArmMovingSupervisor.DEBUG_FONT_SIZE,
                    NaoArmMovingSupervisor.DEBUG_FONT_COLOR,
                    NaoArmMovingSupervisor.DEBUG_FONT_TRANSPARANCY );
  }

  /**
   * Main function.
   * @param args
   */
  public static void main( String[] args )
  {
    NaoArmMovingSupervisor controller = new NaoArmMovingSupervisor();
    controller.run();
  }
}
