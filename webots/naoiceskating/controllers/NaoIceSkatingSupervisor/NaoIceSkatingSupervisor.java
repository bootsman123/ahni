// File:          NaoIceSkatingSupervisor.java
// Date:          
// Description:   
// Author:        
// Modifications: 

import com.cyberbotics.webots.controller.Supervisor;
import com.cyberbotics.webots.controller.Node;
import com.cyberbotics.webots.controller.Field;
import com.cyberbotics.webots.controller.Accelerometer;

public class NaoIceSkatingSupervisor extends Supervisor
{
  public static final int STEP_TIME = 64;
  public static final int MILLISECONDS_IN_SECOND = 1000;
  public static final double QUIT_TIME = 10.0;
  
  // Displaying debug info.
  public static final boolean DEBUG = true;
  public static final int DEBUG_UID = 0;
  public static final double DEBUG_X = 0.02;
  public static final double DEBUG_Y = 0.02;
  public static final double DEBUG_FONT_SIZE = 0.12;
  public static final int DEBUG_FONT_COLOR = 0x000000;
  public static final double DEBUG_FONT_TRANSPARANCY = 0.0;
  
  public static final String ROBOT_NAME = "Nao_Robot";
  private Node robot;
  private Field robotTranslation;
  
  private double[] positionInitial;
  private double timeElapsed;  
  
  /**
   * Constructor.
   **/
  public NaoIceSkatingSupervisor()
  {
    super();
  }
  public void run()
  {
    // Get Nao robot.
    this.robot = this.getFromDef( NaoIceSkatingSupervisor.ROBOT_NAME );
    this.robotTranslation = this.robot.getField( "translation" );

    // Initialize position.
    this.positionInitial = this.robotTranslation.getSFVec3f();
    this.timeElapsed = 0.0;
  
    // Main loop.
    do
    {
      // Get distance
      double totalDistance = this.totalDistance( this.positionInitial, this.robotTranslation.getSFVec3f() );
    
      // Create debug message.
      String message = String.format( "Total distance: %.03f\nTime elapsed: %.02f",
                                      totalDistance,
                                      this.timeElapsed );
      
      this.showDebugLabel( message );
      
      if( this.timeElapsed > NaoIceSkatingSupervisor.QUIT_TIME )
      {
        // Revert simulation.
        this.simulationQuit( 1 );
      }
      
      // Update time.
      this.timeElapsed += (double)NaoIceSkatingSupervisor.STEP_TIME / NaoIceSkatingSupervisor.MILLISECONDS_IN_SECOND;
    }
    while( this.step( NaoIceSkatingSupervisor.STEP_TIME ) != -1 );
    
    // Enter here exit cleanup code
  }
  
  /**
   * Calculates the total distance traversed.
   * For now, only the x-coordinate is used.
   * @param p1
   * @param p2
   * @return
   */
  private double totalDistance( double[] p1, double[] p2 )
  {
    return p2[ 0 ] - p1[ 0 ];
  }
  
  /**
   * Shows a debug label.
   * @param message
   */
  private void showDebugLabel( String message )
  {
      this.setLabel( NaoIceSkatingSupervisor.DEBUG_UID,
                    message,
                    NaoIceSkatingSupervisor.DEBUG_X,
                    NaoIceSkatingSupervisor.DEBUG_Y,
                    NaoIceSkatingSupervisor.DEBUG_FONT_SIZE,
                    NaoIceSkatingSupervisor.DEBUG_FONT_COLOR,
                    NaoIceSkatingSupervisor.DEBUG_FONT_TRANSPARANCY );
  }

  /**
   * Main function.
   * @param args
   */
  public static void main( String[] args )
  {
    NaoIceSkatingSupervisor controller = new NaoIceSkatingSupervisor();
    controller.run();
  }
  
  
  /*
    Old potentially useful code.
    
    // Get the ice skating rink.
    this.rink = this.getFromDef( NaoIceSkatingSupervisor.RINK_NAME );
    this.rinkChildren = this.rink.getField( "children" );
    
    // Add Nao robot to the ice skating rink.
    this.rinkChildren.importMFNode( 0, "../../protos/Nao_H25_V40.wbo" );
    //this.robot = this.rinkChildren.getMFNode( 0 );
    
      public static final String RINK_NAME = "Ice_Skating_Rink";
  private Node rink;
  private Field rinkChildren;
  private Field robotRotation;
    this.robotRotation = this.robot.getField( "rotation" );
    
            final double[] location = { 0, 0.32, 0.5 };
        this.robotTranslation.setSFVec3f( location );
        
        final double[] rotation = { 1, 0, 0, -1.5708 }; 
        this.robotRotation.setSFRotation( rotation );
        
            // Use emitter and receiver.
    //Receiver r = this.getReceiver( "" );
    
    // World info.
   
    Node worldInfoNode = this.getFromDef( "WorldInfo" );
    Field gravityField = worldInfoNode.getField( "gravity" );
    double[] gravity = gravityField.getSFVec3f();
    System.out.printf( "Gravity: %f\n", gravity[ 1 ] );
   
  */
}
