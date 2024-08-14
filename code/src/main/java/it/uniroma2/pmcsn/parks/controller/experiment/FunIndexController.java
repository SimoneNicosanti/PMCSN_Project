package it.uniroma2.pmcsn.parks.controller.experiment;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import it.uniroma2.pmcsn.parks.SimulationMode;
import it.uniroma2.pmcsn.parks.controller.Simulation;
import it.uniroma2.pmcsn.parks.engineering.Constants;
import it.uniroma2.pmcsn.parks.engineering.interfaces.Controller;
import it.uniroma2.pmcsn.parks.engineering.singleton.ClockHandler;
import it.uniroma2.pmcsn.parks.model.job.GroupPriority;
import it.uniroma2.pmcsn.parks.model.job.RiderGroup;
import it.uniroma2.pmcsn.parks.model.server.concrete_servers.ExitCenter;
import it.uniroma2.pmcsn.parks.utils.FunIndexComputer;
import it.uniroma2.pmcsn.parks.writers.FunIndexWriter;
import it.uniroma2.pmcsn.parks.writers.WriterHelper;

public class FunIndexController implements Controller<RiderGroup> {

    public static void main(String[] args) {
        WriterHelper.createAllFolders();
        new FunIndexController().simulate();
    }

    public FunIndexController() {
    }

    @Override
    public void simulate() {

        init_simulation();

        Map<GroupPriority, Double> funIndexMap = new HashMap<>();
        for (GroupPriority prio : GroupPriority.values()) {
            funIndexMap.put(prio, 0.0);
        }

        for (int i = 0; i < Constants.FUN_INDEX_REPLICATIONS_NUMBER; i++) {
            System.out.println("Replication Number >>> " + i);

            ExitCenter exitCenter = new Simulation(SimulationMode.NORMAL).simulateOnce().getExitCenter();

            // EventLogger.writeRandomLogString(RandomHandler.getInstance().getRandomLog());

            List<RiderGroup> exitRiderGroups = exitCenter.getExitJobs();

            // JobInfoWriter.writeAllJobsInfo("Job", "Job_Info_2.csv", exitRiderGroups);

            Map<GroupPriority, Double> currentFunIndexMap = FunIndexComputer.computeAvgsFunIndex(exitRiderGroups);

            funIndexMap.replaceAll((prio, value) -> value + currentFunIndexMap.getOrDefault(prio, 0.0));
        }

        funIndexMap.replaceAll((key, value) -> value / Constants.FUN_INDEX_REPLICATIONS_NUMBER);

        // System.out.println(funIndexMap.toString());

        FunIndexWriter.writeFunIndexResults(funIndexMap);
    }

    private void init_simulation() {
        // Reset statistics
        // WriterHelper.clearDirectory(Path.of(Constants.DATA_PATH,
        // "Center").toString());
        WriterHelper.clearDirectory(Constants.JOB_DATA_PATH);
        // Prepare the logger and set the system clock to 0
        // WriterHelper.clearDirectory(Constants.LOG_PATH);
        ClockHandler.getInstance().setClock(0);
    }

}