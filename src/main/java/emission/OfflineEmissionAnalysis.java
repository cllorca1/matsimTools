package emission;

import org.matsim.api.core.v01.Scenario;
import org.matsim.contrib.emissions.EmissionModule;
import org.matsim.contrib.emissions.utils.EmissionsConfigGroup;
import org.matsim.core.api.experimental.events.EventsManager;
import org.matsim.core.config.Config;
import org.matsim.core.config.ConfigGroup;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.controler.AbstractModule;
import org.matsim.core.controler.Injector;
import org.matsim.core.events.EventsUtils;
import org.matsim.core.events.MatsimEventsReader;
import org.matsim.core.events.algorithms.EventWriterXML;
import org.matsim.core.scenario.ScenarioUtils;

/**
 * Class to calculate emisions from events (copied from the Emission extension example folder)
 */
public class OfflineEmissionAnalysis {




    private Config config;

    // =======================================================================================================


    public void run(String eventsFileWithoutEmissions, String eventsFileWithEmission,
                    String individualVehicleFile, String populationFile, String networkFile, String coldEmissionFile, String warmEmissionFile) {

        config = ConfigUtils.createConfig(new EmissionsConfigGroup());
        config.controler().setOutputDirectory("");
        config.vehicles().setVehiclesFile(individualVehicleFile);
        config.network().setInputFile(networkFile);
        config.plans().setInputFile(populationFile);



        EmissionsConfigGroup ecg = ConfigUtils.addOrGetModule(this.config, EmissionsConfigGroup.class);
        ecg.setHbefaRoadTypeSource(EmissionsConfigGroup.HbefaRoadTypeSource.fromLinkAttributes);
        ecg.setDetailedVsAverageLookupBehavior(EmissionsConfigGroup.DetailedVsAverageLookupBehavior.directlyTryAverageTable);
        ecg.setHbefaVehicleDescriptionSource(EmissionsConfigGroup.HbefaVehicleDescriptionSource.fromVehicleTypeDescription);
        ecg.setHandlesHighAverageSpeeds(true);
        ecg.setAverageColdEmissionFactorsFile(coldEmissionFile);
        ecg.setAverageWarmEmissionFactorsFile(warmEmissionFile);

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

        EventWriterXML emissionEventWriter = new EventWriterXML(eventsFileWithEmission);
        emissionModule.getEmissionEventsManager().addHandler(emissionEventWriter);
        eventsManager.initProcessing();
        MatsimEventsReader matsimEventsReader = new MatsimEventsReader(eventsManager);
        matsimEventsReader.readFile(eventsFileWithoutEmissions);
        eventsManager.finishProcessing();
        emissionEventWriter.closeFile();
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
