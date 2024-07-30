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

import it.uniroma2.pmcsn.parks.engineering.Constants;
import it.uniroma2.pmcsn.parks.engineering.interfaces.Center;
import it.uniroma2.pmcsn.parks.engineering.singleton.ClockHandler;
import it.uniroma2.pmcsn.parks.model.job.RiderGroup;
import it.uniroma2.pmcsn.parks.model.server.StatsCenter;
import it.uniroma2.pmcsn.parks.model.server.concrete_servers.ExitCenter;
import it.uniroma2.pmcsn.parks.model.stats.CenterStatistics;
import it.uniroma2.pmcsn.parks.model.stats.QueueStats;

public class StatisticsWriter {

    public static void resetStatistics(String statsCase) {
        Path statisticsDirectory = Path.of(".", Constants.DATA_PATH, statsCase);

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

        Path filePath = Path.of(".", Constants.DATA_PATH, statsFolder, fileName);
        String[] header = { "GroupId", "GroupSize", "Priority", "QueueTime", "RidingTime", "TotalTime", "NumberRides" };

        // Writing the header
        writeHeader(filePath, header);

        // Writing the file
        List<Object> record = List.of(groupId, groupSize, priority, totalQueueTime, totalRidingTime,
                ClockHandler.getInstance().getClock(), totalRiding);
        writeRecord(filePath, record);
    }

    public static void writeCenterStatistics(String statsFolder, String fileName, Center<RiderGroup> center) {
        if (center instanceof ExitCenter)
            return;

        String name = center.getName();
        StatsCenter statsCenter = (StatsCenter) center;
        CenterStatistics stats = ((StatsCenter) center).getCenterStats();
        double avgServiceTimePerPerson = stats.getAvgServiceTimePerPerson();
        double avgServiceTimePerGroup = stats.getAvgServiceTimePerGroup();
        double avgServiceTimePerCompletedService = stats.getAvgServiceTimePerCompletedService();

        // double avgQueueTime = stats.getAvgQueueTime();
        long peopleServed = stats.getNumberOfServedPerson();
        long groupsServed = stats.getNumberOfServedGroup();

        List<QueueStats> perPrioQueueStats = statsCenter.getQueueStats();
        QueueStats generalQueueStats = statsCenter.getGeneralQueueStats();
        double avgQueueTimePerPersonNormal = 0.0;
        double avgQueueTimePerPersonPrio = 0.0;
        long numberOfPriorityRider = 0;
        long numberOfNormalRider = 0;
        long numberOfPriorityGroup = 0;
        long numberOfNormalGroup = 0;
        double avgQueueTimePerPerson = generalQueueStats.getAvgWaitingTimePerPerson();
        double avgQueueTimePerGroup = generalQueueStats.getAvgWaitingTimePerGroups();
        double avgQueueTimePerGroupNormal = 0.0;
        double avgQueueTimePerGroupPrio = 0.0;

        for (QueueStats queue : perPrioQueueStats) {
            switch (queue.getPriority()) {
                case NORMAL:
                    if (avgQueueTimePerPersonNormal != 0.0)
                        throw new RuntimeException(name + " has more than one normal queue");
                    numberOfNormalRider = queue.getNumberOfPerson();
                    numberOfNormalGroup = queue.getNumberOfGroup();
                    avgQueueTimePerPersonNormal = queue.getAvgWaitingTimePerPerson();
                    avgQueueTimePerGroupNormal = queue.getAvgWaitingTimePerGroups();
                    break;

                case PRIORITY:
                    if (avgQueueTimePerPersonPrio != 0.0)
                        throw new RuntimeException(name + " has more than one priority queue");
                    numberOfPriorityRider = queue.getNumberOfPerson();
                    numberOfPriorityGroup = queue.getNumberOfGroup();
                    avgQueueTimePerPersonPrio = queue.getAvgWaitingTimePerPerson();
                    avgQueueTimePerGroupPrio = queue.getAvgWaitingTimePerGroups();
                    break;
                default:
                    throw new RuntimeException("Unknown queue priority");
            }
        }

        Path filePath = Path.of(".", Constants.DATA_PATH, statsFolder, fileName + ".csv");
        String[] header = {
                "Center name", "Avg Service Time - Services",
                "Groups Served", "Normal Group Served", "Priority Group Served",
                "Avg Service Time - Groups",
                "Avg Queue Time - Groups", "Avg Queue Time Normal - Groups", "Avg Queue Time Prio - Groups",
                "People Served", "Normal People Served", "Priority People Served",
                "Avg Service Time - People",
                "Avg Queue Time - People", "Avg Queue Time Normal - People", "Avg Queue Time Prio - People"
        };

        // Writing the header
        writeHeader(filePath, header);

        // Writing the file
        List<Object> record = List.of(
                name, avgServiceTimePerCompletedService,
                groupsServed, numberOfNormalGroup, numberOfPriorityGroup,
                avgServiceTimePerGroup,
                avgQueueTimePerGroup, avgQueueTimePerGroupNormal, avgQueueTimePerGroupPrio,
                peopleServed, numberOfNormalRider, numberOfPriorityRider,
                avgServiceTimePerPerson,
                avgQueueTimePerPerson, avgQueueTimePerPersonNormal, avgQueueTimePerPersonPrio);
        writeRecord(filePath, record);
    }

    private static void writeHeader(Path filePath, String[] header) {

        if (!filePath.toFile().exists()) {
            try {
                Files.createDirectories(filePath.getParent());
                filePath.toFile().createNewFile();
                try (
                        Writer writer = new FileWriter(filePath.toFile(), true);
                        CSVPrinter csvPrinter = new CSVPrinter(writer,
                                Builder.create(CSVFormat.DEFAULT).setHeader(header).build())) {
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static void writeRecord(Path filePath, List<Object> record) {
        try (
                Writer writer = new FileWriter(filePath.toFile(), true);
                CSVPrinter csvPrinter = new CSVPrinter(writer,
                        Builder.create(CSVFormat.DEFAULT).build())) {

            csvPrinter.printRecord(record);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
