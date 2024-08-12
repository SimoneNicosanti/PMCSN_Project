package it.uniroma2.pmcsn.parks.model.routing.probabilities;

import java.util.List;

import it.uniroma2.pmcsn.parks.engineering.interfaces.Center;
import it.uniroma2.pmcsn.parks.model.job.RiderGroup;

public class AttractionRouterProbabilities extends RouterProbabilities<RiderGroup> {

    private List<Center<RiderGroup>> attractions;

    public AttractionRouterProbabilities(List<Center<RiderGroup>> attractions) {
        this.attractions = attractions;
    }

    @Override
    public List<Double> compute(RiderGroup job) {
        Double sumPop = 0.0;
        Double maxVisit = 1.0;
        Double maxQueue = 1.0;

        for (Center<RiderGroup> attraction : attractions) {
            sumPop += attraction.getPopularity();
            maxVisit = Math.max(maxVisit, 1 + job.getGroupStats().getVisitsPerAttraction(attraction.getName()));
            maxQueue = Math.max(maxQueue, attraction.getQueueLenght(job.getPriority()));
        }

        for (Center<RiderGroup> attraction : attractions) {

            // double popularityTerm = 5 * attraction.getPopularity() / sumPop;
            // double queueTerm = 0.2 * (1 - attraction.getQueueLenght(job.getPriority()) /
            // maxQueue);
            // double visitTerm = 0.15 * (1 -
            // job.getGroupStats().getVisitsPerAttraction(attraction.getName()) / maxVisit);

            // double score = popularityTerm + queueTerm + visitTerm;

            // double attractionProb = Math.exp(score);

            double attractionProb = 1;

            this.probabilities.add(attractionProb);
            this.sumProbabilities += attractionProb;
        }
        this.normalize();

        return this.probabilities;

    }

}
