package figurebuilder;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import umontreal.ssj.rng.MRG31k3p;
import umontreal.ssj.rng.RandomStream;

public class DataProcessor {
	RandomStream r = new MRG31k3p();
	
	public static void addFileToMap(Map<Integer, List<Double[]>> map, String fileName, int valIndex, int valueScale, int start, int end) {
		try {
			BufferedReader br = new BufferedReader(new FileReader(fileName));
		    String line;

		    while ((line = br.readLine()) != null) {
		    	String[] data = line.split(",");
		    	try {
		    		int block = Integer.parseInt(data[0]);
		    		if(block >= start && block <= end) {
			    		List<Double[]> list;
			    		if(map.containsKey(block)) {
			    			list = map.get(block);
			    		} else {
			    			list = new ArrayList<Double[]>();
			    			map.put(block, list);
			    		}
			    		int index = Integer.parseInt(data[4]);
				    	if(index > 0) {
				    		double value = 1. * Double.parseDouble(data[valIndex]) / valueScale;
				    		double weight = Double.parseDouble(data[6]) / 1000;
				    		list.add(new Double[] {weight, value});
				    	}
		    		}
		    	} catch(Exception e) {
		    		
		    	}
		    }
		    br.close();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public static double[][] averages(Map<Integer, List<Double[]>> map) {
		int[] minMax = minMax(map.keySet());
		double[][] avgArray = new double[minMax[1]-minMax[0]+1][2];
		for(int i=minMax[0];i<=minMax[1];i++) {
			double sum = 0;
			int n = 0;
			if(map.containsKey(i)) {
			List<Double[]> list = map.get(i);
				for(Double[] j : list) {
					sum += j[0] * j[1];
					n += j[0];
				}
			}
			if(n == 0) {
				avgArray[i-minMax[0]][0] = i;
				avgArray[i-minMax[0]][1] = 0;
			} else {
				avgArray[i-minMax[0]][0] = i;
				avgArray[i-minMax[0]][1] = sum/n;
			}
		}
		return avgArray;
	}
	
	public static double rollingMedian(double[][] x, int start, int end) {
		List<Double> values = new ArrayList<Double>();
		for(int i=start;i<=end;i++) {
			if(i >= 0 && i < x.length) {
				values.add(x[i][1]);
			}
		}
		Collections.sort(values);
		int n = values.size();
		if(n == 0) return 0;
//		System.out.println(n);
		if(n % 2 == 1) {
			return values.get((int) (n-0.5)/2);
		}
		return (values.get(n/2) + values.get(n/2+1))/2;
	}
	
	public static double[][] medianSmoother(double[][] x, int halfwidth) {
		double[][] y = new double[x.length][x[0].length];
		for(int i=0;i<x.length;i++) {
			y[i][0] = x[i][0];
			y[i][1] = rollingMedian(x, i-halfwidth,i+halfwidth);
		}
		return y;
	}
	
	
	public static int[] minMax(Set<Integer> x) {
		int max = Integer.MIN_VALUE;
		int min = Integer.MAX_VALUE;
		
		for(int i : x) {
			if(i < min) min = i;
			if(i > max) max = i;
		}
		return new int[] {min, max};
	}
	
	public static void writeFile(String header, String outputFileName, double[][] graphArray, String empty) {
		FileWriter writer;
		try {
			writer = new FileWriter(outputFileName);
			writer.append(header);

			for (int j = 0; j < graphArray.length; j++) {
				writer.append(String.valueOf(graphArray[j][0]));
				for (int k = 1; k < graphArray[j].length; k++) {
					if(graphArray[j][k] == 0) writer.append(","+empty);
					else writer.append(","+String.valueOf(graphArray[j][k]));
				}
			    writer.append("\n");
			}
			writer.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	

	public static double[][] longTermAverageValue(String[] files, String outputFileName, int start, int end, int granularity, int smoothRange, String empty, int valIndex, int valScale) {
		long startLoadTime = System.currentTimeMillis();
		Map<Integer, List<Double[]>> map = new TreeMap<Integer, List<Double[]>>();
		for(int i=0;i<files.length;i++) {
			System.out.print("reading "+files[i]+"... ");
			addFileToMap(map, files[i], valIndex, valScale, start, end);
			System.out.println("done");
		}
		
		double[][] avgArray = averages(map);
		int[] minMax = minMax(map.keySet());
		
		System.out.println(avgArray.length);
		System.out.println(minMax[0]+", "+minMax[1]);
		
		int nX = granularity;
		double cSum = 0;
		int cIdx = 0;
		double[][] graphArray = new double[nX-1][2];
		
		for(int i=minMax[0];i<=minMax[1];i++) {
			if(i > minMax[0] + (cIdx+1) * (minMax[1]-minMax[0]) / nX) {
				graphArray[cIdx][0] = minMax[0] + (cIdx + 0.5) * (minMax[1]-minMax[0]) / nX;
				graphArray[cIdx][1] = cSum * nX /(minMax[1]-minMax[0]);
				cIdx += 1;
				cSum = 0;
			}
			cSum += avgArray[i-minMax[0]][1];
		}
		
		if(smoothRange > 0) {
			double[][] smoothedArray = medianSmoother(graphArray, smoothRange);
			graphArray = smoothedArray;
		}
		
		long endLoadTime = System.currentTimeMillis();
		System.out.println("processing time: "+1.*(endLoadTime-startLoadTime)/1000+" seconds");
		
		return graphArray;
	}
}
