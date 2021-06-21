package network;

import org.matsim.api.core.v01.Coord;
import org.matsim.api.core.v01.network.Link;
import org.matsim.api.core.v01.network.Network;
import org.matsim.core.network.NetworkUtils;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class VerifyOldNetwork {

    public static void main(String[] args) throws IOException {


        String linkFile = "bast_station_links.csv";
        BufferedReader bufferedReader = new BufferedReader(new FileReader(linkFile));

        Map<String, MATSimLinkWithCount> linksWithStations = new HashMap<>();

        bufferedReader.readLine();
        String line;
        while ((line = bufferedReader.readLine()) != null) {
            String matsimLinkId = line.split(",")[5];
            String osmLinkId = line.split(",")[6];
            int direction = Integer.parseInt(line.split(",")[7]);
            int stationId = Integer.parseInt(line.split(",")[0]);
            MATSimLinkWithCount myMATSimLinkWithCount = new MATSimLinkWithCount(matsimLinkId, osmLinkId, direction, stationId);
            myMATSimLinkWithCount.x = Double.parseDouble(line.split(",")[2]);
            myMATSimLinkWithCount.y = Double.parseDouble(line.split(",")[3]);
            linksWithStations.put(matsimLinkId, myMATSimLinkWithCount);

        }
        bufferedReader.close();

        String networkFile =  "final_V10.xml.gz";
        Network network = NetworkUtils.readNetwork(networkFile);

        for (Link link : network.getLinks().values()) {
            if (linksWithStations.containsKey(link.getId().toString())) {
                MATSimLinkWithCount myMATSimLinkWithCount = linksWithStations.get(link.getId().toString());
                myMATSimLinkWithCount.exists = true;
                myMATSimLinkWithCount.fromNode = link.getFromNode().getId().toString();
                myMATSimLinkWithCount.toNode = link.getToNode().getId().toString();

                double distance = NetworkUtils.getEuclideanDistance(link.getFromNode().getCoord(), new Coord(myMATSimLinkWithCount.x, myMATSimLinkWithCount.y));

                System.out.println(myMATSimLinkWithCount.stationId + " is " + distance +  " far from the link starting node");

                if (!link.getAttributes().getAttribute("origid").equals(myMATSimLinkWithCount.osmLinkId)){
                    System.out.println("The osm links do not match");
                }
            }
        }

        String outputCsv =  "bast_station_links_2.csv";
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
        double x;
        double y;

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
                    (toNode == null? -1  :toNode) + "," +
                    x + "," +
                    y;
        }

        public static String getHeader(){
            return "linkId,osmLinkId,direction,stationId,exists,fromNode,toNode,x,y";
        }
    }
}
