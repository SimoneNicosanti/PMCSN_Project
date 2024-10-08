package it.uniroma2.pmcsn.parks.engineering.singleton;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import it.uniroma2.pmcsn.parks.engineering.Constants;
import it.uniroma2.pmcsn.parks.model.job.RiderGroup;
import it.uniroma2.pmcsn.parks.random.Rngs;
import it.uniroma2.pmcsn.parks.random.Rvgs;

public class RandomHandler {

    private static final long SEED = Constants.SEED;
    private static long MAX_STREAM_NUM = 256;

    private static RandomHandler instance = null;

    private Rngs streamGenerator;
    private Rvgs distributionGenerator;

    // ** Assigned named streams */
    private Map<String, Integer> assignedStreams;
    private int streamCount;

    private String randomLog;

    private RandomHandler() {
        this.streamGenerator = new Rngs();
        this.streamGenerator.plantSeeds(SEED);
        this.distributionGenerator = new Rvgs(streamGenerator);
        this.assignedStreams = new HashMap<>();

        this.streamCount = 0;
        this.randomLog = "";

        List.of(Constants.ARRIVAL_STREAM, Constants.PRIORITY_STREAM, Constants.GROUP_SIZE_STREAM)
                .forEach((key) -> assignedStreams.put(key, getNewStreamIndex()));
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

    private void assignNewStream(String name) {
        if (this.assignedStreams.containsKey(name)) {
            throw new RuntimeException("Stream name already assigned");
        }
        int streamIndex = getNewStreamIndex();
        this.assignedStreams.put(name, streamIndex);

        return;
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
        double randomNumber = streamGenerator.random();
        // randomLog += streamName + " " + randomNumber + "\n";
        return randomNumber;
    }

    public double getRandom(String streamName, RiderGroup job) {
        int stream = getStream(streamName);
        streamGenerator.selectStream(stream);
        double randomNumber = streamGenerator.random();
        // randomLog += streamName + " " + randomNumber + " " + job.getGroupId() + "\n";
        return randomNumber;
    }

    public double getRandom(String streamName, long jobId) {
        int stream = getStream(streamName);
        streamGenerator.selectStream(stream);
        double randomNumber = streamGenerator.random();
        // randomLog += streamName + " " + randomNumber + " " + jobId + "\n";
        return randomNumber;
    }

    public double getUniform(String streamName, double a, double b) {
        int stream = getStream(streamName);
        streamGenerator.selectStream(stream);
        double randomNumber = distributionGenerator.uniform(a, b);
        // randomLog += streamName + " " + randomNumber + "\n";
        return randomNumber;
    }

    public double getExponential(String streamName, double m) {
        int stream = getStream(streamName);
        streamGenerator.selectStream(stream);
        double randomNumber = distributionGenerator.exponential(m);
        // randomLog += streamName + " " + randomNumber + "\n";
        return randomNumber;
    }

    public double getPoisson(String streamName, double m) {
        int stream = getStream(streamName);
        streamGenerator.selectStream(stream);
        double randomNumber = distributionGenerator.poisson(m);
        // randomLog += streamName + " " + randomNumber + "\n";
        return randomNumber;
    }

    public double getErlang(String streamName, long k, double m) {
        int stream = getStream(streamName);
        streamGenerator.selectStream(stream);
        double randomNumber = distributionGenerator.erlang(k, m);
        // randomLog += streamName + " " + randomNumber + "\n";
        return randomNumber;
    }

    public Map<String, Integer> getStreamMap() {
        return this.assignedStreams;
    }

    public static void reset() {
        instance = null;
    }

    public String getRandomLog() {
        return randomLog;
    }

}
