package it.uniroma2.pmcsn.parks.writers;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;

import it.uniroma2.pmcsn.parks.engineering.Constants;
import it.uniroma2.pmcsn.parks.model.queue.QueuePriority;
import it.uniroma2.pmcsn.parks.utils.ConfidenceIntervalComputer.ConfidenceInterval;

public class IntervalsQueueTimesWriter {

    public static void writeIntervalsQueueTimes(
            Map<String, ConfidenceInterval> queueTimesConfidenceIntervals) {
        Path fileDirectory = Path.of(Constants.DATA_PATH, "Intervals");
        Path filePath = Path.of(fileDirectory.toString(),
                "QueueTime.csv");

        String[] header = { "IntervalIndex", "CenterName", "Priority", "AvgQueueTime", "ConfInterval" };
        CsvWriter.writeHeader(filePath, header);

        queueTimesConfidenceIntervals.forEach((key, confInterval) -> {
            String timeInterval = extractFromKey(0, key);
            String priority = extractFromKey(2, key);
            List<Object> record = List.of(
                    timeInterval,
                    confInterval.centerName(),
                    priority,
                    confInterval.mean(),
                    confInterval.interval());

            CsvWriter.writeRecord(filePath, record);
        });
    }

    private static String extractFromKey(int idx, String key) {
        return key.split("::")[idx];
    }

    public static void writeTransientQueueTimes(Map<String, ConfidenceInterval> transientTimesConfidenceIntervals) {
        Path fileDirectory = Path.of(Constants.DATA_PATH, "Intervals");
        Path filePath = Path.of(fileDirectory.toString(),
                "TransientQueueTimes.csv");

        String[] header = { "IntervalIndex", "CenterName", "Priority", "AvgQueueTime", "ConfInterval" };
        CsvWriter.writeHeader(filePath, header);

        transientTimesConfidenceIntervals.forEach((key, confInterval) -> {
            String timeInterval = extractFromKey(0, key);
            String priority = extractFromKey(2, key);
            List<Object> record = List.of(
                    timeInterval,
                    confInterval.centerName(),
                    priority,
                    confInterval.mean(),
                    confInterval.interval());

            CsvWriter.writeRecord(filePath, record);
        });
    }
}
