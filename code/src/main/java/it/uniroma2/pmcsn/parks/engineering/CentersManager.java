package it.uniroma2.pmcsn.parks.engineering;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import it.uniroma2.pmcsn.parks.engineering.interfaces.CenterInterface;
import it.uniroma2.pmcsn.parks.model.server.Attraction;
import it.uniroma2.pmcsn.parks.model.server.Restaurant;

public class CentersManager<T> {

    private Map<String, CenterInterface<T>> centerMap;

    public CentersManager() {
        centerMap = new HashMap<>();
    }

    public void addCenterList(List<CenterInterface<T>> centerList) {

        for (CenterInterface<T> center : centerList) {
            this.centerMap.put(center.getName(), center);
        }
    }

    public CenterInterface<T> getCenterByName(String name) {
        if (!centerMap.containsKey(name)) {
            throw new RuntimeException("Center " + name + " does not exist");
        }
        return centerMap.get(name);
    }

    public Collection<CenterInterface<T>> getAllCenters() {
        return centerMap.values();
    }

    public List<Attraction> getAttractions() {
        List<Attraction> attractions = new ArrayList<>();
        for (CenterInterface<T> center : centerMap.values()) {
            if (center instanceof Attraction) {
                attractions.add((Attraction) center);
            }
        }
        return attractions;
    }

    public List<Restaurant> getRestaurants() {
        List<Restaurant> restaurants = new ArrayList<>();
        for (CenterInterface<T> center : centerMap.values()) {
            if (center instanceof Restaurant) {
                restaurants.add((Restaurant) center);
            }
        }
        return restaurants;

    }

}