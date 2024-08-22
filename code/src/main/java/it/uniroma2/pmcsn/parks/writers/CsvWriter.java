package it.uniroma2.pmcsn.parks.writers;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVFormat.Builder;
import org.apache.commons.csv.CSVPrinter;

public class CsvWriter {

    public static void writeHeader(Path filePath, String[] header) {

        if (!filePath.toFile().exists()) {
            try {
                Files.createDirectories(filePath.getParent());
                filePath.toFile().createNewFile();
                try (
                        Writer writer = new FileWriter(filePath.toFile(), true);
                        CSVPrinter csvPrinter = new CSVPrinter(writer,
                                Builder.create(CSVFormat.DEFAULT).setHeader(header).build())) {
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void writeRecord(Path filePath, List<Object> record) {
        try (
                Writer writer = new FileWriter(filePath.toFile(), true);
                CSVPrinter csvPrinter = new CSVPrinter(writer,
                        Builder.create(CSVFormat.DEFAULT).build())) {

            csvPrinter.printRecord(record);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
