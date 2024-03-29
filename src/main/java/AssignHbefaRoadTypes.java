import org.apache.log4j.Logger;
import org.matsim.api.core.v01.Scenario;
import org.matsim.api.core.v01.network.Link;
import org.matsim.api.core.v01.network.Network;
import org.matsim.api.core.v01.network.NetworkWriter;
import org.matsim.core.config.Config;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.scenario.ScenarioUtils;

public class AssignHbefaRoadTypes {

    private static final Logger logger = Logger.getLogger(AssignHbefaRoadTypes.class);

    static String inputFile = "./networks/matsim/final_V8_emissions.xml.gz";
    static String outputFile = "./networks/matsim/final_V8_emissions.xml.gz";

    /**
     * handled OSM road types:
     * motorway,trunk,primary,secondary, tertiary, unclassified,residential,service
     * motorway_link, trunk_link,primary_link, secondary_link
     * tertiary_link, living_street, pedestrian,track,road
     * <p>
     * Hbefa categories and respective speeds
     * URB/MW-Nat./80 - 130
     * URB/MW-City/60 - 110
     * URB/Trunk-Nat./70 - 110
     * URB/Trunk-City/50 - 90
     * URB/Distr/50 - 80
     * URB/Local/50 - 60
     * URB/Access/30 - 50
     * <p>
     * Conversions from OSM to hbefa types
     * motorway;MW
     * primary;Trunk
     * secondary;Distr
     * tertiary;Local
     * residential;Access
     * living;Access
     **/

    public static void main(String args[]) {

        String upperFolder = args[0];

        outputFile = args[1];


        processLinks(args, upperFolder);


    }

    private static void processLinks(String[] args, String upperFolder) {
        for (int i = 1; i < args.length; i++){
            String networkFile = upperFolder + args[i] + "/matsim/2050/2050.output_network_2.xml.gz";
            Config config = ConfigUtils.createConfig();
            config.network().setInputFile(networkFile);
            Scenario scenario = ScenarioUtils.loadScenario(config);
            Network network = scenario.getNetwork();

            for (Link link : network.getLinks().values()) {
                link.getAttributes().putAttribute("hbefa_road_type", getHbefaType(link));
            }

            networkFile = upperFolder + args[i] + "/matsim/2050/2050.output_network_2.xml.gz";
            new NetworkWriter(network).write(networkFile);
        }
    }

    private static void processLinksMunich(String[] args, String upperFolder) {
        for (int i = 1; i < args.length; i++){
            String networkFile = upperFolder + args[i] + "/matsim/2050/2050.output_network.xml.gz";
            Config config = ConfigUtils.createConfig();
            config.network().setInputFile(networkFile);
            Scenario scenario = ScenarioUtils.loadScenario(config);
            Network network = scenario.getNetwork();

            for (Link link : network.getLinks().values()) {
                if (!link.getAllowedModes().contains("pt")){
                    link.getAttributes().putAttribute("hbefa_road_type", getHbefaType(link));
                }
            }

            networkFile = upperFolder + args[i] + "/matsim/2050/2050.output_network_2.xml.gz";
            new NetworkWriter(network).write(networkFile);
        }
    }


    public static String getHbefaType(Link link) {

        double freeFlowSpeed_kmh = link.getFreespeed() * 3.6;
        String osmType = link.getAttributes().getAttribute("type").toString().split("_")[0];

        String type = "";
        String speedRange = "";

        if (osmType.equals("motorway")) {
            if (freeFlowSpeed_kmh > 80) {
                type = "MW-Nat.";
                speedRange = "80";
            } else if (freeFlowSpeed_kmh > 60) {
                type = "MW-City";
                speedRange = "60";
            } else {
                logger.error("Road classified with lower cat: " + osmType + " - " + freeFlowSpeed_kmh);
                type = "Distr";
                speedRange = "50";
            }
        } else if (osmType.equals("primary") || osmType.equals("trunk")) {
            if (freeFlowSpeed_kmh >= 70) {
                type = "Trunk-Nat.";
                speedRange = "70";
            } else if (freeFlowSpeed_kmh >= 50) {
                type = "Trunk-City";
                speedRange = "50";
            } else {
                logger.error("Road classified with lower cat: " + osmType + " - " + freeFlowSpeed_kmh);
                type = "Distr";
                speedRange = "50";
            }
        } else if (osmType.equals("secondary")) {
            type = "Distr";
            speedRange = "50";
        } else if (osmType.equals("tertiary")) {
            type = "Local";
            speedRange = "50";
        } else if (osmType.equals("residential") ||
                osmType.equals("access") ||
                osmType.equals("service") ||
                osmType.equals("living") ||
                osmType.equals("unclassified")) {
            type = "Access";
            speedRange = "30";
        } else {
            logger.error(osmType + " - " + freeFlowSpeed_kmh);
            throw new RuntimeException("road type not known");
        }


        return "URB/" + type + "/" + speedRange;
    }

}
