package it.uniroma2.pmcsn.parks.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import it.uniroma2.pmcsn.parks.model.job.GroupPriority;
import it.uniroma2.pmcsn.parks.model.job.RiderGroup;
import it.uniroma2.pmcsn.parks.model.stats.GroupStats;

public class FunIndexComputer {

    public record FunIndexInfo(
            double avgNumberOfRides,
            double avgServiceTime,
            double avgQueueTime,
            Double avgFunIndex) {

        public static FunIndexInfo sum(FunIndexInfo arg0, FunIndexInfo arg1) {
            return new FunIndexInfo(arg0.avgNumberOfRides + arg1.avgNumberOfRides,
                    arg0.avgServiceTime + arg1.avgServiceTime, arg0.avgQueueTime + arg1.avgQueueTime,
                    arg0.avgFunIndex + arg1.avgFunIndex);
        }

        public static FunIndexInfo divideValuesBy(FunIndexInfo funIndexInfo, double divisor) {
            return new FunIndexInfo(
                    funIndexInfo.avgNumberOfRides / divisor,
                    funIndexInfo.avgServiceTime / divisor,
                    funIndexInfo.avgQueueTime / divisor,
                    funIndexInfo.avgFunIndex / divisor);
        }
    }

    public static Map<String, FunIndexInfo> computeAvgsFunIndex(List<RiderGroup> exitGroups) {
        Map<String, List<FunIndexInfo>> valuesMap = new HashMap<>();
        for (RiderGroup group : exitGroups) {

            valuesMap.putIfAbsent(findGroupPriorityName(group), new ArrayList<>());
            valuesMap.putIfAbsent("NORMAL + SMALL", new ArrayList<>());

            if (group.getGroupStats().getTotalNumberOfRides() > 0) {
                List<FunIndexInfo> list = computeFunIndexInfo(group);
                valuesMap.get(findGroupPriorityName(group)).addAll(list);

                if (group.getPriority() == GroupPriority.NORMAL) {
                    valuesMap.get("NORMAL + SMALL").addAll(list);
                }
            }
        }

        Map<String, FunIndexInfo> returnMap = new HashMap<>();
        for (String prio : valuesMap.keySet()) {
            List<FunIndexInfo> values = valuesMap.get(prio);
            FunIndexInfo summed = values.stream().reduce(
                    new FunIndexInfo(0, 0, 0, 0.0),
                    (arg0, arg1) -> FunIndexInfo.sum(arg0, arg1));

            FunIndexInfo mean = FunIndexInfo.divideValuesBy(summed, values.size());
            returnMap.put(prio, mean);
        }

        return returnMap;
    }

    private static List<FunIndexInfo> computeFunIndexInfo(RiderGroup group) {
        GroupStats stats = group.getGroupStats();
        Double funIndex = (stats.getServiceTime()) / (stats.getQueueTime() + 1);

        // return new FunIndexInfo(stats.getTotalNumberOfRides(),
        // stats.getServiceTime(), stats.getQueueTime(), funIndex);

        List<FunIndexInfo> list = new ArrayList<>();

        for (int i = 0; i < group.getGroupSize(); i++) {
            list.add(new FunIndexInfo(stats.getTotalNumberOfRides(), stats.getServiceTime(), stats.getQueueTime(),
                    funIndex));
        }

        return list;
    }

    private static String findGroupPriorityName(RiderGroup group) {
        if (group.isSmallGroup()) {
            return "SMALL";
        } else {
            return group.getPriority().name();
        }
    }
}
