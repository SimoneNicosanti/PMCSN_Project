package it.uniroma2.pmcsn.parks.verification;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.math3.dfp.DfpField.RoundingMode;

import it.uniroma2.pmcsn.parks.engineering.Constants;
import it.uniroma2.pmcsn.parks.engineering.interfaces.Center;
import it.uniroma2.pmcsn.parks.model.job.RiderGroup;
import it.uniroma2.pmcsn.parks.model.server.concrete_servers.StatsCenter;
import it.uniroma2.pmcsn.parks.model.server.AbstractCenter;
import it.uniroma2.pmcsn.parks.model.stats.BatchStats;
import it.uniroma2.pmcsn.parks.utils.ConfidenceIntervalComputer.ConfidenceInterval;
import it.uniroma2.pmcsn.parks.utils.ConfidenceIntervalComputer.CumulativeAvg;
import it.uniroma2.pmcsn.parks.writers.CsvWriter;
import it.uniroma2.pmcsn.parks.writers.WriterHelper;

public class VerificationWriter {

    public static void writeConfidenceIntervals(List<ConfidenceInterval> confidenceIntervals,
            Map<String, Map<String, Double>> theoryMap, String fileName) {
        String subFolder = switch (Constants.MODE) {
            case NORMAL -> "";
            case VERIFICATION -> "Verification";
            case VALIDATION -> "Validation";
            case CONSISTENCY_CHECK -> "Consistency Checks";
        };

        Path filePath = Path.of(".", Constants.DATA_PATH, subFolder, fileName + ".csv");

        String[] header = {
                "Center Name",
                "Metric Name",
                "Mean Value",
                "Autocorrelation",
                "Interval",
                "Lower Bound",
                "Theory Value",
                "Upper Bound",
                "Theory Value Is Inside"
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
            Double theoryValue = theoryMap.get(interval.centerName()).get(interval.statsName());
            List<Object> record = List.of(
                    interval.centerName(),
                    interval.statsName(),
                    interval.mean(),
                    interval.autocorrelation(),
                    interval.interval(),
                    interval.mean() - interval.interval(),
                    theoryValue,
                    interval.mean() + interval.interval(),
                    (interval.mean() - interval.interval() < theoryValue)
                            && (theoryValue < interval.mean() + interval.interval()));
            CsvWriter.writeRecord(filePath, record);
        }

    }

    public static void resetData() {
        WriterHelper.clearDirectory(Constants.VERIFICATION_PATH);
    }

    public static void writeTheoreticalQueueTimeValues(Map<String, Map<String, Double>> theoryMap) {
        Path filePath = Path.of(".", Constants.DATA_PATH, "Verification", "TheoreticalQueueTime" + ".csv");
        String[] header = {
                "Center Name",
                "Theoretical Queue Time",
        };
        CsvWriter.writeHeader(filePath, header);

        List<String> keys = new ArrayList<>(theoryMap.keySet());
        keys.sort(String::compareTo);

        for (String centerName : keys) {
            List<Object> record = List.of(centerName, theoryMap.get(centerName).get("QueueTime"));
            CsvWriter.writeRecord(filePath, record);
        }
    }

    public static void writeCumulativeAvgs(List<CumulativeAvg> cumulativeAvgs) {
        Path filePath = Path.of(Constants.VERIFICATION_PATH, "CumulativeAvgs.csv");
        String[] header = { "CenterName", "StatsName", "TheoryValue" };
        CsvWriter.writeHeader(filePath, header);

        for (CumulativeAvg cumAvg : cumulativeAvgs) {
            List<Object> record = new ArrayList<>();
            record.add(cumAvg.centerName());
            record.add(cumAvg.statsName());
            record.add(cumAvg.theoryValue());
            record.addAll(cumAvg.cumulativeAvg());
            CsvWriter.writeRecord(filePath, record);
        }
    }

    public static void writeSimulationResult(List<Center<RiderGroup>> centerList,
            Map<String, Map<String, Double>> theoryMap) {
        Path filePath = Path.of(Constants.VERIFICATION_PATH, "RawResults.csv");
        String[] header = { "CenterName", "StatName", "TheoryValue" };
        CsvWriter.writeHeader(filePath, header);

        for (Center<RiderGroup> center : centerList) {
            List<BatchStats> batchStatList = ((StatsCenter) center).getBatchStats();
            for (BatchStats batchStats : batchStatList) {
                String statName = batchStats.getStatName();
                List<Double> batchAvgs;
                if (statName == "QueueTime") {
                    batchAvgs = batchStats.getTimeAvgs();
                    writeSimulationRecord(theoryMap, filePath, center, batchAvgs, "QueueTime");

                    batchAvgs = batchStats.getNumberAvgs();
                    writeSimulationRecord(theoryMap, filePath, center, batchAvgs, "N_Q");
                } else {
                    batchAvgs = batchStats.getTimeAvgs();
                    writeSimulationRecord(theoryMap, filePath, center, batchAvgs, "ServiceTime");

                    Integer m = ((AbstractCenter) ((StatsCenter) center).getCenter()).getSlotNumber();
                    batchAvgs = batchStats.getNumberAvgs();
                    batchAvgs.replaceAll(elem -> elem / m);

                    writeSimulationRecord(theoryMap, filePath, center, batchAvgs, "Rho");
                }
            }
        }
    }

    private static void writeSimulationRecord(Map<String, Map<String, Double>> theoryMap, Path filePath,
            Center<RiderGroup> center,
            List<Double> batchAvgs, String statName) {
        Double theoryValue = theoryMap.get(center.getName()).get(statName);

        // List<Double> numberAvg = batchStats.getNumberAvgs();

        List<Object> record = new ArrayList<>();
        record.add(center.getName());
        record.add(statName);
        record.add(theoryValue);
        record.addAll(batchAvgs);

        CsvWriter.writeRecord(filePath, record);
    }

}
