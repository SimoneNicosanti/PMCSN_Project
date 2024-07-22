package it.uniroma2.pmcsn.parks.model.routing;

import java.util.ArrayList;
import java.util.List;

import it.uniroma2.pmcsn.parks.engineering.Config;
import it.uniroma2.pmcsn.parks.engineering.interfaces.RoutingNode;
import it.uniroma2.pmcsn.parks.engineering.singleton.RandomHandler;
import it.uniroma2.pmcsn.parks.model.job.RiderGroup;
import it.uniroma2.pmcsn.parks.model.server.Attraction;
import it.uniroma2.pmcsn.parks.model.server.Center;


public class AttractionRoutingNode implements RoutingNode<RiderGroup> {

    private List<Attraction> attractionList;
    private int randomStreamIdx;

    public AttractionRoutingNode(List<Attraction> attractionList) {
        this.attractionList = attractionList;
        this.randomStreamIdx = RandomHandler.getInstance().getNewStreamIndex();
    }

    @Override
    public Center<RiderGroup> route(RiderGroup riderGroup) {

        // Computing normalized probability array for each attraction
        List<Double> probabilityArray = computeProbabilityArray(riderGroup);

        double routingProb = RandomHandler.getInstance().getRandom(randomStreamIdx);
        double cumulativeSum = probabilityArray.get(0);
        Attraction routeAttraction = null;
        for (int i = 0; i < attractionList.size(); i++) {
            if (routingProb < cumulativeSum) {
                routeAttraction = attractionList.get(i);
                break;
            }
            cumulativeSum += probabilityArray.get(i);
        }

        return routeAttraction;
    }

    @Override
    public String getName() {
        return Config.ATTRACTION_ROUTING_NODE ;
    }

    private List<Double> computeProbabilityArray(RiderGroup riderGroup) {
        List<Double> probabilityArray = new ArrayList<>();
        double sum = 0.0;
        for (Attraction attraction : attractionList) {
            int visitsToAttraction = riderGroup.getGroupStats().getVisitsPerAttraction(attraction.getName());
            if (visitsToAttraction == 0) {
                visitsToAttraction = 1;
            }
            // TODO Add number of people in queue in probability computation
            double attractionProb = attraction.getPopularity() / visitsToAttraction;

            probabilityArray.add(attractionProb);
            sum += attractionProb;
        }

        // Normalizing probability array
        for (int idx = 0; idx < probabilityArray.size(); idx++) {
            double notNormalized = probabilityArray.get(idx);
            double normalized = notNormalized / sum;

            probabilityArray.set(idx, normalized);
        }
        
        return probabilityArray;
    }
}