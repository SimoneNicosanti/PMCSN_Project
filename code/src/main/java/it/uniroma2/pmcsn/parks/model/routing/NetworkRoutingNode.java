package it.uniroma2.pmcsn.parks.model.routing;

import it.uniroma2.pmcsn.parks.engineering.Config;
import it.uniroma2.pmcsn.parks.engineering.interfaces.RoutingNode;
import it.uniroma2.pmcsn.parks.engineering.singleton.ProbabilityManager;
import it.uniroma2.pmcsn.parks.engineering.singleton.RandomHandler;
import it.uniroma2.pmcsn.parks.model.job.RiderGroup;
import it.uniroma2.pmcsn.parks.model.server.Center;

public class NetworkRoutingNode implements RoutingNode<RiderGroup> {

    private int randomStreamIdx;
    private RoutingNode<RiderGroup> attractionNode;
    private RoutingNode<RiderGroup> restaurantNode;

    public NetworkRoutingNode(int stream, RoutingNode<RiderGroup> attractionNode, RoutingNode<RiderGroup> restaurantNode) {
        this.randomStreamIdx = stream;
        this.attractionNode = attractionNode;
        this.restaurantNode = restaurantNode;
    }

    public Center<RiderGroup> route(RiderGroup job) {
        double attractionProb = ProbabilityManager.getInstance().getProbability(attractionNode.getName());

        double routingProb = RandomHandler.getInstance().getRandom(randomStreamIdx);

        if(routingProb <= attractionProb) {
            // Go to attractions
            return attractionNode.route(job);
        } else {
            // Go to restaurants
            return restaurantNode.route(job);
        }
    }

    @Override
    public String getName() {
        return Config.NETWORK_ROUTING_NODE;
    }

}