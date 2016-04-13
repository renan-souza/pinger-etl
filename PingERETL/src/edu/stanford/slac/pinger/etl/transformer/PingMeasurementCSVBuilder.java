package edu.stanford.slac.pinger.etl.transformer;

import java.util.HashMap;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import edu.stanford.slac.pinger.beans.ContinentBean;
import edu.stanford.slac.pinger.beans.MetricBean;
import edu.stanford.slac.pinger.beans.PingMeasurementBean;
import edu.stanford.slac.pinger.etl.loader.local.FileHandler;
import edu.stanford.slac.pinger.general.C;
import edu.stanford.slac.pinger.general.ErrorCode;
import edu.stanford.slac.pinger.general.Logger;
import edu.stanford.slac.pinger.general.utils.MeasurementUtils;
import edu.stanford.slac.pinger.general.utils.Utils;

public class PingMeasurementCSVBuilder {

	private FileHandler fileHandler;
	private String monitoring, metric, tickParameter;
	private JsonArray monitoredNodes;
	private HashMap<String, HashMap<String, String>> map;

	private JsonObject days = Utils.getJsonAsObject(C.DAYS_JSON);

	public PingMeasurementCSVBuilder(FileHandler fileHandler,
			String monitoring, HashMap<String, HashMap<String, String>> map,
			JsonArray monitoredNodes, String metric,
			String tickParameter, String year, String month) {
		this.fileHandler = fileHandler;
		this.map = map;
		this.monitoredNodes = monitoredNodes;
		this.monitoring = monitoring;
		this.metric = metric;
		this.tickParameter = tickParameter;
	}

	public void run() {

		JsonObject sourceNodeDetails = Utils.getNodeDetails().get(monitoring)
				.getAsJsonObject();
		JsonObject countriesJson = Utils.getCountriesJson();
		int sourceId = 0, sourceCountryId = 0, sourceContinentId = 0;
		try {
			sourceId = Integer.parseInt(sourceNodeDetails.get("NodeID")
					.getAsString());
			String countryName = sourceNodeDetails.get("Country").getAsString();
			sourceCountryId = countriesJson.get(countryName).getAsJsonObject()
					.get("isoNumeric").getAsInt();
			String continentInitials = countriesJson.get(countryName)
					.getAsJsonObject().get("continent").getAsString();
			sourceContinentId = Integer.parseInt(ContinentBean.MAP.get(
					continentInitials).getId());
		} catch (Exception e) {
			Logger.error("Tried to get details about " + monitoring, ErrorCode.MONITOR_DETAILS);
			return;
		}

		for (JsonElement monitoredEl : monitoredNodes) {
			String monitored = monitoredEl.getAsString();
			HashMap<String, String> timeVal = map.get(monitored);
			if (timeVal == null)
				continue; // Some nodes that are said to be monitored by the
							// monitoring host do not have data on the map
							// retrieved from Pintable TSV.

			JsonObject destinationNodeDetails = null;
			int destinationId = 0, destinationCountryId = 0, destinationContinentId = 0;
			try {
				destinationNodeDetails = Utils.getNodeDetails().get(monitored)
						.getAsJsonObject();
				destinationId = Integer.parseInt(destinationNodeDetails.get(
						"NodeID").getAsString());
				String countryName = destinationNodeDetails.get("Country")
						.getAsString();
				destinationCountryId = countriesJson.get(countryName)
						.getAsJsonObject().get("isoNumeric").getAsInt();
				String continentInitials = countriesJson.get(countryName)
						.getAsJsonObject().get("continent").getAsString();
				destinationContinentId = Integer.parseInt(ContinentBean.MAP
						.get(continentInitials).getId());

			} catch (Exception e) {
				Logger.error("Tried to get details about " + monitored, ErrorCode.MONITOR_DETAILS);
				continue;
			}
			instantiate(fileHandler, timeVal, sourceId, sourceCountryId,
					sourceContinentId, destinationId, destinationCountryId,
					destinationContinentId, tickParameter);
		}
	}

	/**
	 * It was verified that this is the function that takes the longest. For
	 * each monitored host, this function currently takes approximately 9
	 * seconds to run (last365days). This function should be especially
	 * parallelized.
	 * 
	 * @param timeValue
	 * @param metric
	 * @param metricURI
	 * @param packetSize
	 * @param tickParameter
	 */
	private void instantiate(FileHandler fileHandler,
			HashMap<String, String> timeValue, int sourceId,
			int sourceCountryId, int sourceContinentId, int destinationId,
			int destinationCountryId, int destinationContinentId,
			String tickParameter) {
		HashMap<String, MetricBean> mapMetricBean = null;
		try {
			mapMetricBean = MeasurementUtils.mapMetricBean;
		} catch (Exception e) {
			Logger.error("Error when getting map.", ErrorCode.MAP_ERROR);
			System.exit(-1);
		}
		MetricBean metricBean = null;
		try {
			metricBean = mapMetricBean.get(metric);
		} catch (Exception e) {
			Logger.error("Error when getting Metric Bean.", ErrorCode.MAP_ERROR);
			System.exit(-1);
		}

		for (String time : timeValue.keySet()) {

			PingMeasurementBean pmb = new PingMeasurementBean();
			// pmb.setId(sequence++);
			pmb.setMetricId(metricBean.getId());

			pmb.setSourceNodeId(sourceId);
			pmb.setSourceCountryId(sourceCountryId);
			pmb.setSourceContinentId(sourceContinentId);
			
			pmb.setDestinationNodeId(destinationId);
			pmb.setDestinationCountryId(destinationCountryId);
			pmb.setDestinationContinentId(destinationContinentId);
			
			long timeFromDaysJson = 0;
			try {
				timeFromDaysJson = Long.parseLong(days.get(time).getAsString());
			} catch (Exception e) {
				Logger.error(time + " is not a valid time.", ErrorCode.TIME_FORMAT);
				continue;
			}

			pmb.setTimeId(timeFromDaysJson);

			pmb.setValue(timeValue.get(time));

			fileHandler.addRow(pmb.toString());
		}
	}

}
