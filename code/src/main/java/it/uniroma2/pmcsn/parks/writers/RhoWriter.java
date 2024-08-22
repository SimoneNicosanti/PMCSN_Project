package it.uniroma2.pmcsn.parks.writers;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;

import it.uniroma2.pmcsn.parks.engineering.Constants;
import it.uniroma2.pmcsn.parks.utils.ConfidenceIntervalComputer.ConfidenceInterval;

public class RhoWriter {

    public static void writeMultipliedRhoValues(Map<String, ConfidenceInterval> confIntervalMap,
            Map<String, Integer> seatsNumberMap) {

        Path fileDirectory = Path.of(Constants.DATA_PATH, "Rho");
        Path filePath = Path.of(fileDirectory.toString(), "MultipliedRhos.csv");

        String[] header = { "SmallGroupSize", "SmallPercentageSize", "CenterName", "MultipliedRho", "Interval", "Rho" };
        CsvWriter.writeHeader(filePath, header);

        for (String centerName : confIntervalMap.keySet()) {
            ConfidenceInterval confInterval = confIntervalMap.get(centerName);

            List<Object> record = List.of(
                    Constants.SMALL_GROUP_LIMIT_SIZE,
                    Constants.SMALL_GROUP_PERCENTAGE_PER_RIDE,
                    centerName,
                    confInterval.mean(),
                    confInterval.interval(),
                    confInterval.mean() / seatsNumberMap.get(centerName));
            CsvWriter.writeRecord(filePath, record);
        }
    }

}
