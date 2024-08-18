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

    public static Map<GroupPriority, FunIndexInfo> computeAvgsFunIndex(List<RiderGroup> exitGroups) {
        Map<GroupPriority, List<FunIndexInfo>> valuesMap = new HashMap<>();
        for (RiderGroup group : exitGroups) {
            valuesMap.putIfAbsent(group.getPriority(), new ArrayList<>());
            if (group.getGroupStats().getTotalNumberOfRides() > 0) {
                valuesMap.get(group.getPriority()).add(computeFunIndexInfo(group));
            }
        }

        Map<GroupPriority, FunIndexInfo> returnMap = new HashMap<>();
        for (GroupPriority prio : valuesMap.keySet()) {
            List<FunIndexInfo> values = valuesMap.get(prio);
            FunIndexInfo summed = values.stream().reduce(
                    new FunIndexInfo(0, 0, 0, 0.0),
                    (arg0, arg1) -> FunIndexInfo.sum(arg0, arg1));

            FunIndexInfo mean = FunIndexInfo.divideValuesBy(summed, values.size());
            returnMap.put(prio, mean);
        }

        return returnMap;
    }

    private static FunIndexInfo computeFunIndexInfo(RiderGroup group) {
        GroupStats stats = group.getGroupStats();
        Double funIndex = (stats.getServiceTime() * stats.getTotalNumberOfRides()) / (stats.getQueueTime() + 1);

        return new FunIndexInfo(stats.getTotalNumberOfRides(), stats.getServiceTime(), stats.getQueueTime(), funIndex);
    }
}
