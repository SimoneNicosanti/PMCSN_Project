package it.uniroma2.pmcsn.parks.engineering.factory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.tuple.Pair;

import it.uniroma2.pmcsn.parks.engineering.Constants;
import it.uniroma2.pmcsn.parks.engineering.Parameters;
import it.uniroma2.pmcsn.parks.engineering.interfaces.Center;
import it.uniroma2.pmcsn.parks.engineering.singleton.RandomHandler;
import it.uniroma2.pmcsn.parks.model.Interval;
import it.uniroma2.pmcsn.parks.model.RoutingNodeType;
import it.uniroma2.pmcsn.parks.model.job.RiderGroup;
import it.uniroma2.pmcsn.parks.model.routing.probabilities.AttractionRouterProbabilities;
import it.uniroma2.pmcsn.parks.model.routing.probabilities.RouterProbabilities;
import it.uniroma2.pmcsn.parks.model.server.concrete_servers.Attraction;
import it.uniroma2.pmcsn.parks.verification.AttractionRouterProbabilitiesVerify;
import it.uniroma2.pmcsn.parks.verification.AttractionVerify;

public class SimulationBuilder {

    public static int getJobSize() {
        if (Constants.VERIFICATION_MODE) {
            return 1;
        } else {
            return 1 + Double.valueOf(RandomHandler.getInstance().getPoisson(Constants.GROUP_SIZE_STREAM, 2.6))
                    .intValue();
        }
    }

    // Return a fake interval created for exiting all the jobs from the
    // system
    public static Pair<Interval, Parameters> getInifiniteInterval() {
        Interval interval = new Interval(0.0, Double.MAX_VALUE);
        Map<RoutingNodeType, Double> probabilityMap = new HashMap<>();
        probabilityMap.put(RoutingNodeType.ATTRACTION, 0.9);
        probabilityMap.put(RoutingNodeType.RESTAURANT, 0.0);
        probabilityMap.put(RoutingNodeType.EXIT, 0.1);

        Parameters parameters = new Parameters(probabilityMap, 7.0);

        return Pair.of(interval, parameters);
    }

    public static RouterProbabilities<RiderGroup> buildAttractionRouterProbabilities(
            List<Center<RiderGroup>> attractionList) {

        if (Constants.VERIFICATION_MODE) {
            return new AttractionRouterProbabilitiesVerify(attractionList);
        } else {
            return new AttractionRouterProbabilities(attractionList);
        }
    }

    public static Attraction buildAttraction(String name, int numberOfSeats, double popularity,
            double avgDuration) {
        if (Constants.VERIFICATION_MODE) {
            return new AttractionVerify(name, numberOfSeats, popularity, avgDuration);
        } else {
            return new Attraction(name, numberOfSeats, popularity, avgDuration);
        }
    }
}
