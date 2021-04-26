package network;

import org.matsim.api.core.v01.network.Link;
import org.matsim.api.core.v01.network.Network;
import org.matsim.core.network.NetworkUtils;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class ExtractFromNewNetwork {

    public static void main(String[] args) throws IOException {


        String linkFile = "IN2";
        BufferedReader bufferedReader = new BufferedReader(new FileReader(linkFile));

        Map<String, VerifyOldNetwork.MATSimLinkWithCount> linksWithStationsMappedByMATSim = new HashMap<>();
        Map<String, VerifyOldNetwork.MATSimLinkWithCount> linksWithStationsMappedByOsm = new HashMap<>();

        bufferedReader.readLine();
        String line;
        while ((line = bufferedReader.readLine()) != null) {
            String matsimLinkId = line.split(",")[0];
            String osmLinkId = line.split(",")[1];
            int direction = Integer.parseInt(line.split(",")[2]);
            int stationId = Integer.parseInt(line.split(",")[3]);
            String fromNode = line.split(",")[5];
            String toNode = line.split(",")[6];
            VerifyOldNetwork.MATSimLinkWithCount linkWithCount = new VerifyOldNetwork.MATSimLinkWithCount(matsimLinkId, osmLinkId, direction, stationId);
            linkWithCount.toNode = toNode;
            linkWithCount.fromNode = fromNode;
            linksWithStationsMappedByMATSim.put(matsimLinkId, linkWithCount);
            linksWithStationsMappedByOsm.put(osmLinkId, linkWithCount);
        }
        bufferedReader.close();

        String networkFile = "IN1";
        Network network = NetworkUtils.readNetwork(networkFile);

        for (Link link : network.getLinks().values()) {
            String matsimLinkId = link.getId().toString();
            String osmLinkId = link.getAttributes().getAttribute("origid").toString();
            if (linksWithStationsMappedByMATSim.containsKey(matsimLinkId)){
                linksWithStationsMappedByMATSim.get(matsimLinkId).linkObject = link;
            } else if (linksWithStationsMappedByOsm.containsKey(osmLinkId)){
                VerifyOldNetwork.MATSimLinkWithCount myMATSimLinkWithCount = linksWithStationsMappedByOsm.get(osmLinkId);
                if(link.getToNode().getId().toString().equals(myMATSimLinkWithCount.toNode) &&
                        link.getFromNode().getId().toString().equals(myMATSimLinkWithCount.fromNode)){
                    myMATSimLinkWithCount.linkObject = link;
                }
            }
        }

        String outputCsv = "OUT";
        PrintWriter pw = new PrintWriter(outputCsv);
        pw.println(VerifyOldNetwork.MATSimLinkWithCount.getHeader());
        for (VerifyOldNetwork.MATSimLinkWithCount myMATSimLinkWithCount : linksWithStationsMappedByMATSim.values()) {
            if (myMATSimLinkWithCount.linkObject != null){
                myMATSimLinkWithCount.matsimLinkId = myMATSimLinkWithCount.linkObject.getId().toString();
                myMATSimLinkWithCount.exists = true;
                pw.println(myMATSimLinkWithCount);
            } else {
                System.out.println("This link was not found: " + myMATSimLinkWithCount);
            }
        }
        pw.close();

    }


}
