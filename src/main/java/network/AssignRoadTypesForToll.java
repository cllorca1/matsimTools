package network;

import org.matsim.api.core.v01.network.Link;
import org.matsim.api.core.v01.network.Network;
import org.matsim.core.network.NetworkUtils;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class AssignRoadTypesForToll {


    public static void main(String[] args) throws IOException {


        String linkFile = "F:/germany_wide_network/road_germany_2030_ab.csv";
        BufferedReader bufferedReader = new BufferedReader(new FileReader(linkFile));

        Map<String, String> typeByOsmLink = new HashMap<>();

        bufferedReader.readLine();
        String line;
        while ((line = bufferedReader.readLine()) != null) {
            String osmLinkId = line.split(",")[1];
            String type = line.split(",")[8];
            typeByOsmLink.put(osmLinkId, type);
        }
        bufferedReader.close();

        String networkFile = "C:/models/germanymodel/matsim/eu_germany_network_w_connector_trucks.xml.gz";
        String networkFileOutput = "C:/models/germanymodel/matsim/eu_germany_network_w_connector_trucks_ab.xml.gz";
        Network network = NetworkUtils.readNetwork(networkFile);

        double lBundestrasse = 0;
        double lAutobahn= 0;
        double lOther = 0;

        for (Link link : network.getLinks().values()) {
            String osmLinkId = link.getAttributes().getAttribute("origid").toString();
            if (typeByOsmLink.containsKey(osmLinkId)){
                final String adminType = typeByOsmLink.get(osmLinkId);

                if (adminType.equals("autobahn")){
                    link.getAttributes().putAttribute("admin_type", adminType);
                    lAutobahn += link.getLength();
                } else if (adminType.equals("bundestrasse") && link.getFreespeed() * 3.6 > 50) {
                    link.getAttributes().putAttribute("admin_type", adminType);
                    lBundestrasse += link.getLength();
                } else {
                    link.getAttributes().putAttribute("admin_type", "other");
                    lOther += link.getLength();
                }
            } else {
                link.getAttributes().putAttribute("admin_type", "other");
                lOther += link.getLength();
            }
        }

        System.out.println("Autobahn: " + lAutobahn/1000);
        System.out.println("Bundestrasse: " + lBundestrasse/1000);
        System.out.println("Other: " + lOther/1000);

        NetworkUtils.writeNetwork(network, networkFileOutput);
    }
}



