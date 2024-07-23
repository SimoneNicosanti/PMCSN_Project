
package it.uniroma2.pmcsn.parks.model.server;

import java.util.ArrayList;
import java.util.List;

import it.uniroma2.pmcsn.parks.engineering.interfaces.QueueManager;
import it.uniroma2.pmcsn.parks.engineering.singleton.RandomHandler;

public abstract class Center<T> {
    protected final String name;

    protected QueueManager<T> queueManager;

    protected List<T> currentServingJobs;
    protected final int slotNumber;

    public Center(String name, QueueManager<T> queueManager, int slotNumber) {
        this.name = name;
        this.queueManager = queueManager;
        this.currentServingJobs = new ArrayList<>();
        this.slotNumber = slotNumber;
        // Assing a new named stream to the center
        RandomHandler.getInstance().assignNewStream(name);

    }

    public void arrival(T job) {
        queueManager.addToQueues(job);
    }

    protected void serveJobs() {
        if (!this.isServerEmpty()) {
            throw new RuntimeException("Cannot start service, there are ongoing jobs to serve");
        }
        List<T> servingJobs = queueManager.extractFromQueues(slotNumber);
        this.currentServingJobs.addAll(servingJobs);

    }

    protected boolean isServerEmpty() {
        return currentServingJobs.isEmpty();
    }

    public boolean isEmpty() {
        return this.isServerEmpty() && queueManager.areQueuesEmpty();
    }

    public QueueManager<T> getQueueManager() {
        return queueManager;
    }

    public String getName() {
        return name;
    }

    public abstract double startService();

    public abstract List<T> endService();

}