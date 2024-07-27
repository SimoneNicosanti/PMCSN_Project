package it.uniroma2.pmcsn.parks.model;

public class Interval {
    private Double start;
    private Double end;

    public Interval(Double start, Double end) {
        this.start = start;
        this.end = end;
    }

    @Override
    public boolean equals(Object o) {
        Interval interval = (Interval) o;
        return interval.end == this.end && interval.start == this.start;
    }

    public boolean contains(Double time) {
        // start <= time < end
        return this.start <= time && time < this.end;
    }

    public double getStart() {
        return this.start;
    }

    public double getEnd() {
        return this.end;
    }

}