// File:          NaoArmMovingSupervisor.java
// Date:          
// Description:   
// Author:        
// Modifications: 

import com.cyberbotics.webots.controller.Supervisor;
import com.cyberbotics.webots.controller.Node;
import com.cyberbotics.webots.controller.Field;
import com.cyberbotics.webots.controller.Accelerometer;

public class NaoArmMovingSupervisor extends Supervisor
{
  public static final int STEP_TIME = 64;
  public static final int MILLISECONDS_IN_SECOND = 1000;
  public static final double QUIT_TIME = 2.0;
  
  public static final String ROBOT_NAME = "Nao_Robot";
  private Node robot;
  private Field robotTranslation;
  
  private double[] positionInitial;
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
    // Initialize position.
    this.timeElapsed = 0.0;
  
    // Main loop.
    do
    {
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
   * Main function.
   * @param args
   */
  public static void main( String[] args )
  {
    NaoArmMovingSupervisor controller = new NaoArmMovingSupervisor();
    controller.run();
  }
}
