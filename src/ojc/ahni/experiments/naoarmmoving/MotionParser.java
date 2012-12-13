package ojc.ahni.experiments.naoarmmoving;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Scanner;

public class MotionParser {
	public static double[][] loadMotionFile( String filePath ){
		ArrayList<Double> values = new ArrayList<Double>() ; 
		//TODO: make sure every file can be loaded
		File file = new File( filePath ) ;
		int lengthPitches = 0  ; 
		try {
			Scanner scanner = new Scanner(file, "UTF-8");
			String s = scanner.nextLine() ; //remove the first line as this line does not contain anything important
			
			while (scanner.hasNext()){
				s = scanner.nextLine(); 
				String[] pieces = s.split(",") ;
				for (int x = 2; x < pieces.length; x++){
					values.add((Double.parseDouble(pieces[x])+1.0)/2.0) ; //TODO: change this to actually meaningfull constants when looking at the joint configurations of the NAO
				}
				lengthPitches = pieces.length-2 ; 
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		double[][] motionArray = new double[values.size()/lengthPitches][lengthPitches];
		int counter = 0 ; 
		for (Double x : values){
			motionArray[counter/lengthPitches][counter%lengthPitches] = x ; 
			counter++ ; 
		}
		
		return motionArray ; 
	}
	
	public static void writeMotionFile(double[][] motionFile, String fileName){ 
		FileWriter fos;
		try {
			DecimalFormatSymbols decimalFormatSymbols = new DecimalFormatSymbols();
			decimalFormatSymbols.setDecimalSeparator( '.' );
			
			// Create new file writer.
			File file = new File( "data/generations" + File.separator+ fileName);			
			fos = new FileWriter(file, false);
			BufferedWriter out = new BufferedWriter(fos); 
			
			out.write("#WEBOTS_MOTION,V1.0,LHipYawPitch,LHipRoll,LHipPitch,LKneePitch,LAnklePitch,LAnkleRoll,RHipYawPitch,RHipRoll,RHipPitch,RKneePitch,RAnklePitch,RAnkleRoll\n") ;
			int timestep = 40 ; 
			for (int x = 0 ; x < motionFile.length; x++){
				
				if ((x*timestep)%1000 < 100 ){
					out.write("00:0" + (int)((x*timestep)/1000) + ":0" + (x*timestep)%1000 + ",Pose" + (x+1)) ; 
				}
				else{
					out.write("00:0" + (int)((x*timestep)/1000) + ":" + (x*timestep)%1000 + ",Pose" + (x+1)) ;
				}
				
				for(int y = 0; y < motionFile[x].length; y++)
				{
					out.write("," + new DecimalFormat("#.###", decimalFormatSymbols ).format(((motionFile[x][y]*2.0)-1.0))) ;
				}
				out.write("\n") ; 
			}
			out.close(); 
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} 
	}
}

