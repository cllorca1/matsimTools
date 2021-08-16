package emission;


import emission.data.AnalyzedLink;
import emission.data.AnalyzedVehicle;
import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.Scenario;
import org.matsim.api.core.v01.events.*;
import org.matsim.api.core.v01.network.Link;
import org.matsim.contrib.emissions.EmissionModule;
import org.matsim.contrib.emissions.Pollutant;
import org.matsim.contrib.emissions.utils.EmissionsConfigGroup;
import org.matsim.core.api.experimental.events.EventsManager;
import org.matsim.core.config.Config;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.controler.AbstractModule;
import org.matsim.core.controler.Injector;
import org.matsim.core.events.EventsUtils;
import org.matsim.core.events.MatsimEventsReader;
import org.matsim.core.scenario.ScenarioUtils;
import org.matsim.vehicles.Vehicle;
import org.matsim.api.core.v01.events.VehicleLeavesTrafficEvent; //A

import java.beans.EventHandler;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.*;

public class EmissionEventsAnalysis{

    public Config config;
    final static Set<Pollutant> selectedPollutants = new HashSet<>();
    String vehicleId;

    public EmissionEventsAnalysis() {
        selectedPollutants.add(Pollutant.CO2_TOTAL);
        selectedPollutants.add(Pollutant.CO);
        selectedPollutants.add(Pollutant.NOx);
        selectedPollutants.add(Pollutant.PM);
        selectedPollutants.add(Pollutant.HC);
    }

    public void run(String eventsFileWithEmission,
                    String individualVehicleFile,
                    String populationFile,
                    String networkFile,
                    String linkWarmEmissionFile,
                    String vehicleWarmEmissionFile,
                    String vehicleColdEmissionFile,
                    String warmEmissionSummary,
                    String efaWarmFile, String efaHotFile) throws FileNotFoundException {
        config = ConfigUtils.createConfig(new EmissionsConfigGroup());
        config.controler().setOutputDirectory("");
        config.vehicles().setVehiclesFile(individualVehicleFile);
        config.network().setInputFile(networkFile);
        config.plans().setInputFile(populationFile);


        EmissionsConfigGroup ecg = ConfigUtils.addOrGetModule(this.config, EmissionsConfigGroup.class);
        ecg.setDetailedVsAverageLookupBehavior(EmissionsConfigGroup.DetailedVsAverageLookupBehavior.directlyTryAverageTable);
        ecg.setHbefaRoadTypeSource(EmissionsConfigGroup.HbefaRoadTypeSource.fromLinkAttributes);
        ecg.setHbefaVehicleDescriptionSource(EmissionsConfigGroup.HbefaVehicleDescriptionSource.fromVehicleTypeDescription);
        ecg.setHandlesHighAverageSpeeds(true);
        ecg.setAverageColdEmissionFactorsFile(efaWarmFile);
        ecg.setAverageWarmEmissionFactorsFile(efaHotFile);
        final Scenario scenario = ScenarioUtils.loadScenario(this.config);
        final EventsManager eventsManager = EventsUtils.createEventsManager();

        AbstractModule module = new AbstractModule() {
            public void install() {
                this.bind(Scenario.class).toInstance(scenario);
                this.bind(EventsManager.class).toInstance(eventsManager);
                this.bind(EmissionModule.class);
            }
        };
        com.google.inject.Injector injector = Injector.createInjector(this.config, new AbstractModule[]{module});
        EmissionModule emissionModule = injector.getInstance(EmissionModule.class);

        LinkEmissionHandler linkEmissionHandler = new LinkEmissionHandler(scenario.getNetwork());
        emissionModule.getEmissionEventsManager().addHandler(linkEmissionHandler);

        MatsimEventsReader matsimEventsReader = new MatsimEventsReader(eventsManager);
        matsimEventsReader.readFile(eventsFileWithEmission);

        Map<Id<Link>, AnalyzedLink> analyzedLinks = linkEmissionHandler.getEmmisionsByLink();
        Map<Id<Vehicle>, AnalyzedVehicle> analyzedVehicles = linkEmissionHandler.getEmmisionsByVehicle();


        /*private static CountVehicleType getTypeFromId;

        CountVehicleType vehicleType = getTypeFromId(linkLeaveEvent.getVehicleId().toString());
        Map<String, Map<Id<org.matsim.core.mobsim.jdeqsim.Vehicle>, Double>> delayByRoadType = delayByVehicleAndRoadType.get(vehicleType);
        (String vehicleId){
            if (vehicleId.contains("truck")) {
                return CountVehicleType.truck;
            } else if (vehicleId.contains("ld")) {
                return CountVehicleType.car_ld;
            } else {
                return CountVehicleType.car_sd;
            }
        }
        public Map<Id<org.matsim.core.mobsim.jdeqsim.Vehicle>, Double> getVehicleDelayMap () {
            return vehicleDelayMap;
        }

        Map<CountVehicleType, Map<String, Map<Id<Vehicle>, Double>>> delayByVehicleAndRoadType = new HashMap<>();


    }*/

// A



        printOutLinkWarmEmissions(linkWarmEmissionFile, analyzedLinks, true);
        printOutWarmEmissionsSummary(warmEmissionSummary, linkEmissionHandler.getEmissionsByVehicleAndRoadType());


//        printOutVehicleWarmEmissions(vehicleWarmEmissionFile, analyzedVehicles, true);
//        printOutVehicleWarmEmissions(vehicleColdEmissionFile, analyzedVehicles, false);

    }

    private void printOutVehicleWarmEmissions(String fileName,
                                                     Map<Id<Vehicle>, AnalyzedVehicle> analyzedVehicles,
                                                     boolean warm) throws FileNotFoundException {

        PrintWriter pw = new PrintWriter(new File(fileName));

        StringBuilder header = new StringBuilder();
        header.append("id,distance,startTime,endTime,operatingTime");


        for (Pollutant pollutant : selectedPollutants) {
            header.append(",").append(pollutant.toString());
        }
        pw.println(header);

        for (AnalyzedVehicle vehicle : analyzedVehicles.values()) {
            StringBuilder sb = new StringBuilder();
            sb.append(vehicle.getId().toString().replace("_truck", ""));
            sb.append(",").append(vehicle.getDistanceTravelled());
            sb.append(",").append(vehicle.getStartingTime());
            sb.append(",").append(vehicle.getEndTime());
            sb.append(",").append(vehicle.getOperatingTime());

            for (Pollutant pollutant : selectedPollutants) {
                if (warm) {
                    sb.append(",").append(vehicle.getWarmEmissions().get(pollutant.toString()));
                } else {
                    sb.append(",").append(vehicle.getColdEmissions().get(pollutant.toString()));
                }

            }

            pw.println(sb);
        }

        pw.close();

    }

    private void printOutLinkWarmEmissions(String fileName,
                                                Map<Id<Link>, AnalyzedLink> analyzedLinks,
                                                boolean warm) throws FileNotFoundException {

        PrintWriter pw = new PrintWriter(new File(fileName));

        StringBuilder header = new StringBuilder();
        header.append("link,length,type");
        for (Pollutant pollutant : selectedPollutants) {
            header.append(",").append(pollutant.toString());
        }
        pw.println(header);

        for (AnalyzedLink link : analyzedLinks.values()) {
            StringBuilder sb = new StringBuilder();
            sb.append(link.getId().toString()).append(",");
            sb.append(link.getMatsimLink().getLength()).append(",");
            sb.append(link.getMatsimLink().getAttributes().getAttribute("admin_type"));

            //for(AnalyzedVehicle vehicle : analyzedVehicles.values()){
            // }


            for (Pollutant pollutant : selectedPollutants) {
                if (warm) {
                    sb.append(",").append(link.getWarmEmissions().get(pollutant.toString()));
                } else {
                    sb.append(",").append(link.getColdEmissions().get(pollutant.toString()));
                }
            }

            pw.println(sb);
        }



        //////


        pw.close();

    }

    private void printOutWarmEmissionsSummary(String fileName,
                                              Map<CountVehicleType, Map<String, Map<Pollutant, Double>>> emissionsByVehicleAndRoadType) throws FileNotFoundException {

        PrintWriter pw = new PrintWriter(new File(fileName));

        StringBuilder header = new StringBuilder();
        header.append("vehicleType,linkType,pollutantType,value");
        pw.println(header);

        for(CountVehicleType countVehicleType : emissionsByVehicleAndRoadType.keySet()) {
            for (String linkType : emissionsByVehicleAndRoadType.get(countVehicleType).keySet()) {
                for (Pollutant pollutant : emissionsByVehicleAndRoadType.get(countVehicleType).get(linkType).keySet()) {
                    double value = emissionsByVehicleAndRoadType.get(countVehicleType).get(linkType).get(pollutant);
                    StringBuilder line = new StringBuilder();
                    line.append(countVehicleType).append(",");
                    line.append(linkType).append(",");
                    line.append(pollutant).append(",");
                    line.append(value);
                    pw.println(line);
                }
            }
        }

        //////

        pw.close();

    }


    public Config prepareConfig(String configFile, String outDirectory, String vehicleFile, String networkFile, String populationFile ) {
        config = ConfigUtils.loadConfig(configFile, new EmissionsConfigGroup());
        config.controler().setOutputDirectory(outDirectory);
        config.vehicles().setVehiclesFile(vehicleFile);
        config.network().setInputFile(networkFile);
        config.plans().setInputFile(populationFile);
        return config;
    }

}
