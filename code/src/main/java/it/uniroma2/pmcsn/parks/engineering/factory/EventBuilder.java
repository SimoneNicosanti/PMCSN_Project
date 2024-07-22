package it.uniroma2.pmcsn.parks.engineering.factory;

import it.uniroma2.pmcsn.parks.engineering.Config;
import it.uniroma2.pmcsn.parks.engineering.RandomStream;
import it.uniroma2.pmcsn.parks.engineering.singleton.ClockHandler;
import it.uniroma2.pmcsn.parks.engineering.singleton.RandomHandler;
import it.uniroma2.pmcsn.parks.model.event.Event;
import it.uniroma2.pmcsn.parks.model.event.EventType;
import it.uniroma2.pmcsn.parks.model.event.EventsPoolId;
import it.uniroma2.pmcsn.parks.model.job.GroupPriority;
import it.uniroma2.pmcsn.parks.model.job.RiderGroup;
import it.uniroma2.pmcsn.parks.model.server.Center;

public class EventBuilder extends RandomStream {
    private final EventType eventType;
    private final Center<RiderGroup> center;

    public EventBuilder(EventType eventType, Center<RiderGroup> center) {
        this.eventType = eventType;
        this.center = center;
    }

    public static <T> Event<T> buildEventFrom(Event<T> event, EventType newEventType) {
        if (event.getEventType() == newEventType) {
            throw new RuntimeException("Cannot produce a " + newEventType + " event from itself");
        }
        Center<T> center = event.getEventCenter();
        EventsPoolId poolId = new EventsPoolId(center.getName(), newEventType);
        return new Event<>(poolId, center, ClockHandler.getInstance().getClock(), event.getJob());
    }

    // ** Builds an arrival associated to the Entrance Center */
    public Event<RiderGroup> buildEntranceArrivalEvent() {
        if (eventType != EventType.ARRIVAL || center.getName() != Config.ENTRANCE) {
            throw new RuntimeException("The builder does not match the requested event");
        }

        EventsPoolId poolId = new EventsPoolId(center.getName(), eventType);
        double currentTime = ClockHandler.getInstance().getClock();
        RiderGroup job = buildRiderGroup(currentTime);
        // TODO find a correct distribution for coming jobs
        double arrivalTime = RandomHandler.getInstance().getExponential(streamIndex, 1);

        return new Event<RiderGroup>(poolId, center, currentTime + arrivalTime, job);

    }

    private RiderGroup buildRiderGroup(double currentTime) {
        int groupSize = 0; // TODO find a distribution for group sizes
        GroupPriority priority = GroupPriority.NORMAL; // TODO find a distribution for group priorities

        return new RiderGroup(groupSize, priority, currentTime);
    }

}
