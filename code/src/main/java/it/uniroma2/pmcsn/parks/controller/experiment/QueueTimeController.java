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
import it.uniroma2.pmcsn.parks.model.Interval;
import it.uniroma2.pmcsn.parks.model.job.RiderGroup;
import it.uniroma2.pmcsn.parks.model.queue.QueuePriority;
import it.uniroma2.pmcsn.parks.model.server.concrete_servers.StatsCenter;
import it.uniroma2.pmcsn.parks.model.stats.AreaStats;
import it.uniroma2.pmcsn.parks.model.stats.StatsType;
import it.uniroma2.pmcsn.parks.utils.ConfidenceIntervalComputer;
import it.uniroma2.pmcsn.parks.utils.ConfidenceIntervalComputer.ConfidenceInterval;
import it.uniroma2.pmcsn.parks.utils.ExperimentsUtils;
import it.uniroma2.pmcsn.parks.writers.IntervalsQueueTimesWriter;
import it.uniroma2.pmcsn.parks.writers.WriterHelper;

public class QueueTimeController implements Controller<RiderGroup> {

    public static void main(String[] args) {
        WriterHelper.createAllFolders();
        new QueueTimeController().simulate();
    }

    public QueueTimeController() {
        Constants.IMPROVED_MODEL = false;
        Constants.PRIORITY_PASS_PROB = 0.1;
        Constants.PRIORITY_PERCENTAGE_PER_RIDE = 0.4;

        // Using this percentage, the HP attraction has 3 seats reserved for small
        // groups, while the others have 0 or 1 seat
        // Constants.SMALL_GROUP_PERCENTAGE_PER_RIDE = 0.017;
        Constants.SMALL_GROUP_PERCENTAGE_PER_RIDE = 0.1;
        Constants.SMALL_GROUP_LIMIT_SIZE = 1;

        Constants.TRANSIENT_ANALYSIS = true;
    }

    @Override
    public void simulate() {

        this.init_simulation();

        Map<String, List<Double>> intervalsQueueTimes = new HashMap<>();

        for (int i = 0; i < Constants.REPLICATIONS_NUMBER; i++) {
            System.out.println("Replication Number >>> " + i);

            NetworkBuilder networkBuilder = new Simulation(SimulationMode.NORMAL).simulateOnce();
            List<StatsCenter> attractionStatsCenters = ExperimentsUtils.getAllStatsAttractions(networkBuilder);

            for (StatsCenter attractionStatCenter : attractionStatsCenters) {

                attractionStatCenter.getStatsPerInterval().forEach((interval, stats) -> {
                    for (QueuePriority prio : QueuePriority.values()) {

                        if (!Constants.IMPROVED_MODEL && prio.equals(QueuePriority.SMALL)) {
                            // Skip small queue for normal model
                            continue;
                        }

                        String key = queueStatsKey(interval, attractionStatCenter.getCenter(), prio);
                        // Update the confidence interval for the current interval
                        intervalsQueueTimes.putIfAbsent(key, new ArrayList<>());
                        AreaStats queueAreaStats = stats.getQueueAreaStats(StatsType.PERSON, prio);
                        if (queueAreaStats != null) {
                            intervalsQueueTimes.get(key).add(queueAreaStats.getSizeAvgdStat());
                        }
                    }
                });
            }
        }

        Map<String, ConfidenceInterval> queueTimesConfidenceIntervals = new HashMap<>();

        intervalsQueueTimes.forEach((key, queueTimes) -> {
            queueTimesConfidenceIntervals.put(key,
                    ConfidenceIntervalComputer.computeConfidenceInterval(queueTimes, this.extractCenterNameFromKey(key),
                            "QueueTimeIntervals"));
        });

        IntervalsQueueTimesWriter.writeIntervalsQueueTimes(queueTimesConfidenceIntervals);
    }

    private void init_simulation() {
        // Reset statistics
        WriterHelper.clearDirectory(Constants.JOB_DATA_PATH);

        Path fileDirectory = Path.of(Constants.DATA_PATH, "Intervals");
        WriterHelper.clearDirectory(fileDirectory.toString());
        // Prepare the logger and set the system clock to 0

        ClockHandler.getInstance().setClock(0);
    }

    // Constructs a key for the map that stores the queue stats for each interval,
    // given a center and one of its queue priority
    private String queueStatsKey(Interval interval, Center<RiderGroup> center, QueuePriority prio) {
        return interval.getIndex() + "::" + center.getName() + "::" + prio.name();
    }

    private String extractCenterNameFromKey(String key) {
        return key.split("::")[1];
    }

}