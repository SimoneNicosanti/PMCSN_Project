package it.uniroma2.pmcsn.parks.controller;

import java.util.ArrayList;
import java.util.List;

import it.uniroma2.pmcsn.parks.engineering.singleton.RandomHandler;
import it.uniroma2.pmcsn.parks.model.job.RiderGroup;
import it.uniroma2.pmcsn.parks.model.server.Attraction;

public class AttractionRoutingComputer {

    private List<Attraction> attractionList;
    private int randomStreamIdx;

    public AttractionRoutingComputer(List<Attraction> attractionList) {
        this.attractionList = attractionList;
        this.randomStreamIdx = RandomHandler.getInstance().getNewStreamIndex();
    }

    public Attraction computeNextAttractionForJob(RiderGroup riderGroup) {

        List<Double> probabilityArray = computeProbabilityArray(riderGroup);

        double routingProb = RandomHandler.getInstance().getRandom(randomStreamIdx);
        double cumulativeSum = probabilityArray.get(randomStreamIdx);
        Attraction routeAttraction = null;
        for (int routingIdx = 0; routingIdx < attractionList.size(); routingIdx++) {
            if (routingProb < cumulativeSum) {
                routeAttraction = attractionList.get(routingIdx);
            }
        }

        return routeAttraction;

    }

    private List<Double> computeProbabilityArray(RiderGroup riderGroup) {
        List<Double> probabilityArray = new ArrayList<>();
        double sum = 0.0;
        for (Attraction attraction : attractionList) {
            int visitsToAttraction = riderGroup.getVisitsPerAttraction(attraction.getName());
            if (visitsToAttraction == 0) {
                visitsToAttraction = 1;
            }
            // TODO Add number of people in queue in probability value
            double prob = attraction.getPopularity() / visitsToAttraction;

            probabilityArray.add(prob);
            sum += prob;
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
