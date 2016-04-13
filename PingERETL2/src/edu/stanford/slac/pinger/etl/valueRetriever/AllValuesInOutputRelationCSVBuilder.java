package edu.stanford.slac.pinger.etl.valueRetriever;

import com.google.gson.JsonObject;

import edu.stanford.slac.pinger.beans.ContinentBean;
import edu.stanford.slac.pinger.beans.MetricBean;
import edu.stanford.slac.pinger.general.C;
import edu.stanford.slac.pinger.general.ErrorCode;
import edu.stanford.slac.pinger.general.Logger;
import edu.stanford.slac.pinger.general.utils.MeasurementUtils;
import edu.stanford.slac.pinger.general.utils.Utils;
import edu.stanford.slac.pinger.general.utils.OutputFileHandler;

public class AllValuesInOutputRelationCSVBuilder {

	private static final int SOURCE_NODE_INDEX_IN_CSV_FILE = 0;
	private static final int DESTINATION_NODE_INDEX_IN_CSV_FILE = 1;
	private static final int FIRST_HOUR_INDEX_IN_CSV_FILE = 2;
	private static final int LAST_HOUR_INDEX_IN_CSV_FILE = 24;
	private static final int SIZE_OF_NO_DATA_FILE = 63; //Experimentally, it was seen that a file that only has header, but no data has 63 bytes.
	private OutputFileHandler outputFileHandler;
	private String day, month, metric;
	private String metricId;
	private String year, inputDir;
	
	private static final String OUTPUT_SEP = ";";
	
	private boolean saveFileSize;
	
	private JsonObject hours = Utils.getJsonAsObject(C.HOURS_JSON);

	public AllValuesInOutputRelationCSVBuilder(OutputFileHandler outputFileHandler, String inputDir, String year, String month, String day, String metric, boolean saveFileSize) {
		this.inputDir = inputDir;
		this.outputFileHandler = outputFileHandler;
		this.month = month;
		this.day = day;
		this.year = year;
		this.metric = metric;
		this.saveFileSize = saveFileSize;
		
		MetricBean metricBean = null;
		try {
			metricBean = MeasurementUtils.mapMetricBean.get(metric);
			this.metricId = metricBean.getId()+"";
		} catch (Exception e) {
			Logger.error("Error when getting map.", ErrorCode.MAP_ERROR);
			System.exit(-1);
		}
	}

	public void run() {
		String filePath = inputDir + Utils.getFileNameHourlyFtpData(metric, year, month, day);
		String content = null;
		if (saveFileSize) {
			content = Utils.readGZipFileAndSaveFileSize(filePath, metric, year, month, day);
		} else {
			content = Utils.readGZipFileWithouSavingFileSize(filePath);
		}
		if (content==null || content.length()==SIZE_OF_NO_DATA_FILE) {
			return;
		}
		String lines[] = content.split("\n");
		int countDots = 0;
		
		for (int i = 1; i < lines.length; i++) {
			String separetedValues[] = lines[i].split("\\s");
			if (separetedValues.length==0) {
				continue;
			}
			String sourceNode = null;
			try {
				sourceNode = separetedValues[SOURCE_NODE_INDEX_IN_CSV_FILE]; 
			} catch (Exception e) {
				Logger.log("Could not retrieve Source node from " + filePath, e);
				continue;
			}
			JsonObject sourceNodeDetails = null;
			try {
				sourceNodeDetails = Utils.getNodeDetails().get(sourceNode)
						.getAsJsonObject();
			} catch (Exception e) {continue;}		
			JsonObject countriesJson = Utils.getCountriesJson();
			int sourceId = 0, sourceCountryId = 0, sourceContinentId = 0;
			String sourceCountryName = null;
			try {
				sourceId = Integer.parseInt(sourceNodeDetails.get("NodeID")
						.getAsString());
			} catch (Exception e) {continue;}
			try {
				sourceCountryName = sourceNodeDetails.get("Country").getAsString();
				sourceCountryId = countriesJson.get(sourceCountryName).getAsJsonObject()
						.get("isoNumeric").getAsInt();
			} catch (Exception e) {}
			try {
				String continentSourceInitials = countriesJson.get(sourceCountryName)
						.getAsJsonObject().get("continent").getAsString();
				sourceContinentId = Integer.parseInt(ContinentBean.MAP.get(
						continentSourceInitials).getId());
			} catch (Exception e) {}

			String destinationNode = null;
			try {
				destinationNode = separetedValues[DESTINATION_NODE_INDEX_IN_CSV_FILE];
			} catch (Exception e) {
				Logger.log("Could not retrieve Destination Node node from " + filePath, e);
				continue;
			}
			
			JsonObject destinationNodeDetails = null; String destinationNodeName = null;
			int destinationId = 0, destinationCountryId = 0, destinationContinentId = 0;			
			try {
				destinationNodeDetails = Utils.getNodeDetails().get(destinationNode)
						.getAsJsonObject();
			} catch (Exception e) {continue;}
			try {
				destinationId = Integer.parseInt(destinationNodeDetails.get(
						"NodeID").getAsString());
			} catch (Exception e) {continue;}
			try {
				destinationNodeName = destinationNodeDetails.get("Country")
						.getAsString();
			} catch (Exception e) {}
			try {
				destinationCountryId = countriesJson.get(destinationNodeName)
						.getAsJsonObject().get("isoNumeric").getAsInt();
			} catch (Exception e) {}

			try {
				String destinationContinentInitials = countriesJson.get(destinationNodeName)
						.getAsJsonObject().get("continent").getAsString();
				destinationContinentId = Integer.parseInt(ContinentBean.MAP
						.get(destinationContinentInitials).getId());
			} catch (Exception e) {}	

			
			int hour = 0;			
			for (int j = FIRST_HOUR_INDEX_IN_CSV_FILE; j <= LAST_HOUR_INDEX_IN_CSV_FILE; j++) {
				if (separetedValues[j] != null) {
					 if (!separetedValues[j].equals(".")) {
						instantiate(
							separetedValues[j],
							sourceId,
							sourceCountryId,
							sourceContinentId,
							destinationId,
							destinationCountryId,
							destinationContinentId,
							hour
						);
					 } else {
						 countDots++;
					 }
				}
				hour++;
			}
		}
		if (saveFileSize)
			Utils.openAppendContentAndClose(metric+","+year+","+month+","+day+","+Long.toString(C.BI+countDots), C.filesSizeLog);
		//this is to count how many dots there are in a file for a given combination of parameters.
		//The right number of dots of a given combination of parameters is size - 1 billion


	}

	/**
	 * This is the longest method
	 * @param metric
	 * @param metricURI
	 * @param packetSize
	 * @param tickParameter
	 */
	private void instantiate(
			String value,
			int sourceId,
			int sourceCountryId,
			int sourceContinentId,
			int destinationId,
			int destinationCountryId,
			int destinationContinentId,
			int hour
		) {
		
		String hourStr = (hour<10)?"0"+hour:hour+"";
		String timeLabel =  year + "-" + month + "-" +  day + "-" + hourStr;
		long timeFromHoursJson = 0;
		try {
			timeFromHoursJson = Long.parseLong(hours.get(timeLabel).getAsString());
		} catch (Exception e) {
			Logger.error(timeFromHoursJson + " is not a valid time.", ErrorCode.TIME_FORMAT);
			return;
		}


		String row = 
				sourceId + OUTPUT_SEP +
				sourceCountryId  + OUTPUT_SEP +
				sourceContinentId  + OUTPUT_SEP +
				destinationId  + OUTPUT_SEP +
				destinationCountryId + OUTPUT_SEP +
				destinationContinentId + OUTPUT_SEP +
				metricId  + OUTPUT_SEP +
				timeFromHoursJson + OUTPUT_SEP +
				value + OUTPUT_SEP +
				year + "\n";
		
		outputFileHandler.addRow(row);
	}

}
