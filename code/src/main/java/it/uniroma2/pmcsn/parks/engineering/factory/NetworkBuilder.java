package it.uniroma2.pmcsn.parks.engineering.factory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import it.uniroma2.pmcsn.parks.SimulationMode;
import it.uniroma2.pmcsn.parks.engineering.Constants;
import it.uniroma2.pmcsn.parks.engineering.interfaces.Center;
import it.uniroma2.pmcsn.parks.engineering.interfaces.RoutingNode;
import it.uniroma2.pmcsn.parks.model.job.RiderGroup;
import it.uniroma2.pmcsn.parks.model.routing.AttractionRoutingNode;
import it.uniroma2.pmcsn.parks.model.routing.NetworkRoutingNode;
import it.uniroma2.pmcsn.parks.model.routing.RestaurantRoutingNode;
import it.uniroma2.pmcsn.parks.model.server.ServerType;
import it.uniroma2.pmcsn.parks.model.server.concrete_servers.StatsCenter;
import it.uniroma2.pmcsn.parks.model.server.concrete_servers.Attraction;
import it.uniroma2.pmcsn.parks.model.server.concrete_servers.Entrance;
import it.uniroma2.pmcsn.parks.model.server.concrete_servers.ExitCenter;
import it.uniroma2.pmcsn.parks.model.server.concrete_servers.Restaurant;

public class NetworkBuilder {

    private Map<String, Center<RiderGroup>> centerMap;
    private ExitCenter exitCenter;

    public NetworkBuilder() {
        this.centerMap = new HashMap<>();
    }

    public void buildNetwork() {

        this.exitCenter = new ExitCenter(Constants.EXIT);

        List<Center<RiderGroup>> restaurants = buildListOfCenters(ServerType.RESTAURANT);
        List<Center<RiderGroup>> attractions = buildListOfCenters(ServerType.ATTRACTION);
        List<Center<RiderGroup>> entrances = buildListOfCenters(ServerType.ENTRANCE);

        // Create the routing nodes
        RoutingNode<RiderGroup> attractionRoutingNode = new AttractionRoutingNode(attractions);
        RoutingNode<RiderGroup> restaurantsRoutingNode = new RestaurantRoutingNode(restaurants);
        RoutingNode<RiderGroup> networkRoutingNode = new NetworkRoutingNode(attractionRoutingNode,
                restaurantsRoutingNode, this.exitCenter);

        for (Center<RiderGroup> attraction : attractions) {
            attraction.setNextRoutingNode(networkRoutingNode);
            this.centerMap.put(attraction.getName(), attraction);
        }
        for (Center<RiderGroup> restaurant : restaurants) {
            restaurant.setNextRoutingNode(networkRoutingNode);
            this.centerMap.put(restaurant.getName(), restaurant);
        }

        Center<RiderGroup> entrance = entrances.get(0);
        this.centerMap.put(Constants.ENTRANCE, entrance);
        entrance.setNextRoutingNode(networkRoutingNode);

    }

    private List<Center<RiderGroup>> buildListOfCenters(ServerType serverType) {
        String buildFileName = getBuildFileName(serverType);
        List<Center<RiderGroup>> centers = new ArrayList<>();
        switch (serverType) {
            case ATTRACTION:
                List<Attraction> attractions = CenterFactory.buildAttractionsFromFile(buildFileName);
                centers.addAll(attractions);
                break;

            case RESTAURANT:
                List<Restaurant> restaurants = CenterFactory.buildRestaurantsFromFile(buildFileName);
                centers.addAll(restaurants);
                break;

            case ENTRANCE:
                List<Entrance> entrances = CenterFactory.buildEntranceFromFile(buildFileName);
                centers.addAll(entrances);
                break;

        }

        List<Center<RiderGroup>> statsCenters = new ArrayList<>();
        for (Center<RiderGroup> center : centers) {
            statsCenters.add(new StatsCenter(center));
        }

        return statsCenters;

    }

    private String getBuildFileName(ServerType serverType) {
        switch (serverType) {
            case ENTRANCE:
                if (Constants.MODE == SimulationMode.VERIFICATION) {
                    return Constants.VERIFICATION_ENTRANCE_FILE;
                } else {
                    return Constants.ENTRANCE_FILE;
                }

            case ATTRACTION:
                return switch (Constants.MODE) {
                    case NORMAL -> Constants.ATTRACTION_FILE;
                    case VERIFICATION -> Constants.VERIFICATION_ATTRACTION_FILE;
                    case VALIDATION -> Constants.VALIDATION_ATTRACTION_FILE;
                    case CONSISTENCY_CHECK -> Constants.CONSISTENCY_CHECKS_ATTRACTION_FILE;
                };

            case RESTAURANT:
                if (Constants.MODE == SimulationMode.VERIFICATION) {
                    return Constants.VERIFICATION_RESTAURANT_FILE;
                } else {
                    return Constants.RESTAURANT_FILE;
                }
        }

        return "";

    }

    public Center<RiderGroup> getCenterByName(String centerName) {

        if (centerName.equals(exitCenter.getName())) {
            return this.exitCenter;
        }

        if (!centerMap.containsKey(centerName)) {
            throw new RuntimeException("Center " + centerName + " does not exist");
        }
        return centerMap.get(centerName);
    }

    public List<Center<RiderGroup>> getAllCenters() {
        return List.copyOf(centerMap.values());
    }

    public ExitCenter getExitCenter() {
        return this.exitCenter;
    }

}
