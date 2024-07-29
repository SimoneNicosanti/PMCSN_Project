package it.uniroma2.pmcsn.parks.model.routing;

import java.util.List;

import it.uniroma2.pmcsn.parks.engineering.Constants;
import it.uniroma2.pmcsn.parks.engineering.interfaces.RoutingNode;
import it.uniroma2.pmcsn.parks.engineering.singleton.RandomHandler;
import it.uniroma2.pmcsn.parks.model.job.RiderGroup;
import it.uniroma2.pmcsn.parks.model.routing.probabilities.AttractionRouterProbabilities;
import it.uniroma2.pmcsn.parks.verification.AttractionRouterProbabilitiesVerify;
import it.uniroma2.pmcsn.parks.model.routing.probabilities.RouterProbabilities;
import it.uniroma2.pmcsn.parks.model.server.concrete_servers.Attraction;

public class AttractionRoutingNode implements RoutingNode<RiderGroup> {

    private List<Attraction> attractionList;

    public AttractionRoutingNode(List<Attraction> attractionList) {
        this.attractionList = attractionList;
    }

    @Override
    public Attraction route(RiderGroup riderGroup) {
        double routingProb = RandomHandler.getInstance().getRandom(Constants.ATTRACTION_ROUTING_NODE);

        // Computing normalized probability array for each attraction
        RouterProbabilities<RiderGroup> probabilities = null;
        if (Constants.VERIFICATION_MODE) {
            probabilities = new AttractionRouterProbabilitiesVerify(attractionList);
        } else {
            probabilities = new AttractionRouterProbabilities(attractionList);
        }

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