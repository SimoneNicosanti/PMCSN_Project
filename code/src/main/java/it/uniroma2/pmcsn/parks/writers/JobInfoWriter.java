package it.uniroma2.pmcsn.parks.writers;

import java.nio.file.Path;
import java.util.List;

import it.uniroma2.pmcsn.parks.engineering.Constants;
import it.uniroma2.pmcsn.parks.engineering.singleton.ClockHandler;
import it.uniroma2.pmcsn.parks.model.job.RiderGroup;

public class JobInfoWriter {

    public static void writeAllJobsInfo(String statsFolder, String fileName, List<RiderGroup> riderGroups) {
        for (RiderGroup group : riderGroups) {
            writeJobInfo(statsFolder, fileName, group);
        }
    }

    public static void writeJobInfo(String statsFolder, String fileName, RiderGroup riderGroup) {

        Long groupId = riderGroup.getGroupId();
        Integer groupSize = riderGroup.getGroupSize();
        String priority = riderGroup.getPriority().name();
        Double totalQueueTime = riderGroup.getGroupStats().getQueueTime();
        Double totalRidingTime = riderGroup.getGroupStats().getServiceTime();
        Integer totalRiding = riderGroup.getGroupStats().getTotalNumberOfRides();

        Path filePath = Path.of(".", Constants.DATA_PATH, statsFolder, fileName);
        String[] header = { "GroupId", "GroupSize", "Priority", "QueueTime", "RidingTime", "TotalTime", "ExitTime",
                "NumberRides" };

        // Writing the header
        CsvWriter.writeHeader(filePath, header);

        // Writing the file
        List<Object> record = List.of(
                groupId,
                groupSize,
                priority,
                totalQueueTime,
                totalRidingTime,
                ClockHandler.getInstance().getClock() - riderGroup.getGroupStats().getSystemEntranceTime(),
                ClockHandler.getInstance().getClock(),
                totalRiding);
        CsvWriter.writeRecord(filePath, record);
    }

}
