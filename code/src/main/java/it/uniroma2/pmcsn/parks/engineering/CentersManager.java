package it.uniroma2.pmcsn.parks.engineering;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import it.uniroma2.pmcsn.parks.engineering.interfaces.Center;
import it.uniroma2.pmcsn.parks.model.server.concreate_servers.Attraction;
import it.uniroma2.pmcsn.parks.model.server.concreate_servers.Restaurant;

public class CentersManager<T> {

    private Map<String, Center<T>> centerMap;

    public CentersManager() {
        centerMap = new HashMap<>();
    }

    public void addCenterList(List<Center<T>> centerList) {

        for (Center<T> center : centerList) {
            this.centerMap.put(center.getName(), center);
        }
    }

    public Center<T> getCenterByName(String name) {
        if (!centerMap.containsKey(name)) {
            throw new RuntimeException("Center " + name + " does not exist");
        }
        return centerMap.get(name);
    }

    public Collection<Center<T>> getAllCenters() {
        return centerMap.values();
    }

    public List<Attraction> getAttractions() {
        List<Attraction> attractions = new ArrayList<>();
        for (Center<T> center : centerMap.values()) {
            if (center instanceof Attraction) {
                attractions.add((Attraction) center);
            }
        }
        return attractions;
    }

    public List<Restaurant> getRestaurants() {
        List<Restaurant> restaurants = new ArrayList<>();
        for (Center<T> center : centerMap.values()) {
            if (center instanceof Restaurant) {
                restaurants.add((Restaurant) center);
            }
        }
        return restaurants;

    }

}