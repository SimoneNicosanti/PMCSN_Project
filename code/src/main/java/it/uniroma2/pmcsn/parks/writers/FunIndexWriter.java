package it.uniroma2.pmcsn.parks.writers;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;

import it.uniroma2.pmcsn.parks.engineering.Constants;
import it.uniroma2.pmcsn.parks.model.job.GroupPriority;
import it.uniroma2.pmcsn.parks.random.Estimate;

public class FunIndexWriter {

    public static void writeFunIndexResults(Map<GroupPriority, Double> funIndexMap) {

        Path fileDirectory = Path.of(Constants.DATA_PATH, "Fun");
        WriterHelper.clearDirectory(fileDirectory.toString());

        Path filePath = Path.of(fileDirectory.toString(), "FunIndex.csv");

        String[] header = { "Priority", "FunIndex" };
        CsvWriter.writeHeader(filePath, header);

        for (GroupPriority priority : funIndexMap.keySet()) {
            Double value = funIndexMap.get(priority);

            List<Object> record = List.of(priority.name(), value);
            CsvWriter.writeRecord(filePath, record);
        }

    }

}
