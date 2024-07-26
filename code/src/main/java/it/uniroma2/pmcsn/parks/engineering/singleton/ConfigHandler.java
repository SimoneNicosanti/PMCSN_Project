package it.uniroma2.pmcsn.parks.engineering.singleton;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import it.uniroma2.pmcsn.parks.engineering.Parameters;
import it.uniroma2.pmcsn.parks.model.Distribution;
import it.uniroma2.pmcsn.parks.model.Interval;
import it.uniroma2.pmcsn.parks.model.RoutinNodeType;

public class ConfigHandler {

    private static ConfigHandler instance = null;

    private String configFileName;

    private Double openingTime;
    private List<Interval> intervals;
    private Map<Interval, Parameters> parametersMap;

    /*
     * Map<Interval, Parameters>
     */

    private Map<String, Distribution> centersDistribution;

    private ConfigHandler() {
        this.configFileName = "config/Config_1.json";
        this.openingTime = null;
        this.intervals = new ArrayList<>();
        this.centersDistribution = new HashMap<>();

        this.parseIntervals();
        this.parseCentersDistribution();
    }

    public static ConfigHandler getInstance() {
        if (instance == null) {
            instance = new ConfigHandler();
        }
        return instance;
    }

    private void setParametersIntervals() {
        // Read the config.json file
        JsonNode rootNode = this.getRoodNode();

        JsonNode jsonIntervals = rootNode.path("timeIntervals");
        for (JsonNode jsonInterval : jsonIntervals) {
            String startStr = jsonInterval.path("start").asText();
            String endStr = jsonInterval.path("end").asText();

            JsonNode routingProbs = jsonInterval.path("routingProbability");
            Map<RoutinNodeType, Double> probabilityMap = parseRoutingProbs(routingProbs);

            Double start = this.parseClock(startStr);
            Double end = this.parseClock(endStr);

            Interval interval = new Interval(start, end);
            this.intervals.add(interval);

            Parameters intervalParams = new Parameters(probabilityMap);

            this.parametersMap.put(interval, intervalParams);

        }

        return intervals;
    }

    private JsonNode getRoodNode() throws IOException {
        // Read the config.json file
        File configFile = new File(this.configFileName);
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readTree(configFile);
    }

    private void parseCentersDistribution() {
        String entranceKey = "entranceDistribution";
        String attractionKey = "attractionsDistribution";
        String restKey = "restaurantsDistribution";

        // Get the root node based on the config file
        JsonNode rootNode = this.getRoodNode();

        JsonNode entranceDist = rootNode.path(entranceKey);
        JsonNode attractionDist = rootNode.path(attractionKey);
        JsonNode restDist = rootNode.path(restKey);
        // Add distribution for all center types
        centersDistribution.put(entranceKey, getDistribution(entranceDist.asText()));
        centersDistribution.put(attractionKey, getDistribution(attractionDist.asText()));
        centersDistribution.put(restKey, getDistribution(restDist.asText()));
    }

    private Distribution getDistribution(String distName) {
        if (distName == "Uniform") {
            return Distribution.UNIFORM;
        }
        if (distName == "Exponential") {
            return Distribution.EXPONENTIAL;
        }
        // Add other distributions as needed
    }

    // Parse a time in the format HH:MM in a clock that starts from zero, based on
    // the first interval
    private Double parseClock(String timeStr) {
        String[] parts = timeStr.split(":");
        int hours = Integer.parseInt(parts[0]);
        int minutes = Integer.parseInt(parts[1]);
        Double totalMinutes = (double) hours * 60 + minutes;
        if (this.openingTime == null) {
            this.openingTime = totalMinutes;
        }
        return (double) (totalMinutes - this.openingTime);
    }

    // Return the system opening time in minutes from midnight
    public double getOpeningTimeInMinutes() {
        return this.openingTime;
    }

    public void changeParameters(Interval interval) {
        // TODO
    }

    private Map<RoutinNodeType, Double> parseRoutingProbs(JsonNode routingNode) {
        Double attractionsProb = Double.parseDouble(routingNode.path("attractions").asText());
        Double restaurantsProb = Double.parseDouble(routingNode.path("restaurants").asText());
        Double exitProbs = Double.parseDouble(routingNode.path("exit").asText());

        return Map.of(
            RoutinNodeType.ATTRACTION, attractionsProb, 
            RoutinNodeType.RESTAURANT, restaurantsProb, 
            RoutinNodeType.EXIT, exitProbs);
    }

    // Return the system closing time in minutes from midnight
    public double getClosingTimeInMinutes() {
        return this.intervals.get(this.intervals.size() - 1).getEnd() + this.openingTime;
    }

}
