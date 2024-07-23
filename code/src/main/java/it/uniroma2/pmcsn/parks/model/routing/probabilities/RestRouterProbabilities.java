package it.uniroma2.pmcsn.parks.model.routing.probabilities;

import java.util.List;

import it.uniroma2.pmcsn.parks.engineering.singleton.ProbabilityManager;
import it.uniroma2.pmcsn.parks.model.job.RiderGroup;
import it.uniroma2.pmcsn.parks.model.server.Restaurant;

public class RestRouterProbabilities extends RouterProbabilities<RiderGroup> {

    private List<Restaurant> restaurants;

    public RestRouterProbabilities(List<Restaurant> restaurants) {
        this.restaurants = restaurants;
    }

    @Override
    public List<Double> compute(RiderGroup job) {
        ProbabilityManager manager = ProbabilityManager.getInstance();

        // TODO different routing for restaurant? Based on queue occupation?
        for (Restaurant restaurant : restaurants) {
            this.probabilities.add(manager.getProbability(restaurant.getName()));
        }

        this.normalize();

        return this.probabilities;
    }

}
