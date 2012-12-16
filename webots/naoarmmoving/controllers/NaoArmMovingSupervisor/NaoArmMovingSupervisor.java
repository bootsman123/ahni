// File:          NaoArmMovingSupervisor.java
// Date:          
// Description:   
// Author:        
// Modifications: 

import com.cyberbotics.webots.controller.Supervisor;
import com.cyberbotics.webots.controller.Node;
import com.cyberbotics.webots.controller.Field;

public class NaoArmMovingSupervisor extends Supervisor
{
  public static final int STEP_TIME = 64;
  public static final int MILLISECONDS_IN_SECOND = 1000;
  public static final double QUIT_TIME = 3000.0;
  
  // Displaying debug info.
  public static final boolean DEBUG = true;
  public static final int DEBUG_UID = 0;
  public static final double DEBUG_X = 0.02;
  public static final double DEBUG_Y = 0.02;
  public static final double DEBUG_FONT_SIZE = 0.12;
  public static final int DEBUG_FONT_COLOR = 0x000000;
  public static final double DEBUG_FONT_TRANSPARANCY = 0.0;

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
    this.timeElapsed = 0.0;
    
    Node RWristYaw = this.getFromDef( "RWristYaw" );
    double[] originalPosition = RWristYaw.getPosition();
  
    // Main loop.
    do
    {
      double position[] = RWristYaw.getPosition();
      
      // Create debug message.
      String message = String.format( "RWristYaw: %.03f\t%.03f\t%.03f\nDistance: %.03f",
                                      position[ 0 ],
                                      position[ 1 ],
                                      position[ 2 ],
                                      position[ 1 ] - originalPosition[ 1 ] );
      this.showDebugLabel( message );
    
      if( this.timeElapsed > NaoArmMovingSupervisor.QUIT_TIME )
      {
        // Revert simulation.
        this.simulationQuit( 1 );
      }
      
      // Update time.
      this.timeElapsed += (double)NaoArmMovingSupervisor.STEP_TIME / NaoArmMovingSupervisor.MILLISECONDS_IN_SECOND;
    }
    while( this.step( NaoArmMovingSupervisor.STEP_TIME ) != -1 );
    
    // Enter here exit cleanup code
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
