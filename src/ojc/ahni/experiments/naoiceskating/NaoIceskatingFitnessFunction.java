package ojc.ahni.experiments.naoiceskating;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.Date;

import org.jgapcustomised.Chromosome;

import com.anji.integration.Activator;

import ojc.ahni.evaluation.HyperNEATFitnessFunction;

public class NaoIceskatingFitnessFunction extends HyperNEATFitnessFunction
{
	public static final String WEBOTS_DIRECTORY = String.format( "%s/%s", System.getProperty( "user.dir" ), "webots" );;
	public static final String WEBOTS_EXECUTABLE = "C:/Programs/Webots/webots.exe";
	
	public static final String DATA_DIRECTORY = String.format( "%s/%s", System.getProperty( "user.dir" ), "data" );
	public static final String DATA_FILE_NAME_ORIGINAL_ACCELEROMETER = "original_accelerometer.csv";
	public static final String DATA_FILE_PATH_ORIGINAL_ACCELEROMETER = String.format( "%s/%s", DATA_DIRECTORY, DATA_FILE_NAME_ORIGINAL_ACCELEROMETER );
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -5056600254730199552L;

	@Override
	public int getMaxFitnessValue()
	{
		return Integer.MAX_VALUE;
	}

	@Override
	protected int evaluate( Chromosome genotype, Activator substrate, int evalThreadIndex )
	{
		double[][] stimuli = MotionParser.loadMotionFile( String.format( "%s/%s", WEBOTS_DIRECTORY, "naoiceskating/motions/Forwards.motion" ) );
		double[][] activation = substrate.nextSequence( stimuli );

		MotionParser.writeMotionFile( activation, "ForwardsEvolved.motion" );

		// Run Webots.
		Runtime runtime = Runtime.getRuntime() ;
		
		try
		{
			// Run simulation
			System.out.printf( "[Webots]: Running simuation...\n" );
						
			Process process = runtime.exec( String.format( "%s %s \"%s/%s\"", WEBOTS_EXECUTABLE, "--mode=run", WEBOTS_DIRECTORY, "naoiceskating/worlds/naoiceskating.wbt" ) );
			int code = process.waitFor();
			
			//System.out.printf( "[Webots]: Simulation ended with code %n.\n", code );
			
			// Determine fitness.			
			Fitnessizer fitnessizer = new Fitnessizer( DATA_FILE_PATH_ORIGINAL_ACCELEROMETER );
			double fitness = fitnessizer.calculate( String.format( "%s/%s", DATA_DIRECTORY, "accelerometer.csv" ) );
			
			System.out.printf( "Fitness: %f\n", fitness );
			
			MotionParser.writeMotionFile( activation, String.format( "%f - %d.motion", fitness, System.nanoTime() ) );
			
			return (int)fitness;
		}
		catch( IOException e )
		{
			e.printStackTrace();
		}
		catch( InterruptedException e )
		{
			e.printStackTrace();
		}
		
		return this.getMaxFitnessValue();
	}
}
