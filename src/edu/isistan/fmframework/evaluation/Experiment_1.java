package edu.isistan.fmframework.evaluation;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import edu.isistan.fmframework.core.Configuration;
import edu.isistan.fmframework.optimization.Algorithm;
import edu.isistan.fmframework.optimization.BasicProblem;
import edu.isistan.fmframework.optimization.Problem;
import edu.isistan.fmframework.optimization.opt01LP.Java01LPalgorithm;
import edu.isistan.fmframework.optimization.optCSA.CSAalgorithm;
import edu.isistan.fmframework.optimization.optCSA.heuristicFunctions.HeuristicMO;
import edu.isistan.fmframework.optimization.optCSA.heuristicFunctions.HeuristicTC;
import edu.isistan.fmframework.optimization.optCSA.variableSelectors.MaxHeuristicValue;
import edu.isistan.fmframework.optimization.optCSA.variableSelectors.MaxValuePerWeight;
import edu.isistan.fmframework.optimization.optCSA.variableSelectors.MostConstrainedFeature;
import edu.isistan.fmframework.utils.CSVUtils;
import edu.isistan.fmframework.utils.StatsUtils;
import fm.FeatureModelException;
import net.sf.javailp.OptType;

public class Experiment_1 {

	public static void main(String[] args) throws IOException, FeatureModelException {
		experiment1_part1();
		experiment1_part2();
	}

	static Algorithm<Problem> algs[] = new Algorithm[] {

			CSAalgorithm.build("BT+HMO+MCF", CSAalgorithm.Strategy.BT, new HeuristicMO(), new MostConstrainedFeature()),
			CSAalgorithm.build("BT+HMO+MVW", CSAalgorithm.Strategy.BT, new HeuristicMO(), new MaxValuePerWeight()),
			CSAalgorithm.build("BT+HTC+MCF", CSAalgorithm.Strategy.BT, new HeuristicTC(), new MostConstrainedFeature()),
			CSAalgorithm.build("BT+HTC+MVW", CSAalgorithm.Strategy.BT, new HeuristicTC(), new MaxValuePerWeight()),
			CSAalgorithm.build("BT+HMO+MHV", CSAalgorithm.Strategy.BT, new HeuristicMO(),
					new MaxHeuristicValue(new HeuristicMO())),
			CSAalgorithm.build("BT+HTC+MHV", CSAalgorithm.Strategy.BT, new HeuristicTC(),
					new MaxHeuristicValue(new HeuristicTC())),

			CSAalgorithm.build("BandB+HTC+MHV", CSAalgorithm.Strategy.BandB, new HeuristicTC(),
					new MaxHeuristicValue(new HeuristicTC())),
			CSAalgorithm.build("BestFS+HTC+MHV", CSAalgorithm.Strategy.BestFS, new HeuristicTC(),
					new MaxHeuristicValue(new HeuristicTC())),

			CSAalgorithm.build("BandB+HTC+MCF", CSAalgorithm.Strategy.BandB, new HeuristicTC(),
					new MostConstrainedFeature()),
			CSAalgorithm.build("BestFS+HTC+MCF", CSAalgorithm.Strategy.BestFS, new HeuristicTC(),
					new MostConstrainedFeature()),

			CSAalgorithm.build("BandB+HTC+MVW", CSAalgorithm.Strategy.BandB, new HeuristicTC(),
					new MaxValuePerWeight()),
			CSAalgorithm.build("BestFS+HTC+MVW", CSAalgorithm.Strategy.BestFS, new HeuristicTC(),
					new MaxValuePerWeight()),

			CSAalgorithm.build("BandB+HMO+MCF", CSAalgorithm.Strategy.BandB, new HeuristicMO(),
					new MostConstrainedFeature()),
			CSAalgorithm.build("BestFS+HMO+MCF", CSAalgorithm.Strategy.BestFS, new HeuristicMO(),
					new MostConstrainedFeature()),

			CSAalgorithm.build("BandB+HMO+MHV", CSAalgorithm.Strategy.BandB, new HeuristicMO(),
					new MaxHeuristicValue(new HeuristicMO())),
			CSAalgorithm.build("BestFS+HMO+MHV", CSAalgorithm.Strategy.BestFS, new HeuristicMO(),
					new MaxHeuristicValue(new HeuristicMO())),

			CSAalgorithm.build("BandB+HMO+MVW", CSAalgorithm.Strategy.BandB, new HeuristicMO(),
					new MaxValuePerWeight()),
			CSAalgorithm.build("BestFS+HMO+MVW", CSAalgorithm.Strategy.BestFS, new HeuristicMO(),
					new MaxValuePerWeight())

	};

	static boolean[] allAlgorithms = new boolean[] { true, true, true, true, true, true, true, true, true, true, true,
			true, true, true, true, true, true, true };
	static boolean[] allAlgorithmsLessExactVariantsWithHMOwithMHVandMVW = new boolean[] { true, true, true, true, true,
			true, true, true, true, true, true, true, true, true, false, false, false, false };
	static boolean[] allAlgorithmsLessExactVariantsWithHMO = new boolean[] { true, true, true, true, true, true, true,
			true, true, true, true, true, false, false, false, false, false, false };
	static boolean[] allAlgorithmsLessExactVariantsWithHMOandHTCwithMCFandMVW = new boolean[] { true, true, true, true,
			true, true, true, true, false, false, false, false, false, false, false, false, false, false };
	static boolean[] allAlgorithmsLessExactVariants = new boolean[] { true, true, true, true, true, true, false, false,
			false, false, false, false, false, false, false, false, false, false };

	private static void experiment1_part1() throws IOException {

		ProblemGenerator[] samples_original_numFeatures_splotdist = new ProblemGenerator[45];
		for (int i = 0; i < samples_original_numFeatures_splotdist.length; i++) {
			int numFeatures = (i + 1) * 10;
			samples_original_numFeatures_splotdist[i] = new ProblemGenerator(Integer.toString(numFeatures), numFeatures)
					.setBranchingFactor(ProblemGenerator.SPLOT_BRANCHING_FACTOR)
					.setTreeConstraintDistribution(ProblemGenerator.SPLOT_TCTDISTRIBUTION)
					.setCTCRatio(ProblemGenerator.SPLOT_CTC_RATIO);//.setIRs(1);
		}

		boolean[][] runningalgorithms_original_numFeatures_splotdist = new boolean[][] { allAlgorithms, allAlgorithms,
				allAlgorithms, allAlgorithms, allAlgorithms, // 50
				allAlgorithmsLessExactVariantsWithHMOwithMHVandMVW, allAlgorithmsLessExactVariantsWithHMOwithMHVandMVW,
				allAlgorithmsLessExactVariantsWithHMOwithMHVandMVW, allAlgorithmsLessExactVariantsWithHMOwithMHVandMVW,
				allAlgorithmsLessExactVariantsWithHMOwithMHVandMVW, // 100
				allAlgorithmsLessExactVariantsWithHMO, allAlgorithmsLessExactVariantsWithHMO,
				allAlgorithmsLessExactVariantsWithHMO, allAlgorithmsLessExactVariantsWithHMO, // 140
				allAlgorithmsLessExactVariantsWithHMOandHTCwithMCFandMVW,
				allAlgorithmsLessExactVariantsWithHMOandHTCwithMCFandMVW,
				allAlgorithmsLessExactVariantsWithHMOandHTCwithMCFandMVW,
				allAlgorithmsLessExactVariantsWithHMOandHTCwithMCFandMVW,
				allAlgorithmsLessExactVariantsWithHMOandHTCwithMCFandMVW,
				allAlgorithmsLessExactVariantsWithHMOandHTCwithMCFandMVW,
				allAlgorithmsLessExactVariantsWithHMOandHTCwithMCFandMVW,
				allAlgorithmsLessExactVariantsWithHMOandHTCwithMCFandMVW,
				allAlgorithmsLessExactVariantsWithHMOandHTCwithMCFandMVW,
				allAlgorithmsLessExactVariantsWithHMOandHTCwithMCFandMVW, // 240
				allAlgorithmsLessExactVariants, allAlgorithmsLessExactVariants, allAlgorithmsLessExactVariants,
				allAlgorithmsLessExactVariants, allAlgorithmsLessExactVariants, allAlgorithmsLessExactVariants,
				allAlgorithmsLessExactVariants, allAlgorithmsLessExactVariants, allAlgorithmsLessExactVariants,
				allAlgorithmsLessExactVariants, allAlgorithmsLessExactVariants, allAlgorithmsLessExactVariants,
				allAlgorithmsLessExactVariants, allAlgorithmsLessExactVariants, allAlgorithmsLessExactVariants,
				allAlgorithmsLessExactVariants, allAlgorithmsLessExactVariants, allAlgorithmsLessExactVariants,
				allAlgorithmsLessExactVariants, allAlgorithmsLessExactVariants, allAlgorithmsLessExactVariants // 450
		};

		run_experiment(200, samples_original_numFeatures_splotdist, runningalgorithms_original_numFeatures_splotdist,
				"Results/Exp1-Part1", algs);

	};

	private static void experiment1_part2() throws IOException {
		ProblemGenerator[] samples = new ProblemGenerator[] {
				new ProblemGenerator("OPTIONALS", 30)
						.setTreeConstraintDistribution(ProblemGenerator.SPLOT_TCTDISTRIBUTION_OR_XOR_INTO_OPTIONALS),
				new ProblemGenerator("NORMAL", 30)
						.setTreeConstraintDistribution(ProblemGenerator.SPLOT_TCTDISTRIBUTION),
				new ProblemGenerator("CARDINALITY", 30)
						.setTreeConstraintDistribution(ProblemGenerator.SPLOT_TCTDISTRIBUTION_OR_INTO_CARDINALITY),
				new ProblemGenerator("CARDINALITY+CTC0.147", 30)
						.setTreeConstraintDistribution(ProblemGenerator.SPLOT_TCTDISTRIBUTION_OR_INTO_CARDINALITY)
						.setCTCRatio(ProblemGenerator.SPLOT_CTC_RATIO),
				new ProblemGenerator("CARDINALITY+CTC0.147+IR0.9x5", 30)
						.setTreeConstraintDistribution(ProblemGenerator.SPLOT_TCTDISTRIBUTION_OR_INTO_CARDINALITY)
						.setCTCRatio(ProblemGenerator.SPLOT_CTC_RATIO).setIRs(5, 0.9), };

		for (ProblemGenerator problemGenerator : samples) {
			((ProblemGenerator) problemGenerator).setBranchingFactor(ProblemGenerator.SPLOT_BRANCHING_FACTOR);
			BasicProblem instance = problemGenerator.generateBasicProblemInstance();
		}

		boolean[][] runningalgorithms = new boolean[][] { allAlgorithms, allAlgorithms, allAlgorithms, allAlgorithms,
				allAlgorithms };

		run_experiment(200, samples, runningalgorithms, "Results/Exp1-Part2", algs);
	}

	private static void run_experiment(int instances_per_sample, ProblemGenerator[] samples, boolean[][] runalgorithms,
			String folderPath, Algorithm<Problem> algs[]) throws IOException {

		// RandomUtils.setSeed(123121);
		int NUM_EXPERIMENTS = instances_per_sample;

		String[] algnames = null;
		algnames = new String[algs.length + 1];
		algnames[0] = "Sample name";
		for (int a = 0; a < algs.length; a++) {
			algnames[a + 1] = algs[a].getName();
		}

		File directory = new File(folderPath);
		if (!directory.exists()) {
			directory.mkdir();
		}

		String fileaverages = folderPath + "/Time-average.csv";
		String filedesvest = folderPath + "/Time-desvest.csv";
		String fileall = folderPath + "/Time.csv";
		String fileoptimalityaverages = folderPath + "/Optimality-average.csv";

		CSVUtils.appendRow(fileaverages, algnames);
		CSVUtils.appendRow(filedesvest, algnames);
		CSVUtils.appendRow(fileall, algnames);
		CSVUtils.appendRow(fileoptimalityaverages, algnames);
		System.out.println(Arrays.toString(algnames));

		String[] algaverage = new String[algs.length + 1];
		String[] algdesvest = new String[algs.length + 1];
		String[] algall = new String[algs.length + 1];
		String[] algoptimalityaverages = new String[algs.length + 1];

		Configuration conf;
		Algorithm<BasicProblem> algbest = new Java01LPalgorithm(OptType.MIN);
		Algorithm<BasicProblem> algworst = new Java01LPalgorithm(OptType.MAX);

		for (int s = 0; s < samples.length; s++) {
			ProblemGenerator generator = samples[s];
			boolean[] algorithms = runalgorithms[s];
			long times[][] = new long[algs.length][NUM_EXPERIMENTS];
			double optimality[][] = new double[algs.length][NUM_EXPERIMENTS];
			boolean firstTime = true;
			for (int e = 0; e < NUM_EXPERIMENTS; e++) {
				System.out.println(e);
				BasicProblem instance = generator.generateBasicProblemInstance();

				algbest.preprocessInstance(instance);
				conf = algbest.selectConfiguration(instance);
				double bestValue = instance.objectiveFunctions[0].evaluate(conf);
				algworst.preprocessInstance(instance);
				conf = algworst.selectConfiguration(instance);
				double worseValue = instance.objectiveFunctions[0].evaluate(conf);
				try {
					for (int a = 0; a < algs.length; a++) {
						if (algorithms[a]) {
							algs[a].preprocessInstance(instance);
							System.gc();

							long start = System.nanoTime();
							conf = algs[a].selectConfiguration(instance);
							times[a][e] = System.nanoTime() - start;
							double foundValue = instance.objectiveFunctions[0].evaluate(conf);
							optimality[a][e] = calculateOptimalityDegree(foundValue, bestValue, worseValue);

						} else {
							times[a][e] = 0;
							optimality[a][e] = 0.0;
						}
					}
					if (firstTime) {
						e--;
						firstTime = false;
					}
				} catch (RuntimeException exc) {
					System.out.println(exc.getMessage());
					e--;
				}
				;
			}
			algaverage[0] = generator.toString();
			algdesvest[0] = generator.toString();
			algall[0] = generator.toString();
			algoptimalityaverages[0] = generator.toString();

			for (int a = 0; a < algs.length; a++) {
				double average = StatsUtils.averageL(times[a]) / 1000000.0;
				double desvest = StatsUtils.deviationL(times[a]) / 1000000.0;
				algaverage[a + 1] = Double.toString(average);
				algdesvest[a + 1] = Double.toString(desvest);
				algall[a + 1] = String.format("%.4f", average) + " � " + String.format("%.4f", desvest);

				double averageoptimality = StatsUtils.averageD(optimality[a]);
				algoptimalityaverages[a + 1] = Double.toString(averageoptimality);
			}
			CSVUtils.appendRow(fileaverages, algaverage);
			CSVUtils.appendRow(filedesvest, algdesvest);
			CSVUtils.appendRow(fileall, algall);
			CSVUtils.appendRow(fileoptimalityaverages, algoptimalityaverages);
			System.out.println(Arrays.toString(algaverage));
			System.out.println(Arrays.toString(algdesvest));
			System.out.println(Arrays.toString(algall));
			System.out.println(Arrays.toString(algoptimalityaverages));
		}

	}

	public static double calculateOptimalityDegree(double found, double best, double worst) {
		double optimalityValue = 0.0;
		if (Double.isNaN(found) == false && Double.isNaN(best) == false && Double.isNaN(worst) == false) {
			if (best != worst) {
				optimalityValue = (Math.abs(found - worst) / Math.abs(best - worst));
			} else {
				if (best != 0) {
					optimalityValue = found / best;
				}
			}
		}
		return optimalityValue;
	};

}
