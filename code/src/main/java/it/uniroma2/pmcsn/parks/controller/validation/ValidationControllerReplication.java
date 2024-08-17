package it.uniroma2.pmcsn.parks.controller.validation;

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
import it.uniroma2.pmcsn.parks.model.job.RiderGroup;
import it.uniroma2.pmcsn.parks.model.server.concrete_servers.Attraction;
import it.uniroma2.pmcsn.parks.model.server.concrete_servers.StatsCenter;
import it.uniroma2.pmcsn.parks.model.stats.AreaStats;
import it.uniroma2.pmcsn.parks.model.stats.StatsType;
import it.uniroma2.pmcsn.parks.writers.ValidationWriter;
import it.uniroma2.pmcsn.parks.writers.WriterHelper;
import it.uniroma2.pmcsn.parks.model.server.AbstractCenter;

public class ValidationControllerReplication implements Controller<RiderGroup> {

    public static void main(String[] args) {
        WriterHelper.createAllFolders();
        new ValidationControllerReplication().simulate();
    }

    public ValidationControllerReplication() {
        Constants.MODE = SimulationMode.VALIDATION;
    }

    @Override
    public void simulate() {
        init_simulation();

        Map<String, List<Double>> queueTimeMap = new HashMap<>();

        for (int i = 0; i < Constants.VALDATION_REPLICATIONS_NUMBER; i++) {
            System.out.println("Replication Number >>> " + i);

            NetworkBuilder networkBuilder = new Simulation(SimulationMode.VALIDATION).simulateOnce();

            List<Center<RiderGroup>> centerList = networkBuilder.getAllCenters();

            for (Center<RiderGroup> center : centerList) {
                AbstractCenter absCenter = (AbstractCenter) ((StatsCenter) center).getCenter();
                if (absCenter instanceof Attraction) {
                    AreaStats queueTimeStats = ((StatsCenter) center).getWholeDayStats()
                            .getQueueAreaStats(StatsType.PERSON, null);

                    if (queueTimeMap.get(center.getName()) == null) {
                        queueTimeMap.put(center.getName(), new ArrayList<>());
                    }
                    queueTimeMap.computeIfPresent(center.getName(),
                            (String key, List<Double> value) -> {
                                value.add(queueTimeStats.getSizeAvgdStat());
                                return value;
                            });
                }
            }
        }

        ValidationWriter.writeReplicationsResult(queueTimeMap);

    }

    private void init_simulation() {
        // Reset statistics
        // WriterHelper.clearDirectory("Job");
        // WriterHelper.clearDirectory(Path.of(Constants.DATA_PATH,
        // "Center").toString());
        // WriterHelper.clearDirectory(Path.of(Constants.DATA_PATH, "Job").toString());
        // Prepare the logger and set the system clock to 0
        WriterHelper.clearDirectory(Constants.LOG_PATH);
        ClockHandler.getInstance().setClock(0);
    }

}