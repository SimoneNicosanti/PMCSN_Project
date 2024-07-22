package it.uniroma2.pmcsn.parks.engineering.factory;

import it.uniroma2.pmcsn.parks.engineering.singleton.ClockHandler;
import it.uniroma2.pmcsn.parks.model.event.Event;
import it.uniroma2.pmcsn.parks.model.event.EventType;
import it.uniroma2.pmcsn.parks.model.event.EventsPoolId;
import it.uniroma2.pmcsn.parks.model.job.GroupPriority;
import it.uniroma2.pmcsn.parks.model.job.RiderGroup;
import it.uniroma2.pmcsn.parks.model.server.Center;
import it.uniroma2.pmcsn.parks.model.server.Entrance;

public class EventBuilder {

    // Builds a new generic event
    public static <T> Event<T> buildEventFrom(Center<T> center, EventType eventType, T job, double eventTime) {
        EventsPoolId poolId = new EventsPoolId(center.getName(), eventType);
        return new Event<>(poolId, center, eventTime, job);
    }

    // ** Builds an arrival event associated to the Entrance Center */
    public static Event<RiderGroup> buildEntranceArrivalEvent(Entrance entranceCenter, double arrivalInterval) {
        // Build the event
        EventsPoolId poolId = new EventsPoolId(entranceCenter.getName(), EventType.ARRIVAL);
        double currentTime = ClockHandler.getInstance().getClock();
        RiderGroup job = buildRiderGroup(currentTime);

        return new Event<>(poolId, entranceCenter, currentTime + arrivalInterval, job);
    }

    private static RiderGroup buildRiderGroup(double currentTime) {
        int groupSize = 0; // TODO find a distribution for group sizes
        GroupPriority priority = GroupPriority.NORMAL; // TODO find a distribution for group priorities

        return new RiderGroup(groupSize, priority, currentTime);
    }

}
