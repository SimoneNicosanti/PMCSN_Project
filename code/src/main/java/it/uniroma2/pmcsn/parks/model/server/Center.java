package it.uniroma2.pmcsn.parks.model.server;

import java.util.List;

import it.uniroma2.pmcsn.parks.engineering.interfaces.QueueManager;
import it.uniroma2.pmcsn.parks.model.job.ServingGroup;

public abstract class Center<T> {
    protected final String name;

    protected QueueManager<T> queueManager;
    protected final Integer slotNumber;

    public Center(String name, QueueManager<T> queueManager, Integer slotNumber) {
        this.name = name;
        this.queueManager = queueManager;
        this.slotNumber = slotNumber;
        // Assign a new named stream to the center
        // RandomHandler.getInstance().assignNewStream(name);
    }

    /*
     * Add a job to the center queue.
     * 
     * @param job : job to enqueue with this call
     */
    public void arrival(T job) {
        queueManager.addToQueues(job);
    }

    public abstract boolean isCenterEmpty();

    public String getName() {
        return name;
    }

    /*
     * @return List<T> : List of jobs starting service with this call (may be one or
     * more)
     * 
     * @return Double : time for this service
     */
    public abstract List<ServingGroup<T>> startService();

    /*
     * @param endedJobs : job ending service with this call (may be one or more)
     */
    public abstract void endService(List<T> endedJobs);

}