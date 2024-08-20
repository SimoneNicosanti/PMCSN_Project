package it.uniroma2.pmcsn.parks.controller.experiment;

import it.uniroma2.pmcsn.parks.engineering.interfaces.Controller;
import it.uniroma2.pmcsn.parks.model.job.RiderGroup;

public class QueueTimeController implements Controller<RiderGroup> {

    @Override
    public void simulate() {

        /*
            Stesso approccio di funIndexController
            Al variare di prio_perc_seats

            Map<Interval, Map<QueuePrio, List<Double>> 
            Problema Interval --> start-end

        */
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'simulate'");
    }

    
}