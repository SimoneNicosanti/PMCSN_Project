package it.uniroma2.pmcsn.parks.writers;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;

import it.uniroma2.pmcsn.parks.engineering.Constants;
import it.uniroma2.pmcsn.parks.utils.ConfidenceIntervalComputer.ConfidenceInterval;

public class IntervalsQueueTimesWriter {

    public static void writeIntervalsQueueTimes(
            Map<String, ConfidenceInterval> queueTimesConfidenceIntervals) {
        Path fileDirectory = Path.of(Constants.DATA_PATH, "Intervals");
        Path filePath = Path.of(fileDirectory.toString(),
                "QueueTime_" + Constants.SMALL_GROUP_LIMIT_SIZE + ".csv");

        String[] header = { "TimeInterval", "Percentage", "Priority", "CenterName", "AvgQueueTime", "ConfInterval" };
        CsvWriter.writeHeader(filePath, header);

        Double percentage = Constants.IMPROVED_MODEL ? Constants.SMALL_GROUP_PERCENTAGE_PER_RIDE
                : Constants.PRIORITY_PERCENTAGE_PER_RIDE;

        queueTimesConfidenceIntervals.forEach((key, confInterval) -> {
            String timeInterval = extractFromKey(0, key);
            String priority = extractFromKey(2, key);
            List<Object> record = List.of(
                    timeInterval,
                    percentage,
                    priority,
                    confInterval.centerName(),
                    confInterval.mean(),
                    confInterval.interval());

            CsvWriter.writeRecord(filePath, record);
        });
    }

    private static String extractFromKey(int idx, String key) {
        return key.split("::")[idx];
    }
}
