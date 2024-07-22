package it.uniroma2.pmcsn.parks.controller;

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

            // TODO: Ask Andrea if this was the old logic and thus we need to delete it
            switch (event.getEventType()) {
                case ARRIVAL:
                    // Add new job to the Entrance center
                    Center<RiderGroup> entrance = centerManager.getCenterByName(Config.ENTRANCE);
                    entrance.arrival(event.getJob());
                    break;
                case START_PROCESS:
                    // Start service and schedule a new end_process event
                    break;
                case END_PROCESS:
                    // End the service and schedule a new start_process event if the queue is not
                    // empty
                    break;
            }

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
