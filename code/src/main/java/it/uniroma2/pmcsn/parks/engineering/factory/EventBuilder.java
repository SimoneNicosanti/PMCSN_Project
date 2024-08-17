package it.uniroma2.pmcsn.parks.engineering.factory;

import it.uniroma2.pmcsn.parks.SimulationMode;
import it.uniroma2.pmcsn.parks.engineering.Constants;
import it.uniroma2.pmcsn.parks.engineering.interfaces.Center;
import it.uniroma2.pmcsn.parks.engineering.singleton.ClockHandler;
import it.uniroma2.pmcsn.parks.engineering.singleton.ConfigHandler;
import it.uniroma2.pmcsn.parks.engineering.singleton.RandomHandler;
import it.uniroma2.pmcsn.parks.model.event.EventType;
import it.uniroma2.pmcsn.parks.model.event.SystemEvent;
import it.uniroma2.pmcsn.parks.model.job.GroupPriority;
import it.uniroma2.pmcsn.parks.model.job.RiderGroup;

public class EventBuilder {

    private static Long riderGroupId = 0L;

    public static SystemEvent getNewArrivalEvent(Center<RiderGroup> arrivalCenter) {
        Double arrivalRate = ConfigHandler.getInstance().getCurrentArrivalRate();
        // If arrivalRate == 0, stop arrivals
        if (arrivalRate == 0.0) {
            return null;
        }

        double interarrivalTime = RandomHandler.getInstance().getExponential(Constants.ARRIVAL_STREAM,
                1 / arrivalRate);

        int groupSize = SimulationBuilder.getJobSize();
        GroupPriority priority = computeGroupPriority(riderGroupId);
        RiderGroup riderGroup = new RiderGroup(riderGroupId, groupSize, priority,
                ClockHandler.getInstance().getClock() + interarrivalTime);

        SystemEvent arrivalEvent = buildEventFrom(arrivalCenter, EventType.ARRIVAL,
                riderGroup, ClockHandler.getInstance().getClock() + interarrivalTime);

        riderGroupId++;

        return arrivalEvent;
    }

    private static GroupPriority computeGroupPriority(long jobId) {
        if (Constants.MODE == SimulationMode.VERIFICATION)
            return GroupPriority.NORMAL;

        double groupPriorityProb = RandomHandler.getInstance().getRandom(Constants.PRIORITY_STREAM, jobId);
        if (groupPriorityProb < Constants.PRIORITY_PASS_PROB) {
            return GroupPriority.PRIORITY;
        } else {
            return GroupPriority.NORMAL;
        }
    }

    // Builds a new generic event
    public static SystemEvent buildEventFrom(Center<RiderGroup> center, EventType eventType,
            RiderGroup job, double eventTime) {

        return new SystemEvent(eventType, center.getName(), eventTime, job);
    }

}
