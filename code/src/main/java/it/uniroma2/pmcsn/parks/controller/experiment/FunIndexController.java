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
import it.uniroma2.pmcsn.parks.model.job.GroupPriority;
import it.uniroma2.pmcsn.parks.model.job.RiderGroup;
import it.uniroma2.pmcsn.parks.model.queue.QueuePriority;
import it.uniroma2.pmcsn.parks.model.server.concrete_servers.Attraction;
import it.uniroma2.pmcsn.parks.model.server.concrete_servers.ExitCenter;
import it.uniroma2.pmcsn.parks.model.server.concrete_servers.StatsCenter;
import it.uniroma2.pmcsn.parks.model.stats.AreaStats;
import it.uniroma2.pmcsn.parks.model.stats.StatsType;
import it.uniroma2.pmcsn.parks.utils.ConfidenceIntervalComputer;
import it.uniroma2.pmcsn.parks.utils.ConfidenceIntervalComputer.ConfidenceInterval;
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
        Constants.IMPROVED_MODEL = true;
    }

    @Override
    public void simulate() {
        this.init_simulation();

        if (Constants.IMPROVED_MODEL) {
            for (int smallGroupSize : new int[] { 1, 2 }) {
                Constants.SMALL_GROUP_LIMIT_SIZE = smallGroupSize;

                // Not sure about the loop constraints
                for (Double smallPercSeats = 0.0; smallPercSeats < 0.3; smallPercSeats += 0.02) {
                    Constants.PRIORITY_PERCENTAGE_PER_RIDE = smallPercSeats;

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

        Path fileDirectory = Path.of(Constants.DATA_PATH, "Fun");
        WriterHelper.clearDirectory(fileDirectory.toString());
        // Prepare the logger and set the system clock to 0

        ClockHandler.getInstance().setClock(0);
    }

    private void simulateForOneValue() {

        Map<String, List<FunIndexInfo>> funIndexMap = new HashMap<>();
        Map<String, List<Double>> priorityQueueTimeMap = new HashMap<>();
        Map<String, List<Double>> normalQueueTimeMap = new HashMap<>();
        Map<String, List<Double>> smallQueueTimeMap = new HashMap<>();

        for (GroupPriority prio : GroupPriority.values()) {
            funIndexMap.put(prio.name(), new ArrayList<>());
        }
        funIndexMap.put("SMALL", new ArrayList<>());

        for (int i = 0; i < Constants.REPLICATIONS_NUMBER; i++) {
            System.out.println("Replication Number >>> " + i);

            NetworkBuilder networkBuilder = new Simulation(SimulationMode.NORMAL).simulateOnce();
            ExitCenter exitCenter = networkBuilder.getExitCenter();

            // EventLogger.writeRandomLogString(RandomHandler.getInstance().getRandomLog());

            List<RiderGroup> exitRiderGroups = exitCenter.getExitJobs();

            Map<String, FunIndexInfo> currentFunIndexMap = FunIndexComputer.computeAvgsFunIndex(exitRiderGroups);

            for (String prio : currentFunIndexMap.keySet()) {
                funIndexMap.get(prio).add(currentFunIndexMap.get(prio));
            }
            // funIndexMap.replaceAll(
            // (prio, value) -> FunIndexInfo.sum(value,
            // currentFunIndexMap.getOrDefault(prio, new FunIndexInfo(0, 0, 0, 0.0))));

            List<Center<RiderGroup>> centerList = networkBuilder.getAllCenters();
            for (Center<RiderGroup> center : centerList) {
                StatsCenter statCenter = (StatsCenter) center;
                if (statCenter.getCenter() instanceof Attraction) {
                    AreaStats priorityQueueAreaStats = statCenter.getWholeDayStats().getQueueAreaStats(StatsType.GROUP,
                            QueuePriority.PRIORITY);
                    priorityQueueTimeMap.putIfAbsent(center.getName(), new ArrayList<>());
                    priorityQueueTimeMap.get(center.getName()).add(priorityQueueAreaStats.getSizeAvgdStat());

                    AreaStats normalQueueAreaStats = statCenter.getWholeDayStats().getQueueAreaStats(StatsType.GROUP,
                            QueuePriority.NORMAL);
                    normalQueueTimeMap.putIfAbsent(center.getName(), new ArrayList<>());
                    normalQueueTimeMap.get(center.getName()).add(normalQueueAreaStats.getSizeAvgdStat());

                    if (Constants.IMPROVED_MODEL) {
                        AreaStats smallQueueAreaStats = statCenter.getWholeDayStats().getQueueAreaStats(StatsType.GROUP,
                                QueuePriority.SMALL);
                        smallQueueTimeMap.putIfAbsent(center.getName(), new ArrayList<>());
                        smallQueueTimeMap.get(center.getName()).add(smallQueueAreaStats.getSizeAvgdStat());
                    }
                }
            }
        }

        Map<String, ConfidenceInterval> funIdxConfInterMap = new HashMap<>();
        for (String prioName : funIndexMap.keySet()) {
            List<FunIndexInfo> funIndexInfoList = funIndexMap.get(prioName);
            List<Double> funIndexValues = new ArrayList<>();
            funIndexInfoList.forEach(arg0 -> funIndexValues.add(arg0.avgFunIndex()));

            ConfidenceInterval funIndexConfInt = ConfidenceIntervalComputer.computeConfidenceInterval(funIndexValues,
                    prioName, "FunIndex");
            funIdxConfInterMap.put(prioName, funIndexConfInt);
        }

        Map<String, Map<QueuePriority, ConfidenceInterval>> perPrioQueueTimeMap = new HashMap<>();
        for (String centerName : priorityQueueTimeMap.keySet()) {
            perPrioQueueTimeMap.putIfAbsent(centerName, new HashMap<>());

            perPrioQueueTimeMap.get(centerName).putIfAbsent(QueuePriority.PRIORITY, ConfidenceIntervalComputer
                    .computeConfidenceInterval(priorityQueueTimeMap.get(centerName), centerName, "QueueTime"));

            perPrioQueueTimeMap.get(centerName).putIfAbsent(QueuePriority.NORMAL, ConfidenceIntervalComputer
                    .computeConfidenceInterval(normalQueueTimeMap.get(centerName), centerName, "QueueTime"));

            if (Constants.IMPROVED_MODEL) {
                perPrioQueueTimeMap.get(centerName).putIfAbsent(QueuePriority.SMALL, ConfidenceIntervalComputer
                        .computeConfidenceInterval(smallQueueTimeMap.get(centerName), centerName, "QueueTime"));
            }
        }

        FunIndexWriter.writeFunIndexResults(funIdxConfInterMap);
        FunIndexWriter.writePriorityQueueTimes(perPrioQueueTimeMap);
    }

}