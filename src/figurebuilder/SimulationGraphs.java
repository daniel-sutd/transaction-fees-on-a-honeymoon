package figurebuilder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import umontreal.ssj.randvar.ParetoGen;
import umontreal.ssj.rng.MRG31k3p;
import umontreal.ssj.rng.RandomStream;
import updaterules.AimdEIP1559;
import updaterules.FastEIP1559;
import updaterules.SlowEIP1559;
import updaterules.StandardEIP1559;
import updaterules.UpdateRule;

public class SimulationGraphs {
	
	RandomStream r = new MRG31k3p();

	final static String[] INPUT_FILES = {
			System.getProperty("user.dir")+"/input/mainnet_fees_13025550_13026450.csv"
	};
	
	public int drawPoisson(double l) {
		int n = 0;
		double t = 0;
		while(true) {
			t -= Math.log(Math.random())/l;
			if(t >= 1) return n;
			n++;
		}
	}
	
//	public void simulateUniform(String[] files, String outputFileName, int start, int end, int granularity, int smoothRange, String empty, UpdateRule rule) {
//		double[][] graphArray = DataProcessor.longTermAverageValue(files, outputFileName, start, end, granularity, smoothRange, empty, 8, 1000000000);
//		simulateUniform(graphArray, outputFileName, start, end, granularity, smoothRange, empty, rule);
//	}
//	
//	public void simulateUniform(double[][] graphArray, String outputFileName, int start, int end, int granularity, int smoothRange, String empty, UpdateRule rule) {
//		double[][] resultArray = new double[end-start][3];
//		int i = 0;
//		double g = 0;
//		double b = 30;
//		double T = 1000;
//		
//		for(var h=0;h<end - start;h++) {
//			if(i < graphArray.length - 1 && Math.abs(graphArray[i][0] - start - h) > Math.abs(graphArray[i+1][0] - start - h)) {
//				i++;
//			}
//			int n = drawPoisson(3*T);
//			List<Double> V = new ArrayList<Double>();
//			for(int j=0;j<n;j++) {
//				V.add(20 + 10*Math.random()); 
//			}
//
//			Collections.sort(V);
//			List<Double> B = new ArrayList<Double>(n);
//			for(int j=V.size()-1;j>=0;j--) {
//				double v = V.get(j);
//				if(v > b) B.add(v);
//				if(B.size() >= T) break;
//			}
//			double avgB = 0;
//			for(double v : B) {
//				avgB += v;
//			}
//			avgB /= B.size();
//			
//			g = B.size()/T;
//			b = rule.updateBasefee(b, g);
//			double d = rule.getD();
//			
//			resultArray[h] = new double[] {start+h, n, avgB, b, g, d};
//			
//			// trim to avoid the mem pool from blowing up
//			for(int j=V.size()-1;j>=0;j--) {
//				if(Math.random() < 0.75) V.remove(j);
//			}
//		}
//		
//		DataProcessor.writeFile("block,n,f,b,g\n",outputFileName, resultArray, empty);
//	}
//
//	public void simulateExponential(String[] files, String outputFileName, int start, int end, int granularity, int smoothRange, String empty, UpdateRule rule) {
//		double[][] graphArray = DataProcessor.longTermAverageValue(files, outputFileName, start, end, granularity, smoothRange, empty, 8, 1000000000);
//		simulateExponential(graphArray, outputFileName, start, end, granularity, smoothRange, empty, rule);
//	}
//	
//	public void simulateExponential(double[][] graphArray, String outputFileName, int start, int end, int granularity, int smoothRange, String empty, UpdateRule rule) {
//		double[][] resultArray = new double[end-start][3];
//		int i = 0;
//		double g = 0;
//		double b = 30;
//		double T = 100;
//		List<Double> V = new ArrayList<Double>();
//		
//		for(var h=0;h<end - start;h++) {
//			if(i < graphArray.length - 1 && Math.abs(graphArray[i][0] - start - h) > Math.abs(graphArray[i+1][0] - start - h)) {
//				i++;
//			}
//			int n = drawPoisson(3*T);
//			
//			for(int j=0;j<n;j++) {
//				V.add(-Math.log(Math.random())/(Math.log(6)/graphArray[i][1])); 
//			}
//			List<Double> B = new ArrayList<Double>(n);
//			for(int j=V.size()-1;j>=0;j--) {
//				double v = V.get(j);
//				if(v > b) B.add(v);
//				if(B.size() >= T) break;
//			}
//			double avgB = 0;
//			for(double v : B) {
//				avgB += v;
//			}
//			avgB /= B.size();
//			
//			g = B.size()/T;
//			b = rule.updateBasefee(b, g);
//			double d = rule.getD();
//			
//			resultArray[h] = new double[] {start+h, n, avgB, b, g, d};
//			
//			// trim to avoid the mem pool from blowing up
//			for(int j=V.size()-1;j>=0;j--) {
//				if(Math.random() < 0.75) V.remove(j);
//			}
//		}
//		
//		DataProcessor.writeFile("block,n,f,b,g\n",outputFileName, resultArray, empty);
//	}
	
	public void simulateUniformPlusWhales(String[] files, String outputFileName, int start, int end, int granularity, int smoothRange, String empty, UpdateRule rule) {
		double[][] graphArray = DataProcessor.longTermAverageValue(files, outputFileName, start, end, granularity, smoothRange, empty, 8, 1000000000);
		simulateUniformPlusWhales(graphArray, outputFileName, start, end, granularity, smoothRange, empty, rule);
	}
	
	public void simulateUniformPlusWhales(double[][] graphArray, String outputFileName, int start, int end, int granularity, int smoothRange, String empty, UpdateRule rule) {
		double[][] resultArray = new double[end-start][3];
		int i = 0;
		double g = 0;
		double b = 30;
		double T = 100;

		List<Double> V = new ArrayList<Double>();
		
		for(var h=0;h<end - start;h++) {
			if(i < graphArray.length - 1 && Math.abs(graphArray[i][0] - start - h) > Math.abs(graphArray[i+1][0] - start - h)) {
				i++;
			}
			int n = drawPoisson(3*T); // 3*T can be changed to 2.75*T, to ensure lambda = 3 as in the text of the paper 
			for(int j=0;j<n;j++) {
				V.add(graphArray[i][1]*0.75 - 2.5*Math.random()); 
			}
			int n2 = drawPoisson(0.25*T);
			for(int j=0;j<n2;j++) {
				V.add(ParetoGen.nextDouble(r, 1.5, graphArray[i][1]/10+1) + graphArray[i][1]*0.8); // here, 1.5 is the shape of the Pareto distribution - this can be changed to 1.35 to reflect the text of the paper
			}
			Collections.sort(V);
			List<Double> B = new ArrayList<Double>(n);
			for(int j=V.size()-1;j>=0;j--) {
				double v = V.get(j);
				if(v > b) B.add(v);
				if(B.size() >= T) break;
			}
			
			double avgB = 0;
			for(double v : B) {
				avgB += v;
			}
			avgB /= B.size();
			
			g = B.size()/T;
			b = rule.updateBasefee(b, g);
			double d = rule.getD();
			
			resultArray[h] = new double[] {start+h, n, avgB, b, g, d};
			
			// trim to avoid the mem pool from blowing up
			for(int j=V.size()-1;j>=0;j--) {
				if(Math.random() < 0.75) V.remove(j);
			}
		}
		
		DataProcessor.writeFile("block,n,f,b,g,d\n",outputFileName, resultArray, empty);
	}
	
	
	
	public static void main(String[] a) {
		// stable, price, basefee, block size
		final String STABLE_OUTPUT = System.getProperty("user.dir")+"/output/sim_price_stable.csv";
		new SimulationGraphs().simulateUniformPlusWhales(INPUT_FILES, STABLE_OUTPUT, 13026000, 13026450, 450, 30, "0", new StandardEIP1559());
		final String BURST_OUTPUT = System.getProperty("user.dir")+"/output/sim_price_burst.csv";
		new SimulationGraphs().simulateUniformPlusWhales(INPUT_FILES, BURST_OUTPUT, 13025550, 13026000, 450, 30, "0", new StandardEIP1559());
		
		// stable, price, basefee, block size
		final String STABLE_OUTPUT_SLOW_LEARN = System.getProperty("user.dir")+"/output/sim_price_stable_slow.csv";
		new SimulationGraphs().simulateUniformPlusWhales(INPUT_FILES, STABLE_OUTPUT_SLOW_LEARN, 13026000, 13026450, 450, 30, "0", new SlowEIP1559());
		final String BURST_OUTPUT_SLOW_LEARN = System.getProperty("user.dir")+"/output/sim_price_burst_slow.csv";
		new SimulationGraphs().simulateUniformPlusWhales(INPUT_FILES, BURST_OUTPUT_SLOW_LEARN, 13025550, 13026000, 450, 30, "0", new SlowEIP1559());
		
		// stable, price, basefee, block size
		final String STABLE_OUTPUT_FAST_LEARN = System.getProperty("user.dir")+"/output/sim_price_stable_fast.csv";
		new SimulationGraphs().simulateUniformPlusWhales(INPUT_FILES, STABLE_OUTPUT_FAST_LEARN, 13026000, 13026450, 450, 30, "0", new FastEIP1559());
		final String BURST_OUTPUT_FAST_LEARN = System.getProperty("user.dir")+"/output/sim_price_burst_fast.csv";
		new SimulationGraphs().simulateUniformPlusWhales(INPUT_FILES, BURST_OUTPUT_FAST_LEARN, 13025550, 13026000, 450, 30, "0", new FastEIP1559());
		
		// stable, price, basefee, block size
		final String STABLE_OUTPUT_AIMD_LEARN = System.getProperty("user.dir")+"/output/sim_price_stable_aimd.csv";
		new SimulationGraphs().simulateUniformPlusWhales(INPUT_FILES, STABLE_OUTPUT_AIMD_LEARN, 13026000, 13026450, 450, 30, "0", new AimdEIP1559());
		final String BURST_OUTPUT_AIMD_LEARN = System.getProperty("user.dir")+"/output/sim_price_burst_aimd.csv";
		new SimulationGraphs().simulateUniformPlusWhales(INPUT_FILES, BURST_OUTPUT_AIMD_LEARN, 13025550, 13026000, 450, 30, "0", new AimdEIP1559());
	}
}
