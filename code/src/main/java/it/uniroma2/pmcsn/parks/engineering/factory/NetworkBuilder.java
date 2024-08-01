package it.uniroma2.pmcsn.parks.engineering.factory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import it.uniroma2.pmcsn.parks.engineering.Constants;
import it.uniroma2.pmcsn.parks.engineering.interfaces.Center;
import it.uniroma2.pmcsn.parks.engineering.interfaces.RoutingNode;
import it.uniroma2.pmcsn.parks.model.job.RiderGroup;
import it.uniroma2.pmcsn.parks.model.routing.AttractionRoutingNode;
import it.uniroma2.pmcsn.parks.model.routing.NetworkRoutingNode;
import it.uniroma2.pmcsn.parks.model.routing.RestaurantRoutingNode;
import it.uniroma2.pmcsn.parks.model.server.concrete_servers.Attraction;
import it.uniroma2.pmcsn.parks.model.server.concrete_servers.ExitCenter;
import it.uniroma2.pmcsn.parks.model.server.concrete_servers.Restaurant;

public class NetworkBuilder {

    private Map<String, Center<RiderGroup>> centerMap;

    public NetworkBuilder() {
        this.centerMap = new HashMap<>();
    }

    public void buildNetwork() {
        // Just for testing, delete once the system is live
        String restaurantsFileName;
        if (Constants.VERIFICATION_MODE) {
            restaurantsFileName = "RestaurantsDataVerify.csv";
        } else {
            restaurantsFileName = "RestaurantsData.csv";
        }
        List<Restaurant> restaurants = CenterFactory.buildRestaurantsFromFile(restaurantsFileName); // TestingUtils.createTestingRestaurants();

        String attractionsFileName;
        if (Constants.VERIFICATION_MODE) {
            attractionsFileName = Constants.VERIFICATION_ATTRACTION_FILE;
        } else {
            attractionsFileName = Constants.ATTRACTION_FILE;
        }

        List<Attraction> attractions = CenterFactory.buildAttractionsFromFile(attractionsFileName); // TestingUtils.createTestingAttractions();

        Center<RiderGroup> entranceCenter = CenterFactory.buildEntranceFromFile("EntranceData.csv").get(0); // TestingUtils.createTestingEntrance();
        Center<RiderGroup> exitCenter = new ExitCenter(Constants.EXIT);

        // Create the routing nodes
        RoutingNode<RiderGroup> attractionRoutingNode = new AttractionRoutingNode(attractions);
        RoutingNode<RiderGroup> restaurantsRoutingNode = new RestaurantRoutingNode(restaurants);
        RoutingNode<RiderGroup> networkRoutingNode = new NetworkRoutingNode(attractionRoutingNode,
                restaurantsRoutingNode, exitCenter);

        for (Attraction attraction : attractions) {
            attraction.setNextRoutingNode(networkRoutingNode);
            this.centerMap.put(attraction.getName(), attraction);
        }
        for (Restaurant restaurant : restaurants) {
            restaurant.setNextRoutingNode(networkRoutingNode);
            this.centerMap.put(restaurant.getName(), restaurant);
        }

        this.centerMap.put(Constants.ENTRANCE, entranceCenter);

        entranceCenter.setNextRoutingNode(networkRoutingNode);
    }

    public Center<RiderGroup> getCenterByName(String name) {
        if (!centerMap.containsKey(name)) {
            throw new RuntimeException("Center " + name + " does not exist");
        }
        return centerMap.get(name);
    }

    public List<Center<RiderGroup>> getAllCenters() {
        return List.copyOf(centerMap.values());
    }

}
