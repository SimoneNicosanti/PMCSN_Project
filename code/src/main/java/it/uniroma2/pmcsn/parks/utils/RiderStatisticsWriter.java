package it.uniroma2.pmcsn.parks.utils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Path;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVFormat.Builder;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.io.FileUtils;

import it.uniroma2.pmcsn.parks.engineering.Config;
import it.uniroma2.pmcsn.parks.engineering.singleton.ClockHandler;
import it.uniroma2.pmcsn.parks.model.job.RiderGroup;

public class RiderStatisticsWriter {

    public static void resetStatistics(String statsCase) {
        Path statisticsDirectory = Path.of(Config.DATA_PATH, statsCase);

        try {
            FileUtils.deleteDirectory(statisticsDirectory.toFile());
            new File(statisticsDirectory.toString()).mkdirs();
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException();
        }

    }

    public static void writeStatistics(String fileName, RiderGroup riderGroup) {

        Integer groupSize = riderGroup.getGroupSize();
        Integer priority = riderGroup.getPriority().ordinal();
        Double totalQueueTime = riderGroup.getGroupStats().getQueueTime();
        Double totalRidingTime = riderGroup.getGroupStats().getServiceTime();
        Integer totalRiding = riderGroup.getGroupStats().getTotalNumberOfVisits();

        Path filePath = Path.of("Out", "Data", fileName);
        String[] header = { "Group Size", "Priority", "Queue Time", "Riding Time", "Total Time", "Number of rides" };

        // Writing the header
        if (!filePath.toFile().exists()) {
            try (
                    Writer writer = new FileWriter(filePath.toFile(), true);
                    CSVPrinter csvPrinter = new CSVPrinter(writer,
                            Builder.create(CSVFormat.DEFAULT).setHeader(header).build())) {

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // Creating the file
        try (
                Writer writer = new FileWriter(filePath.toFile(), true);
                CSVPrinter csvPrinter = new CSVPrinter(writer,
                        Builder.create(CSVFormat.DEFAULT).build())) {

            csvPrinter.printRecord(groupSize, priority, totalQueueTime, totalRidingTime,
                    ClockHandler.getInstance().getClock(), totalRiding);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
