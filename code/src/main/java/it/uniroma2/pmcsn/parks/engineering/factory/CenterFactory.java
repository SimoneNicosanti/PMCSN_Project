package it.uniroma2.pmcsn.parks.engineering.factory;

import it.uniroma2.pmcsn.parks.model.server.Attraction;
import it.uniroma2.pmcsn.parks.model.server.Entrance;
import it.uniroma2.pmcsn.parks.model.server.Restaurant;

public class CenterFactory {

    public static Restaurant buildRestaurant(String name, int numberOfSeats, double popularity,
            double avgDuration) {
        return new Restaurant(name, numberOfSeats, popularity, avgDuration);
    }

    public static Attraction buildAttraction(String name, int numberOfSeats, double popularity,
            double avgDuration) {
        return new Attraction(name, numberOfSeats, popularity, avgDuration);
    }

    public static Entrance buildEntrance(String name, int numberOfSeats) {
        return new Entrance(name, numberOfSeats);
    }
}
