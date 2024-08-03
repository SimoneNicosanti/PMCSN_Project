package it.uniroma2.pmcsn.parks.verification;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import it.uniroma2.pmcsn.parks.engineering.Constants;
import it.uniroma2.pmcsn.parks.engineering.interfaces.Center;
import it.uniroma2.pmcsn.parks.model.job.RiderGroup;
import it.uniroma2.pmcsn.parks.model.server.concrete_servers.StatsCenter;
import it.uniroma2.pmcsn.parks.model.server.concrete_servers.ExitCenter;
import it.uniroma2.pmcsn.parks.model.stats.CenterStatistics;
import it.uniroma2.pmcsn.parks.model.stats.QueueStats;
import it.uniroma2.pmcsn.parks.utils.CsvWriter;
import it.uniroma2.pmcsn.parks.utils.WriterHelper;
import it.uniroma2.pmcsn.parks.verification.ConfidenceIntervalComputer.ConfidenceInterval;

public class VerificationWriter {

    public static void writeAllVerificationStatistics(String statFolder, String fileName,
            List<Center<RiderGroup>> centerList) {
        for (Center<RiderGroup> center : centerList) {
            writeVerificationStatistics(statFolder, fileName, center);
        }
    }

    private static void writeVerificationStatistics(String statsFolder, String filename, Center<RiderGroup> center) {
        if (center instanceof ExitCenter)
            return;

        String centerName = center.getName();

        StatsCenter statsCenter = (StatsCenter) center;
        CenterStatistics stats = ((StatsCenter) center).getCenterStats();
        QueueStats generalQueueStats = statsCenter.getCenterStats().getAggregatedQueueStats();

        double avgQueueTime = stats.getAvgGroupWaitByArea() - stats.getAvgServiceTimePerGroup();
        double avgServiceTimePerCompletedService = stats.getAvgServiceTimePerCompletedService();

        double avgSystemTime = avgQueueTime + avgServiceTimePerCompletedService;

        List<QueueStats> perPrioQueueStats = stats.getQueueStats();

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

        CsvWriter.writeRecord(filePath, record);
    }

    private static Path writeVerificationHeader(String statsFolder, String filename) {

        Path filePath = Path.of(statsFolder, filename + ".csv");
        String[] header = {
                "Center name",
                "Avg Queue Time", "Avg Queue Time - Normal", "Avg Queue Time - Prio",
                "Normal Jobs Served", "Priority Jobs Served",
                "Avg Single Service Time",
                "Avg System Time",
        };

        CsvWriter.writeHeader(filePath, header);

        return filePath;
    }

    public static void writeConfidenceIntervals(List<ConfidenceInterval> confidenceIntervals, String fileName) {
        List<String> metricsList = new ArrayList<>();
        for (ConfidenceInterval confidenceInterval : confidenceIntervals) {
            if (!metricsList.contains(confidenceInterval.statsName())) {
                metricsList.add(confidenceInterval.statsName());
            }
        }
        for (String metric : metricsList) {
            Path filePath = Path.of(".", Constants.DATA_PATH, "Verification", fileName + "_" + metric + ".csv");
            String[] header = {
                    "Center Name",
                    metric + " - Mean Value",
                    "Interval",
                    "Lower Bound",
                    "Upper Bound"
            };
            CsvWriter.writeHeader(filePath, header);

            for (ConfidenceInterval interval : confidenceIntervals) {
                if (metric.equals(interval.statsName())) {
                    List<Object> record = List.of(
                            interval.centerName(),
                            interval.mean(),
                            interval.interval(),
                            interval.mean() - interval.interval(),
                            interval.mean() + interval.interval());
                    CsvWriter.writeRecord(filePath, record);
                }
            }
        }

    }

    public static void resetData() {
        WriterHelper.clearDirectory(Constants.VERIFICATION_PATH);
    }

    public static void writeTheoreticalQueueTimeValues(Map<String, Double> theoryMap) {
        Path filePath = Path.of(".", Constants.DATA_PATH, "Verification", "TheoreticalQueueTime" + ".csv");
        String[] header = {
                "Center Name",
                "Theoretical Queue Time",
        };
        CsvWriter.writeHeader(filePath, header);

        for (String centerName : theoryMap.keySet()) {
            List<Object> record = List.of(centerName, theoryMap.get(centerName));
            CsvWriter.writeRecord(filePath, record);
        }
    }

}
