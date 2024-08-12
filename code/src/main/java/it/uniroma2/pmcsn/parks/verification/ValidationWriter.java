package it.uniroma2.pmcsn.parks.verification;

import java.io.Writer;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

import it.uniroma2.pmcsn.parks.engineering.Constants;
import it.uniroma2.pmcsn.parks.random.Estimate;
import it.uniroma2.pmcsn.parks.utils.ConfidenceIntervalComputer.ConfidenceInterval;
import it.uniroma2.pmcsn.parks.writers.CsvWriter;
import it.uniroma2.pmcsn.parks.writers.WriterHelper;

public class ValidationWriter {

    public static void writeConfidenceIntervals(List<ConfidenceInterval> confidenceIntervals, String fileName) {

        Path fileDirectory = Path.of(".", Constants.DATA_PATH, "Validation", "Consistency");
        WriterHelper.clearDirectory(fileDirectory.toString());

        Path filePath = Path.of(fileDirectory.toString(), fileName + ".csv");
        WriterHelper.clearDirectory(filePath.toString());

        String[] header = {
                "Center Name",
                "Metric Name",
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
                    interval.statsName(),
                    interval.mean(),
                    interval.autocorrelation(),
                    interval.interval(),
                    interval.mean() - interval.interval(),
                    interval.mean() + interval.interval());
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

            List<Object> record = List.of(centerName, mean, interval, mean - interval, mean + interval);
            CsvWriter.writeRecord(filePath, record);
        }
    }

}
