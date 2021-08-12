package emission;

import java.io.*;

public class EmissionsGermany {

    public static void main(String[] args) throws FileNotFoundException {

        long time = System.currentTimeMillis();

        String inputFolder = "Z:/projects/2019/BASt/data/input_files_matsim/emissions_noise/";
        //String matsimRunFolder = "Z:/projects/2019/BASt/data/results/matsim/base_210712";
        String matsimRunFolder = "F:\\matsim_germany\\output\\toll_test_20210611";

        String coldEmissionEfaFile = inputFolder + "EFA_ColdStart_Vehcat_EFA_COLD_VehCat_3.txt";
        String warmEmissionEfaFile = inputFolder + "EFA_HOT_Vehcat_EFA_HOT_VehCat_3.txt";

        String eventFileWithoutEmissions = matsimRunFolder + "/all.output_events.xml.gz";
        String populationFile = matsimRunFolder + "/all.output_plans.xml.gz";
        String networkFile = inputFolder + "final_network_emissions.xml.gz";

        String eventFileWithEmissions = matsimRunFolder + "all.output_events_emissions.xml.gz";
        String individualVehicleFile = matsimRunFolder + "/all.output_vehicles.xml.gz";
        String linkWarmEmissionFile = matsimRunFolder + "/linkWarmEmissionFile.csv";
        String vehicleWarmEmissionFile = matsimRunFolder + "/vehicleWarmEmissionFile.csv";
        String vehicleColdEmissionFile = matsimRunFolder + "/vehicleColdEmissionFile.csv";

        CreateVehicles createVehicles = new CreateVehicles();
        createVehicles.run(eventFileWithoutEmissions, individualVehicleFile);

        OfflineEmissionAnalysis offlineEmissionAnalysis = new OfflineEmissionAnalysis();
        offlineEmissionAnalysis.run(eventFileWithoutEmissions,
                eventFileWithEmissions,
                individualVehicleFile,
                populationFile,
                networkFile,
                coldEmissionEfaFile,
                warmEmissionEfaFile);

        EmissionEventsAnalysis emissionEventsAnalysis = new EmissionEventsAnalysis();
        emissionEventsAnalysis.run(eventFileWithEmissions,
                individualVehicleFile,
                populationFile,
                networkFile,
                linkWarmEmissionFile,
                vehicleWarmEmissionFile,
                vehicleColdEmissionFile,
                coldEmissionEfaFile,
                warmEmissionEfaFile);

        long endTime = System.currentTimeMillis();
        double minutes =  (endTime - time)/1000./60.;

        System.out.println("Runtime = " + minutes);

    }
}
