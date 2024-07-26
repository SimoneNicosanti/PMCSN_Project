package it.uniroma2.pmcsn.parks.utils;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.tuple.Pair;

import it.uniroma2.pmcsn.parks.engineering.Config;
import it.uniroma2.pmcsn.parks.engineering.factory.CenterFactory;
import it.uniroma2.pmcsn.parks.engineering.singleton.ProbabilityManager;
import it.uniroma2.pmcsn.parks.model.server.Attraction;
import it.uniroma2.pmcsn.parks.model.server.Entrance;
import it.uniroma2.pmcsn.parks.model.server.ExitCenter;
import it.uniroma2.pmcsn.parks.model.server.Restaurant;

public class TestingUtils {

    public static List<Attraction> createTestingAttractions() {
        List<Attraction> attractions = new ArrayList<>();

        Attraction attraction1 = CenterFactory.buildAttraction("Attraction_1", 10, 3, 10);
        attractions.add(attraction1);
        Attraction attraction2 = CenterFactory.buildAttraction("Attraction_2", 100, 5, 5);
        attractions.add(attraction2);

        return attractions;
    }

    public static List<Restaurant> createTestingRestaurants() {
        List<Restaurant> restaurants = new ArrayList<>();

        String restName1 = "Stupid_Restaurant";
        String restName2 = "Smart_Restaurant";
        Restaurant rest1 = CenterFactory.buildRestaurant(restName1, 100, 1, 10);
        restaurants.add(rest1);

        Restaurant rest2 = CenterFactory.buildRestaurant(restName2, 100, 2, 5);
        restaurants.add(rest2);

        return restaurants;
    }

    public static Entrance createTestingEntrance() {
        return (Entrance) CenterFactory.buildEntrance(Config.ENTRANCE, 3);
    }

    public static ExitCenter createTestingExit() {
        return new ExitCenter(Config.EXIT);
    }

    public static void initTestingProbabilities() {
        ProbabilityManager.getInstance().changeProbabilities(List.of(
                Pair.of(Config.ATTRACTION_ROUTING_NODE, 0.8),
                Pair.of(Config.RESTAURANT_ROUTING_NODE, 0.1),
                Pair.of("Stupid_Restaurant", 0.4),
                Pair.of("Smart_Restaurant", 0.6)));
    }

}
