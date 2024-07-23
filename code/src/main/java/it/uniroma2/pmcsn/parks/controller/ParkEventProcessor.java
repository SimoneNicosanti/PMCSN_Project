package it.uniroma2.pmcsn.parks.controller;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.tuple.Pair;

import it.uniroma2.pmcsn.parks.engineering.CentersManager;
import it.uniroma2.pmcsn.parks.engineering.Config;
import it.uniroma2.pmcsn.parks.engineering.factory.EventBuilder;
import it.uniroma2.pmcsn.parks.engineering.interfaces.EventProcessor;
import it.uniroma2.pmcsn.parks.engineering.singleton.ClockHandler;
import it.uniroma2.pmcsn.parks.model.event.Event;
import it.uniroma2.pmcsn.parks.model.event.EventType;
import it.uniroma2.pmcsn.parks.model.job.RiderGroup;
import it.uniroma2.pmcsn.parks.model.routing.AttractionRoutingNode;
import it.uniroma2.pmcsn.parks.model.routing.NetworkRoutingNode;
import it.uniroma2.pmcsn.parks.model.routing.RestaurantRoutingNode;
import it.uniroma2.pmcsn.parks.model.server.Attraction;
import it.uniroma2.pmcsn.parks.model.server.Center;
import it.uniroma2.pmcsn.parks.model.server.Entrance;
import it.uniroma2.pmcsn.parks.model.server.Restaurant;

public class ParkEventProcessor implements EventProcessor<RiderGroup> {

    private CentersManager<RiderGroup> centersManager;
    private NetworkRoutingNode networkRoutingNode;

    public ParkEventProcessor() {
        List<Attraction> attractions = centersManager.getAttractions();
        List<Restaurant> restaurants = centersManager.getRestaurants();
        this.networkRoutingNode = new NetworkRoutingNode(new AttractionRoutingNode(attractions),
                new RestaurantRoutingNode(restaurants));

        this.centersManager = new CentersManager<>();
        // TODO PASS A VALID LIST TO THIS
    }

    @Override
    public List<Event<RiderGroup>> processEvent(Event<RiderGroup> event) {
        Center<RiderGroup> center = event.getEventCenter();
        List<RiderGroup> jobList = event.getJobList();
        List<Event<RiderGroup>> nextEvents = null;
        switch (event.getEventType()) {
            case ARRIVAL:
                center.arrival(jobList.get(0));
                nextEvents = generateNextEventsFromArrival(event);
                break;

            case START_PROCESS:
                Pair<List<RiderGroup>, Double> couple = center.startService();
                double serviceTime = couple.getRight();
                List<RiderGroup> startedJobs = couple.getLeft();
                nextEvents = generateNextEventsFromStart(event, startedJobs, serviceTime);
                break;

            case END_PROCESS:
                List<RiderGroup> endedJobs = event.getJobList();
                center.endService(endedJobs);
                nextEvents = generateNextEventsFromEnd(event);
                break;
        }

        return nextEvents;
    }

    @Override
    public List<Event<RiderGroup>> generateNextEventsFromArrival(Event<RiderGroup> event) {
        List<Event<RiderGroup>> newEventList = new ArrayList<>();
        Center<RiderGroup> center = event.getEventCenter();
        double currentTime = ClockHandler.getInstance().getClock();

        if (center.isCenterEmpty()) {
            // Starting service immediately
            Event<RiderGroup> newEvent = EventBuilder.buildEventFrom(center, EventType.START_PROCESS,
                    event.getJobList(), currentTime);
            newEventList.add(newEvent);
        }

        if (center instanceof Entrance) {
            // Has to schedule new arrival event to the system
            Event<RiderGroup> newArrivalEvent = generateArrivalEvent();
            newEventList.add(newArrivalEvent);
        }

        return newEventList;
    }

    @Override
    public List<Event<RiderGroup>> generateNextEventsFromStart(Event<RiderGroup> event, List<RiderGroup> startedJobs,
            double serviceTime) {
        List<Event<RiderGroup>> newEventList = new ArrayList<>();
        Center<RiderGroup> center = event.getEventCenter();
        double currentTime = ClockHandler.getInstance().getClock();

        Event<RiderGroup> newEvent = EventBuilder.buildEventFrom(center, EventType.END_PROCESS, event.getJobList(),
                currentTime + serviceTime);
        newEventList.add(newEvent);

        return newEventList;
    }

    @Override
    public List<Event<RiderGroup>> generateNextEventsFromEnd(Event<RiderGroup> event) {
        // TODO To schedule next service event in attraction we have to check if the
        // system is not empty
        List<Event<RiderGroup>> newEventList = new ArrayList<>();
        double currentTime = ClockHandler.getInstance().getClock();

        for (RiderGroup completedJob : event.getJobList()) {
            Center<RiderGroup> nextCenter = networkRoutingNode.route(completedJob);
            if (nextCenter == null) {
                // TODO Manage exit from system
                // Take stats and print on csv
                // Find a way to manage
            }

            newEventList.add(
                    EventBuilder.buildEventFrom(nextCenter, EventType.ARRIVAL, List.of(completedJob), currentTime));

        }

        return newEventList;
    }

    public Event<RiderGroup> generateArrivalEvent() {
        Center<RiderGroup> entranceCenter = centersManager.getCenterByName(Config.ENTRANCE);
        return EventBuilder.getNewArrivalEvent(entranceCenter);
    }

}
