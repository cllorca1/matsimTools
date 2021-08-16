package emission;


import emission.data.AnalyzedLink;
import emission.data.AnalyzedVehicle;
import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.network.Link;
import org.matsim.api.core.v01.network.Network;
import org.matsim.contrib.emissions.*;
import org.matsim.contrib.emissions.events.ColdEmissionEvent;
import org.matsim.contrib.emissions.events.ColdEmissionEventHandler;
import org.matsim.contrib.emissions.events.WarmEmissionEvent;
import org.matsim.contrib.emissions.events.WarmEmissionEventHandler;
import org.matsim.vehicles.Vehicle;

import java.util.HashMap;
import java.util.Map;

public class LinkEmissionHandler implements WarmEmissionEventHandler, ColdEmissionEventHandler {



    private Network network;
    private Map<Id<Vehicle>, AnalyzedVehicle> emmisionsByVehicle;
    private Map<Id<Link>, AnalyzedLink> emmisionsByLink;
    Map<CountVehicleType, Map<String, Map<Pollutant, Double>>> emissionsByVehicleAndRoadType = new HashMap<>();

    public LinkEmissionHandler(Network network) {
        this.network = network;
        emmisionsByLink = new HashMap<>();
        emmisionsByVehicle = new HashMap<>();
        for(CountVehicleType countVehicleType : CountVehicleType.values()){
            emissionsByVehicleAndRoadType.put(countVehicleType, new HashMap<>());
        }
    }

    @Override
    public void handleEvent(WarmEmissionEvent event) {
        Id<Link> linkId = event.getLinkId();
        Link matsimLink = network.getLinks().get(linkId);
        String linkType = matsimLink.getAttributes().getAttribute("admin_type").toString();
        CountVehicleType countVehicleType = getTypeFromId(event.getVehicleId().toString());
        emmisionsByLink.putIfAbsent(linkId,new AnalyzedLink(linkId, matsimLink));

        Map<Pollutant, Double> warmEmissionsOriginal = event.getWarmEmissions();
        emissionsByVehicleAndRoadType.get(countVehicleType).putIfAbsent(linkType, new HashMap<>());
        Map<Pollutant, Double> currentEmissionValue = emissionsByVehicleAndRoadType.get(countVehicleType).get(linkType);
        Map<String, Double> warmEmissions = new HashMap<>();
        for (Map.Entry<Pollutant, Double> entry: warmEmissionsOriginal.entrySet()){
            warmEmissions.put(entry.getKey().toString(), entry.getValue());
            currentEmissionValue.putIfAbsent(entry.getKey(), .0);
            double newEmissionValue = currentEmissionValue.get(entry.getKey()) + entry.getValue();
            currentEmissionValue.put(entry.getKey(), newEmissionValue);
        }

        if (emmisionsByLink.get(linkId).getWarmEmissions().isEmpty()){
            emmisionsByLink.get(linkId).getWarmEmissions().putAll(warmEmissions);
        } else {
            Map<String, Double> currentEmissions = emmisionsByLink.get(linkId).getWarmEmissions();
            for (String pollutant : currentEmissions.keySet()){
                currentEmissions.put(pollutant, currentEmissions.get(pollutant) + warmEmissions.get(pollutant));
            }
            emmisionsByLink.get(linkId).getWarmEmissions().putAll(currentEmissions);
        }

//        Id<Vehicle> vehicleId = event.getVehicleId();
//        emmisionsByVehicle.putIfAbsent(vehicleId, new AnalyzedVehicle(vehicleId));
//        emmisionsByVehicle.get(vehicleId).addDistanceTravelled(matsimLink.getLength());
//        emmisionsByVehicle.get(vehicleId).registerPointOfTime(event.getTime());
//        //todo currently we get operating times based on free flow conditions
//        double speed_ms;
//        if (vehicleId.toString().contains("cargoBike")){
//            speed_ms = 5.6;
//        } else {
//            speed_ms = matsimLink.getFreespeed();
//        }
//        emmisionsByVehicle.get(vehicleId).addOperatingTime(matsimLink.getLength() / speed_ms);
//
//
//
//        if (emmisionsByVehicle.get(vehicleId).getWarmEmissions().isEmpty()){
//            emmisionsByVehicle.get(vehicleId).getWarmEmissions().putAll(warmEmissions);
//        } else {
//            Map<String, Double> currentEmissions = emmisionsByVehicle.get(vehicleId).getWarmEmissions();
//            for (String pollutant : currentEmissions.keySet()){
//                currentEmissions.put(pollutant, currentEmissions.get(pollutant) + warmEmissions.get(pollutant));
//            }
//            emmisionsByVehicle.get(vehicleId).getWarmEmissions().putAll(currentEmissions);
//        }
    }


    private static CountVehicleType getTypeFromId(String vehicleId){
        //todo review this
        if(vehicleId.contains("truck")){
            return CountVehicleType.truck;
        } else if (vehicleId.contains("ld")) {
            return CountVehicleType.car_ld;
        } else {
            return CountVehicleType.car_sd;
        }
    }



    @Override
    public void reset(int iteration) {

    }

    @Override
    public void handleEvent(ColdEmissionEvent event) {

//        Id<Link> linkId = event.getLinkId();
//        Link matsimLink = network.getLinks().get(linkId);
//        emmisionsByLink.putIfAbsent(linkId,new AnalyzedLink(linkId, matsimLink));
//
//
//        Map<Pollutant, Double> coldEmissionsOriginal = event.getColdEmissions();
//        Map<String, Double> coldEmissions = new HashMap<>();
//        for (Map.Entry<Pollutant, Double> entry: coldEmissionsOriginal.entrySet()){
//            coldEmissions.put(entry.getKey().toString(), entry.getValue());
//        }
//
//
//        if (emmisionsByLink.get(linkId).getColdEmissions().isEmpty()){
//            emmisionsByLink.get(linkId).getColdEmissions().putAll(coldEmissions);
//        } else {
//            Map<String, Double> currentEmissions = emmisionsByLink.get(linkId).getColdEmissions();
//            for (String pollutant : currentEmissions.keySet()){
//                currentEmissions.put(pollutant, currentEmissions.get(pollutant) + coldEmissions.get(pollutant));
//            }
//            emmisionsByLink.get(linkId).getColdEmissions().putAll(currentEmissions);
//        }
//
//        Id<Vehicle> vehicleId = event.getVehicleId();
//        emmisionsByVehicle.putIfAbsent(vehicleId, new AnalyzedVehicle(vehicleId));
//        emmisionsByVehicle.get(vehicleId).registerPointOfTime(event.getTime());
//
//        if (emmisionsByVehicle.get(vehicleId).getColdEmissions().isEmpty()){
//            emmisionsByVehicle.get(vehicleId).getColdEmissions().putAll(coldEmissions);
//        } else {
//            Map<String, Double> currentEmissions = emmisionsByVehicle.get(vehicleId).getColdEmissions();
//            for (String pollutant : currentEmissions.keySet()){
//                currentEmissions.put(pollutant, currentEmissions.get(pollutant) +coldEmissions.get(pollutant));
//            }
//            emmisionsByVehicle.get(vehicleId).getColdEmissions().putAll(currentEmissions);
//        }
    }

    public Map<Id<Vehicle>, AnalyzedVehicle> getEmmisionsByVehicle() {
        return emmisionsByVehicle;
    }

    public Map<Id<Link>, AnalyzedLink> getEmmisionsByLink() {
        return emmisionsByLink;
    }

    public Map<CountVehicleType, Map<String, Map<Pollutant, Double>>> getEmissionsByVehicleAndRoadType() {
        return emissionsByVehicleAndRoadType;
    }
}
