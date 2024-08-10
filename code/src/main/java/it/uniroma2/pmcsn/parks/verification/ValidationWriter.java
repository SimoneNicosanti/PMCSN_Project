package it.uniroma2.pmcsn.parks.verification;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import it.uniroma2.pmcsn.parks.engineering.Constants;
import it.uniroma2.pmcsn.parks.engineering.interfaces.Center;
import it.uniroma2.pmcsn.parks.model.job.RiderGroup;
import it.uniroma2.pmcsn.parks.model.server.AbstractCenter;
import it.uniroma2.pmcsn.parks.model.server.concrete_servers.StatsCenter;
import it.uniroma2.pmcsn.parks.model.stats.BatchStats;
import it.uniroma2.pmcsn.parks.utils.CsvWriter;
import it.uniroma2.pmcsn.parks.utils.WriterHelper;
import it.uniroma2.pmcsn.parks.verification.ConfidenceIntervalComputer.ConfidenceInterval;
import it.uniroma2.pmcsn.parks.verification.ConfidenceIntervalComputer.CumulativeAvg;

public class ValidationWriter {

    public static void writeConfidenceIntervals(List<ConfidenceInterval> confidenceIntervals, String fileName) {

        Path filePath = Path.of(".", Constants.DATA_PATH, "Validation", fileName + ".csv");

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

    public static void writeCumulativeAvgs(List<CumulativeAvg> cumulativeAvgs) {
        Path filePath = Path.of(Constants.VERIFICATION_PATH, "CumulativeAvgs.csv");
        String[] header = { "CenterName", "StatsName", "TheoryValue" };
        CsvWriter.writeHeader(filePath, header);

        for (CumulativeAvg cumAvg : cumulativeAvgs) {
            List<Object> record = new ArrayList<>();
            record.add(cumAvg.centerName());
            record.add(cumAvg.statsName());
            record.addAll(cumAvg.cumulativeAvg());
            CsvWriter.writeRecord(filePath, record);
        }
    }

    public static void writeSimulationResult(List<Center<RiderGroup>> centerList,
            Map<String, Map<String, Double>> theoryMap) {
        Path filePath = Path.of(Constants.VALIDATION_PATH, "RawResults.csv");
        String[] header = { "CenterName", "StatName" };
        CsvWriter.writeHeader(filePath, header);

        for (Center<RiderGroup> center : centerList) {
            List<BatchStats> batchStatList = ((StatsCenter) center).getBatchStats();
            for (BatchStats batchStats : batchStatList) {
                String statName = batchStats.getStatName();
                List<Double> batchAvgs;
                if (statName == "QueueTime") {
                    batchAvgs = batchStats.getTimeAvgs();
                    writeSimulationRecord(filePath, center, batchAvgs, "QueueTime");

                    batchAvgs = batchStats.getNumberAvgs();
                    writeSimulationRecord(filePath, center, batchAvgs, "N_Q");
                } else {
                    batchAvgs = batchStats.getTimeAvgs();
                    writeSimulationRecord(filePath, center, batchAvgs, "ServiceTime");

                    Integer m = ((AbstractCenter) ((StatsCenter) center).getCenter()).getSlotNumber();
                    batchAvgs = batchStats.getNumberAvgs();
                    batchAvgs.replaceAll(elem -> elem / m);

                    writeSimulationRecord(filePath, center, batchAvgs, "Rho");
                }
            }
        }
    }

    private static void writeSimulationRecord(Path filePath,
            Center<RiderGroup> center,
            List<Double> batchAvgs, String statName) {

        // List<Double> numberAvg = batchStats.getNumberAvgs();

        List<Object> record = new ArrayList<>();
        record.add(center.getName());
        record.add(statName);
        record.addAll(batchAvgs);

        CsvWriter.writeRecord(filePath, record);
    }

}
