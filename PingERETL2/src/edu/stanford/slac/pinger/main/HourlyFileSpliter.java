package edu.stanford.slac.pinger.main;

import edu.stanford.slac.pinger.etl.valueRetriever.AllValuesInOutputRelationCSVBuilder;
import edu.stanford.slac.pinger.general.C;
import edu.stanford.slac.pinger.general.Logger;
import edu.stanford.slac.pinger.general.utils.OutputFileHandler;

public class HourlyFileSpliter {

	private static final char SEP = '_';
	private static final String HEADER = "SOURCE_ID;S_COUNTRY_ID;S_CONTINENT_ID;DESTINATION_ID;D_COUNTRY_ID;D_CONTINENT_ID;METRIC_ID;TIME_ID;VALUE;YEAR";
	
	public static void main(String[] args) {
		if (args.length == 0) {
			args = new String[]{
					"1",
					"C:/Users/Renan/Documents/PingERWholeData/_inputdir/", //input dir
					//"C:/Users/Renan/Documents/transformed/", //output dir
					"./",
					"throughput", //metric
					"2010", //year
					"01",	//month
					"01",	//day
			};
		}		
		try {
			int debugLevel = Integer.parseInt(args[0]);
			Logger.setDebugLevel(debugLevel);
			long t1 = System.currentTimeMillis();
			start(args);
			long t2 = System.currentTimeMillis();
			Logger.log("Finally done! It took " + ((t2-t1)/1000.0) + " seconds.");
		} catch (Exception e) {
			Logger.log("start", e, "errors");
		}
	}
	
	public static void start(String[] args) {
		String inputDir = args[1];
		String transformedFilesDirectory = args[2];
		String metric = args[3];
		
		String year = args[4];
		String month = args[5];
		String day = args[6];
		boolean saveFileSize = true;
	
		if (saveFileSize) {
			C.filesSizeLog = transformedFilesDirectory + "file_sizes.csv";
		}
		
		//String outputFilePath =  transformedFilesDirectory + year + SEP + month + SEP + day + SEP + metric + ".csv";
		String outputFilePath =  "ERelation.txt";
		OutputFileHandler outputFileHandler = new OutputFileHandler(transformedFilesDirectory, outputFilePath);
		AllValuesInOutputRelationCSVBuilder csvBuilder = new AllValuesInOutputRelationCSVBuilder(outputFileHandler, inputDir, year, month, day, metric, saveFileSize);
		outputFileHandler.addRow(HEADER+"\n");
		csvBuilder.run();
		outputFileHandler.writeContentAndClean();
	}



}
