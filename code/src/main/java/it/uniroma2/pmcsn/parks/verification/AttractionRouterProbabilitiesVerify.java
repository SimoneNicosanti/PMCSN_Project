package it.uniroma2.pmcsn.parks.verification;

import java.util.List;

import it.uniroma2.pmcsn.parks.model.job.RiderGroup;
import it.uniroma2.pmcsn.parks.model.routing.probabilities.RouterProbabilities;
import it.uniroma2.pmcsn.parks.model.server.concrete_servers.Attraction;

// To verify the correctness of the computational model we use fixed attraction probabilities
public class AttractionRouterProbabilitiesVerify extends RouterProbabilities<RiderGroup> {

    private List<Attraction> attractions;

    public AttractionRouterProbabilitiesVerify(List<Attraction> attractions) {
        this.attractions = attractions;
    }

    @Override
    public List<Double> compute(RiderGroup job) {

        double attractionProb = 1;
        for (int i = 0; i < attractions.size(); i++) {
            this.probabilities.add(attractionProb);
            this.sumProbabilities += attractionProb;
        }

        this.normalize();

        return this.probabilities;
    }

}
