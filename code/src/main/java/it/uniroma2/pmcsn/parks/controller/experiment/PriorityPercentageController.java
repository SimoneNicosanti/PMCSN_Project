package it.uniroma2.pmcsn.parks.controller.experiment;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import it.uniroma2.pmcsn.parks.SimulationMode;
import it.uniroma2.pmcsn.parks.controller.Simulation;
import it.uniroma2.pmcsn.parks.engineering.Constants;
import it.uniroma2.pmcsn.parks.engineering.factory.NetworkBuilder;
import it.uniroma2.pmcsn.parks.engineering.interfaces.Center;
import it.uniroma2.pmcsn.parks.engineering.interfaces.Controller;
import it.uniroma2.pmcsn.parks.engineering.singleton.ClockHandler;
import it.uniroma2.pmcsn.parks.engineering.singleton.RandomHandler;
import it.uniroma2.pmcsn.parks.model.job.RiderGroup;
import it.uniroma2.pmcsn.parks.model.queue.QueuePriority;
import it.uniroma2.pmcsn.parks.model.server.concrete_servers.StatsCenter;
import it.uniroma2.pmcsn.parks.model.stats.StatsType;
import it.uniroma2.pmcsn.parks.utils.ConfidenceIntervalComputer;
import it.uniroma2.pmcsn.parks.utils.ConfidenceIntervalComputer.ConfidenceInterval;
import it.uniroma2.pmcsn.parks.utils.ExperimentsUtils;
import it.uniroma2.pmcsn.parks.writers.FunIndexWriter;
import it.uniroma2.pmcsn.parks.writers.WriterHelper;

public class PriorityPercentageController implements Controller<RiderGroup> {

    public static void main(String[] args) {
        WriterHelper.createAllFolders();
        new PriorityPercentageController().simulate();
    }

    public PriorityPercentageController() {
        // Modify this if you want to run the improved model
        Constants.IMPROVED_MODEL = false;
    }

    @Override
    public void simulate() {
        this.init_simulation();

        Double[] priorityPassProbabilityValues = new Double[] { 0.1, 0.2, 0.3 };
        Double[] prioSeatsPercentageValues = new Double[] { 0.3, 0.4, 0.5 };

        for (Double priorityPassProbability : priorityPassProbabilityValues) {
            Constants.PRIORITY_PASS_PROB = priorityPassProbability;
            for (Double prioSeatsPercentage : prioSeatsPercentageValues) {
                Constants.PRIORITY_PERCENTAGE_PER_RIDE = prioSeatsPercentage;

                this.simulateForOneValue();
                RandomHandler.reset();
            }
        }
    }

    private void simulateForOneValue() {

        Map<String, List<Double>> queueTimeMap = new HashMap<>();

        for (int i = 0; i < Constants.REPLICATIONS_NUMBER; i++) {
            System.out.println("Replication Number >>> " + i);

            NetworkBuilder networkBuilder = new Simulation(SimulationMode.NORMAL).simulateOnce();

            List<StatsCenter> attractionStatsCenters = ExperimentsUtils.getAllStatsAttractions(networkBuilder);

            for (StatsCenter attractionStatCenter : attractionStatsCenters) {
                for (QueuePriority prio : QueuePriority.values()) {

                    if (!Constants.IMPROVED_MODEL && prio.equals(QueuePriority.SMALL)) {
                        // Skip small queue for normal model
                        continue;
                    }

                    String key = this.queueStatsKey(attractionStatCenter.getCenter(), prio);

                    queueTimeMap.putIfAbsent(key, new ArrayList<>());
                    queueTimeMap.get(key).add(attractionStatCenter.getWholeDayStats()
                            .getQueueAreaStats(StatsType.PERSON, prio).getSizeAvgdStat());
                }
            }
        }

        Map<String, ConfidenceInterval> queueConfIntervals = new HashMap<>();
        queueTimeMap.forEach((key, queueTimes) -> {
            queueConfIntervals.put(key, ConfidenceIntervalComputer.computeConfidenceInterval(queueTimes,
                    this.extractCenterNameFromKey(key), "QueueTime"));
        });

        FunIndexWriter.writePriorityQueueTimes(queueConfIntervals);
    }

    private String queueStatsKey(Center<RiderGroup> center, QueuePriority prio) {
        return center.getName() + "::" + prio.name();
    }

    private String extractCenterNameFromKey(String key) {
        return key.split("::")[0];
    }

    private void init_simulation() {
        // Reset statistics
        WriterHelper.clearDirectory(Constants.JOB_DATA_PATH);

        Path fileDirectory = Path.of(Constants.DATA_PATH, "Fun", Constants.IMPROVED_MODEL ? "Improved" : "Basic");
        WriterHelper.clearDirectory(fileDirectory.toString());
        // Prepare the logger and set the system clock to 0

        ClockHandler.getInstance().setClock(0);
    }

}
