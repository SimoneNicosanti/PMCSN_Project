package it.uniroma2.pmcsn.parks.controller;

import it.uniroma2.pmcsn.parks.engineering.CenterManager;
import it.uniroma2.pmcsn.parks.engineering.Config;
import it.uniroma2.pmcsn.parks.engineering.factory.EventBuilder;
import it.uniroma2.pmcsn.parks.engineering.singleton.ClockHandler;
import it.uniroma2.pmcsn.parks.engineering.singleton.RandomHandler;
import it.uniroma2.pmcsn.parks.model.event.Event;
import it.uniroma2.pmcsn.parks.model.job.RiderGroup;
import it.uniroma2.pmcsn.parks.model.server.Center;

public class Controller {

    private EventHandler<RiderGroup> eventHandler;
    private CenterManager<RiderGroup> centerManager;

    public Controller() {
        this.eventHandler = new EventHandler<>();
        this.centerManager = new CenterManager<>();
    }

    public void startSimulation() {
        // Initialize system clock and Event Builder
        ClockHandler.getInstance().setClock(0);
        EventBuilder eventBuilder = new EventBuilder(centerManager);

        // TODO find a correct distribution (plus create a class for handling system
        // arrivals -> stream 0 for not but will be handled by arrival system class)
        // Add the distribution time to the event, based on the arrival time
        double arrivalTime = RandomHandler.getInstance().getExponential(0, 1);
        Event<RiderGroup> entranceEvent = eventBuilder.buildEntranceNewArrivalEvent(arrivalTime);
        // Schedule event
        eventHandler.scheduleNewEvent(entranceEvent);

        // TODO set end cycle condition
        while (true) {
            Event<RiderGroup> event = eventHandler.getNextEvent();

            // Check if the clock is entering in a new interval -> if so, change probabilities
            ClockHandler.getInstance().setClock(event.getEventTime());

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
}
