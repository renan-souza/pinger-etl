package test;

import java.util.HashMap;

import edu.stanford.slac.pinger.beans.PingERMeasurementAllMetricsBean;
import edu.stanford.slac.pinger.general.utils.MeasurementUtils;
import edu.stanford.slac.pinger.general.utils.Utils;

public class T {

	public static void main(String[] args) {

		System.out.println(PingERMeasurementAllMetricsBean.HEADER);
		Utils.writeIntoFile(PingERMeasurementAllMetricsBean.HEADER, "./pinger_measurement_header.txt");
	}

}
