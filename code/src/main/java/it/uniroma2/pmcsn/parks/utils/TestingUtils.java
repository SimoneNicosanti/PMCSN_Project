package it.uniroma2.pmcsn.parks.utils;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.tuple.Triple;

import it.uniroma2.pmcsn.parks.engineering.Constants;
import it.uniroma2.pmcsn.parks.engineering.factory.CenterFactory;
import it.uniroma2.pmcsn.parks.engineering.singleton.ClockHandler;
import it.uniroma2.pmcsn.parks.engineering.singleton.ConfigHandler;
import it.uniroma2.pmcsn.parks.model.Interval;
import it.uniroma2.pmcsn.parks.model.server.concreate_servers.Attraction;
import it.uniroma2.pmcsn.parks.model.server.concreate_servers.Entrance;
import it.uniroma2.pmcsn.parks.model.server.concreate_servers.ExitCenter;
import it.uniroma2.pmcsn.parks.model.server.concreate_servers.Restaurant;

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
        return (Entrance) CenterFactory.buildEntrance(Constants.ENTRANCE, 3, 0.5);
    }

    public static ExitCenter createTestingExit() {
        return new ExitCenter(Constants.EXIT);
    }

    // public static void initTestingProbabilities() {
    // Interval interval = ClockHandler.getInstance().getCurrentInterval();
    // List<Triple<String, Interval, Double>> list = List.of(
    // Triple.of(Constants.ATTRACTION_ROUTING_NODE, interval, 0.8),
    // Triple.of(Constants.RESTAURANT_ROUTING_NODE, interval, 0.1),
    // Triple.of("Stupid_Restaurant", interval, 0.4),
    // Triple.of("Smart_Restaurant", interval, 0.6));

    // for (Triple<String, Interval, Double> triple : list) {
    // ConfigHandler.getInstance().changeProbability(triple.getLeft(),
    // triple.getMiddle(), triple.getRight());
    // }
    // }

}
