package edu.stanford.slac.pinger.beans;

public class NetworkNodeBean implements Comparable<NetworkNodeBean>  {

	public static final String CSV_HEADER = "#id,node_name,node_ip,latitude,longitude,nick_name,full_name,site_name,project_type\n";
	String id,nodeName, nodeIP, latitude,longitude,nodeNickName,nodeFullName,nodeSiteName,projectType;
	
	public NetworkNodeBean(String id, String nodeName, String nodeIP, String latitude, String longitude, String nodeNickName, String nodeFullName, String nodeSiteName,String projectType) {
		this.id = id;
		this.nodeName = nodeName;
		this.nodeIP = nodeIP;
		this.latitude = latitude;
		this.longitude = longitude;
		this.nodeNickName = nodeNickName;
		this.nodeFullName = nodeFullName;
		this.nodeSiteName = nodeSiteName;
		this.projectType = projectType;
	}
	
	public String toString(char dmtr) {
		return id + dmtr + nodeName + dmtr + nodeIP + dmtr + latitude + dmtr + longitude + dmtr + nodeNickName + dmtr + nodeFullName + dmtr + nodeSiteName + dmtr + projectType + "\n";
	}
	
	@Override
	public int compareTo(NetworkNodeBean other) {
		int thisId = Integer.parseInt(id);
		int otherId = Integer.parseInt(other.id);
		if (thisId > otherId)
			return 1;
		else if (thisId <  otherId)
			return -1;
		else //(thisId == otherId)
			return 0;
	}
}
