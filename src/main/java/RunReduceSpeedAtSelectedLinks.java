import org.matsim.api.core.v01.network.Link;
import org.matsim.api.core.v01.network.Network;
import org.matsim.core.network.NetworkUtils;

import java.io.*;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class RunReduceSpeedAtSelectedLinks {

    public static void main(String[] args) throws IOException {

        Network network = NetworkUtils.readNetwork(args[0]);

        Set<String> listOfLinks = new HashSet<String>();

        BufferedReader br = new BufferedReader(new FileReader(new File(args[1])));

        br.readLine();

        String line;
        while ((line = br.readLine()) != null){
         String id = line.split(",")[0];
         listOfLinks.add(id);
        }

        System.out.println("Added " + listOfLinks.size() + " links at urban cores");

        for (Link link : network.getLinks().values()){

            if (listOfLinks.contains(link.getId().toString()) && link.getFreespeed() < 50 / 3.6){

                link.setFreespeed(20/3.6);

            }
        }
        
        NetworkUtils.writeNetwork(network, args[2]);

    }


}
