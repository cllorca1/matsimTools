package network;

import org.matsim.api.core.v01.network.Link;
import org.matsim.api.core.v01.network.Network;
import org.matsim.core.network.NetworkUtils;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class VerifyOldNetwork {

    public static void main(String[] args) throws IOException {


        String linkFile = "INPUT_1";
        BufferedReader bufferedReader = new BufferedReader(new FileReader(linkFile));

        Map<String, MATSimLinkWithCount> linksWithStations = new HashMap<>();

        bufferedReader.readLine();
        String line;
        while ((line = bufferedReader.readLine()) != null) {
            String matsimLinkId = line.split(",")[5];
            String osmLinkId = line.split(",")[6];
            int direction = Integer.parseInt(line.split(",")[7]);
            int stationId = Integer.parseInt(line.split(",")[0]);

            linksWithStations.put(matsimLinkId, new MATSimLinkWithCount(matsimLinkId, osmLinkId, direction, stationId));

        }
        bufferedReader.close();

        String networkFile =  "INPUT_2";
        Network network = NetworkUtils.readNetwork(networkFile);

        for (Link link : network.getLinks().values()) {
            if (linksWithStations.containsKey(link.getId().toString())) {
                MATSimLinkWithCount myMATSimLinkWithCount = linksWithStations.get(link.getId().toString());
                myMATSimLinkWithCount.exists = true;
                myMATSimLinkWithCount.fromNode = link.getFromNode().getId().toString();
                myMATSimLinkWithCount.toNode = link.getToNode().getId().toString();
                if (!link.getAttributes().getAttribute("origid").equals(myMATSimLinkWithCount.osmLinkId)){
                    System.out.println("The osm links do nto match");
                }
            }
        }

        String outputCsv =  "OUT_1";
        PrintWriter pw = new PrintWriter(outputCsv);
        pw.println(MATSimLinkWithCount.getHeader());
        for (MATSimLinkWithCount MATSimLinkWithCount : linksWithStations.values()){
                pw.println(MATSimLinkWithCount.toString());
        }
        pw.close();

    }

    protected static class MATSimLinkWithCount {
        String matsimLinkId;
        String osmLinkId;
        int direction;
        int stationId;

        boolean exists;
        String fromNode = null;
        String toNode = null;

        Link linkObject = null;

        public MATSimLinkWithCount(String matsimLinkId, String osmLinkId, int direction, int stationId) {
            this.matsimLinkId = matsimLinkId;
            this.osmLinkId = osmLinkId;
            this.direction = direction;
            this.stationId = stationId;
            exists = false;
        }

        @Override
        public String toString() {
            return matsimLinkId + "," +
                    osmLinkId + "," +
                    direction +"," +
                    stationId + "," +
                    (exists? 1 : 0) + "," +
                    (fromNode == null? -1 : fromNode) + "," +
                    (toNode == null? -1  :toNode);
        }

        public static String getHeader(){
            return "linkId,osmLinkId,direction,stationId,exists,fromNode,toNode";
        }
    }
}
