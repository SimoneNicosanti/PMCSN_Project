package it.uniroma2.pmcsn.parks.engineering.interfaces;

import java.util.List;

import it.uniroma2.pmcsn.parks.model.job.GroupPriority;
import it.uniroma2.pmcsn.parks.model.queue.QueuePriority;

public interface Center<T> {

    public QueuePriority arrival(T job);

    public String getName();

    public boolean isQueueEmptyAndCanServe(Integer jobSize);

    public void endService(T endedJob);

    public void setNextRoutingNode(RoutingNode<T> nextRoutingNode);

    public List<T> startService();

    public Integer getQueueLenght(GroupPriority prio);

    public Double getPopularity();

    /**
     * Check if the center is able to serve a job with size "jobSize"
     */
    public boolean canServe(Integer slots);

    public List<T> closeCenter();

}