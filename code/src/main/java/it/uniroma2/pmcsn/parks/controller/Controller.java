package it.uniroma2.pmcsn.parks.controller;

import it.uniroma2.pmcsn.parks.model.job.RiderGroup;

public class Controller {

    private EventHandler<RiderGroup> eventHandler;

    public Controller() {
        this.eventHandler = new EventHandler<>();
    }


    public void simulate() {

        /*
        * while {
        *      
        * 
        * }
        * 
        * 
        * 
        */

       eventHandler.scheduleNewArrival(, 0);

        // TODO set end cycle condition
        while(true) {
            
        }
    }
}
