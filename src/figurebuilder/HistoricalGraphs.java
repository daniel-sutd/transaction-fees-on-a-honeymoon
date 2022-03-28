package figurebuilder;

public class HistoricalGraphs {

	final static String[] INPUT_FILES = {
			System.getProperty("user.dir")+"/input/mainnet_fees_13025550_13026450.csv"
	};
	
	public void createHistoricalGraph(String[] files, String outputFileName, int start, int end, int granularity, int smoothRange, String empty, int valIndex, int valScale) {
		double[][] graphArray = DataProcessor.longTermAverageValue(files, outputFileName, start, end, granularity, smoothRange, empty, valIndex, valScale);
		DataProcessor.writeFile("block,value\n",outputFileName, graphArray, empty);
	}
	
	public static void main(String[] a) {
		// stable: price, basefee, block size, block size after median filter
		final String STABLE_BID_OUTPUT = System.getProperty("user.dir")+"/output/avg_price_stable.csv";
		new HistoricalGraphs().createHistoricalGraph(INPUT_FILES, STABLE_BID_OUTPUT, 13026000, 13026450, 450, 0, "0", 8, 1000000000);
		final String STABLE_BASEFEE_OUTPUT = System.getProperty("user.dir")+"/output/avg_basefee_stable.csv";
		new HistoricalGraphs().createHistoricalGraph(INPUT_FILES, STABLE_BASEFEE_OUTPUT, 13026000, 13026450, 450, 0, "nan", 3, 1000000000);
		final String STABLE_BLOCKSIZE_OUTPUT = System.getProperty("user.dir")+"/output/avg_blocksize_stable.csv";
		new HistoricalGraphs().createHistoricalGraph(INPUT_FILES, STABLE_BLOCKSIZE_OUTPUT, 13026000, 13026450, 450, 0, "0", 2, 1);
		final String STABLE_BLOCKSIZE_OUTPUT_SMOOTHED = System.getProperty("user.dir")+"/output/avg_blocksize_stable_smoothed.csv";
		new HistoricalGraphs().createHistoricalGraph(INPUT_FILES, STABLE_BLOCKSIZE_OUTPUT_SMOOTHED, 13026000, 13026450, 450, 30, "0", 2, 1);
		
		// burst: price, basefee, block size, block size after median filter
		final String BURST_BID_OUTPUT = System.getProperty("user.dir")+"/output/avg_price_burst.csv";
		new HistoricalGraphs().createHistoricalGraph(INPUT_FILES, BURST_BID_OUTPUT, 13025550, 13026000, 450, 0, "0", 8, 1000000000);
		final String BURST_BASEFEE_OUTPUT = System.getProperty("user.dir")+"/output/avg_basefee_burst.csv";
		new HistoricalGraphs().createHistoricalGraph(INPUT_FILES, BURST_BASEFEE_OUTPUT, 13025550, 13026000, 450, 0, "nan", 3, 1000000000);
		final String BURST_BLOCKSIZE_OUTPUT = System.getProperty("user.dir")+"/output/avg_blocksize_burst.csv";
		new HistoricalGraphs().createHistoricalGraph(INPUT_FILES, BURST_BLOCKSIZE_OUTPUT, 13025550, 13026000, 450, 0, "0", 2, 1);
		final String BURST_BLOCKSIZE_OUTPUT_SMOOTHED = System.getProperty("user.dir")+"/output/avg_blocksize_burst_smoothed.csv";
		new HistoricalGraphs().createHistoricalGraph(INPUT_FILES, BURST_BLOCKSIZE_OUTPUT_SMOOTHED, 13025550, 13026000, 450, 30, "0", 2, 1);
	}
}
