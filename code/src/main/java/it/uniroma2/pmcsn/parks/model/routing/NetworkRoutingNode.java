package it.uniroma2.pmcsn.parks.model.routing;

import it.uniroma2.pmcsn.parks.engineering.Config;
import it.uniroma2.pmcsn.parks.engineering.interfaces.RoutingNode;
import it.uniroma2.pmcsn.parks.engineering.singleton.ProbabilityManager;
import it.uniroma2.pmcsn.parks.engineering.singleton.RandomHandler;
import it.uniroma2.pmcsn.parks.model.job.RiderGroup;
import it.uniroma2.pmcsn.parks.model.server.Center;

public class NetworkRoutingNode implements RoutingNode<RiderGroup> {

    private RoutingNode<RiderGroup> attractionNode;
    private RoutingNode<RiderGroup> restaurantNode;

    public NetworkRoutingNode(RoutingNode<RiderGroup> attractionNode,
            RoutingNode<RiderGroup> restaurantNode) {
        this.attractionNode = attractionNode;
        this.restaurantNode = restaurantNode;
    }

    // TODO: I would not implement this with a uniform probability, since
    // attractions are usually most likely to be visited
    public Center<RiderGroup> route(RiderGroup job) {
        double attractionProb = ProbabilityManager.getInstance().getProbability(attractionNode.getName());
        double restaurantProb = ProbabilityManager.getInstance().getProbability(restaurantNode.getName());

        double routingProb = RandomHandler.getInstance().getRandom(Config.NETWORK_ROUTING_NODE);

        if (routingProb <= attractionProb) {
            // Go to attractions
            return attractionNode.route(job);
        } else if (routingProb <= attractionProb + restaurantProb) {
            // Go to restaurants
            return restaurantNode.route(job);
        } else {
            // Go to exit
            return null;
        }
    }

    @Override
    public String getName() {
        return Config.NETWORK_ROUTING_NODE;
    }

}