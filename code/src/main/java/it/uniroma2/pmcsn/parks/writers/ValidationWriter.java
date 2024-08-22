package it.uniroma2.pmcsn.parks.writers;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Locale;

import it.uniroma2.pmcsn.parks.engineering.Constants;
import it.uniroma2.pmcsn.parks.engineering.interfaces.Center;
import it.uniroma2.pmcsn.parks.model.job.RiderGroup;
import it.uniroma2.pmcsn.parks.model.server.concrete_servers.Attraction;
import it.uniroma2.pmcsn.parks.model.server.concrete_servers.StatsCenter;
import it.uniroma2.pmcsn.parks.model.stats.BatchStats;
import it.uniroma2.pmcsn.parks.random.Estimate;
import it.uniroma2.pmcsn.parks.utils.ConfidenceIntervalComputer.ConfidenceInterval;

public class ValidationWriter {

    public static void resetConsistencyCheckDirectory() {
        Path fileDirectory = Path.of(".", Constants.DATA_PATH, "Validation", "Consistency");
        WriterHelper.clearDirectory(fileDirectory.toString());
    }

    public static void writeRawResults(List<Center<RiderGroup>> centerList, String fileName) {
        Path fileDirectory = Path.of(".", Constants.DATA_PATH, "Validation", "Consistency");
        Path filePath = Path.of(fileDirectory.toString(), fileName + ".csv");
        WriterHelper.clearDirectory(filePath.toString());

        String[] header = {
                "CenterName",
        };
        CsvWriter.writeHeader(filePath, header);

        for (Center<RiderGroup> center : centerList) {
            StatsCenter statCenter = (StatsCenter) center;
            String centerName = statCenter.getName();
            if (statCenter.getCenter() instanceof Attraction) {
                centerName = "Attraction_" + centerName;
            }
            BatchStats queueBatch = statCenter.getQueueBatchStats();
            List<Double> avgValues = queueBatch.getNumberAvgs();

            List<Object> record = new ArrayList<>();
            record.add(centerName);
            record.addAll(avgValues);

            CsvWriter.writeRecord(filePath, record);
        }
    }

    public static void writeConfidenceIntervals(List<ConfidenceInterval> confidenceIntervals, String fileName) {

        Path fileDirectory = Path.of(".", Constants.DATA_PATH, "Validation", "Consistency");

        Path filePath = Path.of(fileDirectory.toString(), fileName + ".csv");
        WriterHelper.clearDirectory(filePath.toString());

        String[] header = {
                "Center Name",
                "Mean Value",
                "Autocorrelation",
                "Interval",
                "Lower Bound",
                "Upper Bound",
        };
        CsvWriter.writeHeader(filePath, header);

        confidenceIntervals.sort((arg0, arg1) -> {
            int centerComparison = arg0.centerName().compareTo(arg1.centerName());
            if (centerComparison != 0) {
                return centerComparison;
            }
            return arg0.statsName().compareTo(arg1.statsName());
        });

        for (ConfidenceInterval interval : confidenceIntervals) {
            List<Object> record = List.of(
                    interval.centerName(),
                    // interval.statsName(),
                    String.format(Locale.US, "%.3f", interval.mean()),
                    String.format(Locale.US, "%.3f", interval.autocorrelation()),
                    String.format(Locale.US, "%.3f", interval.interval()),
                    String.format(Locale.US, "%.3f", interval.mean() - interval.interval()),
                    String.format(Locale.US, "%.3f", interval.mean() + interval.interval()));
            CsvWriter.writeRecord(filePath, record);
        }

    }

    public static void resetData() {
        WriterHelper.clearDirectory(Constants.VALIDATION_PATH);
    }

    public static void writeReplicationsResult(Map<String, List<Double>> queueTimeMap) {

        WriterHelper.clearDirectory(Path.of(Constants.DATA_PATH, "Validation", "Replication").toString());

        Path filePath = Path.of(Constants.DATA_PATH, "Validation", "Replication", "ReplicationResults.csv");

        String[] header = { "Center Name", "E[Tq]", "Interval", "LowerBound", "UpperBound" };
        CsvWriter.writeHeader(filePath, header);

        for (String centerName : queueTimeMap.keySet()) {
            List<Double> values = queueTimeMap.get(centerName);
            Double interval = Estimate.computeConfidenceInterval(values, 0.99);
            Double mean = values.stream().reduce(0.0, Double::sum) / Constants.VALDATION_REPLICATIONS_NUMBER;

            List<Object> record = List.of(
                    centerName,
                    String.format(Locale.US, "%.3f", mean),
                    String.format(Locale.US, "%.3f", interval),
                    String.format(Locale.US, "%.3f", mean - interval),
                    String.format(Locale.US, "%.3f", mean + interval));
            CsvWriter.writeRecord(filePath, record);
        }
    }

}
