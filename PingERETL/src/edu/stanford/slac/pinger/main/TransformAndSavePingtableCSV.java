package edu.stanford.slac.pinger.main;

import java.util.ArrayList;
import java.util.HashMap;

import com.google.gson.JsonArray;

import edu.stanford.slac.pinger.etl.loader.local.FileHandler;
import edu.stanford.slac.pinger.etl.loader.local.FileHandlerHourly;
import edu.stanford.slac.pinger.etl.transformer.CSVProcessorFromFile;
import edu.stanford.slac.pinger.etl.transformer.PingHourlyMeasurementCSVBuilder;
import edu.stanford.slac.pinger.etl.transformer.PingMeasurementCSVBuilder;
import edu.stanford.slac.pinger.general.ErrorCode;
import edu.stanford.slac.pinger.general.Logger;
import edu.stanford.slac.pinger.general.utils.Utils;
import edu.stanford.slac.pinger.main.commons.MainCommons;

public class TransformAndSavePingtableCSV {

	public static void main(String[] args) {
		if (args.length == 0) {
			args = new String[]{
					"debug=0",
					//"transformFile=1,inputDir=C:/Users/Renan/Desktop/PingtableData2/,transformedFilesDirectory=./transformedFiles,monitorNode=pinger.slac.stanford.edu,metric=throughput,tick=daily,year=2003",
					//"transformFile=1,inputDir=C:/Users/Renan/Documents/Sample/Sample/,transformedFilesDirectory=./transformedFiles,metric=iqr,tick=hourly,year=1998,month=01",
					"transformFile=1,ftpData=1,inputDir=C:/Users/Renan/Documents/archive-average_rtt-100-by-node/archive-average_rtt-100-by-node~/tmp/average_rtt-100-by-node/,transformedFilesDirectory=./transformedFiles,metric=iqr,tick=hourly,year=2010,month=01",
			};
		}		
		start(args);
	}

	public static void transformFile(String arg) {
		String ags[] = arg.split(",");
		String inputDir = null;
		String monitorNode = null;	
		String metric = null;
		String tickParameter = null;
		String transformedFilesDirectory = null;
		String year = null;
		String month = null;
		boolean ftpData = false;
		for (String ag : ags) {
			if (ag.contains("metric")) {
				metric = ag.replace("metric=", "").trim();
			} else if (ag.contains("inputDir")) {
				inputDir = ag.replace("inputDir=", "").trim();
			} else if (ag.contains("tick")) {
				tickParameter = ag.replace("tick=", "").trim();
			} else if (ag.contains("year")) {
				year = ag.replace("year=", "").trim();
			} else if (ag.contains("month")) {
				month = ag.replace("month=", "").trim();
			} else if (ag.contains("monitorNode")) {
				monitorNode = ag.replace("monitorNode=", "").trim();				
			} else if (ag.contains("ftpData=1")) {
				ftpData = true;
			} else if (ag.contains("transformedFilesDirectory")) {
				transformedFilesDirectory = ag.replace("transformedFilesDirectory=", "").trim();				
			} 
		}
		
		if (tickParameter.equals("hourly")) {
			startHourly(transformedFilesDirectory, inputDir, metric, year, month, tickParameter, ftpData);
		} else {

			FileHandler outputFileHandler = new FileHandler(transformedFilesDirectory, tickParameter, metric, year, monitorNode);
			JsonArray monitoredArr = null;
			try {
				monitoredArr = Utils.getMonitorMonitoredJSON().get(monitorNode).getAsJsonArray();
			} catch (Exception e) {
				Logger.error("Could not find monitor node " + monitorNode, ErrorCode.MONITOR_DETAILS);
				return;
			}
			CSVProcessorFromFile csvProcessor = new CSVProcessorFromFile(inputDir, year, metric, tickParameter, monitorNode); 

			ArrayList<HashMap<String,HashMap<String, String>>> monthsInAnYearMaps = csvProcessor.getMonthsInAnYearMaps();

			for (HashMap<String,HashMap<String, String>> map : monthsInAnYearMaps) {
				PingMeasurementCSVBuilder measurement = new PingMeasurementCSVBuilder(outputFileHandler, monitorNode, map, monitoredArr, metric, tickParameter, year, null);
				measurement.run();
			}
			outputFileHandler.writeContentAndClean();

		}

	}

	public static void startHourly(String transformedFilesDirectory, String inputDir, String metric, String year, String month, String tick, boolean ftpData) {
		FileHandlerHourly outputFileHandler = new FileHandlerHourly(transformedFilesDirectory, tick, metric, year, month);
		if (ftpData) {
			
		} else {
			PingHourlyMeasurementCSVBuilder csvBuilder = new PingHourlyMeasurementCSVBuilder(outputFileHandler, inputDir, year, month, metric, tick);
			csvBuilder.run();
		}
		outputFileHandler.writeContentAndClean();
	}

	public static void start(String[] args) {	
		try {
			long t1 = System.currentTimeMillis();
			for (String arg : args) {
				if (arg.contains("debug")) {
					MainCommons.debug(arg);
				} if (arg.contains("transformFile=1")) {
					transformFile(arg);
				}
			}
			long t2 = System.currentTimeMillis();
			Logger.log("Finally done! It took " + ((t2-t1)/1000.0/60.0) + " minutes.");
		} catch (Exception e) {
			Logger.log("start", e, "errors");
		}
	}

}
