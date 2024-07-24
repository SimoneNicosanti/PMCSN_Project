package it.uniroma2.pmcsn.parks.engineering.interfaces;

public interface RoutingNode<T> {

    public CenterInterface<T> route(T job);

    public String getName();

}