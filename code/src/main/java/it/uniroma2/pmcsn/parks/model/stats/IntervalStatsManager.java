package it.uniroma2.pmcsn.parks.model.stats;

import it.uniroma2.pmcsn.parks.engineering.singleton.ConfigHandler;
import it.uniroma2.pmcsn.parks.model.Interval;
import it.uniroma2.pmcsn.parks.model.queue.QueuePriority;

import java.util.Map;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class IntervalStatsManager {

    private Map<Interval, CenterStatistics> intervalStatsMap;

    public IntervalStatsManager() {
        this.intervalStatsMap = new HashMap<>();
        for (Interval interval : ConfigHandler.getInstance().getAllIntervals()) {
            intervalStatsMap.put(interval, new CenterStatistics());
        }
    }

    public void updateServiceTime(Double startService, Double endService, Integer jobSize, Integer multiplier) {
        List<Interval> intervalList = findCoveredIntervals(startService, endService);
        for (Interval interval : intervalList) {
            Double coveredTime = findTimeIntersectionWithInterval(startService, endService, interval);
            if (coveredTime <= 0) {
                // As the list of interval is sorted we can break the for
                break;
            }
            CenterStatistics intervalStatistics = intervalStatsMap.get(interval);
            intervalStatistics.updateServiceArea(coveredTime, jobSize, multiplier);
        }
    }

    public void updateQueueTime(Double startQueue, Double endQueue, QueuePriority prio, Integer jobSize) {
        List<Interval> intervalList = findCoveredIntervals(startQueue, endQueue);
        for (Interval interval : intervalList) {
            Double coveredTime = findTimeIntersectionWithInterval(startQueue, endQueue, interval);
            if (coveredTime <= 0) {
                // As the list of interval is sorted we can break the for
                break;
            }
            CenterStatistics intervalStatistics = intervalStatsMap.get(interval);
            intervalStatistics.updateQueueArea(coveredTime, prio, jobSize);
        }
    }

    private Double findTimeIntersectionWithInterval(Double start, Double end, Interval interval) {

        Double newStart = Math.max(start, interval.getStart());
        Double newEnd = Math.min(end, interval.getEnd());

        return newEnd - newStart;
    }

    // Return a sorted list of intersected intervals
    private List<Interval> findCoveredIntervals(Double startTime, Double endTime) {
        List<Interval> coveredIntervals = new ArrayList<>();

        for (Interval interval : intervalStatsMap.keySet()) {
            if (findTimeIntersectionWithInterval(startTime, endTime, interval) > 0) {
                coveredIntervals.add(interval);
            }
        }

        coveredIntervals.sort((arg0, arg1) -> arg0.getIndex() - arg1.getIndex());

        return coveredIntervals;
    }

    public Map<Interval, CenterStatistics> getAllIntervalStats() {
        return this.intervalStatsMap;
    }

}
