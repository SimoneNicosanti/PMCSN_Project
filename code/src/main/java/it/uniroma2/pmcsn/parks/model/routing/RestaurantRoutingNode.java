package it.uniroma2.pmcsn.parks.model.routing;

import java.util.List;

import it.uniroma2.pmcsn.parks.engineering.Constants;
import it.uniroma2.pmcsn.parks.engineering.interfaces.Center;
import it.uniroma2.pmcsn.parks.engineering.interfaces.RoutingNode;
import it.uniroma2.pmcsn.parks.engineering.singleton.RandomHandler;
import it.uniroma2.pmcsn.parks.model.job.RiderGroup;
import it.uniroma2.pmcsn.parks.model.routing.probabilities.RestRouterProbabilities;

public class RestaurantRoutingNode implements RoutingNode<RiderGroup> {

    private List<Center<RiderGroup>> restaurantList;

    public RestaurantRoutingNode(List<Center<RiderGroup>> restaurantList) {
        this.restaurantList = restaurantList;
    }

    @Override
    public Center<RiderGroup> route(RiderGroup riderGroup) {

        double routingProb = RandomHandler.getInstance().getRandom(Constants.RESTAURANT_ROUTING_NODE);

        // Computing normalized probability array for each Restaurant
        RestRouterProbabilities probabilities = new RestRouterProbabilities(restaurantList);
        probabilities.compute(riderGroup);

        // Select route index based on probability
        int routeIdx = probabilities.getRouteIdxFromRand(routingProb);
        return restaurantList.get(routeIdx);
    }

    @Override
    public String getName() {
        return Constants.RESTAURANT_ROUTING_NODE;
    }

}