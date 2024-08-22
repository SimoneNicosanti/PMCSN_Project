package it.uniroma2.pmcsn.parks.engineering.factory;

import java.util.List;

import it.uniroma2.pmcsn.parks.SimulationMode;
import it.uniroma2.pmcsn.parks.engineering.Constants;
import it.uniroma2.pmcsn.parks.engineering.interfaces.Center;
import it.uniroma2.pmcsn.parks.engineering.interfaces.QueueManager;
import it.uniroma2.pmcsn.parks.engineering.queue.AttractionQueueManager;
import it.uniroma2.pmcsn.parks.engineering.queue.ImprovedAttractionQueueManager;
import it.uniroma2.pmcsn.parks.engineering.singleton.ConfigHandler;
import it.uniroma2.pmcsn.parks.engineering.singleton.RandomHandler;
import it.uniroma2.pmcsn.parks.model.job.RiderGroup;
import it.uniroma2.pmcsn.parks.model.routing.probabilities.AttractionRouterProbabilities;
import it.uniroma2.pmcsn.parks.model.routing.probabilities.RouterProbabilities;
import it.uniroma2.pmcsn.parks.model.server.concrete_servers.Attraction;
import it.uniroma2.pmcsn.parks.verification.AttractionRouterProbabilitiesVerify;
import it.uniroma2.pmcsn.parks.verification.AttractionVerify;

public class SimulationBuilder {

    public static int getJobSize() {
        if (Constants.MODE == SimulationMode.VERIFICATION) {
            return 1;
        } else {
            return 1 + Double.valueOf(RandomHandler.getInstance().getPoisson(Constants.GROUP_SIZE_STREAM, Constants.AVG_GROUP_SIZE_POISSON))
                    .intValue();
        }
    }

    public static double getArrivalRate() {
        Double configArrivalRate = ConfigHandler.getInstance().getCurrentArrivalRate();
        // If arrivalRate == 0, stop arrivals
        if (configArrivalRate == 0.0) {
            return configArrivalRate;
        }

        if (Constants.MODE == SimulationMode.VERIFICATION) {
            // It is the same as the one on the config file
            return configArrivalRate ;
        }

        return configArrivalRate / (1 + Constants.AVG_GROUP_SIZE_POISSON) ;
    }

    public static RouterProbabilities<RiderGroup> buildAttractionRouterProbabilities(
            List<Center<RiderGroup>> attractionList) {

        if (Constants.MODE == SimulationMode.VERIFICATION) {
            return new AttractionRouterProbabilitiesVerify(attractionList);
        } else {
            return new AttractionRouterProbabilities(attractionList);
        }
    }

    public static Attraction buildAttraction(String name, int numberOfSeats, double popularity,
            double avgDuration) {
        QueueManager<RiderGroup> queueManager = null;
        if (Constants.IMPROVED_MODEL) {
            queueManager = new ImprovedAttractionQueueManager();
        } else {
            queueManager = new AttractionQueueManager();
        }

        if (Constants.MODE == SimulationMode.VERIFICATION) {
            return new AttractionVerify(name, queueManager, numberOfSeats, popularity, avgDuration);
        } else {
            return new Attraction(name, queueManager, numberOfSeats, popularity, avgDuration);
        }
    }
}
