package it.uniroma2.pmcsn.parks.verification;

import java.util.List;

import it.uniroma2.pmcsn.parks.engineering.interfaces.Center;
import it.uniroma2.pmcsn.parks.model.job.RiderGroup;
import it.uniroma2.pmcsn.parks.model.routing.probabilities.RouterProbabilities;

// To verify the correctness of the computational model we use fixed attraction probabilities
public class AttractionRouterProbabilitiesVerify extends RouterProbabilities<RiderGroup> {

    private List<Center<RiderGroup>> attractions;

    public AttractionRouterProbabilitiesVerify(List<Center<RiderGroup>> attractions) {
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
