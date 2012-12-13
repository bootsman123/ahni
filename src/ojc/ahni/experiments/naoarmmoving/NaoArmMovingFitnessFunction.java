package ojc.ahni.experiments.naoarmmoving;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

import org.jgapcustomised.Chromosome;

import com.anji.integration.Activator;

import ojc.ahni.evaluation.HyperNEATFitnessFunction;

public class NaoArmMovingFitnessFunction extends HyperNEATFitnessFunction
{
	public static final String WEBOTS_DIRECTORY = String.format( "%s%s%s%s", System.getProperty( "user.dir" ), File.separator, "webots", File.separator );
	public static final String WEBOTS_EXECUTABLE = "C:/Programs/Webots/webots.exe";
	
	public static final String DATA_DIRECTORY = String.format( "%s%s%s%s", System.getProperty( "user.dir" ), File.separator, "data", File.separator );
	public static final String DATA_FILE_NAME_ACCELEROMETER = "accelerometer.csv";
	public static final String DATA_FILE_NAME_ORIGINAL_ACCELEROMETER = "original_accelerometer.csv";
	public static final String DATA_FILE_PATH_ORIGINAL_ACCELEROMETER = String.format( "%s%s", DATA_DIRECTORY, DATA_FILE_NAME_ORIGINAL_ACCELEROMETER );
	
	private final String uid = ( UUID.randomUUID() ).toString();
	
	public NaoArmMovingFitnessFunction()
	{
		super();
		
		boolean success = ( new File( String.format( "%s%s%s%s%s", DATA_DIRECTORY, File.separator, "generations", File.separator, this.uid ) ) ).mkdir();
	}

	@Override
	public int getMaxFitnessValue()
	{
		return Integer.MAX_VALUE;
	}

	@Override
	protected int evaluate( Chromosome genotype, Activator substrate, int evalThreadIndex )
	{
		double[][] stimuli = MotionParser.loadMotionFile( String.format( "%s%s%s%s", DATA_DIRECTORY, "generations", File.separator, "ForwardsEvolved.motion" ) );
		double[][] activation = substrate.nextSequence( stimuli );

		MotionParser.writeMotionFile( activation, "ForwardsEvolved.motion" );

		// Run Webots.
		Runtime runtime = Runtime.getRuntime() ;
		
		try
		{
			// Run simulation
			System.out.printf( "[Webots]: Running simuation...\n" );
						
			Process process = runtime.exec( String.format( "%s --mode=run \"%s%s\"", WEBOTS_EXECUTABLE, WEBOTS_DIRECTORY, "naoiceskating/worlds/naoiceskating.wbt" ) );
			int code = process.waitFor();
			
			//System.out.printf( "[Webots]: Simulation ended with code %n.\n", code );
			
			// Determine fitness.			
			Fitnessizer fitnessizer = new Fitnessizer( DATA_FILE_PATH_ORIGINAL_ACCELEROMETER );
			double fitness = fitnessizer.calculate( String.format( "%s%s", DATA_DIRECTORY, DATA_FILE_NAME_ACCELEROMETER ) );
			
			System.out.printf( "Fitness: %f\n", fitness );
			
			// Write a backup file.
			MotionParser.writeMotionFile( activation, String.format( "%s%s%f.motion", this.uid, File.separator, fitness ) );
			
			// Set fitness value.
			genotype.setFitnessValue( (int)fitness );
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
