package it.uniroma2.pmcsn.parks.controller;

import java.util.List;

import it.uniroma2.pmcsn.parks.engineering.CenterManager;
import it.uniroma2.pmcsn.parks.engineering.Config;
import it.uniroma2.pmcsn.parks.engineering.factory.EventBuilder;
import it.uniroma2.pmcsn.parks.engineering.singleton.ClockHandler;
import it.uniroma2.pmcsn.parks.model.event.Event;
import it.uniroma2.pmcsn.parks.model.job.RiderGroup;
import it.uniroma2.pmcsn.parks.model.server.Center;
import it.uniroma2.pmcsn.parks.model.server.Entrance;

public class Controller {

    private EventHandler<RiderGroup> eventHandler;
    private CenterManager<RiderGroup> centerManager;

    public Controller() {
        this.eventHandler = new EventHandler<>();
        this.centerManager = new CenterManager<>();
    }

    public void startSimulation() {

        this.init_simulation();

        // TODO: set end cycle condition and use EventProcessor to process events
        while (true) {
            Event<RiderGroup> event = eventHandler.getNextEvent();

            // Check if the clock is entering in a new interval -> if so, change
            // probabilities
            ClockHandler.getInstance().setClock(event.getEventTime());

            EventProcessor<RiderGroup> eventProcessor = new EventProcessor<>();
            List<Event<RiderGroup>> eventsToSchedule = eventProcessor.processEvent(event);

        }
    }

    /*
     * Initialize the simulation.
     * It sets the clock to 0 and schedules the first event.
     */
    private void init_simulation() {
        ClockHandler.getInstance().setClock(0);

        Entrance entranceCenter = (Entrance) centerManager.getCenterByName(Config.ENTRANCE);
        double arrivalInterval = entranceCenter.getArrivalInterval();
        Event<RiderGroup> entranceEvent = EventBuilder.buildEntranceArrivalEvent(entranceCenter, arrivalInterval);
        // Event scheduling
        eventHandler.scheduleNewEvent(entranceEvent);
    }
}
