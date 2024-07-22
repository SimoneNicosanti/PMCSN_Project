package it.uniroma2.pmcsn.parks.model.routing;

import java.util.ArrayList;
import java.util.List;

import it.uniroma2.pmcsn.parks.engineering.Config;
import it.uniroma2.pmcsn.parks.engineering.interfaces.RoutingNode;
import it.uniroma2.pmcsn.parks.engineering.singleton.ProbabilityManager;
import it.uniroma2.pmcsn.parks.engineering.singleton.RandomHandler;
import it.uniroma2.pmcsn.parks.model.job.RiderGroup;
import it.uniroma2.pmcsn.parks.model.server.Center;
import it.uniroma2.pmcsn.parks.model.server.Restaurant;

public class RestaurantRoutingNode implements RoutingNode<RiderGroup> {

    private List<Restaurant> restaurantList;
    private int randomStreamIdx;

    public RestaurantRoutingNode(List<Restaurant> restaurantList) {
        this.restaurantList = restaurantList;
        this.randomStreamIdx = RandomHandler.getInstance().getNewStreamIndex();
    }

    @Override
    public Center<RiderGroup> route(RiderGroup riderGroup) {

        // Computing normalized probability array for each Restaurant
        List<Double> probabilityArray = computeProbabilityArray(riderGroup);

        double routingProb = RandomHandler.getInstance().getRandom(randomStreamIdx);
        double cumulativeSum = probabilityArray.get(0);
        Center<RiderGroup> routeRestaurant = null;
        for (int i = 0; i < restaurantList.size(); i++) {
            if (routingProb < cumulativeSum) {
                routeRestaurant = restaurantList.get(i);
                break;
            }
            cumulativeSum += probabilityArray.get(i);
        }

        return routeRestaurant;
    }

    @Override
    public String getName() {
        return Config.RESTAURANT_ROUTING_NODE ;
    }

    private List<Double> computeProbabilityArray(RiderGroup riderGroup) {
        List<Double> probabilityArray = new ArrayList<>();
        double sum = 0.0;
        ProbabilityManager manager = ProbabilityManager.getInstance();

        //TODO different routing for restaurant? Based on queue occupation?
        for (Restaurant restaurant : restaurantList) {
            probabilityArray.add(manager.getProbability(restaurant.getName()));
        }

        // Normalizing probability array
        for (int idx = 0; idx < probabilityArray.size(); idx++) {
            double notNormalized = probabilityArray.get(idx);
            double normalized = notNormalized / sum;

            probabilityArray.set(idx, normalized);
        }
        
        return probabilityArray;
    }
}