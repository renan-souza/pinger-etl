package edu.stanford.slac.pinger.beans;

public class PingERMeasurementAllMetricsBean {

	private int sourceNodeId;
	private int destinationNodeId; 
	private long timeId;
	
	public PingERMeasurementAllMetricsBean() {}
	
	public int getSourceNodeId() {
		return sourceNodeId;
	}
	public void setSourceNodeId(int sourceNodeId) {
		this.sourceNodeId = sourceNodeId;
	}
	public int getDestinationNodeId() {
		return destinationNodeId;
	}
	public void setDestinationNodeId(int destinationNodeId) {
		this.destinationNodeId = destinationNodeId;
	}
	public long getTimeId() {
		return timeId;
	}
	public void setTimeId(long timeId) {
		this.timeId = timeId;
	}

	public final static String HEADER = 
			"SOURCE_NODE_ID,SOURCE_COUNTRY_ID,SOURCE_CONTINENT_ID,"+
			"DESTINATION_NODE_ID,DESTINATION_COUNTRY_ID,DESTINATION_CONTINENT_ID,"+
			"TIME_ID,"+
			"throughput,"+
			"packet_loss,"+
			"average_rtt,"+
			"MOS,"+
			"alpha,"+
			"conditional_loss_probability,"+
			"duplicate_packets,"+
			"ipdv,"+
			"unreachability,"+
			"zero_packet_loss_frequency,"+
			"minimum_rtt,"+
			"iqr,"+
			"maximum_rtt,"+
			"minimum_packet_loss,"+
			"out_of_order_packets,"+
			"unpredictability";
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + destinationNodeId;
		result = prime * result + sourceNodeId;
		result = prime * result + (int) (timeId ^ (timeId >>> 32));
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		PingERMeasurementAllMetricsBean other = (PingERMeasurementAllMetricsBean) obj;
		if (destinationNodeId == other.destinationNodeId && sourceNodeId == other.sourceNodeId && timeId == other.timeId)
			return true;
		return false;
	}

	
}
