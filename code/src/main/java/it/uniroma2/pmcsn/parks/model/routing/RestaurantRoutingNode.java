package it.uniroma2.pmcsn.parks.model.routing;

import java.util.List;

import it.uniroma2.pmcsn.parks.engineering.Config;
import it.uniroma2.pmcsn.parks.engineering.interfaces.RoutingNode;
import it.uniroma2.pmcsn.parks.engineering.singleton.RandomHandler;
import it.uniroma2.pmcsn.parks.model.job.RiderGroup;
import it.uniroma2.pmcsn.parks.model.routing.probabilities.RestRouterProbabilities;
import it.uniroma2.pmcsn.parks.model.server.AbstractCenter;
import it.uniroma2.pmcsn.parks.model.server.concreate_servers.Restaurant;

public class RestaurantRoutingNode implements RoutingNode<RiderGroup> {

    private List<Restaurant> restaurantList;

    public RestaurantRoutingNode(List<Restaurant> restaurantList) {
        this.restaurantList = restaurantList;
    }

    @Override
    public AbstractCenter route(RiderGroup riderGroup) {

        double routingProb = RandomHandler.getInstance().getRandom(Config.RESTAURANT_ROUTING_NODE);

        // Computing normalized probability array for each Restaurant
        RestRouterProbabilities probabilities = new RestRouterProbabilities(restaurantList);
        probabilities.compute(riderGroup);

        // Select route index based on probability
        int routeIdx = probabilities.getRouteIdxFromRand(routingProb);
        return restaurantList.get(routeIdx);
    }

    @Override
    public String getName() {
        return Config.RESTAURANT_ROUTING_NODE;
    }

}