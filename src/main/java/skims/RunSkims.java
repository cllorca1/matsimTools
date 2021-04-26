package skims;

import ch.sbb.matsim.analysis.skims.CalculateSkimMatrices;
import org.matsim.core.config.ConfigUtils;

import java.io.IOException;
import java.util.Random;

public class RunSkims {

    public static void main(String[] args) throws IOException {

        String zonesShapeFilename = args[0];
        String zonesIdAttributeName = args[1];
        String outputDirectory = args[2];
        String networkFilename = args[3];
        //CalculateSkimMatrices skims = new CalculateSkimMatrices(zonesShapeFilename, zonesIdAttributeName, outputDirectory, 16);
        //skims.calculateSamplingPointsPerZoneFromFacilities(facilitiesFilename, numberOfPointsPerZone, r, facility -> 1.0);
        //alternative if you don't have facilities:
//        skims.calculateSamplingPointsPerZoneFromNetwork(networkFilename, 1, new Random(0));
//        skims.calculateNetworkMatrices(networkFilename, null, new double[]{8*60*60}, ConfigUtils.createConfig(), null, link -> true);
//        //skims.calculatePTMatrices(networkFilename, transitScheduleFilename, earliestTime, latestTime, config, null, (line, route) -> route.getTransportMode().equals("train"));
//        skims.calculateBeelineMatrix();


    }


}
