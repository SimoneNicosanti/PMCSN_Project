package it.uniroma2.pmcsn.parks.engineering;

import java.util.Map;

import it.uniroma2.pmcsn.parks.model.RoutingNodeType;

public class Parameters {

    private Map<RoutingNodeType, Double> routingProbabilities;
    private Double arrivalRate;

    public Parameters(Map<RoutingNodeType, Double> routingProbabilities, Double arrivalRate) {
        this.routingProbabilities = routingProbabilities;
        this.arrivalRate = arrivalRate;
    }

    public void setRoutingProbability(RoutingNodeType nodeType, Double probability) {
        this.routingProbabilities.put(nodeType, probability);
    }

    public Double getRoutingProbability(RoutingNodeType nodeType) {
        return this.routingProbabilities.get(nodeType);
    }

    public Double getArrivalRate() {
        return arrivalRate;
    }

}