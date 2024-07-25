package it.uniroma2.pmcsn.parks.utils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVFormat.Builder;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.io.FileUtils;

import it.uniroma2.pmcsn.parks.engineering.Config;
import it.uniroma2.pmcsn.parks.model.job.RiderGroup;

public class RiderStatisticsWriter {

    private String filepath;

    public RiderStatisticsWriter(String outputFileName) {

        this.filepath = outputFileName;

        String[] header = { "Group Size", "Priority", "Queue Time", "Riding Time", "Total Time", "Number of rides" };

        try (
                Writer writer = new FileWriter(this.filepath);
                CSVPrinter csvPrinter = new CSVPrinter(writer,
                        Builder.create(CSVFormat.DEFAULT).setHeader(header).build())) {
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void resetStatistics(String statsCase) {
        Path statisticsDirectory = Path.of(Config.DATA_PATH, statsCase);

        try {
            FileUtils.deleteDirectory(statisticsDirectory.toFile());
            new File(statisticsDirectory.toString()).mkdirs();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    public void writeStatistics(RiderGroup riderGroup) {

        Integer groupSize = riderGroup.getGroupSize();
        Integer priority = riderGroup.getPriority().ordinal();
        Double totalQueueTime = riderGroup.getGroupStats().getQueueTime();
        Double totalRidingTime = riderGroup.getGroupStats().getServiceTime();
        Integer totalRiding = riderGroup.getGroupStats().getTotalNumberOfVisits();

        try (
                Writer writer = new FileWriter(this.filepath, true);
                CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.DEFAULT)) {
            csvPrinter.printRecord(groupSize, priority, totalQueueTime, totalRidingTime, totalRiding);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
