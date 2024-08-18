package it.uniroma2.pmcsn.parks.writers;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;

import it.uniroma2.pmcsn.parks.engineering.Constants;
import it.uniroma2.pmcsn.parks.model.job.GroupPriority;
import it.uniroma2.pmcsn.parks.model.queue.QueuePriority;
import it.uniroma2.pmcsn.parks.utils.FunIndexComputer.FunIndexInfo;

public class FunIndexWriter {

    public static void writeFunIndexResults(Map<GroupPriority, FunIndexInfo> funIndexMap) {

        Path fileDirectory = Path.of(Constants.DATA_PATH, "Fun");
        Path filePath = Path.of(fileDirectory.toString(), "FunIndex.csv");

        String[] header = { "Percentage", "Priority", "AvgVisits", "AvgServiceTime", "AvgQueueTime", "FunIndex" };
        CsvWriter.writeHeader(filePath, header);

        for (GroupPriority priority : funIndexMap.keySet()) {
            FunIndexInfo funIndexInfo = funIndexMap.get(priority);

            List<Object> record = List.of(
                    Constants.PRIORITY_PERCENTAGE_PER_RIDE,
                    priority.name(),
                    funIndexInfo.avgNumberOfRides(),
                    funIndexInfo.avgServiceTime(),
                    funIndexInfo.avgQueueTime(),
                    funIndexInfo.avgFunIndex());
            CsvWriter.writeRecord(filePath, record);
        }

    }

    public static void writePriorityQueueTimes(Map<QueuePriority, Map<String, Double>> perPrioQueueTimeMap) {
        Path fileDirectory = Path.of(Constants.DATA_PATH, "Fun");
        Path filePath = Path.of(fileDirectory.toString(), "PriorityQueueTime.csv");

        String[] header = { "Percentage", "Priority", "CenterName", "AvgQueueTime" };
        CsvWriter.writeHeader(filePath, header);

        for (QueuePriority prio : perPrioQueueTimeMap.keySet()) {
            Map<String, Double> priorityQueueTimeMap = perPrioQueueTimeMap.get(prio);

            for (String centerName : priorityQueueTimeMap.keySet()) {

                List<Object> record = List.of(
                        Constants.PRIORITY_PERCENTAGE_PER_RIDE,
                        prio.name(),
                        centerName,
                        priorityQueueTimeMap.get(centerName));
                CsvWriter.writeRecord(filePath, record);
            }
        }

    }

}
