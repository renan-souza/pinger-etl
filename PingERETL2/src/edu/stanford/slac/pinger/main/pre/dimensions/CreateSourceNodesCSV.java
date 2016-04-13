package edu.stanford.slac.pinger.main.pre.dimensions;

import java.util.ArrayList;
import java.util.Arrays;

import com.google.gson.JsonObject;

import edu.stanford.slac.pinger.beans.NetworkNodeBean;
import edu.stanford.slac.pinger.general.C;
import edu.stanford.slac.pinger.general.Logger;
import edu.stanford.slac.pinger.general.utils.NodesUtils;
import edu.stanford.slac.pinger.general.utils.Utils;

public class CreateSourceNodesCSV {

	public static void main(String[] args) {

		JsonObject nodeDetails = Utils.getNodeDetails();

		ArrayList<String> sourceNodes = NodesUtils.getSourceNodes();
		if (sourceNodes==null) {
			Logger.error("Could not retrieve source nodes.");
			return;
		}


		ArrayList<NetworkNodeBean> lstNodes = new ArrayList<NetworkNodeBean>();

		
		for (String sourceNode : sourceNodes) {
			try {
				JsonObject eachSourceNodeDetails = nodeDetails.get(sourceNode).getAsJsonObject();
				String nodeID =  eachSourceNodeDetails.get("NodeID").getAsString();
				String nodeName =  eachSourceNodeDetails.get("NodeName").getAsString();
				String nodeIP =  eachSourceNodeDetails.get("NodeIP").getAsString();
				String latitude =  eachSourceNodeDetails.get("Latitude").getAsString();
				String longitude =  eachSourceNodeDetails.get("Longitude").getAsString();
				String nodeNickName =  eachSourceNodeDetails.get("NodeNickName").getAsString();
				String nodeFullName =  eachSourceNodeDetails.get("NodeFullName").getAsString();
				String nodeSiteName =  eachSourceNodeDetails.get("NodeSiteName").getAsString();
				String projectType =  eachSourceNodeDetails.get("ProjectType").getAsString();


				NetworkNodeBean nsb = new NetworkNodeBean(nodeID, nodeName, nodeIP, latitude, longitude, nodeNickName, nodeFullName, nodeSiteName, projectType);

				lstNodes.add(nsb);

			} catch (Exception e) {
				System.out.println(e);
			}

		}

		Object[] lstNodesArr = lstNodes.toArray();
		Arrays.sort(lstNodesArr);

		StringBuilder sb = new StringBuilder();
		sb.append(NetworkNodeBean.CSV_HEADER);

		for (Object nodeObj : lstNodesArr) {
			NetworkNodeBean nb = (NetworkNodeBean) nodeObj;
			sb.append(nb.toString(','));
		}
		Utils.writeIntoFile(sb.toString(), C.SOURCE_NODE_CSV);

	}

}
