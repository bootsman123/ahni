// File:          NaoIceSkating.cpp
// Date:          
// Description:   
// Author:        
// Modifications: 

// You may need to add webots include files such as
// <webots/DistanceSensor.hpp>, <webots/LED.hpp>, etc.
// and/or to add some other includes
#include <webots/Robot.hpp>
#include <webots/Supervisor.hpp>
#include <webots/LED.hpp>

#include <stdio.h>
#include <cstring>

#define TIME_STEP 64

// All the webots classes are defined in the "webots" namespace
using namespace webots;

// Here is the main class of your controller.
// This class defines how to initialize and how to run your controller.
// Note that this class derives Robot and so inherits all its functions
class NaoIceSkating : public Robot
{
  LED* leftEyeLed;
  LED* rightEyeLed;
  
  public:
    
    // NaoIceSkating constructor
    NaoIceSkating(): Robot()
    {
      
      // You should insert a getDevice-like function in order to get the
      // instance of a device of the robot. Something like:
      this->leftEyeLed = this->getLED( "Face/Led/Right" );
      this->rightEyeLed = this->getLED( "Face/Led/Left" );
      
      
      this->leftEyeLed->set( 0xff0000 );
      this->rightEyeLed->set( 0xff0000 );
    }

    // NaoIceSkating destructor
    virtual ~NaoIceSkating()
    {
      
      // Enter here exit cleanup code
      
    }
    
    // User defined function for initializing and running
    // the NaoIceSkating class
    void run()
    {
      
      // Main loop
      do
      {
        
        // Read the sensors:
        // Enter here functions to read sensor data, like:
        //  double val = distanceSensor->getValue();
        
        // Process sensor data here
        
        // Enter here functions to send actuator commands, like:
        //  led->set(1);
        
        //td::string name = this->getName();
        //printf( "%s\n", name.c_str() );

      }
      while( this->step( TIME_STEP ) != -1 );
    }
};

// This is the main program of your controller.
// It creates an instance of your Robot subclass, launches its
// function(s) and destroys it at the end of the execution.
// Note that only one instance of Robot should be created in
// a controller program.
// The arguments of the main function can be specified by the
// "controllerArgs" field of the Robot node
int main(int argc, char **argv)
{
  NaoIceSkating* controller = new NaoIceSkating();
  controller->run();
  delete controller;
  return 0;
}
