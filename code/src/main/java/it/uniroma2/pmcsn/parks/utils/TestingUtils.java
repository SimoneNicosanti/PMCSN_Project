package it.uniroma2.pmcsn.parks.utils;

import java.util.ArrayList;
import java.util.List;

import it.uniroma2.pmcsn.parks.engineering.Config;
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

        Restaurant rest1 = new Restaurant("Stupid Restaurant", 10, 1, 10);
        centerList.add(rest1);
        Restaurant rest2 = new Restaurant("Smart Restaurant", 100, 2, 5);
        centerList.add(rest2);

        return centerList;
    }

}
