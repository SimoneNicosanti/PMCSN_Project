package it.uniroma2.pmcsn.parks.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import it.uniroma2.pmcsn.parks.SimulationMode;
import it.uniroma2.pmcsn.parks.engineering.Constants;
import it.uniroma2.pmcsn.parks.engineering.factory.EventBuilder;
import it.uniroma2.pmcsn.parks.engineering.factory.NetworkBuilder;
import it.uniroma2.pmcsn.parks.engineering.interfaces.Center;
import it.uniroma2.pmcsn.parks.engineering.interfaces.Controller;
import it.uniroma2.pmcsn.parks.engineering.singleton.ClockHandler;
import it.uniroma2.pmcsn.parks.engineering.singleton.ConfigHandler;
import it.uniroma2.pmcsn.parks.engineering.singleton.EventsPool;
import it.uniroma2.pmcsn.parks.model.Interval;
import it.uniroma2.pmcsn.parks.model.event.SystemEvent;
import it.uniroma2.pmcsn.parks.model.job.GroupPriority;
import it.uniroma2.pmcsn.parks.model.job.RiderGroup;
import it.uniroma2.pmcsn.parks.model.server.concrete_servers.ExitCenter;
import it.uniroma2.pmcsn.parks.utils.FunIndexComputer;
import it.uniroma2.pmcsn.parks.writers.EventLogger;
import it.uniroma2.pmcsn.parks.writers.FunIndexWriter;
import it.uniroma2.pmcsn.parks.writers.JobInfoWriter;
import it.uniroma2.pmcsn.parks.writers.WriterHelper;

public class FunIndexController implements Controller<RiderGroup> {

    private NetworkBuilder networkBuilder;
    private ClockHandler clockHandler;
    private ConfigHandler configHandler;
    private Interval currentInterval;

    public static void main(String[] args) {
        WriterHelper.createAllFolders();
        new FunIndexController().simulate();
    }

    public FunIndexController() {
        Constants.MODE = SimulationMode.NORMAL;
        // Constants.IMPROVED_MODEL = true;
        this.networkBuilder = new NetworkBuilder();
    }

    @Override
    public void simulate() {

        init_simulation();

        Map<GroupPriority, Double> funIndexMap = new HashMap<>();
        for (GroupPriority prio : GroupPriority.values()) {
            funIndexMap.put(prio, 0.0);
        }

        for (int i = 0; i < Constants.FUN_INDEX_REPLICATIONS_NUMBER; i++) {
            System.out.println("Replication Number >>> " + i);

            ExitCenter exitCenter = new Simulation().simulateOnce();

            // EventLogger.writeRandomLogString(RandomHandler.getInstance().getRandomLog());

            List<RiderGroup> exitRiderGroups = exitCenter.getExitJobs();

            JobInfoWriter.writeAllJobsInfo("Job", "Job_Info_2.csv", exitRiderGroups);

            Map<GroupPriority, Double> currentFunIndexMap = FunIndexComputer.computeAvgsFunIndex(exitRiderGroups);

            funIndexMap.replaceAll((prio, value) -> value + currentFunIndexMap.getOrDefault(prio, 0.0));
        }

        funIndexMap.replaceAll((key, value) -> value / Constants.FUN_INDEX_REPLICATIONS_NUMBER);

        System.out.println(funIndexMap.toString());

        FunIndexWriter.writeFunIndexResults(funIndexMap);

    }

    public void simulateOnce() {
        this.networkBuilder = new NetworkBuilder();
        this.networkBuilder.buildNetwork();

        ClockHandler.getInstance().setClock(0.0);
        EventsPool.reset();
        this.configHandler = ConfigHandler.getInstance();
        this.currentInterval = configHandler.getCurrentInterval();
        this.configHandler.changeParameters(currentInterval);

        this.scheduleArrivalEvent();
        clockHandler = ClockHandler.getInstance();

        while (true) {

            SystemEvent nextEvent = EventsPool.getInstance().getNextEvent();
            if (nextEvent == null) {
                // When all the events finish, the simulation ends
                break;
            }

            clockHandler.setClock(nextEvent.getEventTime());

            // Check if the current interval changed
            Interval interval = configHandler.getInterval(clockHandler.getClock());
            if (isIntervalChanged(interval)) {
                changeInterval(interval);

                // If the park is closing...
                if (configHandler.isParkClosing(currentInterval)) {
                    // ... free and terminate the current services and ...
                    // eventsPool.freePool();
                    // ... close all centers (free the queues)
                    for (Center<RiderGroup> center : this.networkBuilder.getAllCenters()) {
                        center.closeCenter();
                    }
                }
            }

            RiderGroup job = nextEvent.getJob();
            Center<RiderGroup> center = nextEvent.getEventCenter();
            switch (nextEvent.getEventType()) {
                case ARRIVAL:
                    boolean mustServe = center.isQueueEmptyAndCanServe(job.getGroupSize());
                    center.arrival(job);
                    if (mustServe) {
                        center.startService();
                    }
                    break;

                case END_PROCESS:
                    center.endService(job);
                    if (center.canServe(1)) {
                        center.startService();
                    }
                    break;
            }
        }

        // IntervalStatisticsWriter.writeCenterStatistics(networkBuilder.getAllCenters());

        EventLogger.logRandomStreams("RandomStreams");

        System.out.println("LAST CLOCK >>> " + ClockHandler.getInstance().getClock());
        System.out.println();
    }

    private void scheduleArrivalEvent() {
        Center<RiderGroup> entranceCenter = networkBuilder.getCenterByName(Constants.ENTRANCE);
        SystemEvent arrivalEvent = EventBuilder.getNewArrivalEvent(entranceCenter);
        EventsPool.getInstance().scheduleNewEvent(arrivalEvent);
    }

    private void init_simulation() {
        // Reset statistics
        // WriterHelper.clearDirectory(Path.of(Constants.DATA_PATH,
        // "Center").toString());
        WriterHelper.clearDirectory(Constants.JOB_DATA_PATH);
        // Prepare the logger and set the system clock to 0
        // WriterHelper.clearDirectory(Constants.LOG_PATH);
        ClockHandler.getInstance().setClock(0);
    }

    private boolean isIntervalChanged(Interval interval) {
        return !interval.equals(this.currentInterval);
    }

    private void changeInterval(Interval interval) {
        // System.out.println("CURRENT INTERVAL CHANGED");
        // System.out.println(interval.getStart() + " - " + interval.getEnd());
        // System.out.println("");

        // Change the parameters based on the interval
        changeParameters(interval);
        this.currentInterval = interval;
    }

    private void changeParameters(Interval interval) {
        ConfigHandler.getInstance().changeParameters(interval);
    }

}
