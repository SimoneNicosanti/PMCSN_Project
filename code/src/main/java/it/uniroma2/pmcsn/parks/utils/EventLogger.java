package it.uniroma2.pmcsn.parks.utils;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

import it.uniroma2.pmcsn.parks.engineering.singleton.ClockHandler;
import it.uniroma2.pmcsn.parks.engineering.singleton.RandomHandler;
import it.uniroma2.pmcsn.parks.model.event.SystemEvent;
import it.uniroma2.pmcsn.parks.model.job.RiderGroup;

public class EventLogger {

    public static void logRandomStreams(String streamFileName) {
        Map<String, Integer> streamMap = RandomHandler.getInstance().getStreamMap();
        Path randomStreamsPath = Path.of("Out", "Log", streamFileName + ".log");

        if (!randomStreamsPath.toFile().exists()) {
            try {
                Files.createDirectories(randomStreamsPath.getParent());
                randomStreamsPath.toFile().createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try (FileOutputStream streamLogWriter = new FileOutputStream(randomStreamsPath.toFile(), true)) {
            for (String streamName : streamMap.keySet()) {
                String logString = "Stream Name >>> " + streamName + "\n" +
                        "Stream Index >>> " + streamMap.get(streamName) + "\n\n";
                streamLogWriter.write(logString.getBytes());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static void logEvent(String processingType, SystemEvent<RiderGroup> event) {

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

        // System.out.println(logString);
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
