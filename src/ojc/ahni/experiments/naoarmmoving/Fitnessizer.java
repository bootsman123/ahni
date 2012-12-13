package ojc.ahni.experiments.naoarmmoving;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ojc.ahni.experiments.nao.CSVReader;

public class Fitnessizer
{
	private String dataFilePath;
	private CSVReader dataReader;
	private Map<Double, Double> lookupValuesX;
	private Map<Double, Double> lookupValuesY;
	private Map<Double, Double> lookupValuesZ;
	
	public Fitnessizer( String dataFilePath ) throws FileNotFoundException, IOException
	{
		this.dataFilePath = dataFilePath;

		this.dataReader = new CSVReader( this.dataFilePath, ";" );

		this.lookupValuesX = new HashMap<Double,Double>();
		this.lookupValuesY = new HashMap<Double, Double>();
		this.lookupValuesZ = new HashMap<Double, Double>();
		
		// Process values.
		this.process();

	}
	
	public Double calculate( String filePath ) throws FileNotFoundException, IOException
	{
		Double fitness = 0.0;
		CSVReader reader = new CSVReader( filePath, ";" );
		
		// Calculate the fitness.
		List<String> values;
		while( ( values = reader.read() ) != null )
		{
			Double time = Double.valueOf( values.get( 0 ) );
			Double x = Double.valueOf( values.get( 1 ) );
			Double y = Double.valueOf( values.get( 2 ) );
			Double z = Double.valueOf( values.get( 3 ) );
			
			// Get values from the lookup-table.
			Double dataX = this.lookupValuesX.get( time );
			Double dataY = this.lookupValuesY.get( time );
			Double dataZ = this.lookupValuesZ.get( time );
			
			if( dataX == null || dataY == null || dataZ == null )
			{
				continue;
			}
			
			fitness += Math.pow( dataX - x, 2 );
			fitness += Math.pow( dataY - y, 2 );
			fitness += Math.pow( dataZ - z, 2 );
		}
		
		return fitness;
	}

	private void process() throws IOException
	{
		List<String> values;
		
		while( ( values = this.dataReader.read() ) != null )
		{
			Double time = Double.valueOf( values.get( 0 ) );
			Double x = Double.valueOf( values.get( 1 ) );
			Double y = Double.valueOf( values.get( 2 ) );
			Double z = Double.valueOf( values.get( 3 ) );

			this.lookupValuesX.put( time, x );
			this.lookupValuesY.put( time, y );
			this.lookupValuesZ.put( time, z );
			
			//System.out.printf( "time: %f - x: %f - y: %f - z: %f\n", time, x, y, z );
		}
	}
}
