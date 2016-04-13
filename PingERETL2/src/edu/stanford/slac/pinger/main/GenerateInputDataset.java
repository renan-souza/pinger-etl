package edu.stanford.slac.pinger.main;

import java.util.ArrayList;

import edu.stanford.slac.pinger.general.Logger;
import edu.stanford.slac.pinger.general.utils.MeasurementUtils;
import edu.stanford.slac.pinger.general.utils.Utils;

public class GenerateInputDataset {

	public static void main(String[] args) {
		if (args.length == 0) {
			args = new String[]{
					"inputDatasetFile=./Input.dataset"
			};
		}	
		//start(args);
		//inputForHourlyFileSplitter();
		//inputForHourlyFileSplitter2();
		inputForAllMetricsPerDayMapper();
		
		//inputForTransform2();
	}

	public static void inputForAllMetricsPerDayMapper() {
		String inputDatasetFile = "./Input.dataset";

		//METRIC;YEAR;MONTH;TICK
		StringBuilder sb = new StringBuilder();
		sb.append("YEAR;MONTH;DAY\n");

		ArrayList<String> years = new ArrayList<String>();
		for (int i = 2014; i >= 1998; i--) {
		//for (int i = 2014; i >= 2014; i--) {
			years.add(i+"");
		}
			for (String year : years) 
				for (String month : Utils.getMonths())
				//for (String month : new String[] {"01"})
					for (String day : Utils.getDaysOfAMonth(year, month))
						sb.append(year+";"+month+";"+day+"\n");

		Utils.writeIntoFile(sb.toString(), inputDatasetFile);
		Logger.log("Input dataset was generated!");
	}
	public static void inputForHourlyFileSplitter() {
		String inputDatasetFile = "./Input.dataset";

		//METRIC;YEAR;MONTH;TICK
		StringBuilder sb = new StringBuilder();
		sb.append("METRIC;YEAR;MONTH;DAY\n");

		ArrayList<String> years = new ArrayList<String>();
		for (int i = 2014; i >= 1998; i--) {
			years.add(i+"");
		}

		for (String metric : MeasurementUtils.METRICS)
			for (String year : years) 
				for (String month : Utils.getMonths())
					for (String day : Utils.getDaysOfAMonth(year, month))
						sb.append(metric+";"+year+";"+month+";"+day+"\n");

		Utils.writeIntoFile(sb.toString(), inputDatasetFile);
		Logger.log("Input dataset was generated!");
	}

	public static void inputForHourlyFileSplitter2() {
		String inputDatasetFile = "./Input.dataset";

		//METRIC;YEAR;MONTH;TICK
		StringBuilder sb = new StringBuilder();
		sb.append("METRIC;YEAR;MONTH;DAY\n");

		sb.append("throughput;2014;01;01\n");
		sb.append("throughput;2014;01;02\n");
		sb.append("throughput;2014;01;03\n");

		Utils.writeIntoFile(sb.toString(), inputDatasetFile);
		Logger.log("Input dataset was generated!");
	}


}
