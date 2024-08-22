package it.uniroma2.pmcsn.parks.writers;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;

import it.uniroma2.pmcsn.parks.engineering.Constants;
import it.uniroma2.pmcsn.parks.utils.ConfidenceIntervalComputer.ConfidenceInterval;

public class FunIndexWriter {

    // FunIndex related methods

    public static void writeFunIndexResults(Map<String, ConfidenceInterval> funIdxConfInterMap) {

        Path filePath = Path.of(filePath().toString(), "FunIndex_" + Constants.SMALL_GROUP_LIMIT_SIZE + ".csv");

        CsvWriter.writeHeader(filePath, funIdxHeader());

        funIdxConfInterMap.forEach((prio, confInterval) -> {
            CsvWriter.writeRecord(filePath, funIdxRecord(prio, confInterval));
        });
    }

    private static String[] funIdxHeader() {
        if (Constants.IMPROVED_MODEL) {
            return new String[] { "SmallSeatsPercentage", "PoissonParam", "Priority", "FunIndex",
                    "ConfInterval" };
        } else {
            return new String[] { "PrioSeatsPercentage", "Priority", "FunIndex",
                    "ConfInterval" };
        }
    }

    private static List<Object> funIdxRecord(String prio, ConfidenceInterval confInterval) {

        if (Constants.IMPROVED_MODEL) {
            return List.of(
                    Constants.SMALL_GROUP_PERCENTAGE_PER_RIDE,
                    Constants.AVG_GROUP_SIZE_POISSON,
                    prio,
                    confInterval.mean(),
                    confInterval.interval());
        } else {
            return List.of(
                    Constants.PRIORITY_PERCENTAGE_PER_RIDE,
                    prio,
                    confInterval.mean(),
                    confInterval.interval());
        }
    }

    // PriorityQueue related methods

    public static void writePriorityQueueTimes(
            Map<String, ConfidenceInterval> queueConfIntervals) {

        Path filePath = Path.of(filePath().toString(),
                "PriorityQueueTime_" + Constants.SMALL_GROUP_LIMIT_SIZE + ".csv");

        CsvWriter.writeHeader(filePath, queueTimesHeader());

        queueConfIntervals.forEach((key, confInterval) -> {
            String prio = extractFromKey(1, key);
            CsvWriter.writeRecord(filePath, queueTimesRecord(confInterval.centerName(), prio, confInterval));
        });
    }

    private static String[] queueTimesHeader() {
        if (Constants.IMPROVED_MODEL) {
            return new String[] { "SmallSeatsPercentage", "PoissonParam", "CenterName", "Priority", "AvgQueueTime",
                    "ConfInterval" };
        } else {
            return new String[] { "PrioPassProb", "PrioSeatsPercentage", "CenterName", "Priority", "AvgQueueTime",
                    "ConfInterval" };
        }
    }

    private static List<Object> queueTimesRecord(String centerName, String prio, ConfidenceInterval confInterval) {

        if (Constants.IMPROVED_MODEL) {
            return List.of(
                    Constants.SMALL_GROUP_PERCENTAGE_PER_RIDE,
                    Constants.AVG_GROUP_SIZE_POISSON,
                    centerName,
                    prio,
                    confInterval.mean(),
                    confInterval.interval());
        } else {
            return List.of(
                    Constants.PRIORITY_PASS_PROB,
                    Constants.PRIORITY_PERCENTAGE_PER_RIDE,
                    centerName,
                    prio,
                    confInterval.mean(),
                    confInterval.interval());
        }
    }

    private static Path filePath() {
        return Path.of(Constants.DATA_PATH, "Fun", Constants.IMPROVED_MODEL ? "Improved" : "Basic");
    }

    private static String extractFromKey(int idx, String key) {
        return key.split("::")[idx];
    }
}
