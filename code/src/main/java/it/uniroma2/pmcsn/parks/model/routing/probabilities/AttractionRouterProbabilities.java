package it.uniroma2.pmcsn.parks.model.routing.probabilities;

import java.util.List;

import it.uniroma2.pmcsn.parks.engineering.interfaces.Center;
import it.uniroma2.pmcsn.parks.model.job.RiderGroup;
import it.uniroma2.pmcsn.parks.model.server.concrete_servers.Attraction;

public class AttractionRouterProbabilities extends RouterProbabilities<RiderGroup> {

    private List<Center<RiderGroup>> attractions;

    public AttractionRouterProbabilities(List<Center<RiderGroup>> attractions) {
        this.attractions = attractions;
    }

    @Override
    public List<Double> compute(RiderGroup job) {
        for (Center<RiderGroup> attraction : attractions) {
            int visitsToAttraction = job.getGroupStats().getVisitsPerAttraction(attraction.getName());
            if (visitsToAttraction == 0) {
                visitsToAttraction = 1;
            }
            Integer queueLength = attraction.getQueueLenght(job.getPriority());
            if (queueLength == 0) {
                queueLength = 1;
            }
            double attractionProb = attraction.getPopularity() / (visitsToAttraction * queueLength);
            // TODO: Add also time since the job is in the system ?

            this.probabilities.add(attractionProb);
            this.sumProbabilities += attractionProb;
        }
        this.normalize();

        return this.probabilities;

    }

}
