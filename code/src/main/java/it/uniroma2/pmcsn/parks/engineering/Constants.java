package it.uniroma2.pmcsn.parks.engineering;

import java.nio.file.Path;

public class Constants {

    public static final String DATA_PATH = Path.of(".", "Out", "Data").toString();
    public static final String INTERVAL_DATA_PATH = Path.of(DATA_PATH, "Interval").toString();
    public static final String GENERAL_DATA_PATH = Path.of(DATA_PATH, "General").toString();
    public static final String JOB_DATA_PATH = Path.of(DATA_PATH, "Job").toString();
    public static final String LOG_PATH = Path.of(".", "Out", "Log").toString();
    public static final String VERIFICATION_PATH = Path.of(".", "Out", "Data", "Verification").toString();
    public static final String CONFIG_PATH = Path.of(".", "config").toString();

    public static final String ENTRANCE = "Entrance";
    public static final String EXIT = "Exit";

    public static final String ATTRACTION_ROUTING_NODE = "AttractionRoutingNode";
    public static final String NETWORK_ROUTING_NODE = "NetworkRoutingNode";
    public static final String RESTAURANT_ROUTING_NODE = "RestaurantRoutingNode";

    public static final String GROUP_SIZE_STREAM = "ARRIVAL BUILDER - GROUP SIZE";
    public static final String ARRIVAL_STREAM = "ARRIVAL BUILDER - ARRIVAL";
    public static final String PRIORITY_STREAM = "ARRIVAL BUILDER - PRIORITY";

    public static final String GROUP_DIRECTORY = "groups";
    public static final String PEOPLE_DIRECTORY = "people";

    public static final String JOB_STATS_FILENAME = "job_stats.csv";
    public static final String CONFIG_FILENAME = "Config_1.json";
    public static final String ATTRACTION_FILE = "AttractionsData_1.csv"; // TODO
    public static final String ENTRANCE_FILE = "EntranceData.csv";
    public static final String RESTAURANT_FILE = "RestaurantsData.csv";

    public static final double PRIORITY_PASS_PROB = 0.1;
    public static final double PRIORITY_PERCENTAGE_PER_RIDE = 0.4;
    public static final long SEED = 4321;

    // Indicates whether to collect statistics for each interval or not
    public static final boolean INTERVAL_STATS = false;

    public static boolean VERIFICATION_MODE = false;
    public static final String VERIFICATION_ATTRACTION_FILE = "AttractionsDataVerify.csv";
    public static final String VERIFICATION_RESTAURANT_FILE = "RestaurantsDataVerify.csv";
    public static final String VERIFICATION_ENTRANCE_FILE = "EntranceDataVerify.csv";
    public static final String VERIFICATION_CONFIG_FILENAME = "ConfigVerify.json";
    public static final int VERIFY_SIMULATION_NUM = 75;
    public static final int BATCH_SIZE = 1792; // DO NOT CHANGE THIS VALUE
    public static final int BATCH_NUMBER = 64; // DO NOT CHANGE THIS VALUE

}