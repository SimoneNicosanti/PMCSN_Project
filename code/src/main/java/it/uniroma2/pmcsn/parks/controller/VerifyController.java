package it.uniroma2.pmcsn.parks.controller;

import java.util.List;
import java.util.Map;

import it.uniroma2.pmcsn.parks.SimulationMode;
import it.uniroma2.pmcsn.parks.engineering.factory.NetworkBuilder;
import it.uniroma2.pmcsn.parks.engineering.interfaces.Center;
import it.uniroma2.pmcsn.parks.engineering.interfaces.Controller;
import it.uniroma2.pmcsn.parks.model.job.RiderGroup;
import it.uniroma2.pmcsn.parks.utils.ConfidenceIntervalComputer;
import it.uniroma2.pmcsn.parks.utils.TheoreticalValueComputer;
import it.uniroma2.pmcsn.parks.utils.ConfidenceIntervalComputer.ConfidenceInterval;
import it.uniroma2.pmcsn.parks.verification.VerificationWriter;
import it.uniroma2.pmcsn.parks.writers.WriterHelper;

public class VerifyController implements Controller<RiderGroup> {

    public static void main(String[] args) {
        WriterHelper.createAllFolders();
        long start = System.currentTimeMillis();
        new VerifyController().simulate();
        Long time = System.currentTimeMillis() - start;
        System.out.println("Run Time >>> " + time);
    }

    public VerifyController() {

    }

    @Override
    public void simulate() {

        VerificationWriter.resetData();

        NetworkBuilder networkBuilder = new Simulation(SimulationMode.VERIFICATION).batchSimulation();
        List<Center<RiderGroup>> centerList = networkBuilder.getAllCenters();

        TheoreticalValueComputer theoryValueComputer = new TheoreticalValueComputer();
        Map<String, Map<String, Double>> theoryMap = theoryValueComputer.computeAllTheoreticalValues(centerList);
        // VerificationWriter.writeTheoreticalQueueTimeValues(theoryMap);

        VerificationWriter.writeSimulationResult(centerList, theoryMap);

        ConfidenceIntervalComputer computer = new ConfidenceIntervalComputer();
        computer.updateAllStatistics(centerList);
        List<ConfidenceInterval> confidenceIntervals = computer.computeAllConfidenceIntervals();
        VerificationWriter.writeConfidenceIntervals(confidenceIntervals, theoryMap,
                "ConfidenceIntervals");

        // Write confidence intervals for all statistics
    }

}
