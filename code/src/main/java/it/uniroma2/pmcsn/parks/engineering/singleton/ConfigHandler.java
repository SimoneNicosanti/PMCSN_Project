package it.uniroma2.pmcsn.parks.engineering.singleton;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.tuple.Pair;

import it.uniroma2.pmcsn.parks.engineering.Constants;
import it.uniroma2.pmcsn.parks.engineering.Parameters;
import it.uniroma2.pmcsn.parks.engineering.factory.ParametersParser;
import it.uniroma2.pmcsn.parks.model.Distribution;
import it.uniroma2.pmcsn.parks.model.Interval;
import it.uniroma2.pmcsn.parks.model.RoutingNodeType;

public class ConfigHandler {

    private static ConfigHandler instance = null;

    private Double openingTime;
    private List<Interval> intervals;
    private Map<Interval, Parameters> parametersMap;

    private Parameters currentParameters;

    private Map<String, Distribution> centersDistribution;

    private ConfigHandler() {
        this.openingTime = null;
        this.intervals = new ArrayList<>();
        this.centersDistribution = new HashMap<>();
        this.parametersMap = new HashMap<>();

        String configFilePath;
        if (Constants.VERIFICATION_MODE) {
            configFilePath = Constants.VERIFICATION_CONFIG_FILENAME;
        } else {
            configFilePath = Constants.CONFIG_FILENAME;
        }
        List<Pair<Interval, Parameters>> pairList = ParametersParser.parseParameters(configFilePath);

        centersDistribution = ParametersParser.parseCentersDistribution(Constants.CONFIG_FILENAME);

        checkOrder(pairList);

        Interval previousInterval = null;
        for (Pair<Interval, Parameters> pair : pairList) {
            if (pair.getLeft().getStart() >= pair.getLeft().getEnd())
                throw new RuntimeException("The input is not an interval");
            if (previousInterval != null && previousInterval.getEnd() != pair.getLeft().getStart())
                throw new RuntimeException("Intervals are not a partition for the whole time");
            this.intervals.add(pair.getLeft());
            this.parametersMap.put(pair.getLeft(), pair.getRight());
            previousInterval = pair.getLeft();
        }

        this.currentParameters = parametersMap.get(intervals.get(0));
    }

    private void checkOrder(List<Pair<Interval, Parameters>> pairList) {
    }

    public static ConfigHandler getInstance() {
        if (instance == null) {
            instance = new ConfigHandler();
        }
        return instance;
    }

    public Double getCurrentArrivalRate() {
        return currentParameters.getArrivalRate();
    }

    // Return the system opening time in minutes from midnight
    public double getOpeningTimeInMinutes() {
        return this.openingTime;
    }

    public Distribution getDistribution(String centerName) {
        return centersDistribution.get(centerName);
    }

    public void changeParameters(Interval interval) {
        this.currentParameters = parametersMap.get(interval);
    }

    // Return the system closing time in minutes from midnight
    public double getClosingTimeInMinutes() {
        return this.intervals.get(this.intervals.size() - 1).getEnd() + this.openingTime;
    }

    public double getProbability(RoutingNodeType type) {
        return currentParameters.getRoutingProbability(type);
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
        Double currentClock = ClockHandler.getInstance().getClock();
        return getInterval(currentClock);
    }

    public boolean isParkClosing(Interval interval) {
        int size = intervals.size();
        Interval lastInterval = intervals.get(size - 1);

        return interval.equals(lastInterval);
    }

    public List<Interval> getAllIntervals() {
        return this.intervals;
    }

}
