package figurebuilder;

import java.io.BufferedReader;
import java.io.FileReader;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import umontreal.ssj.rng.MRG31k3p;
import umontreal.ssj.rng.RandomStream;
import updaterules.AimdEIP1559;
import updaterules.FastEIP1559;
import updaterules.SlowEIP1559;
import updaterules.StandardEIP1559;
import updaterules.UpdateRule;

public class SimulationEvaluator {
	
	RandomStream r = new MRG31k3p();

	final static String[] INPUT_FILES = {
			System.getProperty("user.dir")+"/input/mainnet_fees_13025550_13026450.csv"
	};
	
	public class EvaluationResult {
		
		public double avgG;
		
		public double blockFull;
		
		public EvaluationResult(double avg, double fullPerc) {
			this.avgG = avg;
			this.blockFull = fullPerc;
		}
	}
	
	public EvaluationResult evaluateFile(String fileName, int start, int end, int colIdx, double threshold) {
		EvaluationResult result = null;
		try {
			BufferedReader br = new BufferedReader(new FileReader(fileName));
			String line;
			double sumG = 0;
			double nExremeG = 0;
			int lineCount = 0;
			int resultCount = 0;
			double cBlock = 0;

		    while ((line = br.readLine()) != null) {
		    	if(lineCount > 0) {
		    		String[] data = line.split(",");
		    		String block = data[0];
		    		double blockNum = Double.parseDouble(block);
		    		if(blockNum != cBlock) {
		    			if(blockNum >= start && blockNum <= end) {
				    		double g = Double.parseDouble(data[colIdx]);
					    	sumG += g;
					    	if(g > threshold) nExremeG += 1;
					    	resultCount += 1;
					    	cBlock = blockNum;
		    			}
		    		}
		    	}
		    	
		    	lineCount++;
		    }
		    
		    result = new EvaluationResult(sumG/resultCount, nExremeG/resultCount);
		    
		    br.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return result;
	}
	
	public EvaluationResult averageResult(List<EvaluationResult> results) {
		double avg = 0;
		double ext = 0;
		int count = 0;
		for(EvaluationResult result : results) {
			avg += result.avgG;
			ext += result.blockFull;
			count++;
		}
		return new EvaluationResult(avg/count, ext/count);
	}
	
	public EvaluationResult averageSquareResult(List<EvaluationResult> results) {
		double avg = 0;
		double ext = 0;
		int count = 0;
		for(EvaluationResult result : results) {
			avg += result.avgG * result.avgG;
			ext += result.blockFull * result.blockFull;
			count++;
		}
		return new EvaluationResult(avg/count, ext/count);
	}
	
	
	public void evaluatePerformance(String[] files, String outputFileName, int start, int end, int granularity, int smoothRange, String empty, UpdateRule rule) {
		int N = 20;
		double[][] smoothFArray = DataProcessor.longTermAverageValue(files, outputFileName, start, end, granularity, smoothRange, empty, 8, 1000000000);
		
		List<EvaluationResult> results = new ArrayList<EvaluationResult>();
		
		for(int i=0;i<N;i++) {
			new SimulationGraphs().simulateUniformPlusWhales(smoothFArray, outputFileName, start, end, granularity, smoothRange, empty, rule);
			
			EvaluationResult empResult = evaluateFile(outputFileName, start, end, 4, 0.95);
			results.add(empResult);
		}
		
		EvaluationResult totalResult = averageResult(results);
		EvaluationResult squareResult = averageSquareResult(results);
		
		DecimalFormat df = new DecimalFormat("0.000");
		
		System.out.println("\\gaussconfintv{"+df.format(totalResult.avgG)+"}{"+df.format(1.96*Math.sqrt((squareResult.avgG - totalResult.avgG*totalResult.avgG)/N))+"}");
		System.out.println("\\gaussconfintv{"+df.format(totalResult.blockFull)+"}{"+df.format(1.96*Math.sqrt((squareResult.blockFull - totalResult.blockFull*totalResult.blockFull)/N))+"}");
		
		for(int i=0;i<files.length;i++) {
			EvaluationResult empResult = evaluateFile(files[i], start, end, 2, 0.95);

			System.out.println("emp: "+empResult.avgG+" "+empResult.blockFull);
		}
	}
	
	public static void main(String[] a) {
//		stable
		final String TABLE_OUTPUT = System.getProperty("user.dir")+"/output/temp/sim_price_stable_temp.csv";
		new SimulationEvaluator().evaluatePerformance(INPUT_FILES, TABLE_OUTPUT, 13026000, 13026450, 450, 30, "0", new StandardEIP1559());
		new SimulationEvaluator().evaluatePerformance(INPUT_FILES, TABLE_OUTPUT, 13026000, 13026450, 450, 30, "0", new SlowEIP1559());
		new SimulationEvaluator().evaluatePerformance(INPUT_FILES, TABLE_OUTPUT, 13026000, 13026450, 450, 30, "0", new FastEIP1559());
		new SimulationEvaluator().evaluatePerformance(INPUT_FILES, TABLE_OUTPUT, 13026000, 13026450, 450, 30, "0", new AimdEIP1559());
		
//		burst
		new SimulationEvaluator().evaluatePerformance(INPUT_FILES, TABLE_OUTPUT, 13025500, 13026000, 450, 30, "0", new StandardEIP1559());
		new SimulationEvaluator().evaluatePerformance(INPUT_FILES, TABLE_OUTPUT, 13025500, 13026000, 450, 30, "0", new SlowEIP1559());
		new SimulationEvaluator().evaluatePerformance(INPUT_FILES, TABLE_OUTPUT, 13025500, 13026000, 450, 30, "0", new FastEIP1559());
		new SimulationEvaluator().evaluatePerformance(INPUT_FILES, TABLE_OUTPUT, 13025500, 13026000, 450, 30, "0", new AimdEIP1559());
	}
}
