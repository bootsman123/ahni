package ojc.ahni.experiments.naoiceskating;

import java.io.IOException;

import org.jgapcustomised.Chromosome;

import com.anji.integration.Activator;

import ojc.ahni.evaluation.HyperNEATFitnessFunction;

public class NaoIceskatingFitnessFunction extends HyperNEATFitnessFunction
{
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
		double[][] stimuli = MotionParser.loadMotionFile( "C:/Users/bootsman/Desktop/webots/naoiceskating/motions/Forwards.motion" );
		double[][] activation = substrate.nextSequence( stimuli );
		
		MotionParser.writeMotionFile( activation, "12345" );

		// Run Webots.
		Runtime runtime = Runtime.getRuntime() ;
		
		try
		{
			// Run simulation
			System.out.printf( "[Webots]: Running simuation...\n" );
			
			Process process = runtime.exec("C:/Programs/Webots/webots.exe \"C:/Users/bootsman/Desktop/webots/naoiceskating/naoiceskating.wbt\"" );
			int code = process.waitFor();
			
			System.out.printf( "[Webots]: Simulation ended with code %n.\n", code );
			
			// Determine fitness.			
			Fitnessizer fitnessizer = new Fitnessizer( "C:/Users/bootsman/Desktop/interpol_accelerometer_marlies.csv" );
			double fitness = fitnessizer.calculate( "C:/Users/bootsman/Desktop/accelerometer.csv" );
			
			System.out.printf( "Fitness: %f\n", fitness );
			
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
