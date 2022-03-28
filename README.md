# Transaction Fees on a Honeymoon
This repository contains the Java code to (re)produce the figures and results table in the paper ["Transaction Fees on a Honeymoon: Ethereum's EIP-1559 One Month Later"](https://arxiv.org/abs/2110.04753).

## Dependencies
We use [SSJ](https://github.com/umontreal-simul/ssj) to draw pseudorandom numbers from the Pareto distribution. 

## Structure & Data
There are three subfolders, `src`, `input`, and `output`, which contain the source code, input data files, and output data files, respectively. The `output` folder contains sample output files for each of the relevant methods. These files are not required by any of the methods, but the `temp` folder in `output` should not be deleted or else the `SimulationEvaluator` will fail when trying to create temporary result files. 

Inside the `src` folder, the files that create the graph and table data can be found in the `figurebuilder` folder. The update rules can be found in the `updaterules` subfolder. Four update rules have been included: standard EIP-1559 (with d=0.125), "slow" EIP-1559 (with d=0.0625), "fast" EIP-1559 (with d=0.25), and EIP-1599 with AIMD updates. Each of these implements the `UpdateRule` interface.

To keep the set of the dataset manageable, the `input` folder only contains a snippet of transaction data, namely those transactions between the blocks at height 13025550 and at height 13026450. The data used to create this snippet (and the other graphs in the paper) is publicly available. To create graphs that use a wider range of data as input, the relevant blockchain data must be converted to the format in the file `mainnet_fees_13025550_13026450.csv`. It can then be used as input for the methods discussed below (be mindful to also change the data "start" and "end" indices in the `main` functions).

Below, we describe the class files in the `src/figurebuilder` folder in more detail.

### DataProcessor

This class contains several methods that are used in the other classes, in particular to compute averages over batches of blocks, and to apply a median filter over the result.

### HistoricalGraphs

The purpose of this class is to visualize the input data set. The most important method in this class is `createHistoricalGraph`, which divides a portion of the dataset into batches/bins and takes averages over the value in those batches. Afterwards, a median filter can be applied on the output values. The  `createHistoricalGraph` method takes the following parameters as input:

- `files`, a string array that contains the paths of the input files that contain the transaction data
- `outputFileName`, the path of the output file
- `start`, the index of the first block to consider (blocks before this are ignored)
- `end`, the index of the last block to consider (blocks after this are ignored) 
- `granularity`, the number of batches/rows in the output file
- `smoothRange`, the halfwidth of the median filter
- `empty`, a string that indicates what is written if there are no transactions for the blocks in a given batch/output row
- `valIndex`, the index of the input file column whose values are used for the output file
- `valScale`, the scale factor applied to each input values

The output data file can then be turned into a graph using, e.g., gnuplot or pgfplots. Some sample executions of `createHistoricalGraph` can be found in the `main` function. 

### SimulationGraphs

The purpose of this class is to visualize simulation experiments. The most important method in this class is `simulateUniformPlusWhales`, which simulates user valuations by drawing them from a mixture of the uniform and Pareto distributions. The parameters of this distribution are based on the input data. This class also contains methods for purely uniformly and exponentially distributed valuations in the comments, but these were not used for any of the figures in the paper. Given the simulated valuations, the method updates the base fees using `rule`, which can be any desired update rule that uses the `UpdateRule` interface. The `simulateUniformPlusWhales` method takes the following parameters as input:

- `files`, `outputFileName`, `start`, `end`, `granularity`, `smoothRange`, and `empty` as before
- `rule`, an instance of the desired update rule

The output data file can then be turned into a graph using, e.g., gnuplot or pgfplots. Some example of how to run this function are in the bottom of the `main` function. 

### SimulationEvaluator

The purpose of this class is to repeatedly perform simulation experiments and compute the performance of the given update rule in terms of two metrics: the average block size and the fraction of "full" blocks. The most important method in the class is `evaluatePerformance`. The default number of experiments performed by this method is set to N=20. Otherwise, it takes the same parameters as input as `simulateUniformPlusWhales` in the `SimulationGraphs` class. The output is written to the command line. 