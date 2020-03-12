import org.matsim.api.core.v01.network.Link;
import org.matsim.api.core.v01.network.Network;
import org.matsim.core.network.NetworkUtils;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class RunOSMTypeAssignment {

    RunOSMTypeAssignment() {

    }

    public static void main(String[] args) throws IOException {

        String upperFolder = args[0];

        RunOSMTypeAssignment rota = new RunOSMTypeAssignment();
        Map<String, String> linkMap = rota.readOSMDataAsTable(args[1]);

        for (int i = 2; i < args.length; i++) {

            String networkFile = upperFolder + args[i] + "/matsim/2050/2050.output_network.xml.gz";
            Network network = NetworkUtils.readNetwork(networkFile);
            for (Link link : network.getLinks().values()) {
                String id = link.getId().toString().split("_")[0];
                String type = linkMap.get(id);
                if (type == null) {
                    throw new RuntimeException(link.toString());
                } else {
                    link.getAttributes().putAttribute("type", type);
                }
            }
            networkFile = upperFolder + args[i] + "/matsim/2050/2050.output_network_2.xml.gz";
            NetworkUtils.writeNetwork(network, networkFile);
        }
    }


    private Map<String, String> readOSMDataAsTable(String fileName) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(new File(fileName)));
        Map<String, String> linkMap = new HashMap<>();

        String line = br.readLine();
        int errors = 0;
        while ((line = br.readLine()) != null) {
            String[] record = line.split(",");
            if (record.length == 1) {
                errors++;
            } else {
                linkMap.put(record[0], record[1]);
            }
        }
        System.out.println("Read " + linkMap.size() + " records from OSM table as CSV with " + errors + " errors.");
        return linkMap;
    }

}
