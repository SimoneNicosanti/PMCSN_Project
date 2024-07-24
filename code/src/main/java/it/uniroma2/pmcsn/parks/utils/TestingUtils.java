package it.uniroma2.pmcsn.parks.utils;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.tuple.Pair;

import it.uniroma2.pmcsn.parks.engineering.Config;
import it.uniroma2.pmcsn.parks.engineering.singleton.ProbabilityManager;
import it.uniroma2.pmcsn.parks.model.job.RiderGroup;
import it.uniroma2.pmcsn.parks.model.server.Attraction;
import it.uniroma2.pmcsn.parks.model.server.Center;
import it.uniroma2.pmcsn.parks.model.server.Entrance;
import it.uniroma2.pmcsn.parks.model.server.Restaurant;

public class TestingUtils {

    public static List<Center<RiderGroup>> createTestingCentersList() {
        List<Center<RiderGroup>> centerList = new ArrayList<>();

        Entrance entrance = new Entrance(Config.ENTRANCE, 3);
        centerList.add(entrance);

        Attraction attraction1 = new Attraction("Attraction1", 10, 3, 10);
        centerList.add(attraction1);
        Attraction attraction2 = new Attraction("Attraction2", 100, 5, 5);
        centerList.add(attraction2);

        String restName1 = "Stupid Restaurant";
        String restName2 = "Smart Restaurant";
        Restaurant rest1 = new Restaurant(restName1, 10, 1, 10);
        centerList.add(rest1);

        Restaurant rest2 = new Restaurant(restName2, 100, 2, 5);
        centerList.add(rest2);

        ProbabilityManager manager = ProbabilityManager.getInstance();
        manager.changeProbabilities(List.of(
                Pair.of(Config.ATTRACTION_ROUTING_NODE, 0.7),
                Pair.of(Config.RESTAURANT_ROUTING_NODE, 0.2),
                Pair.of(rest1.getName(), 0.4),
                Pair.of(rest2.getName(), 0.6)));

        return centerList;
    }

}
