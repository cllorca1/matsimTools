package network;

import org.matsim.api.core.v01.Coord;
import org.matsim.api.core.v01.network.Link;
import org.matsim.api.core.v01.network.Network;
import org.matsim.core.network.NetworkUtils;

import java.awt.*;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ExtractFromNewNetwork {

    public static void main(String[] args) throws IOException {


        String linkFile = "bast_station_links_2.csv";
        BufferedReader bufferedReader = new BufferedReader(new FileReader(linkFile));

        Map<String, VerifyOldNetwork.MATSimLinkWithCount> linksWithStationsMappedByMATSim = new HashMap<>();
        Map<String, List<VerifyOldNetwork.MATSimLinkWithCount>> linksWithStationsMappedByOsm = new HashMap<>();

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
            linkWithCount.x = Double.parseDouble(line.split(",")[7]);
            linkWithCount.y = Double.parseDouble(line.split(",")[8]);
            linksWithStationsMappedByMATSim.put(matsimLinkId, linkWithCount);
            linksWithStationsMappedByOsm.putIfAbsent(osmLinkId, new ArrayList<>());
            linksWithStationsMappedByOsm.get(osmLinkId).add(linkWithCount);
        }
        bufferedReader.close();

        String networkFile = "c:/models/germanymodel/matsim/eu_germany_network_w_connector_trucks.xml.gz";
        Network network = NetworkUtils.readNetwork(networkFile);

        for (Link link : network.getLinks().values()) {
            String matsimLinkId = link.getId().toString();
            String osmLinkId = link.getAttributes().getAttribute("origid").toString();
             if (linksWithStationsMappedByOsm.containsKey(osmLinkId)){
                for (VerifyOldNetwork.MATSimLinkWithCount myMATSimLinkWithCount : linksWithStationsMappedByOsm.get(osmLinkId)){
                    if(link.getToNode().getId().toString().equals(myMATSimLinkWithCount.toNode) &&
                            link.getFromNode().getId().toString().equals(myMATSimLinkWithCount.fromNode)){
                        myMATSimLinkWithCount.linkObject = link;
                    }
                }
             }
        }

        String outputCsv = "bast_station_links_network_2011.csv";
        PrintWriter pw = new PrintWriter(outputCsv);
        pw.println(VerifyOldNetwork.MATSimLinkWithCount.getHeader());
        for (VerifyOldNetwork.MATSimLinkWithCount myMATSimLinkWithCount : linksWithStationsMappedByMATSim.values()) {
            if (myMATSimLinkWithCount.linkObject != null){
                myMATSimLinkWithCount.matsimLinkId = myMATSimLinkWithCount.linkObject.getId().toString();
                myMATSimLinkWithCount.exists = true;
                pw.println(myMATSimLinkWithCount);
                double distance = NetworkUtils.getEuclideanDistance(new Coord(myMATSimLinkWithCount.x, myMATSimLinkWithCount.y), myMATSimLinkWithCount.linkObject.getFromNode().getCoord());
                System.out.println("Distance is "  + distance);

            } else {
                System.out.println("This link was not found: " + myMATSimLinkWithCount);
            }
        }
        pw.close();

    }


}
