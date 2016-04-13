package edu.stanford.slac.pinger.main;

import edu.stanford.slac.pinger.etl.valueRetriever.AllMetricsPerDayCSVBuilder;
import edu.stanford.slac.pinger.general.C;
import edu.stanford.slac.pinger.general.Logger;
import edu.stanford.slac.pinger.general.utils.OutputFileHandler;

public class AllMetricsPerDayMapper {

	
	public static void main(String[] args) {
		if (args.length == 0) {
			args = new String[]{
					"1",
					"C:/Users/Renan/Documents/PingERWholeData/_inputdir/", //input dir
					"C:/Users/Renan/Documents/transformed/", //output dir
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

		String year = args[3];
		String month = args[4];
		String day = args[5];
		boolean saveFileSize = true;
	
		if (saveFileSize) {
			C.filesSizeLog = transformedFilesDirectory + "file_sizes.csv";
		}
		
		String outputFileName = year+"-"+month+"-"+day+".csv";
		
		OutputFileHandler outputFileHandler = new OutputFileHandler(transformedFilesDirectory, outputFileName);
		AllMetricsPerDayCSVBuilder csvBuilder = new AllMetricsPerDayCSVBuilder(outputFileHandler, inputDir, year, month, day, saveFileSize);
		csvBuilder.run();
		outputFileHandler.writeContentAndClean();
		
		OutputFileHandler erelation = new OutputFileHandler("./", "ERelation.txt");
		erelation.addRow("YEAR;MONTH;OUTPUT_FILE\n"+year+";"+month+";"+outputFileHandler.getAbsoluteFilePath()+"\n");
		erelation.writeContentAndClean();
	}



}
