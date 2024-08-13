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

            // If the group is too large for the attraction, the probability to go there is
            // zero.
            if (attraction.getSlotNumber() < job.getGroupSize()) {
                this.probabilities.add(0.0);
                continue;
            }

            double popularityTerm = 5 * attraction.getPopularity() / sumPop;
            double queueTerm = 0.2 * (1 - attraction.getQueueLenght(job.getPriority()) /
                    maxQueue);
            double visitTerm = 0.15 * (1 -
                    job.getGroupStats().getVisitsPerAttraction(attraction.getName()) / maxVisit);

            double score = popularityTerm + queueTerm + visitTerm;

            double attractionProb = Math.exp(score);

            this.probabilities.add(attractionProb);
            this.sumProbabilities += attractionProb;
        }
        // TODO How to lead the group out of the park?
        // If the group is too large for any attraction, it exits the park
        // if (sumProbabilities == 0.0)

        this.normalize();

        return this.probabilities;

    }

}
