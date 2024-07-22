package it.uniroma2.pmcsn.parks.engineering.interfaces ;

import it.uniroma2.pmcsn.parks.model.server.Center;

public interface RoutingNode<T> {

    public Center<T> route(T job);

    public String getName();

}