package emission;

import org.matsim.api.core.v01.Scenario;
import org.matsim.contrib.emissions.EmissionModule;
import org.matsim.contrib.emissions.utils.EmissionsConfigGroup;
import org.matsim.core.api.experimental.events.EventsManager;
import org.matsim.core.config.Config;
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


    public void run(String configFile, String outDirectory, String eventsFileWithoutEmissions, String eventsFileWithEmission,
                    String individualVehicleFile, String populationFile, String networkFile) {
        if ( config==null ) {
            this.prepareConfig(configFile, outDirectory, individualVehicleFile, networkFile, populationFile) ;
        }
        Scenario scenario = ScenarioUtils.loadScenario(config);
        EventsManager eventsManager = EventsUtils.createEventsManager();

        AbstractModule module = new AbstractModule(){
            @Override
            public void install(){
                bind( Scenario.class ).toInstance( scenario );
                bind( EventsManager.class ).toInstance( eventsManager );
                bind( EmissionModule.class ) ;
            }
        };

        com.google.inject.Injector injector = Injector.createInjector(config, module);

        EmissionModule emissionModule = injector.getInstance(EmissionModule.class);

        EventWriterXML emissionEventWriter = new EventWriterXML(eventsFileWithEmission);
        emissionModule.getEmissionEventsManager().addHandler(emissionEventWriter);

        MatsimEventsReader matsimEventsReader = new MatsimEventsReader(eventsManager);
        matsimEventsReader.readFile(eventsFileWithoutEmissions);

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
