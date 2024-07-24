package it.uniroma2.pmcsn.parks.engineering.factory;

import it.uniroma2.pmcsn.parks.engineering.interfaces.CenterInterface;
import it.uniroma2.pmcsn.parks.model.job.RiderGroup;
import it.uniroma2.pmcsn.parks.model.server.Attraction;
import it.uniroma2.pmcsn.parks.model.server.Entrance;
import it.uniroma2.pmcsn.parks.model.server.Restaurant;
import it.uniroma2.pmcsn.parks.model.server.StatsCenter;

public class CenterFactory {

    public static CenterInterface<RiderGroup> buildRestaurant(String name, int numberOfSeats, double popularity,
            double avgDuration) {
        return new StatsCenter(new Restaurant(name, numberOfSeats, popularity, avgDuration)); 
    }

    public static CenterInterface<RiderGroup> buildAttraction(String name, int numberOfSeats, double popularity,
            double avgDuration) {
        return new StatsCenter(new Attraction(name, numberOfSeats, popularity, avgDuration)); 
    }

    public static CenterInterface<RiderGroup> buildEntrance(String name, int numberOfSeats) {
        return new StatsCenter(new Entrance(name, numberOfSeats)); 
    }
}
