package it.uniroma2.pmcsn.parks.engineering.factory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.tuple.Pair;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import it.uniroma2.pmcsn.parks.engineering.Constants;
import it.uniroma2.pmcsn.parks.engineering.Parameters;
import it.uniroma2.pmcsn.parks.model.Distribution;
import it.uniroma2.pmcsn.parks.model.Interval;
import it.uniroma2.pmcsn.parks.model.RoutingNodeType;

public class ParametersParser {

    public static List<Pair<Interval, Parameters>> parseParameters(String fileName) {
        // Read the config.json file
        JsonNode rootNode;
        List<Pair<Interval, Parameters>> returnList = new ArrayList<>();

        try {
            rootNode = getRootNode(fileName);
            JsonNode jsonIntervals = rootNode.path("timeIntervals");
            Double lastEnd = 0.0;
            for (JsonNode jsonInterval : jsonIntervals) {
                Double startClock = jsonInterval.path("start").asDouble();
                Double endClock = jsonInterval.path("end").asDouble();
                Double arrivalRate = jsonInterval.path("arrivalRate").asDouble();

                JsonNode routingProbs = jsonInterval.path("routingProbability");
                Map<RoutingNodeType, Double> probabilityMap = parseRoutingProbs(routingProbs);

                Interval interval = new Interval(startClock, endClock);
                Parameters intervalParams = new Parameters(probabilityMap, arrivalRate);
                returnList.add(Pair.of(interval, intervalParams));

                lastEnd = endClock;
            }

            // Add the last interval created for exiting all the jobs from the
            // system
            Pair<Interval, Parameters> finalIntervalCouple = buildFinalInterval(lastEnd);
            returnList.add(finalIntervalCouple);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return returnList;
    }

    // Return a fake interval created for exiting all the jobs from the
    // system
    private static Pair<Interval, Parameters> buildFinalInterval(Double lastEnd) {
        Interval interval = new Interval(lastEnd, Double.MAX_VALUE);
        Map<RoutingNodeType, Double> probabilityMap = new HashMap<>();
        probabilityMap.put(RoutingNodeType.ATTRACTION, 0.0);
        probabilityMap.put(RoutingNodeType.RESTAURANT, 0.0);
        probabilityMap.put(RoutingNodeType.EXIT, 1.0);

        Parameters parameters = new Parameters(probabilityMap, 0.0);

        return Pair.of(interval, parameters);
    }

    private static JsonNode getRootNode(String fileName) throws JsonProcessingException, IOException {
        // Read the config.json file
        String configFileName = Path.of(Constants.CONFIG_PATH, fileName).toString();
        File configFile = new File(configFileName);
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readTree(configFile);
    }

    public static Map<String, Distribution> parseCentersDistribution(String configFileName) {
        String entranceKey = "entranceDistribution";
        String attractionKey = "attractionsDistribution";
        String restKey = "restaurantsDistribution";

        Map<String, Distribution> centersDistribution = new HashMap<>();

        try {
            // Get the root node based on the config file
            JsonNode rootNode = getRootNode(configFileName);

            JsonNode entranceDist = rootNode.path(entranceKey);
            JsonNode attractionDist = rootNode.path(attractionKey);
            JsonNode restDist = rootNode.path(restKey);
            // Add distribution for all center types
            centersDistribution.put(entranceKey, getDistribution(entranceDist.asText()));
            centersDistribution.put(attractionKey, getDistribution(attractionDist.asText()));
            centersDistribution.put(restKey, getDistribution(restDist.asText()));
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException();
        }

        return centersDistribution;
    }

    private static Distribution getDistribution(String distName) {
        if (distName.equals("Uniform")) {
            return Distribution.UNIFORM;
        }
        if (distName.equals("Exponential")) {
            return Distribution.EXPONENTIAL;
        }
        throw new RuntimeException("Unkown distribution: " + distName);
        // Add other distributions as needed
    }

    // Parse a time in the format HH:MM in a clock that starts from zero, based on
    // the first interval
    @SuppressWarnings("unused")
    private static Double parseTime(String timeStr) {
        String[] parts = timeStr.split(":");
        int hours = Integer.parseInt(parts[0]);
        int minutes = Integer.parseInt(parts[1]);
        Double totalMinutes = (double) hours * 60 + minutes;

        return (double) totalMinutes;
    }

    private static Map<RoutingNodeType, Double> parseRoutingProbs(JsonNode routingNode) {
        Double attractionsProb = Double.parseDouble(routingNode.path("attractions").asText());
        Double restaurantsProb = Double.parseDouble(routingNode.path("restaurants").asText());
        Double exitProbs = Double.parseDouble(routingNode.path("exit").asText());

        if (attractionsProb + restaurantsProb + exitProbs != 1.0) {
            throw new RuntimeException("Routing probabilities summed are different from 1");
        }

        return Map.of(
                RoutingNodeType.ATTRACTION, attractionsProb,
                RoutingNodeType.RESTAURANT, restaurantsProb,
                RoutingNodeType.EXIT, exitProbs);
    }

}
