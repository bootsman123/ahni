package ojc.ahni.experiments.naoarmmoving;

import java.io.File;

import org.apache.log4j.Logger;
import org.jgapcustomised.Chromosome;

import ojc.ahni.evaluation.HyperNEATTargetFitnessFunction;
import ojc.ahni.hyperneat.HyperNEATEvolver;
import ojc.ahni.nn.GridNet;

import com.anji.integration.Activator;
import com.anji.integration.TranscriberException;
import com.anji.util.Properties;

/**
 * A test fitness function that determines fitness based on how close the output of a network is to a target output
 * given some input. The specific test set can be specified with the property key "fitness.function.test.type". Valid
 * values are:
 * <ul>
 * <li>pass-through: the output should match the input.</li>
 * <li>pass-through-flip: same as pass-through except the target output is a mirror of the input.</li>
 * <li>rotate90: the target output is the input rotated 90 degrees.</li>
 * <li>parity: bitwise parity over all inputs.</li>
 * <li>[More to come!]</li>
 * </ul>
 * The number of trials is determined by the width and height of the input layer as 2 ^ (width + height), thus every
 * possible input pattern is tested. See {@link HyperNEATTargetFitnessFunction} for other available parameters that may
 * be specified via the properties file.
 * 
 * @author Oliver Coleman
 */
public class NaoArmMovingTargetFitnessFunction extends HyperNEATTargetFitnessFunction {
	private static final long serialVersionUID = 1L;
	public static final String TEST_TYPE_KEY = "fitness.function.test.type";

	public static final String DATA_DIRECTORY = String.format( "%s%s%s%s", System.getProperty( "user.dir" ), File.separator, "data", File.separator );
	
	private static Logger logger = Logger.getLogger(NaoArmMovingTargetFitnessFunction.class);

	private int numTrials;
	String testType;

	public NaoArmMovingTargetFitnessFunction() {
	}

	public void init(Properties props) {
		super.init(props);

		testType = props.getProperty(TEST_TYPE_KEY);
		int inputWidth = width[0];
		int inputHeight = height[0];
		int outputWidth = width[depth - 1];
		int outputHeight = height[depth - 1];

		if (testType.equals("pass-through") || testType.equals("pass-through-flip") || testType.equals("rotate90")) {
			if (inputWidth != outputWidth || inputHeight != outputHeight) {
				throw new IllegalArgumentException("HyperNEAT substrate input and output dimensions must be the same for TestTargetFitnessFunction.");
			}
		}
		if (testType.equals("rotate90") && inputWidth != inputHeight) {
			throw new IllegalArgumentException("HyperNEAT substrate input (and output) width and height must be the same for the rotate90 test in TestTargetFitnessFunction.");
		} else if (testType.equals("parity") && (outputWidth != 1 || outputHeight != 1)) {
			throw new IllegalArgumentException("HyperNEAT substrate output width and height must be 1 for the parity test in TestTargetFitnessFunction.");
		}

		numTrials = 1 << (inputWidth * inputHeight);
		logger.info("Target fitness function generating " + numTrials + " trials (if this number seems too large use an input layer with smaller dimensions).");
		generatePatterns();
	}

	protected void scale(int scaleCount, int scaleFactor) {
		for (int l = 0; l < width.length; l++) {
			width[l] *= scaleFactor;
			height[l] *= scaleFactor;
		}
		if (this.connectionRange != -1) {
			connectionRange *= scaleFactor;
		}
		generatePatterns();
	}

	private void generatePatterns() {
		double[][][] inputPatterns = new double[numTrials][height[0]][width[0]];
		double[][][] targetOutputPatterns = new double[numTrials][height[depth - 1]][width[depth - 1]];
		
		//int width = 12 ; 
		//int height = 1; 
		double[][] stimuli = new double[height[0]][width[0]];
		double[][] targets = MotionParser.loadMotionFile(String.format( "%s%s%s%s", DATA_DIRECTORY, "generations", File.separator, "Forwards.motion" ) );
		
		
	
		System.out.println(targets.length + "  "  + targets[0].length);
		for(int x = 0 ; x < height[0]; x++){
			for(int y = 0; y < width[0]; y++){
				stimuli[x][y] = targets[x][y];
			}
		}

		for (int t = 0; t < numTrials; t++) {
			int p = t;
			for (int y = 0; y < height[0]; y++) {
				for (int x = 0; x < width[0]; x++, p >>= 1) {
					//inputPatterns[t][y][x] = p & 0x1;
					inputPatterns[t][y][x] = stimuli[y][x];

					if (testType.equals("pass-through")) {
						targetOutputPatterns[t][y][x] = inputPatterns[t][y][x];
					}
					else if (testType.equals("pass-through-flip")) {
						targetOutputPatterns[t][y][(width[0] - 1) - x] = inputPatterns[t][y][x];
					}
					else if (testType.equals("rotate90")) {
						targetOutputPatterns[t][x][(height[0] - 1) - y] = inputPatterns[t][y][x];
					}
				}
			}
			if (testType.equals("parity")) {
				int parity = t & 0x1;
				for (int i = 1; i < 31; i++) {
					parity ^= (t >> i) & 0x1;
				}
				targetOutputPatterns[t][0][0] = parity;
			}
		}

		setPatterns(inputPatterns, targetOutputPatterns, 0, 1);

	}
	
	@Override
	protected int evaluate(Chromosome genotype, Activator substrate, int evalThreadIndex) {
		synchronized(this){
			return super.evaluate(genotype, substrate, evalThreadIndex, null);
		}
	}
}
