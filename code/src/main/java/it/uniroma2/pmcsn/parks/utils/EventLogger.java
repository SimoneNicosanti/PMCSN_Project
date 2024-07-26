package it.uniroma2.pmcsn.parks.utils;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;

import it.uniroma2.pmcsn.parks.engineering.singleton.ClockHandler;
import it.uniroma2.pmcsn.parks.model.event.Event;
import it.uniroma2.pmcsn.parks.model.job.RiderGroup;

public class EventLogger {

    public static void logEvent(String processingType, Event<RiderGroup> event) {

        Path centerFilePath = Path.of("Out", "Log", event.getEventCenter().getName() + ".log");
        Path generalFilePath = Path.of("Out", "Log", "GeneralEventLog.log");

        String logString = "Simulation Type >> " + processingType + "\n" +
                "Event Type >> " + event.getEventType().name() + "\n" +
                "Center Name >>> " + event.getEventCenter().getName() + "\n" +
                "Group Id >>> " + event.getJob().getGroupId() + "\n" +
                "Event Time >>> " + event.getEventTime() + "\n" +
                "Simulation Clock >>> " + ClockHandler.getInstance().getClock() + "\n\n";

        try {
            centerFilePath.toFile().createNewFile();
            generalFilePath.toFile().createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try (
                FileOutputStream centerLogWriter = new FileOutputStream(centerFilePath.toFile(), true);
                FileOutputStream generalLogWriter = new FileOutputStream(generalFilePath.toFile(), true);) {
            centerLogWriter.write(logString.getBytes());
            generalLogWriter.write(logString.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println(logString);
    }

    public static void prepareLog() {
        Path logDirectoryPath = Path.of("Out", "Log");

        try (DirectoryStream<Path> stream = Files.newDirectoryStream(logDirectoryPath)) {
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

    public static void logExit(double clock) {
        Path exitLogFilePath = Path.of("Out", "Log", "Exit.log");

        String logString = "Simulation Type >> Exit" + "\n" +
                "Simulation Clock >>> " + clock + "\n\n";

        try {
            exitLogFilePath.toFile().createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try (
                FileOutputStream exitLogWriter = new FileOutputStream(exitLogFilePath.toFile(), true)) {
            exitLogWriter.write(logString.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println(logString);
    }
}
