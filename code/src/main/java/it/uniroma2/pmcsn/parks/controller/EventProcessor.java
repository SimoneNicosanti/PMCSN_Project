package it.uniroma2.pmcsn.parks.controller;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.tuple.Pair;

import it.uniroma2.pmcsn.parks.engineering.factory.EventBuilder;
import it.uniroma2.pmcsn.parks.engineering.singleton.ClockHandler;
import it.uniroma2.pmcsn.parks.engineering.singleton.RandomHandler;
import it.uniroma2.pmcsn.parks.model.event.Event;
import it.uniroma2.pmcsn.parks.model.event.EventType;
import it.uniroma2.pmcsn.parks.model.job.RiderGroup;
import it.uniroma2.pmcsn.parks.model.routing.AttractionRoutingNode;
import it.uniroma2.pmcsn.parks.model.server.Attraction;
import it.uniroma2.pmcsn.parks.model.server.Center;
import it.uniroma2.pmcsn.parks.model.server.Entrance;

public class EventProcessor<T> {

    private final String processorName = "EventProcessor";

    private double RESTAURANT_AREA_THR = 0.2;
    private double ATTRACTION_AREA_THR = 0.6;

    private AttractionRoutingNode attractionRoutingNode;

    public EventProcessor() {
        this.attractionRoutingNode = new AttractionRoutingNode(null);
        RandomHandler.getInstance().assignNewStream(processorName);
        // TODO PASS A VALID LIST TO THIS
    }

    public List<Event<T>> processEvent(Event<T> event) {
        Center<T> center = event.getEventCenter();
        List<T> jobList = event.getJobList();
        List<Event<T>> nextEvents = null;
        switch (event.getEventType()) {
            case ARRIVAL:
                center.arrival(jobList.get(0));
                nextEvents = generateNextEventsFromArrival(event);
                break;

            case START_PROCESS:
                Pair<List<T>, Double> couple = center.startService();
                double serviceTime = couple.getRight();
                List<T> startedJobs = couple.getLeft();
                nextEvents = generateNextEventsFromStart(event, startedJobs, serviceTime);
                break;

            case END_PROCESS:
                List<T> endedJobs = event.getJobList();
                center.endService(endedJobs);
                nextEvents = generateNextEventsFromEnd(event);
                break;
        }

        return nextEvents;
    }

    private List<Event<T>> generateNextEventsFromArrival(Event<T> event) {
        List<Event<T>> newEventList = new ArrayList<>();
        Center<T> center = event.getEventCenter();
        double currentTime = ClockHandler.getInstance().getClock();

        if (center instanceof Attraction) {
            // I still don't get why you check that it must be empty and not just serving
            if (center.isCenterEmpty()) {
                Event<T> newEvent = EventBuilder.buildEventFrom(center, EventType.START_PROCESS, event.getJobList(),
                        currentTime);
                newEventList.add(newEvent);
            }
        }

        if (center instanceof Entrance) {

        }
        // if (event.getEventCenter() instanceof Restaurant) {}

        return newEventList;
    }

    private List<Event<T>> generateNextEventsFromStart(Event<T> event, List<T> startedJobs, double serviceTime) {
        List<Event<T>> newEventList = new ArrayList<>();
        Center<T> center = event.getEventCenter();
        double currentTime = ClockHandler.getInstance().getClock();

        if (center instanceof Attraction) {
            Event<T> newEvent = EventBuilder.buildEventFrom(center, EventType.END_PROCESS, event.getJobList(),
                    currentTime + serviceTime);
            newEventList.add(newEvent);
        }
        // if (event.getEventCenter() instanceof Entrance) {}
        // if (event.getEventCenter() instanceof Restaurant) {}

        return newEventList;

    }

    private List<Event<T>> generateNextEventsFromEnd(Event<T> event) {
        // TODO To schedule next service event in attraction we have to check if the
        // system is not empty
        List<Event<RiderGroup>> newEventList = new ArrayList<>();
        if (event.getEventCenter() instanceof Attraction) {
            for (T completedJob : event.getJobList()) {
                double areaProbability = RandomHandler.getInstance().getRandom(routingRandomStreamIdx);

                if (areaProbability <= RESTAURANT_AREA_THR) {
                    // GO TO RESTAURANTS
                    // Schedule Arrival in restaurant
                } else if (areaProbability <= RESTAURANT_AREA_THR + ATTRACTION_AREA_THR) {
                    // GO TO OTHER ATTRACTIONS
                    Attraction nextAttraction = (Attraction) attractionRoutingNode.route((RiderGroup) completedJob);

                    Event<RiderGroup> newEvent = new Event<RiderGroup>(nextAttraction, EventType.ARRIVAL,
                            ClockHandler.getInstance().getClock(), (RiderGroup) completedJob);
                    newEventList.add(newEvent);

                } else {
                    // EXIT FROM SYSTEM
                    // Take stats
                    // Find a way to manage
                }

                /*
                 * 1. Extract random number --> Selecting random stream
                 * 2. Check random value number --> check where it goes according to thr
                 * 3. If goes to other attractions
                 * 4. Compute probability for each attraction
                 * 5. Generate arrival events for those attraction
                 */
            }

        }
        // if (event.getEventCenter() instanceof Entrance) {}
        // if (event.getEventCenter() instanceof Restaurant) {}

        return newEventList;
    }

    /*
     * if (center is attraction)
     * if arrival and queues empty --> schedule next service
     * if start --> schedule end with returned service time
     * if end --> schedule start with same current time (start and end are the same)
     * and schedule arrival to other
     * attractions or exit from system
     * 
     * if (center is entrance)
     * if arrival and queues empty --> schedule next service
     * if start --> schedule end with returned service time
     * if end --> schedule arrival to other centers
     * 
     * if (center is restaurant)
     * if arrival and queues empty --> schedule next service
     * if start --> schedule end
     * if end --> schedule arrival to other centers
     */

}
