package network;

import org.matsim.api.core.v01.Coord;
import org.matsim.api.core.v01.network.Link;
import org.matsim.api.core.v01.network.Network;
import org.matsim.core.network.NetworkUtils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ExtractFromNewNetwork_v2 {

    public static void main(String[] args) throws IOException {


        String linkFile = "bast_stations_second.csv";
        BufferedReader bufferedReader = new BufferedReader(new FileReader(linkFile));

        List<VerifyOldNetwork.MATSimLinkWithCount> counts = new ArrayList<>();


        bufferedReader.readLine();
        String line;
        while ((line = bufferedReader.readLine()) != null) {
            int stationId = Integer.parseInt(line.split(",")[0]);
            String fromNode = line.split(",")[1];
            String toNode = line.split(",")[2];
            VerifyOldNetwork.MATSimLinkWithCount linkWithCount = new VerifyOldNetwork.MATSimLinkWithCount(null, null, 0, stationId);
            linkWithCount.toNode = toNode;
            linkWithCount.fromNode = fromNode;
            counts.add(linkWithCount);
        }
        bufferedReader.close();

        String networkFile = "c:/models/germanymodel/matsim/eu_germany_network_w_connector_trucks.xml.gz";
        Network network = NetworkUtils.readNetwork(networkFile);

        for (Link link : network.getLinks().values()) {
            String matsimLinkId = link.getId().toString();
            String osmLinkId = link.getAttributes().getAttribute("origid").toString();
            for (VerifyOldNetwork.MATSimLinkWithCount myMATSimLinkWithCount : counts) {
                if (link.getToNode().getId().toString().equals(myMATSimLinkWithCount.toNode) &&
                        link.getFromNode().getId().toString().equals(myMATSimLinkWithCount.fromNode)) {
                    myMATSimLinkWithCount.linkObject = link;
                    myMATSimLinkWithCount.osmLinkId = osmLinkId;
                    myMATSimLinkWithCount.matsimLinkId = matsimLinkId;
                }
            }

        }

        String outputCsv = "bast_stations_second_links.csv";
        PrintWriter pw = new PrintWriter(outputCsv);
        pw.println(VerifyOldNetwork.MATSimLinkWithCount.getHeader());
        for (VerifyOldNetwork.MATSimLinkWithCount myMATSimLinkWithCount : counts) {
            if (myMATSimLinkWithCount.linkObject != null) {
                myMATSimLinkWithCount.matsimLinkId = myMATSimLinkWithCount.linkObject.getId().toString();
                myMATSimLinkWithCount.exists = true;
                pw.println(myMATSimLinkWithCount);
                double distance = NetworkUtils.getEuclideanDistance(new Coord(myMATSimLinkWithCount.x, myMATSimLinkWithCount.y), myMATSimLinkWithCount.linkObject.getFromNode().getCoord());
                System.out.println("Distance is " + distance);

            } else {
                System.out.println("This link was not found: " + myMATSimLinkWithCount);
            }
        }
        pw.close();

    }


}
