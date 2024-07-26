package it.uniroma2.pmcsn.parks.engineering;

import java.util.HashMap;
import java.util.Map;

import it.uniroma2.pmcsn.parks.model.RoutinNodeType;

public class Parameters {

    private Map<RoutinNodeType, Double> routingProbabilities;

    public Parameters() {
        this.routingProbabilities = new HashMap<>();
    }

    public void setRoutingProbability(RoutinNodeType nodeType, Double probability) {
        this.routingProbabilities.put(nodeType, probability);
    }

    public Double getRoutingProbability(RoutinNodeType nodeType) {
        return this.routingProbabilities.get(nodeType);
    }

}