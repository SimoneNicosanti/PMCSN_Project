package it.uniroma2.pmcsn.parks.controller;

import it.uniroma2.pmcsn.parks.SimulationMode;
import it.uniroma2.pmcsn.parks.engineering.Constants;
import it.uniroma2.pmcsn.parks.engineering.factory.EventBuilder;
import it.uniroma2.pmcsn.parks.engineering.factory.NetworkBuilder;
import it.uniroma2.pmcsn.parks.engineering.interfaces.Center;
import it.uniroma2.pmcsn.parks.engineering.singleton.ClockHandler;
import it.uniroma2.pmcsn.parks.engineering.singleton.ConfigHandler;
import it.uniroma2.pmcsn.parks.engineering.singleton.EventsPool;
import it.uniroma2.pmcsn.parks.model.Interval;
import it.uniroma2.pmcsn.parks.model.event.SystemEvent;
import it.uniroma2.pmcsn.parks.model.job.RiderGroup;
import it.uniroma2.pmcsn.parks.model.server.concrete_servers.StatsCenter;

public class Simulation {

    private ConfigHandler configHandler;
    private Interval currentInterval;

    public Simulation(SimulationMode mode) {
        Constants.MODE = mode;

        // IF NEEDED RESET RANDOM_HANDLER OUTSIDE THIS!!!!!
        ClockHandler.reset();
        EventsPool.reset();
        ConfigHandler.reset();

        this.configHandler = ConfigHandler.getInstance();
        this.currentInterval = configHandler.getInterval(ClockHandler.getInstance().getClock());
        this.configHandler.changeParameters(currentInterval);
    }

    public NetworkBuilder simulateOnce() {

        NetworkBuilder networkBuilder = new NetworkBuilder();
        networkBuilder.buildNetwork();

        scheduleFirstArrival(networkBuilder.getCenterByName(Constants.ENTRANCE));

        while (true) {

            SystemEvent nextEvent = EventsPool.getInstance().getNextEvent();
            if (nextEvent == null) {
                // When all the events finish, the simulation ends
                break;
            }

            ClockHandler.getInstance().setClock(nextEvent.getEventTime());

            // EventLogger.logEvent(Constants.MODE.name(), nextEvent);

            // Check if the current interval changed
            Interval interval = configHandler.getInterval(ClockHandler.getInstance().getClock());
            if (isIntervalChanged(interval)) {
                changeInterval(interval);

                // If the park is closing...
                if (configHandler.isParkClosing(currentInterval)) {
                    // ... free and terminate the current services and ...
                    // eventsPool.freePool();
                    // ... close all centers (free the queues)
                    for (Center<RiderGroup> center : networkBuilder.getAllCenters()) {
                        center.closeCenter();
                    }
                }
            }

            processNextEvent(nextEvent, networkBuilder);
        }
        System.out.println("LAST CLOCK >>> " + ClockHandler.getInstance().getClock() + "\n");
        // IntervalStatisticsWriter.writeCenterStatistics(networkBuilder.getAllCenters());

        // EventLogger.logRandomStreams("RandomStreams");

        return networkBuilder;
    }

    private void scheduleFirstArrival(Center<RiderGroup> entranceCenter) {
        SystemEvent arrivalEvent = EventBuilder.getNewArrivalEvent(entranceCenter);
        EventsPool.getInstance().scheduleNewEvent(arrivalEvent);
    }

    private boolean isIntervalChanged(Interval interval) {
        return !interval.equals(this.currentInterval);
    }

    // Change the parameters based on the interval
    private void changeInterval(Interval interval) {
        ConfigHandler.getInstance().changeParameters(interval);
        this.currentInterval = interval;
    }

    public NetworkBuilder batchSimulation() {
        NetworkBuilder networkBuilder = new NetworkBuilder();
        networkBuilder.buildNetwork();

        scheduleFirstArrival(networkBuilder.getCenterByName(Constants.ENTRANCE));

        while (!stopBatchSimulation(networkBuilder)) {
            SystemEvent nextEvent = EventsPool.getInstance().getNextEvent();
            Double nextEventTime = nextEvent.getEventTime();
            ClockHandler.getInstance().setClock(nextEventTime);

            processNextEvent(nextEvent, networkBuilder);
        }
        System.out.println("Final Clock >>> " + ClockHandler.getInstance().getClock() + "\n");

        // EventLogger.logRandomStreams("RandomStreams");

        return networkBuilder;
    }

    private void processNextEvent(SystemEvent nextEvent, NetworkBuilder networkBuilder) {
        RiderGroup job = nextEvent.getJob();
        Center<RiderGroup> center = networkBuilder.getCenterByName(nextEvent.getCenterName());
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

    private boolean stopBatchSimulation(NetworkBuilder networkBuilder) {
        for (Center<RiderGroup> center : networkBuilder.getAllCenters()) {
            StatsCenter statCenter = (StatsCenter) center;

            if (!statCenter.areBatchesCompleted()) {
                return false;
            }
        }
        return true;
    }
}