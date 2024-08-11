package it.uniroma2.pmcsn.parks.controller;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.naming.ldap.Control;

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
import it.uniroma2.pmcsn.parks.model.server.concrete_servers.StatsCenter;
import it.uniroma2.pmcsn.parks.utils.EventLogger;
import it.uniroma2.pmcsn.parks.utils.IntervalStatisticsWriter;
import it.uniroma2.pmcsn.parks.utils.WriterHelper;

public class ValidationController_1 implements Controller<RiderGroup> {

    private NetworkBuilder networkBuilder;
    private ClockHandler clockHandler;
    private EventsPool<RiderGroup> eventsPool;
    private ConfigHandler configHandler;
    private Interval currentInterval;

    public static void main(String[] args) {
        WriterHelper.createAllFolders();
        new ValidationController_1().simulate();
    }

    public ValidationController_1() {
        Constants.MODE = SimulationMode.VALIDATION;
        this.networkBuilder.buildNetwork();
        this.configHandler = ConfigHandler.getInstance();
        this.eventsPool = EventsPool.<RiderGroup>getInstance();
    }

    @Override
    public void simulate() {
        init_simulation();

        Map<String, Double> queueTimeMap = new HashMap<>();

        for (int i = 0; i < 100; i++) {
            this.simulateOnce();
            List<Center<RiderGroup>> centerList = this.networkBuilder.getAllCenters();

            for (Center<RiderGroup> center : centerList) {
                if (((StatsCenter) center).getCenter() instanceof )
            }
        }
    }

    public void simulateOnce() {

        ClockHandler.getInstance().setClock(0);
        eventsPool.resetPool();
        this.currentInterval = configHandler.getCurrentInterval();
        configHandler.changeParameters(currentInterval);

        this.scheduleArrivalEvent();
        clockHandler = ClockHandler.getInstance();

        while (true) {

            SystemEvent<RiderGroup> nextEvent = eventsPool.getNextEvent();
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
            Long groupId = job.getGroupId();
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

    private void scheduleArrivalEvent() {
        Center<RiderGroup> entranceCenter = networkBuilder.getCenterByName(Constants.ENTRANCE);
        SystemEvent<RiderGroup> arrivalEvent = EventBuilder.getNewArrivalEvent(entranceCenter);
        eventsPool.scheduleNewEvent(arrivalEvent);
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
}