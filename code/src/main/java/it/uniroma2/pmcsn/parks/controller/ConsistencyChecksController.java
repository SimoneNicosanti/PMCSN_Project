package it.uniroma2.pmcsn.parks.controller;

import java.util.ArrayList;
import java.util.List;

import it.uniroma2.pmcsn.parks.SimulationMode;
import it.uniroma2.pmcsn.parks.engineering.Constants;
import it.uniroma2.pmcsn.parks.engineering.interfaces.Center;
import it.uniroma2.pmcsn.parks.engineering.interfaces.Controller;
import it.uniroma2.pmcsn.parks.engineering.singleton.RandomHandler;
import it.uniroma2.pmcsn.parks.model.job.RiderGroup;
import it.uniroma2.pmcsn.parks.model.server.concrete_servers.StatsCenter;
import it.uniroma2.pmcsn.parks.model.stats.BatchStats;
import it.uniroma2.pmcsn.parks.utils.ConfidenceIntervalComputer;
import it.uniroma2.pmcsn.parks.utils.ConfidenceIntervalComputer.ConfidenceInterval;
import it.uniroma2.pmcsn.parks.writers.WriterHelper;
import it.uniroma2.pmcsn.parks.verification.ValidationWriter;

public class ConsistencyChecksController implements Controller<RiderGroup> {

    public static void main(String[] args) {
        WriterHelper.createAllFolders();
        long start = System.currentTimeMillis();
        new ConsistencyChecksController().simulate();
        Long time = System.currentTimeMillis() - start;
        System.out.println("Run Time >>> " + time);
    }

    public ConsistencyChecksController() {
        Constants.MODE = SimulationMode.CONSISTENCY_CHECK;
        Constants.VERIFICATION_BATCH_NUMBER = 50;
        Constants.VERIFICATION_BATCH_SIZE = 1024;
    }

    @Override
    public void simulate() {

        ValidationWriter.resetConsistencyCheckDirectory();

        // Base Arrival Rate
        Constants.CONSISTENCY_CHECKS_CONFIG_FILENAME = Constants.PRE_CONSISTENCY_CHECKS_CONFIG_FILENAME;
        consistencySimulation(0);

        // Between the two simulations we have to use the same random streams -> Must be
        // the same configuaration, but with an higher arrival rate.
        RandomHandler.reset();

        // Increase the arrival rate
        Constants.CONSISTENCY_CHECKS_CONFIG_FILENAME = Constants.POST_CONSISTENCY_CHECKS_CONFIG_FILENAME;
        consistencySimulation(1);
        printUsedStrams();

    }

    private void printUsedStrams() {
        RandomHandler.getInstance().getStreamMap()
                .forEach(
                        (name, streamIdx) -> System.out.println("Name >>> " + name + " - Stream Idx >>> " + streamIdx));
        ;

    }

    public void consistencySimulation(int i) {

        List<Center<RiderGroup>> centerList = new Simulation(SimulationMode.CONSISTENCY_CHECK).batchSimulation()
                .getAllCenters();

        ConfidenceIntervalComputer computer = new ConfidenceIntervalComputer();
        computer.updateAllStatistics(centerList);

        List<ConfidenceInterval> confidenceIntervals = new ArrayList<>();
        for (Center<RiderGroup> center : centerList) {
            BatchStats queueBatchStats = ((StatsCenter) center).getQueueBatchStats();
            ConfidenceInterval confidenceInterval = ConfidenceIntervalComputer
                    .computeConfidenceInterval(queueBatchStats.getNumberAvgs(), center.getName(), "E[Tq]");
            confidenceIntervals.add(confidenceInterval);
        }
        ValidationWriter.writeConfidenceIntervals(confidenceIntervals, "ConfidenceIntervals_" + i);

    }
}