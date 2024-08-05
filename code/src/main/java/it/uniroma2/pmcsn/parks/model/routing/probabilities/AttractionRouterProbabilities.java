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

            double popularityTerm = 3 * attraction.getPopularity() / sumPop;
            double queueTerm = 1 * (1 - attraction.getQueueLenght(job.getPriority()) / maxQueue);
            double visitTerm = 0 * (1 - job.getGroupStats().getVisitsPerAttraction(attraction.getName()) / maxVisit);

            double score = popularityTerm + queueTerm + visitTerm;

            double attractionProb = Math.exp(score);

            // int visitsToAttraction =
            // job.getGroupStats().getVisitsPerAttraction(attraction.getName());
            // // if (visitsToAttraction == 0) {
            // // visitsToAttraction = 1;
            // // }
            // Integer queueLength = attraction.getQueueLenght(job.getPriority());
            // if (queueLength == 0) {
            // queueLength = 1;
            // }

            // double popularityTerm = 1 + attraction.getPopularity();
            // double visitTerm = 1 + visitsToAttraction;
            // double queueTerm = Math.pow(queueLength, 0.5);
            // double delta = 0.0;

            // double score = popularityTerm - queueTerm - visitTerm + delta;

            // double attractionProb = 1 / (1 + Math.exp(-score));
            // // TODO: Add also time since the job is in the system ?

            this.probabilities.add(attractionProb);
            this.sumProbabilities += attractionProb;
        }
        this.normalize();

        return this.probabilities;

    }

}
