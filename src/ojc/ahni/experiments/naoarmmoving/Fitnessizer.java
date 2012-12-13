package ojc.ahni.experiments.naoarmmoving;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import ojc.ahni.experiments.nao.CSVReader;

public class Fitnessizer
{
	private String dataFilePath;
	private CSVReader dataReader;
	private List<Double> lookupValuesH;
	
	public Fitnessizer( String dataFilePath ) throws FileNotFoundException, IOException
	{
		this.dataFilePath = dataFilePath;

		this.dataReader = new CSVReader( this.dataFilePath, ";" );
		this.lookupValuesH = diff(dataReader);		
	}
	
	public Double calculate( String filePath ) throws FileNotFoundException, IOException
	{
		Double fitness = 0.0;
		List<Double> newDiff = diff(new CSVReader( filePath, ";" ));
		for (int i = 0; i < newDiff.size(); i++)
		{
			fitness += Math.pow(newDiff.get(i) - this.lookupValuesH.get(i), 2);
		}
		
		return fitness;
	}
	
	private List<Double> diff (CSVReader r) throws IOException
	{
		List<String> values = r.read(); //Read first line
		Double t_ = Double.valueOf( values.get( 0 ) );
		Double h_ = Double.valueOf( values.get( 1 ) );
		
		List<Double> d = new ArrayList<Double>();
		while( ( values = r.read() ) != null )
		{
			Double t = Double.valueOf( values.get( 0 ) );
			Double h = Double.valueOf( values.get( 1 ) );
			d.add((h - h_) / (t - t_));
			t_ = t;
			h_ = h;
		}
		return d;
	}
}
