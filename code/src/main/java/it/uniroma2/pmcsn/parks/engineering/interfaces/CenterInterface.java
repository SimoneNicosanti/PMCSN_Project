package it.uniroma2.pmcsn.parks.engineering.interfaces;

import java.util.List;

import it.uniroma2.pmcsn.parks.model.job.ServingGroup;

public interface CenterInterface<T> {

    /*
     * Add a job to the center queue.
     * 
     * @param job : job to enqueue with this call
     */
    public void arrival(T job);

    public String getName();

    public boolean isCenterEmpty();

    /*
     * @return List<T> : List of jobs starting service with this call (may be one or
     * more)
     * 
     * @return Double : time for this service
     */
    public List<ServingGroup<T>> startService();

    /*
     * @param endedJobs : job ending service with this call (may be one or more)
     */
    public void endService(List<T> endedJobs);

}