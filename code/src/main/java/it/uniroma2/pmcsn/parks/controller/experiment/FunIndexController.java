package it.uniroma2.pmcsn.parks.controller.experiment;

import java.nio.file.Path;
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
    }

    @Override
    public void simulate() {

        init_simulation();

        for (Double priorityPercSeats = 0.0; priorityPercSeats < 1.0; priorityPercSeats += 0.05) {
            Constants.PRIORITY_PERCENTAGE_PER_RIDE = priorityPercSeats;

            simulateForOneValue();

            RandomHandler.reset();
        }

        // IntervalStatisticsWriter.writeCenterStatistics(networkBuilder.getAllCenters());
    }

    private void simulateForOneValue() {
        Map<GroupPriority, FunIndexInfo> funIndexMap = new HashMap<>();
        Map<String, Double> priorityQueueTimeMap = new HashMap<>();
        Map<String, Double> normalQueueTimeMap = new HashMap<>();

        for (GroupPriority prio : GroupPriority.values()) {
            funIndexMap.put(prio, new FunIndexInfo(0, 0, 0, 0.0));
        }

        for (int i = 0; i < Constants.FUN_INDEX_REPLICATIONS_NUMBER; i++) {
            System.out.println("Replication Number >>> " + i);

            NetworkBuilder networkBuilder = new Simulation(SimulationMode.NORMAL).simulateOnce();
            ExitCenter exitCenter = networkBuilder.getExitCenter();

            // EventLogger.writeRandomLogString(RandomHandler.getInstance().getRandomLog());

            List<RiderGroup> exitRiderGroups = exitCenter.getExitJobs();

            // JobInfoWriter.writeAllJobsInfo("Job", "Job_Info_2.csv", exitRiderGroups);

            Map<GroupPriority, FunIndexInfo> currentFunIndexMap = FunIndexComputer.computeAvgsFunIndex(exitRiderGroups);

            funIndexMap.replaceAll(
                    (prio, value) -> FunIndexInfo.sum(value,
                            currentFunIndexMap.getOrDefault(prio, new FunIndexInfo(0, 0, 0, 0.0))));

            List<Center<RiderGroup>> centerList = networkBuilder.getAllCenters();
            for (Center<RiderGroup> center : centerList) {
                StatsCenter statCenter = (StatsCenter) center;
                if (statCenter.getCenter() instanceof Attraction) {
                    AreaStats priorityQueueAreaStats = statCenter.getWholeDayStats().getQueueAreaStats(StatsType.GROUP,
                            QueuePriority.PRIORITY);
                    priorityQueueTimeMap.putIfAbsent(center.getName(), 0.0);
                    priorityQueueTimeMap.replace(center.getName(),
                            priorityQueueTimeMap.get(center.getName()) + priorityQueueAreaStats.getSizeAvgdStat());

                    AreaStats normalQueueAreaStats = statCenter.getWholeDayStats().getQueueAreaStats(StatsType.GROUP,
                            QueuePriority.NORMAL);
                    normalQueueTimeMap.putIfAbsent(center.getName(), 0.0);
                    normalQueueTimeMap.replace(center.getName(),
                            normalQueueTimeMap.get(center.getName()) + normalQueueAreaStats.getSizeAvgdStat());
                }
            }
        }

        funIndexMap.replaceAll(
                (key, value) -> FunIndexInfo.divideValuesBy(value, Constants.FUN_INDEX_REPLICATIONS_NUMBER.intValue()));

        priorityQueueTimeMap.replaceAll((key, value) -> value / Constants.FUN_INDEX_REPLICATIONS_NUMBER);
        normalQueueTimeMap.replaceAll((key, value) -> value / Constants.FUN_INDEX_REPLICATIONS_NUMBER);

        Map<QueuePriority, Map<String, Double>> perPrioQueueTimeMap = new HashMap<>();
        perPrioQueueTimeMap.put(QueuePriority.PRIORITY, priorityQueueTimeMap);
        perPrioQueueTimeMap.put(QueuePriority.NORMAL, normalQueueTimeMap);
        // System.out.println(funIndexMap.toString());

        FunIndexWriter.writeFunIndexResults(funIndexMap);
        FunIndexWriter.writePriorityQueueTimes(perPrioQueueTimeMap);
    }

    private void init_simulation() {
        // Reset statistics
        // WriterHelper.clearDirectory(Path.of(Constants.DATA_PATH,
        // "Center").toString());
        WriterHelper.clearDirectory(Constants.JOB_DATA_PATH);

        Path fileDirectory = Path.of(Constants.DATA_PATH, "Fun");
        WriterHelper.clearDirectory(fileDirectory.toString());
        // Prepare the logger and set the system clock to 0

        ClockHandler.getInstance().setClock(0);
    }

}