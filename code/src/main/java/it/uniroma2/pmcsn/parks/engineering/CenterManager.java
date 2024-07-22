package it.uniroma2.pmcsn.parks.engineering;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import it.uniroma2.pmcsn.parks.model.server.Center;

public class CenterManager<T> {

    private Map<String, Center<T>> centerMap;

    public CenterManager() {
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

}