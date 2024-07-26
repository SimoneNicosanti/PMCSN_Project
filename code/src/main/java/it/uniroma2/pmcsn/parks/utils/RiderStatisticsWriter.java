package it.uniroma2.pmcsn.parks.utils;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVFormat.Builder;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.io.FileUtils;

import it.uniroma2.pmcsn.parks.engineering.Config;
import it.uniroma2.pmcsn.parks.engineering.interfaces.Center;
import it.uniroma2.pmcsn.parks.engineering.singleton.ClockHandler;
import it.uniroma2.pmcsn.parks.model.job.RiderGroup;
import it.uniroma2.pmcsn.parks.model.server.StatsCenter;
import it.uniroma2.pmcsn.parks.model.stats.CenterStats;
import it.uniroma2.pmcsn.parks.model.stats.QueueStats;

public class RiderStatisticsWriter {

    public static void resetStatistics(String statsCase) {
        Path statisticsDirectory = Path.of(".", Config.DATA_PATH, statsCase);

        try {
            if (Files.exists(statisticsDirectory)) {
                FileUtils.deleteDirectory(statisticsDirectory.toFile());
            }
            Files.createDirectories(statisticsDirectory);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException();
        }

    }

    public static void writeStatistics(String statsFolder, String fileName, RiderGroup riderGroup) {

        Long groupId = riderGroup.getGroupId();
        Integer groupSize = riderGroup.getGroupSize();
        String priority = riderGroup.getPriority().name();
        Double totalQueueTime = riderGroup.getGroupStats().getQueueTime();
        Double totalRidingTime = riderGroup.getGroupStats().getServiceTime();
        Integer totalRiding = riderGroup.getGroupStats().getTotalNumberOfVisits();

        Path filePath = Path.of(".", Config.DATA_PATH, statsFolder, fileName);
        String[] header = { "GroupId", "GroupSize", "Priority", "QueueTime", "RidingTime", "TotalTime", "NumberRides" };

        // Writing the header
        if (!filePath.toFile().exists()) {
            filePath.toFile();
            try (
                    Writer writer = new FileWriter(filePath.toFile(), true);
                    CSVPrinter csvPrinter = new CSVPrinter(writer,
                            Builder.create(CSVFormat.DEFAULT).setHeader(header).build())) {

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // Writing the file
        try (
                Writer writer = new FileWriter(filePath.toFile(), true);
                CSVPrinter csvPrinter = new CSVPrinter(writer,
                        Builder.create(CSVFormat.DEFAULT).build())) {

            csvPrinter.printRecord(groupId, groupSize, priority, totalQueueTime, totalRidingTime,
                    ClockHandler.getInstance().getClock(), totalRiding);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void writeCenterStatistics(String statsFolder, String fileName, Center<RiderGroup> center) {

        String name = center.getName();
        CenterStats stats = ((StatsCenter) center).getCenterStats();
        double avgServiceTime = stats.getAvgServiceTime();
        double avgQueueTime = stats.getQueueTime();
        long servedJobs = stats.getServedJobs();

        List<QueueStats> queueStats = stats.getQueueStats();
        double avgQueueTimeNormal = 0.0;
        double avgQueueTimePrio = 0.0;

        for (QueueStats queue : queueStats) {
            switch (queue.getPriority()) {
                case NORMAL:
                    if (avgQueueTimeNormal != 0.0)
                        throw new RuntimeException(name + " has more than one normal queue");
                    avgQueueTimeNormal = queue.getAvgWaitingTime();
                    break;

                case PRIORITY:
                    if (avgQueueTimePrio != 0.0)
                        throw new RuntimeException(name + " has more than one priority queue");
                    avgQueueTimePrio = queue.getAvgWaitingTime();
                    break;
                default:
                    throw new RuntimeException("Unknown queue priority");
            }
        }

        Path filePath = Path.of(".", Config.DATA_PATH, statsFolder, fileName);
        String[] header = { "Center name", "Served Jobs", "Average Service Time", "Average Queue Time",
                "Avg Queue Time Normal", "Avg Queue Time Prio" };

        // Writing the header
        if (!filePath.toFile().exists()) {
            filePath.toFile();
            try (
                    Writer writer = new FileWriter(filePath.toFile(), true);
                    CSVPrinter csvPrinter = new CSVPrinter(writer,
                            Builder.create(CSVFormat.DEFAULT).setHeader(header).build())) {

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // Writing the file
        try (
                Writer writer = new FileWriter(filePath.toFile(), true);
                CSVPrinter csvPrinter = new CSVPrinter(writer,
                        Builder.create(CSVFormat.DEFAULT).build())) {

            csvPrinter.printRecord(name, servedJobs, avgServiceTime, avgQueueTime, avgQueueTimeNormal, avgQueueTimePrio);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
