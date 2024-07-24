package it.uniroma2.pmcsn.parks.controller;

import it.uniroma2.pmcsn.parks.engineering.singleton.ClockHandler;
import it.uniroma2.pmcsn.parks.engineering.singleton.EventsPool;

public abstract class Controller<T> {

    protected EventsPool<T> eventsPool;

    public Controller() {
        this.eventsPool = new EventsPool<>();
    }

    protected void init_clock() {
        ClockHandler.getInstance().setClock(0);
    }

    /*
     * Start the simulation
     */
    public abstract void startSimulation();

}
