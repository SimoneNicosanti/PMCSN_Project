package it.uniroma2.pmcsn.parks.engineering.factory;

import java.util.List;

import it.uniroma2.pmcsn.parks.engineering.Constants;
import it.uniroma2.pmcsn.parks.engineering.singleton.RandomHandler;
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
            return Double.valueOf(RandomHandler.getInstance().getUniform(Constants.GROUP_SIZE_STREAM, 1, 10))
                    .intValue();
        }
    }

    public static RouterProbabilities<RiderGroup> buildAttractionRouterProbabilities(List<Attraction> attractionList) {

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
