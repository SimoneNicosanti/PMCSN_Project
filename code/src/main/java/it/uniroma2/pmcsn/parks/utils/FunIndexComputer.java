package it.uniroma2.pmcsn.parks.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import it.uniroma2.pmcsn.parks.model.job.GroupPriority;
import it.uniroma2.pmcsn.parks.model.job.RiderGroup;
import it.uniroma2.pmcsn.parks.model.stats.GroupStats;

public class FunIndexComputer {

    public static Map<GroupPriority, Double> computeAvgsFunIndex(List<RiderGroup> exitGroups) {
        Map<GroupPriority, List<Double>> valuesMap = new HashMap<>();
        for (RiderGroup group : exitGroups) {
            valuesMap.putIfAbsent(group.getPriority(), new ArrayList<>());
            valuesMap.get(group.getPriority()).add(computeFunIndex(group));
        }

        Map<GroupPriority, Double> returnMap = new HashMap<>();
        for (GroupPriority prio : valuesMap.keySet()) {
            List<Double> values = valuesMap.get(prio);
            Double mean = values.stream().reduce(0.0, (elem1, elem2) -> elem1 + elem2);
            mean = mean / values.size();

            returnMap.put(prio, mean);
        }

        return returnMap;
    }

    private static Double computeFunIndex(RiderGroup group) {
        GroupStats stats = group.getGroupStats();
        return (stats.getServiceTime() * stats.getTotalNumberOfVisits()) / (stats.getQueueTime() + 1);
    }
}
