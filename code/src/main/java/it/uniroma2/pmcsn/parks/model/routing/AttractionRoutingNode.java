package it.uniroma2.pmcsn.parks.model.routing;

import java.util.List;

import it.uniroma2.pmcsn.parks.engineering.Constants;
import it.uniroma2.pmcsn.parks.engineering.factory.SimulationBuilder;
import it.uniroma2.pmcsn.parks.engineering.interfaces.Center;
import it.uniroma2.pmcsn.parks.engineering.interfaces.RoutingNode;
import it.uniroma2.pmcsn.parks.engineering.singleton.RandomHandler;
import it.uniroma2.pmcsn.parks.model.job.RiderGroup;
import it.uniroma2.pmcsn.parks.model.routing.probabilities.RouterProbabilities;

public class AttractionRoutingNode implements RoutingNode<RiderGroup> {

    private List<Center<RiderGroup>> attractionList;

    public AttractionRoutingNode(List<Center<RiderGroup>> attractionList) {
        this.attractionList = attractionList;
        RandomHandler.getInstance().getStream(Constants.ATTRACTION_ROUTING_NODE);
    }

    @Override
    public Center<RiderGroup> route(RiderGroup riderGroup) {
        double routingProb = RandomHandler.getInstance().getRandom(Constants.ATTRACTION_ROUTING_NODE);

        // Computing normalized probability array for each attraction
        RouterProbabilities<RiderGroup> probabilities = SimulationBuilder
                .buildAttractionRouterProbabilities(attractionList);

        probabilities.compute(riderGroup);
        // Select route index based on probability
        int routeIdx = probabilities.getRouteIdxFromRand(routingProb);

        return attractionList.get(routeIdx);
    }

    @Override
    public String getName() {
        return Constants.ATTRACTION_ROUTING_NODE;
    }

}