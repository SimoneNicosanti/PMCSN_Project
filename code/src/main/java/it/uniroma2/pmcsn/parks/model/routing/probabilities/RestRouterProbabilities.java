package it.uniroma2.pmcsn.parks.model.routing.probabilities;

import java.util.List;

import it.uniroma2.pmcsn.parks.model.job.RiderGroup;
import it.uniroma2.pmcsn.parks.model.server.concrete_servers.Restaurant;

public class RestRouterProbabilities extends RouterProbabilities<RiderGroup> {

    private List<Restaurant> restaurants;

    public RestRouterProbabilities(List<Restaurant> restaurants) {
        this.restaurants = restaurants;
    }

    @Override
    public List<Double> compute(RiderGroup job) {

        for (Restaurant restaurant : restaurants) {
            Double restProb = restaurant.getPopularity();
            this.probabilities.add(restProb);
            this.sumProbabilities += restProb;
        }

        this.normalize();

        return this.probabilities;
    }

}
