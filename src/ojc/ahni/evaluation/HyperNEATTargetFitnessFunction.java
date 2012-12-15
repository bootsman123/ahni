package ojc.ahni.evaluation;

import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Arrays;

import javax.imageio.ImageIO;

import ojc.ahni.event.AHNIEvent;
import ojc.ahni.event.AHNIEventListener;
import ojc.ahni.event.AHNIRunProperties;
import ojc.ahni.hyperneat.HyperNEATConfiguration;
import ojc.ahni.hyperneat.HyperNEATEvolver;
import ojc.ahni.nn.BainNN;
import ojc.ahni.util.NiceWriter;

import org.apache.log4j.Logger;
import org.jgapcustomised.*;

import com.anji.integration.*;
import com.anji.util.Properties;

/**
 * <p>Given a set of genotypes that encode a neural network, and a set of training examples consisting of input and target
 * (desired) output pattern pairs, determines fitness based on how close the output of the network encoded by each
 * genome is to the target output given some input.</p>
 * 
 * <p>See {@link ojc.ahni.evaluation.TargetFitnessCalculator} for a list of property keys to specify how the error and fitness calculations are performed.</p>
 * 
 * @author Oliver Coleman
 */
public class HyperNEATTargetFitnessFunction extends HyperNEATFitnessFunction implements AHNIEventListener {
	private static Logger logger = Logger.getLogger(HyperNEATTargetFitnessFunction.class);
	private static final long serialVersionUID = 1L;
	
	public static final String LOG_CHAMP_PERGENS_KEY = "fitness.function.log.champ.evaluation.pergenerations";
	
	protected int logChampPerGens = -1;
	
	private TargetFitnessCalculator fitnessCalculator;
	
	private double[][][] inputPatterns;
	private double[][][] targetOutputPatterns;
	private double minTargetOutputValue;
	private double maxTargetOutputValue;

	protected HyperNEATTargetFitnessFunction() {
	}

	/**
	 * Create a HyperNEATTargetFitnessFunction with the specified input and output examples.
	 * 
	 * @param inputPatterns Array containing stimuli (input) examples, in the form [trial][y][x]. The dimensions should match those of the
	 * input layer of the substrate network.
	 * @param targetOutputPatterns Array containing target response (output) examples, in the form [trial][y][x]. The dimensions should match those of the
	 * output layer of the substrate network.
	 * @param minTargetOutputValue The smallest value that occurs in the target outputs.
	 * @param maxTargetOutputValue The largest value that occurs in the target outputs.
	 */
	public HyperNEATTargetFitnessFunction(double[][][] inputPatterns, double[][][] targetOutputPatterns, double minTargetOutputValue, double maxTargetOutputValue) {
		this.inputPatterns = inputPatterns;
		this.targetOutputPatterns = targetOutputPatterns;
		this.minTargetOutputValue = minTargetOutputValue;
		this.maxTargetOutputValue = maxTargetOutputValue;
	}

	public void init(Properties props) {
		super.init(props);
		logChampPerGens = props.getIntProperty(LOG_CHAMP_PERGENS_KEY, logChampPerGens);
		fitnessCalculator = (TargetFitnessCalculator) props.newObjectProperty(TargetFitnessCalculator.class);
		((AHNIRunProperties) props).getEvolver().addEventListener(this);
	}

	/**
	 * @return maximum possible fitness value for this function.
	 */
	public int getMaxFitnessValue() {
		return fitnessCalculator.getMaxFitnessValue();
	}

	/**
	 * Sets the maximum possible fitness value this function will return. Default is 1000000 which is fine for nearly
	 * all purposes.
	 * 
	 * @param newMaxFitnessValue The new maximum fitness.
	 */
	protected void setMaxFitnessValue(int newMaxFitnessValue) {
		fitnessCalculator.setMaxFitnessValue(newMaxFitnessValue);
	}

	/**
	 * Set the input and target output pattern pairs to use for evaluations.
	 * @param inputPatterns Array containing stimuli (input) examples, in the form [trial][y][x]. The dimensions should match those of the
	 * input layer of the substrate network.
	 * @param targetOutputPatterns Array containing target response (output) examples, in the form [trial][y][x]. The dimensions should match those of the
	 * output layer of the substrate network.
	 * @param minTargetOutputValue The smallest value that occurs in the target outputs.
	 * @param maxTargetOutputValue The largest value that occurs in the target outputs.
	 */
	public void setPatterns(double[][][] inputPatterns, double[][][] targetOutputPatterns, double minTargetOutputValue, double maxTargetOutputValue) {
		this.inputPatterns = inputPatterns;
		this.targetOutputPatterns = targetOutputPatterns;
		this.minTargetOutputValue = minTargetOutputValue;
		this.maxTargetOutputValue = maxTargetOutputValue;
	}

	@Override
	protected int evaluate(Chromosome genotype, Activator substrate, int evalThreadIndex) {
		return evaluate(genotype, substrate, evalThreadIndex, null);
	}
	
	protected int evaluate(Chromosome genotype, Activator substrate, int evalThreadIndex, NiceWriter logOutput) {
		//System.out.println("HyperNeatTargetFitnessFunction.evaluate: Evaluating this!");
		double[][][] responses1D = substrate.nextSequence(inputPatterns);
		/*
		for(int t = 0 ; t < responses1D.length;t++){
			System.out.println("Response:");
			for(int x = 0; x < responses1D[t].length;x++){
				for(int y = 0 ; y < responses1D[t][x].length;y++){
					System.out.print(responses1D[t][x][y] + "[" + inputPatterns[t][x][y]+"] ");
				}
				System.out.println("");
			}
		}*/
		
		TargetFitnessCalculator.Results results = fitnessCalculator.evaluate(substrate, inputPatterns, targetOutputPatterns, minTargetOutputValue, maxTargetOutputValue, logOutput);
		genotype.setPerformanceValue(results.performance);
		return results.fitness;
	}
	
	@Override
	public void ahniEventOccurred(AHNIEvent event) {
		if (event.getType() == AHNIEvent.Type.GENERATION_END) {
			HyperNEATEvolver evolver = event.getEvolver();
			boolean finished = evolver.evolutionFinished(); 
			if ((logChampPerGens >= 0 && finished) || (logChampPerGens > 0 && evolver.getGeneration() % logChampPerGens == 0)) {
				try {
					Chromosome bestPerforming = evolver.getBestPerformingFromLastGen();
					Activator substrate = generateSubstrate(bestPerforming, null);
					if (substrate != null) {
						NiceWriter outputfile = new NiceWriter(new FileWriter(props.getProperty(HyperNEATConfiguration.OUTPUT_DIR_KEY) + "best_performing-" + (finished ? "final" : evolver.getGeneration()) + "-evaluation-" + bestPerforming.getId() + ".txt"), "0.00");
						evaluate(bestPerforming, substrate, 0, outputfile);
						outputfile.close();
					}
				} catch (TranscriberException e) {
					logger.info("Error transcribing best performing individual: "  + Arrays.toString(e.getStackTrace()));
				} catch (IOException e) {
					logger.info("Error opening evaluation log file: " + Arrays.toString(e.getStackTrace()));
				}
			}
		}
	}
}
