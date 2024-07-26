package it.uniroma2.pmcsn.parks.engineering.singleton;

import org.apache.commons.lang3.tuple.Pair;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import it.uniroma2.pmcsn.parks.engineering.Parameters;
import it.uniroma2.pmcsn.parks.engineering.factory.ParametersParser;
import it.uniroma2.pmcsn.parks.model.Distribution;
import it.uniroma2.pmcsn.parks.model.Interval;
import it.uniroma2.pmcsn.parks.model.RoutingNodeType;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ConfigHandler {

    private static ConfigHandler instance = null;

    private String configFileName;

    private Double openingTime;
    private List<Interval> intervals;
    private Map<Interval, Parameters> parametersMap;
    private Parameters currentParameters;

    private Map<String, Distribution> centersDistribution;

    private ConfigHandler() {
        this.configFileName = "config/Config_1.json";
        this.openingTime = null;
        this.intervals = new ArrayList<>();
        this.centersDistribution = new HashMap<>();
        this.parametersMap = new HashMap<>();

        List<Pair<Interval, Parameters>> pairList = ParametersParser.parseParameters(configFileName);

        for (Pair<Interval, Parameters> pair : pairList) {
            intervals.add(pair.getLeft());
            parametersMap.put(pair.getLeft(), pair.getRight());
        }

        this.currentParameters = parametersMap.get(intervals.get(0));
    }

    public static ConfigHandler getInstance() {
        if (instance == null) {
            instance = new ConfigHandler();
        }
        return instance;
    }

    // Return the system opening time in minutes from midnight
    public double getOpeningTimeInMinutes() {
        return this.openingTime;
    }

    public void changeParameters(Interval interval) {
        this.currentParameters = parametersMap.get(interval);
    }

    // Return the system closing time in minutes from midnight
    public double getClosingTimeInMinutes() {
        return this.intervals.get(this.intervals.size() - 1).getEnd() + this.openingTime;
    }

    public double getProbability(RoutingNodeType type, Interval interval) {
        return currentParameters.getRoutingProbability(type);
    }
}
