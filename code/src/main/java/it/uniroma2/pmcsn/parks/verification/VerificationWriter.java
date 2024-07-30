package it.uniroma2.pmcsn.parks.verification;

import java.nio.file.Path;
import java.util.List;

import it.uniroma2.pmcsn.parks.engineering.Constants;
import it.uniroma2.pmcsn.parks.engineering.interfaces.Center;
import it.uniroma2.pmcsn.parks.model.job.RiderGroup;
import it.uniroma2.pmcsn.parks.model.server.StatsCenter;
import it.uniroma2.pmcsn.parks.model.server.concrete_servers.ExitCenter;
import it.uniroma2.pmcsn.parks.model.stats.CenterStatistics;
import it.uniroma2.pmcsn.parks.model.stats.QueueStats;
import it.uniroma2.pmcsn.parks.utils.StatisticsWriter;

public class VerificationWriter {

    public static void writeVerificationStatistics(String statsFolder, String filename, Center<RiderGroup> center) {
        if (center instanceof ExitCenter)
            return;

        String centerName = center.getName();

        StatsCenter statsCenter = (StatsCenter) center;
        CenterStatistics stats = ((StatsCenter) center).getCenterStats();
        QueueStats generalQueueStats = statsCenter.getGeneralQueueStats();

        double avgQueueTime = generalQueueStats.getAvgWaitingTimePerGroups();
        double avgServiceTimePerCompletedService = stats.getAvgServiceTimePerCompletedService();

        double avgSystemTime = avgQueueTime + avgServiceTimePerCompletedService;

        List<QueueStats> perPrioQueueStats = statsCenter.getQueueStats();

        double avgQueueTimePerGroupNormal = 0.0;
        double avgQueueTimePerGroupPrio = 0.0;
        long numberOfNormalJobs = 0;
        long numberOfPriorityJobs = 0;

        for (QueueStats queue : perPrioQueueStats) {
            switch (queue.getPriority()) {
                case NORMAL:
                    avgQueueTimePerGroupNormal = queue.getAvgWaitingTimePerGroups();
                    numberOfNormalJobs = queue.getNumberOfGroup();
                    break;

                case PRIORITY:
                    avgQueueTimePerGroupPrio = queue.getAvgWaitingTimePerGroups();
                    numberOfPriorityJobs = queue.getNumberOfGroup();
                    break;
                default:
                    throw new RuntimeException("Unknown queue priority");
            }
        }

        // Writing the header
        Path filePath = writeVerificationHeader(statsFolder, filename);

        // Writing the file
        List<Object> record = List.of(
                centerName, avgQueueTime, avgQueueTimePerGroupNormal, avgQueueTimePerGroupPrio, numberOfNormalJobs,
                numberOfPriorityJobs,
                avgServiceTimePerCompletedService, avgSystemTime);

        StatisticsWriter.writeRecord(filePath, record);
    }

    private static Path writeVerificationHeader(String statsFolder, String filename) {

        Path filePath = Path.of(".", Constants.DATA_PATH, statsFolder, filename + ".csv");
        String[] header = {
                "Center name",
                "Avg Queue Time", "Avg Queue Time - Normal", "Avg Queue Time - Prio",
                "Normal Jobs Served", "Priority Jobs Served",
                "Avg Single Service Time",
                "Avg System Time",
        };

        StatisticsWriter.writeHeader(filePath, header);

        return filePath;
    }

}
