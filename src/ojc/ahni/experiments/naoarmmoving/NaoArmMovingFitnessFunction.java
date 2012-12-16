package ojc.ahni.experiments.naoarmmoving;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

import ojc.ahni.evaluation.HyperNEATFitnessFunction;
import ojc.ahni.nn.GridNet;

import org.jgapcustomised.Chromosome;

import com.anji.integration.Activator;

public class NaoArmMovingFitnessFunction extends HyperNEATFitnessFunction
{
	/**
	 * Serial Version UID.
	 */
	private static final long serialVersionUID = -4527350411511555637L;
	
	public static final String WEBOTS_DIRECTORY = String.format( "%s%s%s%s", System.getProperty( "user.dir" ), File.separator, "webots", File.separator );
	public static final String WEBOTS_EXECUTABLE = "C:/Program Files (x86)/Webots/webots.exe";
	
	public static final String DATA_DIRECTORY = String.format( "%s%s%s%s", System.getProperty( "user.dir" ), File.separator, "data", File.separator );
	public static final String DATA_FILE_NAME_ACCELEROMETER = "armmovingheight.csv";
	public static final String DATA_FILE_NAME_ORIGINAL_ACCELEROMETER = "original_armmovingheight.csv"; //@TODO: Unavailable right now.
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
		/*
		GridNet net = (GridNet) substrate ;
		//MotionParser.writeRaisingHandFile(null, String.format( "raisingthathand.motion"));
		int width = 12 ; 
		int height = 1; 
		double[][] stimuli = new double[height][width];
		double[][] targets = MotionParser.loadMotionFile(String.format( "%s%s%s%s", DATA_DIRECTORY, "generations", File.separator, "Forwards.motion" ) );
		
		/*
		System.out.println("Targets:");
		for (double[] dd: targets){
			for(double d: dd){
				
				System.out.print(d + " ");
				
			}
			System.out.println();
		}*
	
		System.out.println(targets.length + "  "  + targets[0].length);
		for(int x = 0 ; x < height; x++){
			for(int y = 0; y < width; y++){
				stimuli[x][y] = targets[x][y];
			}
		}
		double[][] gridnetActivation = net.nextSequence(stimuli);
		
		double totalfitness = 0.0 ;
		//for (double[][] gridnetActivation2 : gridnetActivation){
		int xcor = 0;
		
		for (double[] dd: gridnetActivation){
			int ycor = 0 ;
			for(double d: dd){
				totalfitness += Math.abs(targets[xcor][ycor]-gridnetActivation[xcor][ycor]);
				System.out.print(gridnetActivation[xcor][ycor] + "[" + targets[xcor][ycor] + "] ");
				//totalfitness += d;
				ycor++;
			}
			xcor++;
			System.out.println();
		}
		//}
		*/
		

		double[][] stimuli = MotionParser.loadMotionFile( String.format( "%s%s%s%s", DATA_DIRECTORY, "generations", File.separator, "ArmMoving.motion" ) );
		double[][] activation = substrate.nextSequence( stimuli );

		MotionParser.writeMotionFile( activation, "ArmMoving.motion" );

		// Run Webots.
		Runtime runtime = Runtime.getRuntime();
		
		try
		{
			// Run simulation
			System.out.printf( "[Webots]: Running simuation...\n" );
						
			//Process process = runtime.exec( String.format( "cmd.exe /C %s --mode=fast \"%s%s\"", WEBOTS_EXECUTABLE, WEBOTS_DIRECTORY, "naoarmmoving/worlds/naoarmmoving.wbt" ) );
			//int code = process.waitFor();
			
			String[] command = {
				"cmd.exe",
				"/C",
				WEBOTS_EXECUTABLE,
				"--mode=fast",
				String.format( "%s%s", WEBOTS_DIRECTORY, "naoarmmoving\\worlds\\naoarmmoving.wbt" )
			};
						
			Process process = runtime.exec( command );
			int code = process.waitFor();

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
