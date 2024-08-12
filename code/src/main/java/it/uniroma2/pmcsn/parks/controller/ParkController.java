package it.uniroma2.pmcsn.parks.controller;

import java.nio.file.Path;

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
import it.uniroma2.pmcsn.parks.model.job.RiderGroup;
import it.uniroma2.pmcsn.parks.writers.EventLogger;
import it.uniroma2.pmcsn.parks.writers.IntervalStatisticsWriter;
import it.uniroma2.pmcsn.parks.writers.WriterHelper;

public class ParkController implements Controller<RiderGroup> {

    private NetworkBuilder networkBuilder;
    private ClockHandler clockHandler;
    private EventsPool eventsPool;
    private ConfigHandler configHandler;
    private Interval currentInterval;

    public static void main(String[] args) {
        WriterHelper.createAllFolders();
        new ParkController().simulate();
    }

    public ParkController() {
        Constants.MODE = SimulationMode.NORMAL;
        this.networkBuilder = new NetworkBuilder();
        this.networkBuilder.buildNetwork();
        this.configHandler = ConfigHandler.getInstance();
        this.eventsPool = EventsPool.getInstance();

        this.init_simulation();

        this.currentInterval = configHandler.getCurrentInterval();
    }

    public NetworkBuilder getNetworkBuilder() {
        return this.networkBuilder;
    }

    @Override
    public void simulate() {

        this.scheduleArrivalEvent();
        clockHandler = ClockHandler.getInstance();

        while (true) {

            SystemEvent nextEvent = eventsPool.getNextEvent();
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
                // TODO Re add START_PROCESS to have more transparency
                // or add the control in the stats center
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

        IntervalStatisticsWriter.writeCenterStatistics(networkBuilder.getAllCenters());

        EventLogger.logRandomStreams("RandomStreams");

        System.out.println("LAST CLOCK >>> " + ClockHandler.getInstance().getClock());
    }

    private boolean isIntervalChanged(Interval interval) {
        return !interval.equals(this.currentInterval);
    }

    private void changeInterval(Interval interval) {
        System.out.println("CURRENT INTERVAL CHANGED");
        System.out.println(interval.getStart() + " - " + interval.getEnd());
        System.out.println("");

        // Change the parameters based on the interval
        changeParameters(interval);
        this.currentInterval = interval;
    }

    private void changeParameters(Interval interval) {
        ConfigHandler.getInstance().changeParameters(interval);
    }

    private void init_simulation() {
        // Reset statistics
        WriterHelper.clearDirectory("Job");
        WriterHelper.clearDirectory(Path.of(Constants.DATA_PATH, "Center").toString());
        WriterHelper.clearDirectory(Path.of(Constants.DATA_PATH, "Job").toString());
        // Prepare the logger and set the system clock to 0
        WriterHelper.clearDirectory(Constants.LOG_PATH);
        ClockHandler.getInstance().setClock(0);
    }

    private void scheduleArrivalEvent() {
        Center<RiderGroup> entranceCenter = networkBuilder.getCenterByName(Constants.ENTRANCE);
        SystemEvent arrivalEvent = EventBuilder.getNewArrivalEvent(entranceCenter);
        eventsPool.scheduleNewEvent(arrivalEvent);
    }
}
