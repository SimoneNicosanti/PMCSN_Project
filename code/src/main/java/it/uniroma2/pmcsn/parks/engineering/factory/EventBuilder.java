package it.uniroma2.pmcsn.parks.engineering.factory;

import java.util.HashMap;
import java.util.Map;

import it.uniroma2.pmcsn.parks.SimulationMode;
import it.uniroma2.pmcsn.parks.engineering.Constants;
import it.uniroma2.pmcsn.parks.engineering.interfaces.Center;
import it.uniroma2.pmcsn.parks.engineering.singleton.ClockHandler;
import it.uniroma2.pmcsn.parks.engineering.singleton.ConfigHandler;
import it.uniroma2.pmcsn.parks.engineering.singleton.RandomHandler;
import it.uniroma2.pmcsn.parks.model.event.EventType;
import it.uniroma2.pmcsn.parks.model.event.EventsPoolId;
import it.uniroma2.pmcsn.parks.model.event.SystemEvent;
import it.uniroma2.pmcsn.parks.model.job.GroupPriority;
import it.uniroma2.pmcsn.parks.model.job.RiderGroup;
import it.uniroma2.pmcsn.parks.model.server.concrete_servers.StatsCenter;

public class EventBuilder {

    private static Long riderGroupId = 0L;
    private static Map<String, StatsCenter> statsCenterMap;

    public static void setStatsCenterMap(Map<String, Center<RiderGroup>> map) {
        statsCenterMap = new HashMap<>();
        for (String centerName : map.keySet()) {
            statsCenterMap.put(centerName, (StatsCenter) map.get(centerName));
        }
    }

    public static SystemEvent<RiderGroup> getNewArrivalEvent(Center<RiderGroup> arrivalCenter) {
        Double arrivalRate = ConfigHandler.getInstance().getCurrentArrivalRate();
        // If arrivalRate == 0, stop arrivals
        if (arrivalRate == 0.0) {
            return null;
        }
        // TODO Manage distributions
        double interarrivalTime = RandomHandler.getInstance().getExponential(Constants.ARRIVAL_STREAM,
                1 / arrivalRate);

        int groupSize = SimulationBuilder.getJobSize();
        GroupPriority priority = computeGroupPriority();
        RiderGroup riderGroup = new RiderGroup(riderGroupId, groupSize, priority,
                ClockHandler.getInstance().getClock() + interarrivalTime);

        Center<RiderGroup> statsCenter = statsCenterMap.get(arrivalCenter.getName());

        if (statsCenter == null) {
            statsCenter = arrivalCenter;
        }

        SystemEvent<RiderGroup> arrivalEvent = buildEventFrom(statsCenter, EventType.ARRIVAL,
                riderGroup, ClockHandler.getInstance().getClock() + interarrivalTime);

        riderGroupId++;

        return arrivalEvent;

    }

    private static GroupPriority computeGroupPriority() {
        if (Constants.MODE == SimulationMode.VERIFICATION)
            return GroupPriority.NORMAL;

        double groupPriorityProb = RandomHandler.getInstance().getRandom(Constants.PRIORITY_STREAM);
        if (groupPriorityProb < Constants.PRIORITY_PASS_PROB) {
            return GroupPriority.PRIORITY;
        } else {
            return GroupPriority.NORMAL;
        }
    }

    // Builds a new generic event
    public static SystemEvent<RiderGroup> buildEventFrom(Center<RiderGroup> center, EventType eventType,
            RiderGroup job, double eventTime) {
        EventsPoolId poolId = new EventsPoolId(center.getName(), eventType);
        Center<RiderGroup> statsCenter = statsCenterMap.get(center.getName());

        if (statsCenter == null) {
            statsCenter = center;
        }
        return new SystemEvent<>(poolId, statsCenter, eventTime, job);
    }

}
