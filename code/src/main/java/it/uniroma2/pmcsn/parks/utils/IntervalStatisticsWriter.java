package it.uniroma2.pmcsn.parks.utils;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;

import it.uniroma2.pmcsn.parks.engineering.Constants;
import it.uniroma2.pmcsn.parks.engineering.interfaces.Center;
import it.uniroma2.pmcsn.parks.engineering.singleton.ClockHandler;
import it.uniroma2.pmcsn.parks.model.Interval;
import it.uniroma2.pmcsn.parks.model.job.RiderGroup;
import it.uniroma2.pmcsn.parks.model.queue.QueuePriority;
import it.uniroma2.pmcsn.parks.model.server.AbstractCenter;
import it.uniroma2.pmcsn.parks.model.server.concrete_servers.ExitCenter;
import it.uniroma2.pmcsn.parks.model.server.concrete_servers.StatsCenter;
import it.uniroma2.pmcsn.parks.model.stats.AreaStats;
import it.uniroma2.pmcsn.parks.model.stats.CenterStatistics;
import it.uniroma2.pmcsn.parks.model.stats.StatsType;

public class IntervalStatisticsWriter {
    public static void writeCenterStatistics(List<Center<RiderGroup>> centerList) {
        for (Center<RiderGroup> center : centerList) {
            if (center instanceof ExitCenter) {
                continue;
            }

            writeWholeDayStatistics(center);
            writeIntervalStatistics(center);
        }

    }

    private static void writeIntervalStatistics(Center<RiderGroup> center) {
        Map<Interval, CenterStatistics> intervalStatsMap = ((StatsCenter) center).getStatsPerInterval();
        for (Interval interval : intervalStatsMap.keySet()) {
            CenterStatistics stats = intervalStatsMap.get(interval);
            String fileName = "Interval_" + interval.getIndex();

            String personFolder = Path.of(Constants.DATA_PATH, "Center", "Interval", "People_NEW").toString();
            writeStatistics(center, StatsType.PERSON, personFolder, fileName, stats,
                    interval.getSize());

            String groupFolder = Path.of(Constants.DATA_PATH, "Center", "Interval", "Group_NEW").toString();
            writeStatistics(center, StatsType.GROUP, groupFolder, fileName, stats,
                    interval.getSize());
        }
    }

    private static void writeStatistics(Center<RiderGroup> center, StatsType statsType, String directoryPath,
            String fileName,
            CenterStatistics stats, Double intervalDuration) {
        String name = center.getName();

        // TODO Should we use the time of last update of the AreaStats instead of
        // the last clock of the simulation??
        AreaStats serviceAreaStats = stats.getServiceAreaStats(statsType);
        Double mPerRho = serviceAreaStats.getArea() / intervalDuration;
        Double avgServiceTime = serviceAreaStats.getSizeAvgdStat();
        Integer totalServed = stats.getServiceAreaStats(statsType).getSize();

        Integer m = ((AbstractCenter) ((StatsCenter) center).getCenter()).getSlotNumber();
        Double rho = mPerRho / m;

        AreaStats queueAreaStats = stats.getQueueAreaStats(statsType, null);
        Double avgNumberInQueue = queueAreaStats.getArea() / intervalDuration;
        Double avgQueueTime = queueAreaStats.getSizeAvgdStat();
        Integer totalDequeued = queueAreaStats.getSize();

        Double avgNumberInCenter = stats.getServiceAreaValue(statsType) / intervalDuration;
        Double avgTimeInCenter = avgQueueTime + avgServiceTime;

        AreaStats normalQueueAreaStats = stats.getQueueAreaStats(statsType, QueuePriority.NORMAL);
        Double avgNormalQueueTime = (normalQueueAreaStats == null) ? 0 : normalQueueAreaStats.getSizeAvgdStat();
        Integer totalNormalDequeued = (normalQueueAreaStats == null) ? 0 : normalQueueAreaStats.getSize();

        AreaStats priorityQueueAreaStats = stats.getQueueAreaStats(statsType, QueuePriority.PRIORITY);
        Double avgPriorityQueueTime = (priorityQueueAreaStats == null) ? 0 : priorityQueueAreaStats.getSizeAvgdStat();
        Integer totalPriorityDequeued = (priorityQueueAreaStats == null) ? 0 : priorityQueueAreaStats.getSize();

        Path filePath = Path.of(directoryPath, fileName + ".csv");
        String[] header = {
                "Center name",
                "E[Nq]", "m*Rho", "Rho", "E[Ns]",
                "E[Tq]", "E[S]", "E[Ts]",
                "NormalEnqueued", "PriorityEnqueued", "TotalEnqueued", "TotalServed",
                "E[Tq] Normal", "E[Tq] Priority"
        };

        // Writing the header
        CsvWriter.writeHeader(filePath, header);

        List<Object> record = List.of(
                name,
                avgNumberInQueue, mPerRho, rho, avgNumberInCenter,
                avgQueueTime, avgServiceTime, avgTimeInCenter,
                totalNormalDequeued, totalPriorityDequeued, totalDequeued, totalServed,
                avgNormalQueueTime, avgPriorityQueueTime);
        CsvWriter.writeRecord(filePath, record);
    }

    private static void writeWholeDayStatistics(Center<RiderGroup> center) {
        CenterStatistics stats = ((StatsCenter) center).getWholeDayStats();

        String personFolder = Path.of(Constants.DATA_PATH, "Center", "Total", "People_NEW").toString();
        writeStatistics(center, StatsType.PERSON, personFolder, "WholeDay", stats,
                ClockHandler.getInstance().getClock());

        String groupFolder = Path.of(Constants.DATA_PATH, "Center", "Total", "Group_NEW").toString();
        writeStatistics(center, StatsType.GROUP, groupFolder, "WholeDay", stats, ClockHandler.getInstance().getClock());
    }
}
