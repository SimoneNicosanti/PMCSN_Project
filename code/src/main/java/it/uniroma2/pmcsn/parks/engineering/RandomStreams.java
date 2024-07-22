package it.uniroma2.pmcsn.parks.engineering;

import java.util.HashMap;
import java.util.Map;

import it.uniroma2.pmcsn.parks.engineering.singleton.RandomHandler;

public class RandomStreams {
    // ** Named streams */
    private Map<String, Integer> streams;

    public RandomStreams() {
        streams = new HashMap<>();
    }

    protected void addStream(String name) {
        int streamIndex = RandomHandler.getInstance().getNewStreamIndex();
        streams.put(name, streamIndex);
    }

    protected int getStream(String name) {
        if (!streams.containsKey(name)) {
            throw new RuntimeException("Stream " + name + " does not exist");
        }
        return streams.get(name);
    }

}
