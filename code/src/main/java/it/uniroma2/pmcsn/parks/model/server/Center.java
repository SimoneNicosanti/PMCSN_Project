
package it.uniroma2.pmcsn.parks.model.server;

import java.util.List;

import it.uniroma2.pmcsn.parks.engineering.RandomStream;
import it.uniroma2.pmcsn.parks.engineering.interfaces.QueueManager;
import it.uniroma2.pmcsn.parks.engineering.singleton.ClockHandler;

public abstract class Center<T> extends RandomStream {
    protected final String name;

    protected QueueManager<T> queueManager;

    protected List<T> currentServingJobs;
    protected final int slotNumber;

    public Center(String name, QueueManager<T> queueManager, int slotNumber) {
        this.name = name;
        this.queueManager = queueManager;
        this.currentServingJobs = null;
        this.slotNumber = slotNumber;

    }

    public void arrival(T job) {
        double currentTime = ClockHandler.getInstance().getClock();
        queueManager.addToQueues(job, currentTime);

    }

    protected void serveJobs() {
        if (currentServingJobs != null) {
            throw new RuntimeException("Cannot start service, there are ongoing jobs to serve");
        }
        double currentTime = ClockHandler.getInstance().getClock();
        List<T> servingJobs = queueManager.extractFromQueues(slotNumber, currentTime);
        //TODO Where deleting jobs from currentServingJobs? Here or in endService?
        this.currentServingJobs.addAll(servingJobs);
    }

    protected void ensureJobsAreServing() {
        if (currentServingJobs == null) {
            throw new RuntimeException("Cannot end service because there are no jobs to serve");
        }
    }

    public abstract double startService();

    public abstract List<T> endService();

    public boolean isEmpty() {
        return currentServingJobs != null && queueManager.areQueuesEmpty();
    }

    public String getName() {
        return name;
    }
}