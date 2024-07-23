package it.uniroma2.pmcsn.parks.utils;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVFormat.Builder;
import org.apache.commons.csv.CSVPrinter;

import it.uniroma2.pmcsn.parks.model.job.RiderGroup;

public class RiderStatisticsWriter {

    private String filepath;

    public RiderStatisticsWriter(String outputFileName) {

        this.filepath = outputFileName;

        String[] header = { "Group Size", "Priority", "Queue Time", "Riding Time", "Total Time", "Number of rides" };

        // TODO Check if file already exists
        // File file = new File(outputFileName);

        // if (file.exists()) {
        // System.out.println("Il file esiste.");
        // } else {
        // System.out.println("Il file non esiste.");
        // }

        try (
                Writer writer = new FileWriter(this.filepath);
                CSVPrinter csvPrinter = new CSVPrinter(writer,
                        Builder.create(CSVFormat.DEFAULT).setHeader(header).build())) {
        } catch (IOException e) {
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
                Writer writer = new FileWriter(this.filepath);
                CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.DEFAULT)) {
            csvPrinter.printRecord(groupSize, priority, totalQueueTime, totalRidingTime, totalRiding);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
