package it.uniroma2.pmcsn.parks.engineering.interfaces;

public interface CenterInterface<T> {

    public void arrival(T job);

    public String getName();

    public boolean isQueueEmptyAndCanServe(Integer jobSize);

    public void endService(T endedJob);

    public void setNextRoutingNode(RoutingNode<T> nextRoutingNode);

}