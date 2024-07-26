package it.uniroma2.pmcsn.parks.engineering.singleton;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.tuple.Pair;

import it.uniroma2.pmcsn.parks.model.Interval;

public class ClockHandler {

    private double clock;

    private static ClockHandler instance = null;
    private List<Interval> intervals;

    private ClockHandler() {
        this.clock = 0.0;
        this.intervals = new ArrayList<>();
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

}
