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
import it.uniroma2.pmcsn.parks.model.server.concrete_servers.ExitCenter;
import it.uniroma2.pmcsn.parks.model.server.concrete_servers.StatsCenter;
import it.uniroma2.pmcsn.parks.model.stats.StatsType;
import it.uniroma2.pmcsn.parks.utils.ConfidenceIntervalComputer;
import it.uniroma2.pmcsn.parks.utils.ConfidenceIntervalComputer.ConfidenceInterval;
import it.uniroma2.pmcsn.parks.utils.ExperimentsUtils;
import it.uniroma2.pmcsn.parks.utils.FunIndexComputer;
import it.uniroma2.pmcsn.parks.utils.FunIndexComputer.FunIndexInfo;
import it.uniroma2.pmcsn.parks.writers.FunIndexWriter;
import it.uniroma2.pmcsn.parks.writers.WriterHelper;

public class FunIndexController implements Controller<RiderGroup> {

    public static void main(String[] args) {
        WriterHelper.createAllFolders();
        new FunIndexController().simulate();
    }

    public FunIndexController() {
        // Modify this if you want to run the improved model
        Constants.IMPROVED_MODEL = false;
    }

    @Override
    public void simulate() {
        this.init_simulation();

        if (Constants.IMPROVED_MODEL) {
            // Double[] poissValues = new Double[] { 1.0, 1.5, 2.0, 2.5, 3.0 };

            for (double poissLambda = 1; poissLambda <= 3.0; poissLambda += 0.5) {
                Constants.AVG_GROUP_SIZE_POISSON = poissLambda;

                for (int smallGroupSize : new int[] { 1, 2 }) {
                    Constants.SMALL_GROUP_LIMIT_SIZE = smallGroupSize;

                    if (smallGroupSize == 1) {
                        Constants.SMALL_GROUP_PERCENTAGE_PER_RIDE = 0.017;
                    } else {
                        Constants.SMALL_GROUP_PERCENTAGE_PER_RIDE = 0.1;
                    }
                    this.simulateForOneValue();
                    RandomHandler.reset();
                }

                for (int smallGroupSize : new int[] { 1, 2 }) {
                    Constants.SMALL_GROUP_LIMIT_SIZE = smallGroupSize;
                    Constants.SMALL_GROUP_PERCENTAGE_PER_RIDE = 0.0;

                    this.simulateForOneValue();
                    RandomHandler.reset();
                }
            }
        } else {
            for (Double priorityPercSeats = 0.0; priorityPercSeats < 1.0; priorityPercSeats += 0.1) {
                Constants.PRIORITY_PERCENTAGE_PER_RIDE = priorityPercSeats;

                this.simulateForOneValue();
                RandomHandler.reset();
            }
        }
    }

    private void init_simulation() {
        // Reset statistics
        WriterHelper.clearDirectory(Constants.JOB_DATA_PATH);

        Path fileDirectory = Path.of(Constants.DATA_PATH, "Fun", Constants.IMPROVED_MODEL ? "Improved" : "Basic");
        WriterHelper.clearDirectory(fileDirectory.toString());
        // Prepare the logger and set the system clock to 0

        ClockHandler.getInstance().setClock(0);
    }

    private void simulateForOneValue() {

        Map<String, List<FunIndexInfo>> funIndexMap = new HashMap<>();
        Map<String, List<Double>> queueTimeMap = new HashMap<>();

        for (int i = 0; i < Constants.REPLICATIONS_NUMBER; i++) {
            System.out.println("Replication Number >>> " + i);

            NetworkBuilder networkBuilder = new Simulation(SimulationMode.NORMAL).simulateOnce();
            ExitCenter exitCenter = networkBuilder.getExitCenter();

            FunIndexComputer.computeAvgsFunIndex(
                    exitCenter.getExitJobs()).forEach((prio, funIndexInfo) -> {
                        funIndexMap.putIfAbsent(prio, new ArrayList<>());
                        funIndexMap.get(prio).add(funIndexInfo);
                    });

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

        Map<String, ConfidenceInterval> funIdxConfInterMap = new HashMap<>();
        funIndexMap.forEach((prioName, funIndexInfoList) -> {
            List<Double> funIndexValues = new ArrayList<>();
            funIndexInfoList.forEach(arg0 -> funIndexValues.add(arg0.avgFunIndex()));

            funIdxConfInterMap.put(prioName, ConfidenceIntervalComputer.computeConfidenceInterval(funIndexValues,
                    prioName, "FunIndex"));
        });

        Map<String, ConfidenceInterval> queueConfIntervals = new HashMap<>();
        queueTimeMap.forEach((key, queueTimes) -> {
            queueConfIntervals.put(key, ConfidenceIntervalComputer.computeConfidenceInterval(queueTimes,
                    this.extractCenterNameFromKey(key), "QueueTime"));
        });

        FunIndexWriter.writeFunIndexResults(funIdxConfInterMap);
        FunIndexWriter.writePriorityQueueTimes(queueConfIntervals);
    }

    private String queueStatsKey(Center<RiderGroup> center, QueuePriority prio) {
        return center.getName() + "::" + prio.name();
    }

    private String extractCenterNameFromKey(String key) {
        return key.split("::")[0];
    }

}