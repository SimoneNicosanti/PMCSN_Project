package it.uniroma2.pmcsn.parks.utils;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;

import it.uniroma2.pmcsn.parks.engineering.Constants;

public class WriterHelper {

    public static void clearDirectory(String directoryPathString) {
        Path path = Path.of(directoryPathString);
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(path)) {
            for (Path file : stream) {
                // Check if it's a file and not a directory
                BasicFileAttributes attrs = Files.readAttributes(file, BasicFileAttributes.class);
                if (attrs.isRegularFile()) {
                    Files.delete(file);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void createAllFolders() {

        try {
            Path.of(Constants.LOG_PATH).toFile().mkdirs();
            Path.of(Constants.DATA_PATH).toFile().mkdirs();
            Path.of(Constants.VERIFICATION_PATH).toFile().mkdirs();
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }

}
