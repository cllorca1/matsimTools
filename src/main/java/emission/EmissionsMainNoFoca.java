package emission;

import java.io.FileNotFoundException;

public class EmissionsMainNoFoca {

    public static void main(String[] args) throws FileNotFoundException {

        String upperFolder = args[0];
        String configFile = "config_average.xml";

        for (int i = 1; i < args.length; i++) {

            String year = "2010";
            String yearNetwork = "2050";

            String eventFileWithoutEmissions = upperFolder + args[i] + "/matsim/" + year + "/" + year + ".output_events.xml.gz";
            String eventFileWithEmissions = upperFolder + args[i] + "/matsim/" + year + "/" + year + ".output_events_2.xml.gz";
            String individualVehicleFile = upperFolder + args[i] + "/matsim/" + year + "/vehicles_2.xml.gz";
            String populationFile = upperFolder + args[i] + "/matsim/" + year + "/" + year + ".output_plans.xml.gz";
            String networkFile = upperFolder + args[i] + "/matsim/" + yearNetwork + "/" + yearNetwork + ".output_network_2.xml.gz";

            String linkWarmEmissionFile = upperFolder + args[i] + "/matsim/" + year + "/linkWarmEmissionFile.csv";
            String vehicleWarmEmissionFile = upperFolder + args[i] + "/matsim/" + year + "/vehicleWarmEmissionFile.csv";
            String vehicleColdEmissionFile = upperFolder + args[i] + "/matsim/" + year + "/vehicleColdEmissionFile.csv";

            CreateVehicles createVehicles = new CreateVehicles();
            createVehicles.run(eventFileWithoutEmissions, individualVehicleFile);

            OfflineEmissionAnalysis offlineEmissionAnalysis = new OfflineEmissionAnalysis();
            offlineEmissionAnalysis.run(configFile,
                    "",
                    eventFileWithoutEmissions,
                    eventFileWithEmissions,
                    individualVehicleFile,
                    populationFile,
                    networkFile);

            EmissionEventsAnalysis emissionEventsAnalysis = new EmissionEventsAnalysis();
            emissionEventsAnalysis.run(configFile,
                    "",
                    eventFileWithEmissions,
                    individualVehicleFile,
                    populationFile,
                    networkFile,
                    linkWarmEmissionFile,
                    vehicleWarmEmissionFile,
                    vehicleColdEmissionFile);
        }
    }
}
