package it.uniroma2.pmcsn.parks.model.routing;

import it.uniroma2.pmcsn.parks.engineering.Constants;
import it.uniroma2.pmcsn.parks.engineering.interfaces.Center;
import it.uniroma2.pmcsn.parks.engineering.interfaces.RoutingNode;
import it.uniroma2.pmcsn.parks.engineering.singleton.ClockHandler;
import it.uniroma2.pmcsn.parks.engineering.singleton.ConfigHandler;
import it.uniroma2.pmcsn.parks.engineering.singleton.RandomHandler;
import it.uniroma2.pmcsn.parks.model.RoutingNodeType;
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

    public Center<RiderGroup> route(RiderGroup job) {
        double attractionProb = ConfigHandler.getInstance().getProbability(RoutingNodeType.ATTRACTION,
                ClockHandler.getInstance().getCurrentInterval());
        double restaurantProb = ConfigHandler.getInstance().getProbability(RoutingNodeType.RESTAURANT,
                ClockHandler.getInstance().getCurrentInterval());

        double routingProb = RandomHandler.getInstance().getRandom(Constants.NETWORK_ROUTING_NODE);

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
        return Constants.NETWORK_ROUTING_NODE;
    }

}