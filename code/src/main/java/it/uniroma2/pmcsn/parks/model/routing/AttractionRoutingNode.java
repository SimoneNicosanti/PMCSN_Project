package it.uniroma2.pmcsn.parks.model.routing;

import java.util.List;

import it.uniroma2.pmcsn.parks.engineering.Config;
import it.uniroma2.pmcsn.parks.engineering.interfaces.RoutingNode;
import it.uniroma2.pmcsn.parks.engineering.singleton.RandomHandler;
import it.uniroma2.pmcsn.parks.model.job.RiderGroup;
import it.uniroma2.pmcsn.parks.model.routing.probabilities.AttractionRouterProbabilities;
import it.uniroma2.pmcsn.parks.model.server.Attraction;
import it.uniroma2.pmcsn.parks.model.server.Center;

public class AttractionRoutingNode implements RoutingNode<RiderGroup> {

    private List<Attraction> attractionList;

    public AttractionRoutingNode(List<Attraction> attractionList) {
        this.attractionList = attractionList;
    }

    @Override
    public Center<RiderGroup> route(RiderGroup riderGroup) {
        double routingProb = RandomHandler.getInstance().getRandom(Config.ATTRACTION_ROUTING_NODE);

        // Computing normalized probability array for each attraction
        AttractionRouterProbabilities probabilities = new AttractionRouterProbabilities(attractionList);
        probabilities.compute(riderGroup);
        // Select route index based on probability
        int routeIdx = probabilities.getRouteIdxFromRand(routingProb);

        return attractionList.get(routeIdx);
    }

    @Override
    public String getName() {
        return Config.ATTRACTION_ROUTING_NODE;
    }

}