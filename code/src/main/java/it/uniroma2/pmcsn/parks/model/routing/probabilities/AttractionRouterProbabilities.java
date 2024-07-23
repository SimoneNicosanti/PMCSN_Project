package it.uniroma2.pmcsn.parks.model.routing.probabilities;

import java.util.List;

import it.uniroma2.pmcsn.parks.engineering.queue.AttractionQueueManager;
import it.uniroma2.pmcsn.parks.model.job.RiderGroup;
import it.uniroma2.pmcsn.parks.model.server.Attraction;

public class AttractionRouterProbabilities extends RouterProbabilities<RiderGroup> {

    private List<Attraction> attractions;

    public AttractionRouterProbabilities(List<Attraction> attractions) {
        this.attractions = attractions;
    }

    @Override
    public List<Double> compute(RiderGroup job) {
        for (Attraction attraction : attractions) {
            int visitsToAttraction = job.getGroupStats().getVisitsPerAttraction(attraction.getName());
            if (visitsToAttraction == 0) {
                visitsToAttraction = 1;
            }
            AttractionQueueManager queueManager = (AttractionQueueManager) attraction.getQueueManager();
            int queueLength = queueManager.queueLength(job.getPriority());
            if (queueLength == 0) {
                queueLength = 1;
            }
            double attractionProb = attraction.getPopularity() / (visitsToAttraction * queueLength);
            // TODO: Add also time since the job is in the system ?

            this.probabilities.add(attractionProb);
            this.sumProbabilites += attractionProb;
        }
        this.normalize();

        return this.probabilities;

    }

}
