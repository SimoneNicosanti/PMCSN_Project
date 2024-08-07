package it.uniroma2.pmcsn.parks.utils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.apache.commons.io.FileUtils;

import it.uniroma2.pmcsn.parks.engineering.Constants;
import it.uniroma2.pmcsn.parks.engineering.interfaces.Center;
import it.uniroma2.pmcsn.parks.engineering.singleton.ClockHandler;
import it.uniroma2.pmcsn.parks.model.job.RiderGroup;
import it.uniroma2.pmcsn.parks.model.queue.QueuePriority;
import it.uniroma2.pmcsn.parks.model.server.concrete_servers.StatsCenter;
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
        CsvWriter.writeHeader(filePath, header);

        // Writing the file
        List<Object> record = List.of(groupId, groupSize, priority, totalQueueTime, totalRidingTime,
                ClockHandler.getInstance().getClock(), totalRiding);
        CsvWriter.writeRecord(filePath, record);
    }

    public static void writeCenterStatistics(String statsFolder, String fileName, Center<RiderGroup> center) {
        if (center instanceof ExitCenter)
            return;

        String personFolder = Path.of(statsFolder, Constants.PEOPLE_DIRECTORY).toString();
        String groupFolder = Path.of(statsFolder, Constants.GROUP_DIRECTORY).toString();

        writePersonStatistics(personFolder, fileName, center);
        writeGroupStatistics(groupFolder, fileName, center);
    }

    public static void writePersonStatistics(String statsFolder, String fileName, Center<RiderGroup> center) {
        if (center instanceof ExitCenter)
            return;

        String name = center.getName();

        CenterStatistics stats = ((StatsCenter) center).getCenterStats();
        double avgServiceTimePerPerson = stats.getAvgServiceTimePerPerson();
        double avgServiceTimePerCompletedService = stats.getAvgServiceTimePerCompletedService();

        double people_N_s = stats.getAvgNumberOfPersonInTheSystem();
        double people_N_q = stats.getAvgNumberOfPersonInTheQueue();

        double peopleWaitByArea = stats.getAvgPersonWaitByArea();

        // double avgQueueTime = stats.getAvgQueueTime();
        long peopleServed = stats.getNumberOfServedPerson();

        double lambda = peopleServed / ClockHandler.getInstance().getClock();
        double mu = avgServiceTimePerPerson;
        double rho = lambda / mu;
        if (rho > 1)
            rho = 1;

        List<QueueStats> perPrioQueueStats = stats.getQueueStats();
        QueueStats generalQueueStats = stats.getAggregatedQueueStats();
        double avgQueueTimePerPersonNormal = 0.0;
        double avgQueueTimePerPersonPrio = 0.0;
        long numberOfPriorityRiderEnqueued = 0;
        long numberOfNormalRiderEnqueued = 0;
        double avgQueueTimePerPerson = generalQueueStats.getAvgWaitingTimePerPerson();
        long numberOfNormalRider = stats.getNumberOfServedPerson(QueuePriority.NORMAL);
        long numberOfPriorityRider = stats.getNumberOfServedPerson(QueuePriority.PRIORITY);

        for (QueueStats queue : perPrioQueueStats) {
            switch (queue.getPriority()) {
                case NORMAL:
                    if (avgQueueTimePerPersonNormal != 0.0)
                        throw new RuntimeException(name + " has more than one normal queue");
                    numberOfNormalRiderEnqueued = queue.getNumberOfPerson();
                    avgQueueTimePerPersonNormal = queue.getAvgWaitingTimePerPerson();
                    break;

                case PRIORITY:
                    if (avgQueueTimePerPersonPrio != 0.0)
                        throw new RuntimeException(name + " has more than one priority queue");
                    numberOfPriorityRiderEnqueued = queue.getNumberOfPerson();
                    avgQueueTimePerPersonPrio = queue.getAvgWaitingTimePerPerson();
                    break;
                default:
                    throw new RuntimeException("Unknown queue priority");
            }
        }

        Path filePath = Path.of(".", Constants.DATA_PATH, statsFolder, fileName + ".csv");
        String[] header = {
                "Center name", "Avg Queue Time", "Avg Service Time per Services",
                "People Served", "Normal People Served", "Priority People Served",
                "N_s", "N_q", "Avg Waiting Time By Area",
                "Avg Service Time", "Lambda", "Rho",
                "Avg Queue Time", "Avg Queue Time Normal", "Avg Queue Time Prio",
                "Normal People Enqueued", "Priority People Enqueued"
        };

        // Writing the header
        CsvWriter.writeHeader(filePath, header);

        // Writing the file
        List<Object> record = List.of(
                name, avgQueueTimePerPerson, avgServiceTimePerCompletedService,
                peopleServed, numberOfNormalRider, numberOfPriorityRider,
                people_N_s, people_N_q, peopleWaitByArea,
                avgServiceTimePerPerson, lambda, rho,
                avgQueueTimePerPerson, avgQueueTimePerPersonNormal, avgQueueTimePerPersonPrio,
                numberOfNormalRiderEnqueued, numberOfPriorityRiderEnqueued);
        CsvWriter.writeRecord(filePath, record);
    }

    public static void writeGroupStatistics(String statsFolder, String fileName, Center<RiderGroup> center) {
        if (center instanceof ExitCenter)
            return;

        String name = center.getName();

        CenterStatistics stats = ((StatsCenter) center).getCenterStats();
        double avgServiceTimePerGroup = stats.getAvgServiceTimePerGroup();
        double avgServiceTimePerCompletedService = stats.getAvgServiceTimePerCompletedService();

        double group_N_s = stats.getAvgNumberOfGroupInTheSystem();
        double group_N_q = stats.getAvgNumberOfGroupInTheQueue();

        double groupsWaitByArea = stats.getAvgGroupWaitByArea();

        // double avgQueueTime = stats.getAvgQueueTime();
        long groupsServed = stats.getNumberOfServedGroup();

        double lambda = groupsServed / ClockHandler.getInstance().getClock();
        double mu = avgServiceTimePerGroup;
        double rho = lambda / mu;
        if (rho > 1)
            rho = 1;

        List<QueueStats> perPrioQueueStats = stats.getQueueStats();
        QueueStats generalQueueStats = stats.getAggregatedQueueStats();
        long numberOfPriorityGroupEnqueued = 0;
        long numberOfNormalGroupEnqueued = 0;
        long numberOfNormalGroup = stats.getNumberOfServedGroup(QueuePriority.NORMAL);
        long numberOfPriorityGroup = stats.getNumberOfServedGroup(QueuePriority.PRIORITY);
        double avgQueueTimePerGroup = generalQueueStats.getAvgWaitingTimePerGroups();
        double avgQueueTimePerGroupNormal = 0.0;
        double avgQueueTimePerGroupPrio = 0.0;

        for (QueueStats queue : perPrioQueueStats) {
            switch (queue.getPriority()) {
                case NORMAL:
                    if (avgQueueTimePerGroupNormal != 0.0)
                        throw new RuntimeException(name + " has more than one normal queue");
                    numberOfNormalGroupEnqueued = queue.getNumberOfGroup();
                    avgQueueTimePerGroupNormal = queue.getAvgWaitingTimePerGroups();
                    break;

                case PRIORITY:
                    if (avgQueueTimePerGroupPrio != 0.0)
                        throw new RuntimeException(name + " has more than one priority queue");
                    numberOfPriorityGroupEnqueued = queue.getNumberOfGroup();
                    avgQueueTimePerGroupPrio = queue.getAvgWaitingTimePerGroups();
                    break;
                default:
                    throw new RuntimeException("Unknown queue priority");
            }
        }

        Path filePath = Path.of(".", Constants.DATA_PATH, statsFolder, fileName + ".csv");
        String[] header = {
                "Center name", "Avg Queue Time", "Avg Service Time per Services",
                "Groups Served", "Normal Group Served", "Priority Group Served",
                "N_s", "N_q", "Avg Waiting Time By Area",
                "Avg Service Time - Groups", "Lambda", "Rho",
                "Avg Queue Time", "Avg Queue Time Normal", "Avg Queue Time Prio",
                "Normal People Enqueued", "Priority People Enqueued"
        };

        // Writing the header
        CsvWriter.writeHeader(filePath, header);

        // Writing the file
        List<Object> record = List.of(
                name, avgQueueTimePerGroup, avgServiceTimePerCompletedService,
                groupsServed, numberOfNormalGroup, numberOfPriorityGroup,
                group_N_s, group_N_q, groupsWaitByArea,
                avgServiceTimePerGroup, lambda, rho,
                avgQueueTimePerGroup, avgQueueTimePerGroupNormal, avgQueueTimePerGroupPrio,
                numberOfNormalGroupEnqueued, numberOfPriorityGroupEnqueued);
        CsvWriter.writeRecord(filePath, record);
    }

}
