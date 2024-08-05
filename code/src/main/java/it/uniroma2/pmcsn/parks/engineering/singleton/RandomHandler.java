package it.uniroma2.pmcsn.parks.engineering.singleton;

import java.util.HashMap;
import java.util.Map;

import it.uniroma2.pmcsn.parks.engineering.Constants;
import it.uniroma2.pmcsn.parks.random.Rngs;
import it.uniroma2.pmcsn.parks.random.Rvgs;

public class RandomHandler {

    private static long SEED = Constants.SEED;
    private static long MAX_STREAM_NUM = 256;

    private static RandomHandler instance = null;

    private Rngs streamGenerator;
    private Rvgs distributionGenerator;

    // ** Assigned named streams */
    private Map<String, Integer> assignedStreams;
    private int streamCount;

    private RandomHandler() {
        this.streamGenerator = new Rngs();
        this.streamGenerator.plantSeeds(SEED);
        this.distributionGenerator = new Rvgs(streamGenerator);
        this.assignedStreams = new HashMap<>();

        this.streamCount = 0;
    }

    public static RandomHandler getInstance() {
        if (instance == null) {
            instance = new RandomHandler();
        }
        return instance;
    }

    private int getNewStreamIndex() {
        int returnCounter = streamCount;
        this.streamCount++;
        if (this.streamCount >= MAX_STREAM_NUM) {
            throw new RuntimeException("ERROR >>> Stream number excedeed");
        }
        return returnCounter;
    }

    public int assignNewStream(String name) {
        if (this.assignedStreams.containsKey(name)) {
            throw new RuntimeException("Stream name already assigned");
        }
        int streamIndex = getNewStreamIndex();
        this.assignedStreams.put(name, streamIndex);

        return streamIndex;
    }

    public int getStream(String name) {
        if (!this.assignedStreams.containsKey(name)) {
            this.assignNewStream(name);
        }
        return this.assignedStreams.get(name);
    }

    public double getRandom(String streamName) {
        int stream = getStream(streamName);
        streamGenerator.selectStream(stream);
        return streamGenerator.random();
    }

    public double getUniform(String streamName, double a, double b) {
        int stream = getStream(streamName);
        streamGenerator.selectStream(stream);
        return distributionGenerator.uniform(a, b);
    }

    public double getExponential(String streamName, double m) {
        int stream = getStream(streamName);
        streamGenerator.selectStream(stream);
        return distributionGenerator.exponential(m);
    }

    public double getPoisson(String streamName, double m) {
        int stream = getStream(streamName);
        streamGenerator.selectStream(stream);
        return distributionGenerator.poisson(m);
    }

    public double getErlang(String streamName, long k, double m) {
        int stream = getStream(streamName);
        streamGenerator.selectStream(stream);
        return distributionGenerator.erlang(k, m);
    }

    public Map<String, Integer> getStreamMap() {
        return this.assignedStreams;
    }

}
