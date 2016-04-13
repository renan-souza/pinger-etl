package edu.stanford.slac.pinger.etl.valueRetriever;

import java.util.HashMap;

import com.google.gson.JsonObject;

import edu.stanford.slac.pinger.beans.ContinentBean;
import edu.stanford.slac.pinger.beans.MetricBean;
import edu.stanford.slac.pinger.beans.PingERMeasurementAllMetricsBean;
import edu.stanford.slac.pinger.general.C;
import edu.stanford.slac.pinger.general.ErrorCode;
import edu.stanford.slac.pinger.general.Logger;
import edu.stanford.slac.pinger.general.utils.MeasurementUtils;
import edu.stanford.slac.pinger.general.utils.OutputFileHandler;
import edu.stanford.slac.pinger.general.utils.Utils;

public class AllMetricsPerDayCSVBuilder {

	private static final int SOURCE_NODE_INDEX_IN_CSV_FILE = 0;
	private static final int DESTINATION_NODE_INDEX_IN_CSV_FILE = 1;
	private static final int FIRST_HOUR_INDEX_IN_CSV_FILE = 2;
	private static final int LAST_HOUR_INDEX_IN_CSV_FILE = 24;
	private static final int SIZE_OF_NO_DATA_FILE = 63; //Experimentally, it was seen that a file that only has header, but no data has 63 bytes.




	private OutputFileHandler outputFileHandler;
	private String year, month, day;
	private String inputDir;

	private static final String SEP = ",";
	
	HashMap<PingERMeasurementAllMetricsBean, String[]> map = new HashMap<>();

	private boolean saveFileSize;

	private JsonObject hours = Utils.getJsonAsObject(C.HOURS_JSON);

	public AllMetricsPerDayCSVBuilder(OutputFileHandler outputFileHandler, String inputDir, String year, String month, String day, boolean saveFileSize) {
		this.inputDir = inputDir;
		this.outputFileHandler = outputFileHandler;
		this.month = month;
		this.day = day;
		this.year = year;
		this.saveFileSize = saveFileSize;
	}

	public void run() {

		long t1 = System.currentTimeMillis();
		for (String metric : MeasurementUtils.METRICS) {
			process(metric);
		}
		long t2 = System.currentTimeMillis();
		System.out.println("It took to process all 16 files " + (t2-t1)/1000.0);
		
		
		for (PingERMeasurementAllMetricsBean bean : map.keySet()) {
			Integer[] sourceCountryContinentIds = getNodeCountryAndContinent(bean.getSourceNodeId());
			if (sourceCountryContinentIds==null) continue;
			Integer[] destinationCountryContinentIds = getNodeCountryAndContinent(bean.getDestinationNodeId());
			if (destinationCountryContinentIds==null) continue;
			
			StringBuilder sb = new StringBuilder();
			sb.append(bean.getSourceNodeId()+SEP);
			sb.append(sourceCountryContinentIds[0]+SEP);
			sb.append(sourceCountryContinentIds[1]+SEP);
			sb.append(bean.getDestinationNodeId()+SEP);
			sb.append(destinationCountryContinentIds[0]+SEP);
			sb.append(destinationCountryContinentIds[1]+SEP);
			sb.append(bean.getTimeId()+SEP);
			
			for (String metricValue : map.get(bean)) {
				if (metricValue==null || metricValue.length()==0)
					sb.append(SEP);
				else
					sb.append(metricValue+SEP);
			}
			
			outputFileHandler.addRow(Utils.removeLastCharacterFromString(sb.toString())+"\n");
		}
		long t3 = System.currentTimeMillis();
		System.out.println("It took to write all contents " + (t3-t2)/1000.0);
	}

	JsonObject countriesJson = Utils.getCountriesJson();
	public HashMap<Integer, Integer[]> nodesCountryAndContinent = new HashMap<>();
	public HashMap<Integer, JsonObject> nodeDetailsById = new HashMap<>();

	public Integer[] getNodeCountryAndContinent(int nodeId) {
		if (nodesCountryAndContinent.containsKey(nodeId))
			return nodesCountryAndContinent.get(nodeId);
		else {
			try {
				String countryName = nodeDetailsById.get(nodeId).get("Country").getAsString();

				int cuntryId = countriesJson.get(countryName).getAsJsonObject()
						.get("isoNumeric").getAsInt();

				String continentSourceInitials = countriesJson.get(countryName)
						.getAsJsonObject().get("continent").getAsString();
				int continentId = Integer.parseInt(ContinentBean.MAP.get(
						continentSourceInitials).getId());

				Integer[] ids = new Integer[] {
						cuntryId,
						continentId
				};
				nodesCountryAndContinent.put(nodeId, ids);
				return ids;
			} catch (Exception e) {return null;}
		}
	}

	public void process(String metric) {
		MetricBean metricBean = null;
		try {
			metricBean = MeasurementUtils.mapMetricBean.get(metric);
		} catch (Exception e) {
			Logger.error("Error when getting map.", ErrorCode.MAP_ERROR);
			System.exit(-1);
		}

		int metricId = metricBean.getId();

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
			int sourceId = 0;
			JsonObject sourceNodeDetails = null;
			try {
				sourceNodeDetails = Utils.getNodeDetails().get(sourceNode)
						.getAsJsonObject();
				sourceId = Integer.parseInt(sourceNodeDetails.get("NodeID")
						.getAsString());
			} catch (Exception e) {continue;}
			
			if (!nodeDetailsById.containsKey(sourceId))
				nodeDetailsById.put(sourceId, sourceNodeDetails);
			
			String destinationNode = null;
			try {
				destinationNode = separetedValues[DESTINATION_NODE_INDEX_IN_CSV_FILE];
			} catch (Exception e) {
				Logger.log("Could not retrieve Destination Node node from " + filePath, e);
				continue;
			}

			int destinationId = 0;
			JsonObject destinationNodeDetails = null;
			try {
				destinationNodeDetails = Utils.getNodeDetails().get(destinationNode)
						.getAsJsonObject();
				destinationId = Integer.parseInt(destinationNodeDetails.get(
						"NodeID").getAsString());
			} catch (Exception e) {continue;}
			
			if (!nodeDetailsById.containsKey(destinationId))
				nodeDetailsById.put(destinationId, destinationNodeDetails);
			
			int hour = 0;			
			for (int j = FIRST_HOUR_INDEX_IN_CSV_FILE; j <= LAST_HOUR_INDEX_IN_CSV_FILE; j++) {
				if (separetedValues[j] != null) {
					if (!separetedValues[j].equals(".")) {
						instantiate(
								separetedValues[j],
								sourceId,
								destinationId,
								metricId,
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



	private void instantiate(
			String value,
			int sourceId,
			int destinationId,
			int metricId,
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

		PingERMeasurementAllMetricsBean bean = new PingERMeasurementAllMetricsBean();
		bean.setSourceNodeId(sourceId);
		bean.setDestinationNodeId(destinationId);
		bean.setTimeId(timeFromHoursJson);

		if (!map.containsKey(bean)) {
			map.put(bean, new String[16]);
		}

		String metricValues[] = map.get(bean);
		metricValues[metricId-1] = value;
		map.replace(bean, metricValues);


	}

}
