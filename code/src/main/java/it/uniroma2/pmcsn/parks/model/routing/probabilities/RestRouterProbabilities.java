package it.uniroma2.pmcsn.parks.model.routing.probabilities;

import java.util.List;

import it.uniroma2.pmcsn.parks.engineering.singleton.ConfigManager;
import it.uniroma2.pmcsn.parks.model.job.RiderGroup;
import it.uniroma2.pmcsn.parks.model.server.concreate_servers.Restaurant;

public class RestRouterProbabilities extends RouterProbabilities<RiderGroup> {

    private List<Restaurant> restaurants;

    public RestRouterProbabilities(List<Restaurant> restaurants) {
        this.restaurants = restaurants;
    }

    @Override
    public List<Double> compute(RiderGroup job) {
        ConfigManager manager = ConfigManager.getInstance();

        // TODO different routing for restaurant? Based on queue occupation or
        // popularity?
        for (Restaurant restaurant : restaurants) {
            Double restProb = restaurant.getPopularity();
            this.probabilities.add(restProb);
            this.sumProbabilites += restProb;
        }

        this.normalize();

        return this.probabilities;
    }

}
