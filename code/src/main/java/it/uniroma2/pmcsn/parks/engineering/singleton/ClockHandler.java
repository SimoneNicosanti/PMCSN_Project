package it.uniroma2.pmcsn.parks.engineering.singleton;

import java.util.List;

import it.uniroma2.pmcsn.parks.model.Interval;

public class ClockHandler {

    private Double clock;

    private static ClockHandler instance = null;
    private List<Interval> intervals;

    private ClockHandler() {
        this.clock = 0.0;

        // Set to only one interval -> to use intervals use "setIntervals"
        this.intervals = List.of(new Interval(0.0, Double.MAX_VALUE));
    }

    public static ClockHandler getInstance() {
        if (instance == null) {
            instance = new ClockHandler();
        }
        return instance;
    }

    public double getClock() {
        return this.clock;
    }

    public void setClock(double newClockValue) {
        this.clock = newClockValue;
    }

    public void setIntervals(List<Interval> intervals) {
        this.intervals = intervals;
    }

    public Interval getInterval(Double time) {
        for (Interval interval : intervals) {
            if (interval.contains(time)) {
                return interval;
            }
        }

        // Interval not found
        throw new RuntimeException("Interval not found for time " + time);
    }

    public Interval getCurrentInterval() {
        Double time = getClock();
        return getInterval(time);
    }

}
