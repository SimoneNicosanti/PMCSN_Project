package it.uniroma2.pmcsn.parks.model.routing;

import it.uniroma2.pmcsn.parks.engineering.interfaces.RoutingNode;
import it.uniroma2.pmcsn.parks.model.job.RiderGroup;
import it.uniroma2.pmcsn.parks.model.server.Center;

public class NetworkRoutingNode implements RoutingNode<RiderGroup> {

    private RoutingNode<RiderGroup> attractionNode;
    private RoutingNode<RiderGroup> restaurantNode;

    public NetworkRoutingNode(RoutingNode<RiderGroup> attractionNode, RoutingNode<RiderGroup> restaurantNode) {
        this.attractionNode = attractionNode;
        this.restaurantNode = restaurantNode;
    }

    public Center<RiderGroup> route(RiderGroup job) {
        ProbabilityManager

        return null;
    }

}