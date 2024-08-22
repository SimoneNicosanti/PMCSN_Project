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
import it.uniroma2.pmcsn.parks.writers.RhoWriter;
import it.uniroma2.pmcsn.parks.writers.WriterHelper;

public class RhoController implements Controller<RiderGroup> {
    public static void main(String[] args) {
        WriterHelper.createAllFolders();
        new RhoController().simulate();
    }

    public RhoController() {
        Constants.IMPROVED_MODEL = false;
    }

    @Override
    public void simulate() {

        init_simulation();

        if (Constants.IMPROVED_MODEL) {
            for (int smallGroupSize : new int[] { 1, 2 }) {
                Constants.SMALL_GROUP_LIMIT_SIZE = smallGroupSize;

                for (double smallPercentSize = 0.0; smallPercentSize < 0.20; smallPercentSize += 0.05) {
                    Constants.SMALL_GROUP_PERCENTAGE_PER_RIDE = smallPercentSize;

                    simulateForOneValue();
                    RandomHandler.reset();
                }
            }

        } else {
            simulateForOneValue();
        }

    }

    private static void simulateForOneValue() {
        Map<String, List<Double>> rhoListMap = new HashMap<>();

        Map<String, Integer> seatsNumberMap = new HashMap<>();

        Map<String, Double> smallRiderNum = new HashMap<>();

        for (int i = 0; i < Constants.REPLICATIONS_NUMBER; i++) {
            System.out.println("Replication Index >>> " + i);
            NetworkBuilder networkBuilder = new Simulation(SimulationMode.NORMAL).simulateOnce();

            List<Center<RiderGroup>> centerList = networkBuilder.getAllCenters();
            ExitCenter exitCenter = networkBuilder.getExitCenter();

            Integer smallGroups = 0;
            for (RiderGroup group : exitCenter.getExitJobs()) {
                if (group.getPriority() == GroupPriority.NORMAL
                        && group.getGroupSize() <= Constants.SMALL_GROUP_LIMIT_SIZE) {
                    smallGroups++;
                }
            }

            System.out.println("Small Groups >>> " + smallGroups + "\n\n");

            // TODO Try to understand by which clock we have to divide
            for (Center<RiderGroup> center : centerList) {
                StatsCenter statCenter = (StatsCenter) center;
                if (statCenter.getCenter() instanceof Attraction) {
                    rhoListMap.putIfAbsent(center.getName(), new ArrayList<>());

                    AreaStats serviceAreaStats = statCenter.getWholeDayStats()
                            .getServiceAreaStats(StatsType.PERSON);

                    Double multipliedRho = serviceAreaStats.getTimeAvgdStat();
                    rhoListMap.get(center.getName()).add(multipliedRho);

                    seatsNumberMap.putIfAbsent(center.getName(), center.getSlotNumber());
                    if (Constants.IMPROVED_MODEL) {
                        AreaStats smallGroupAreaStat = statCenter.getWholeDayStats().getQueueAreaStats(StatsType.PERSON,
                                QueuePriority.SMALL);

                        smallRiderNum.putIfAbsent(center.getName(), 0.0);
                        smallRiderNum.replace(center.getName(), smallRiderNum.get(center.getName())
                                + smallGroupAreaStat.getTimeAvgdStat());
                    }

                }
            }
        }

        if (Constants.IMPROVED_MODEL) {
            smallRiderNum.replaceAll((key, value) -> value /
                    Constants.REPLICATIONS_NUMBER);
            System.out.println(smallRiderNum);
        }

        Map<String, ConfidenceInterval> confidenceIntervalMap = new HashMap<>();
        rhoListMap.forEach((key, value) -> confidenceIntervalMap.put(key,
                ConfidenceIntervalComputer.computeConfidenceInterval(value, key, "MultipliedRho")));

        RhoWriter.writeMultipliedRhoValues(confidenceIntervalMap, seatsNumberMap);

        // IntervalStatisticsWriter.writeCenterStatistics(networkBuilder.getAllCenters());
    }

    private void init_simulation() {
        // Reset statistics
        // WriterHelper.clearDirectory(Path.of(Constants.DATA_PATH,
        // "Center").toString());
        WriterHelper.clearDirectory(Constants.JOB_DATA_PATH);

        Path fileDirectory = Path.of(Constants.DATA_PATH, "Rho");
        WriterHelper.clearDirectory(fileDirectory.toString());
        // Prepare the logger and set the system clock to 0

        ClockHandler.getInstance().setClock(0);
    }
}
