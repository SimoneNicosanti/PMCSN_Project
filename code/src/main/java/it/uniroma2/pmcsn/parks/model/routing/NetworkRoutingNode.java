package it.uniroma2.pmcsn.parks.model.routing;

import it.uniroma2.pmcsn.parks.engineering.Config;
import it.uniroma2.pmcsn.parks.engineering.interfaces.Center;
import it.uniroma2.pmcsn.parks.engineering.interfaces.RoutingNode;
import it.uniroma2.pmcsn.parks.engineering.singleton.ConfigManager;
import it.uniroma2.pmcsn.parks.engineering.singleton.RandomHandler;
import it.uniroma2.pmcsn.parks.model.job.RiderGroup;

public class NetworkRoutingNode implements RoutingNode<RiderGroup> {

    private RoutingNode<RiderGroup> attractionNode;
    private RoutingNode<RiderGroup> restaurantNode;
    private Center<RiderGroup> exitCenter;

    public NetworkRoutingNode(RoutingNode<RiderGroup> attractionNode,
            RoutingNode<RiderGroup> restaurantNode, Center<RiderGroup> exitCenter) {
        this.attractionNode = attractionNode;
        this.restaurantNode = restaurantNode;
        this.exitCenter = exitCenter;
    }

    // TODO: I would not implement this with a uniform probability, since
    // attractions are usually most likely to be visited
    public Center<RiderGroup> route(RiderGroup job) {
        double attractionProb = ConfigManager.getInstance().getProbability(attractionNode.getName());
        double restaurantProb = ConfigManager.getInstance().getProbability(restaurantNode.getName());

        double routingProb = RandomHandler.getInstance().getRandom(Config.NETWORK_ROUTING_NODE);

        if (routingProb <= attractionProb) {
            // Go to attractions
            return attractionNode.route(job);
        } else if (routingProb <= attractionProb + restaurantProb) {
            // Go to restaurants
            return restaurantNode.route(job);
        } else {
            // Go to exit
            return exitCenter;
        }
    }

    @Override
    public String getName() {
        return Config.NETWORK_ROUTING_NODE;
    }

}