package it.uniroma2.pmcsn.parks.engineering.factory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import it.uniroma2.pmcsn.parks.engineering.Config;
import it.uniroma2.pmcsn.parks.engineering.interfaces.RoutingNode;
import it.uniroma2.pmcsn.parks.model.job.RiderGroup;
import it.uniroma2.pmcsn.parks.model.routing.AttractionRoutingNode;
import it.uniroma2.pmcsn.parks.model.routing.NetworkRoutingNode;
import it.uniroma2.pmcsn.parks.model.routing.RestaurantRoutingNode;
import it.uniroma2.pmcsn.parks.model.server.Attraction;
import it.uniroma2.pmcsn.parks.model.server.Center;
import it.uniroma2.pmcsn.parks.model.server.Restaurant;
import it.uniroma2.pmcsn.parks.utils.TestingUtils;

public class NetworkBuilder {

    private Map<String, Center> centerMap;

    public NetworkBuilder() {
        this.centerMap = new HashMap<>();
    }

    public void buildNetwork() {

        // Just for testing, delete once the system is live
        List<Center> centers = TestingUtils.createTestingCentersList();
        this.addCenterList(centers);

        // Get the attractions, restaurants and entrance
        List<Attraction> attractions = getAttractions();
        List<Restaurant> restaurants = getRestaurants();
        Center entrance = getCenterByName(Config.ENTRANCE);

        // Create the routing nodes
        RoutingNode<RiderGroup> attractionRoutingNode = new AttractionRoutingNode(attractions);
        RoutingNode<RiderGroup> restaurantsRoutingNode = new RestaurantRoutingNode(restaurants);
        RoutingNode<RiderGroup> networkRoutingNode = new NetworkRoutingNode(attractionRoutingNode,
                restaurantsRoutingNode);

        for (Attraction attraction : attractions) {
            attraction.setNextRoutingNode(networkRoutingNode);
        }
        for (Restaurant restaurant : restaurants) {
            restaurant.setNextRoutingNode(restaurantsRoutingNode);
        }
        entrance.setNextRoutingNode(networkRoutingNode);
    }

    public void addCenterList(List<Center> centerList) {

        for (Center center : centerList) {
            this.centerMap.put(center.getName(), center);
        }
    }

    public Center getCenterByName(String name) {
        if (!centerMap.containsKey(name)) {
            throw new RuntimeException("Center " + name + " does not exist");
        }
        return centerMap.get(name);
    }

    public Collection<Center> getAllCenters() {
        return centerMap.values();
    }

    public List<Attraction> getAttractions() {
        List<Attraction> attractions = new ArrayList<>();
        for (Center center : centerMap.values()) {
            if (center instanceof Attraction) {
                attractions.add((Attraction) center);
            }
        }
        return attractions;
    }

    public List<Restaurant> getRestaurants() {
        List<Restaurant> restaurants = new ArrayList<>();
        for (Center center : centerMap.values()) {
            if (center instanceof Restaurant) {
                restaurants.add((Restaurant) center);
            }
        }
        return restaurants;

    }

}
