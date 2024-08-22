package it.uniroma2.pmcsn.parks.utils;

import java.util.ArrayList;
import java.util.List;

import it.uniroma2.pmcsn.parks.engineering.factory.NetworkBuilder;
import it.uniroma2.pmcsn.parks.engineering.interfaces.Center;
import it.uniroma2.pmcsn.parks.model.job.RiderGroup;
import it.uniroma2.pmcsn.parks.model.server.concrete_servers.Attraction;
import it.uniroma2.pmcsn.parks.model.server.concrete_servers.StatsCenter;

public class ExperimentsUtils {
    public static List<StatsCenter> getAllStatsAttractions(NetworkBuilder networkBuilder) {
        List<StatsCenter> attractionStatsCenters = new ArrayList<>();
        for (Center<RiderGroup> statsCenter : networkBuilder.getAllCenters()) {
            Center<RiderGroup> center = ((StatsCenter) statsCenter).getCenter();

            if (center instanceof Attraction) {
                attractionStatsCenters.add((StatsCenter) statsCenter);
            }
        }

        return attractionStatsCenters;
    }
}
