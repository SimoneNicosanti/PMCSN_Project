package it.uniroma2.pmcsn.parks.engineering.factory;

import it.uniroma2.pmcsn.parks.engineering.Config;
import it.uniroma2.pmcsn.parks.engineering.singleton.ClockHandler;
import it.uniroma2.pmcsn.parks.engineering.singleton.RandomHandler;
import it.uniroma2.pmcsn.parks.model.event.Event;
import it.uniroma2.pmcsn.parks.model.event.EventType;
import it.uniroma2.pmcsn.parks.model.event.EventsPoolId;
import it.uniroma2.pmcsn.parks.model.job.GroupPriority;
import it.uniroma2.pmcsn.parks.model.job.RiderGroup;
import it.uniroma2.pmcsn.parks.model.server.Center;

public class EventBuilder {

    public static Event<RiderGroup> getNewArrivalEvent(Center<RiderGroup> arrivalCenter) {
        // TODO Manage distributions
        double interarrivalTime = RandomHandler.getInstance().getUniform("ARRIVAL BUILDER - ARRIVAL DISTRIBUTION", 0,
                1);
        int groupSize = Double.valueOf(RandomHandler.getInstance().getUniform("ARRIVAL BUILDER - GROUP SIZE", 1, 10))
                .intValue();

        GroupPriority priority = computeGroupPriority();

        RiderGroup riderGroup = new RiderGroup(groupSize, priority,
                ClockHandler.getInstance().getClock() + interarrivalTime);

        Event<RiderGroup> arrivalEvent = buildEventFrom(arrivalCenter, EventType.ARRIVAL,
                riderGroup, ClockHandler.getInstance().getClock() + interarrivalTime);

        return arrivalEvent;

    }

    private static GroupPriority computeGroupPriority() {
        double groupPriorityProb = RandomHandler.getInstance().getRandom("ARRIVAL BUILDER - PRIORITY");
        if (groupPriorityProb < Config.PRIORITY_PASS_PROB) {
            return GroupPriority.PRIORITY;
        } else {
            return GroupPriority.NORMAL;
        }
    }

    // Builds a new generic event
    public static Event<RiderGroup> buildEventFrom(Center<RiderGroup> center, EventType eventType,
            RiderGroup job,
            double eventTime) {
        EventsPoolId poolId = new EventsPoolId(center.getName(), eventType);
        return new Event<>(poolId, center, eventTime, job);
    }

}
