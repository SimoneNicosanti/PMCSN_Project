package it.uniroma2.pmcsn.parks.writers;

import java.io.IOException;
import java.nio.file.Path;
import org.apache.commons.io.FileUtils;

import it.uniroma2.pmcsn.parks.engineering.Constants;

public class WriterHelper {

    public static void clearDirectory(String directoryPathString) {
        try {
            FileUtils.deleteDirectory(Path.of(directoryPathString).toFile());
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
