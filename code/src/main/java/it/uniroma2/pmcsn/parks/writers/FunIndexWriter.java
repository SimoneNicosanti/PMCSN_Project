package it.uniroma2.pmcsn.parks.writers;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;

import it.uniroma2.pmcsn.parks.engineering.Constants;
import it.uniroma2.pmcsn.parks.model.job.GroupPriority;
import it.uniroma2.pmcsn.parks.model.queue.QueuePriority;
import it.uniroma2.pmcsn.parks.utils.ConfidenceIntervalComputer;
import it.uniroma2.pmcsn.parks.utils.ConfidenceIntervalComputer.ConfidenceInterval;
import it.uniroma2.pmcsn.parks.utils.FunIndexComputer.FunIndexInfo;

public class FunIndexWriter {

    public static void writeFunIndexResults(Map<GroupPriority, ConfidenceInterval> funIdxConfInterMap) {

        Path fileDirectory = Path.of(Constants.DATA_PATH, "Fun");
        Path filePath = Path.of(fileDirectory.toString(), "FunIndex.csv");

        String[] header = { "Percentage", "Priority", "FunIndex", "Interval" };
        CsvWriter.writeHeader(filePath, header);

        for (GroupPriority priority : funIdxConfInterMap.keySet()) {
            ConfidenceInterval confInterval = funIdxConfInterMap.get(priority);

            List<Object> record = List.of(
                    Constants.PRIORITY_PERCENTAGE_PER_RIDE,
                    priority.name(),
                    confInterval.mean(),
                    confInterval.interval());
            CsvWriter.writeRecord(filePath, record);
        }

    }

    public static void writePriorityQueueTimes(
            Map<String, Map<QueuePriority, ConfidenceInterval>> perPrioQueueTimeMap) {
        Path fileDirectory = Path.of(Constants.DATA_PATH, "Fun");
        Path filePath = Path.of(fileDirectory.toString(), "PriorityQueueTime.csv");

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
