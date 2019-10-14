package emission;

import java.io.FileNotFoundException;

public class EmissionsMainNoFoca {

    public static void main(String[] args) throws FileNotFoundException {

        String upperFolder = args[0];
        String configFile = "config_average.xml";

        for (int i = 1; i < args.length; i++) {

            String eventFileWithoutEmissions = upperFolder + args[i] + "/matsim/2040/2040.output_events.xml.gz";
            String eventFileWithEmissions = upperFolder + args[i] + "/matsim/2040/2040.output_events_2.xml.gz";
            String individualVehicleFile = upperFolder + args[i] + "/matsim/2040/vehicles_2.xml.gz";
            String populationFile = upperFolder + args[i] + "/matsim/2040/2040.output_plans.xml.gz";
            String networkFile = upperFolder + args[i] + "/matsim/2040/2040.output_network_2.xml.gz";

            String linkWarmEmissionFile = upperFolder + args[i] + "/matsim/2040/linkWarmEmissionFile.csv";
            String vehicleWarmEmissionFile = upperFolder + args[i] + "/matsim/2040/vehicleWarmEmissionFile.csv";
            String vehicleColdEmissionFile = upperFolder + args[i] + "/matsim/2040/vehicleColdEmissionFile.csv";

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
