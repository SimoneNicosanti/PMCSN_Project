package it.uniroma2.pmcsn.parks.writers;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;

import it.uniroma2.pmcsn.parks.engineering.Constants;
import it.uniroma2.pmcsn.parks.model.queue.QueuePriority;
import it.uniroma2.pmcsn.parks.utils.ConfidenceIntervalComputer.ConfidenceInterval;

public class FunIndexWriter {

    public static void writeFunIndexResults(Map<String, ConfidenceInterval> funIdxConfInterMap) {

        Path fileDirectory = Path.of(Constants.DATA_PATH, "Fun");
        Path filePath = Path.of(fileDirectory.toString(), "FunIndex_" + Constants.SMALL_GROUP_LIMIT_SIZE + ".csv");

        String[] header = { "Percentage", "Priority", "FunIndex", "Interval" };
        CsvWriter.writeHeader(filePath, header);

        for (String priority : funIdxConfInterMap.keySet()) {
            ConfidenceInterval confInterval = funIdxConfInterMap.get(priority);

            List<Object> record = List.of(
                    Constants.PRIORITY_PERCENTAGE_PER_RIDE,
                    priority,
                    confInterval.mean(),
                    confInterval.interval());
            CsvWriter.writeRecord(filePath, record);
        }

    }

    public static void writePriorityQueueTimes(
            Map<String, Map<QueuePriority, ConfidenceInterval>> perPrioQueueTimeMap) {
        Path fileDirectory = Path.of(Constants.DATA_PATH, "Fun");
        Path filePath = Path.of(fileDirectory.toString(), "PriorityQueueTime_" + Constants.SMALL_GROUP_LIMIT_SIZE + ".csv");

        String[] header = { "Percentage", "Priority", "CenterName", "AvgQueueTime", "Interval" };
        CsvWriter.writeHeader(filePath, header);

        for (String centerName : perPrioQueueTimeMap.keySet()) {
            Map<QueuePriority, ConfidenceInterval> confIntervalMap = perPrioQueueTimeMap.get(centerName);

            for (QueuePriority prio : confIntervalMap.keySet()) {

                ConfidenceInterval confInterval = confIntervalMap.get(prio);

                List<Object> record = List.of(
                        Constants.PRIORITY_PERCENTAGE_PER_RIDE,
                        prio.name(),
                        confInterval.centerName(),
                        confInterval.mean(),
                        confInterval.interval());
                CsvWriter.writeRecord(filePath, record);
            }

        }

    }

}
