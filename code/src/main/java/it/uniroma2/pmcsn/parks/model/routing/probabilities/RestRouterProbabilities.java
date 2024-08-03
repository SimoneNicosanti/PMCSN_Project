package it.uniroma2.pmcsn.parks.model.routing.probabilities;

import java.util.List;

import it.uniroma2.pmcsn.parks.engineering.interfaces.Center;
import it.uniroma2.pmcsn.parks.model.job.RiderGroup;
import it.uniroma2.pmcsn.parks.model.server.concrete_servers.Restaurant;

public class RestRouterProbabilities extends RouterProbabilities<RiderGroup> {

    private List<Center<RiderGroup>> restaurants;

    public RestRouterProbabilities(List<Center<RiderGroup>> restaurants) {
        this.restaurants = restaurants;
    }

    @Override
    public List<Double> compute(RiderGroup job) {

        for (Center<RiderGroup> restaurant : restaurants) {
            Double restProb = restaurant.getPopularity();
            this.probabilities.add(restProb);
            this.sumProbabilities += restProb;
        }

        this.normalize();

        return this.probabilities;
    }

}
