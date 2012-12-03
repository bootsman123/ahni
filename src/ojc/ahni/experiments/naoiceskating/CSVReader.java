package ojc.ahni.experiments.naoiceskating;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

public class CSVReader
{
	public static final String DELIMITER = ";";

	private String filePath;
	private String delimiter;
	
	private FileReader fileReader;
	private BufferedReader bufferedReader;
	
	public CSVReader( String filePath, String delimiter ) throws FileNotFoundException
	{
		this.filePath = filePath;
		this.delimiter = delimiter;
		
		this.fileReader = new FileReader( this.filePath );
		this.bufferedReader = new BufferedReader( this.fileReader );
	}
	
	public List<String> read() throws IOException
	{
		String line = this.bufferedReader.readLine();
		
		if( line == null )
		{
			return null;
		}
		
		// Add all tokens to the list.
		List<String> values = new ArrayList<>();
		StringTokenizer tokenizer = new StringTokenizer( line, this.delimiter );
		
		while( tokenizer.hasMoreTokens() )
		{
			values.add( tokenizer.nextToken() );
		}
		
		return values;
	}
	
	public void skip( int n )
	{
		for( int i = 0; i < n; i++ )
		{
			try
			{
				this.read();
			}
			catch( IOException e )
			{
			}
		}
	}
	
	public void skip()
	{
		this.skip( 1 );
	}
	
	public void reset()
	{
		try
		{
			this.bufferedReader.reset();
		}
		catch( IOException e )
		{
		}
	}
}
