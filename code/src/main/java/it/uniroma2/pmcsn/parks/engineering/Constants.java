package it.uniroma2.pmcsn.parks.engineering;

import java.nio.file.Path;

import it.uniroma2.pmcsn.parks.SimulationMode;

public class Constants {

    public static final String DATA_PATH = Path.of(".", "Out", "Data").toString();
    public static final String INTERVAL_DATA_PATH = Path.of(DATA_PATH, "Interval").toString();
    public static final String CENTER_DATA_PATH = Path.of(DATA_PATH, "Center").toString();
    public static final String PEOPLE_DATA_PATH = Path.of(CENTER_DATA_PATH, "People").toString();
    public static final String GROUP_DATA_PATH = Path.of(CENTER_DATA_PATH, "Group").toString();
    public static final String GENERAL_DATA_PATH = Path.of(DATA_PATH, "General").toString();
    public static final String JOB_DATA_PATH = Path.of(DATA_PATH, "Job").toString();
    public static final String LOG_PATH = Path.of(".", "Out", "Log").toString();
    public static final String VERIFICATION_PATH = Path.of(".", "Out", "Data", "Verification").toString();
    public static final String VALIDATION_PATH = Path.of(".", "Out", "Data", "Validation").toString();
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

    public static boolean COLLECT_JOB_STATS = false;
    public static final String JOB_STATS_FILENAME = "job_stats.csv";
    public static final String CONFIG_FILENAME = "Config_Normal.json";
    public static final String ATTRACTION_FILE = "AttractionsData_1.csv";
    public static final String ENTRANCE_FILE = "EntranceData.csv";
    public static final String RESTAURANT_FILE = "RestaurantsData.csv";

    public static final double PRIORITY_PASS_PROB = 0.1;
    public static double PRIORITY_PERCENTAGE_PER_RIDE = 0.4;
    public static final double NORMAL_PERCENTAGE_PER_RIDE = 0.50;
    public static double SMALL_GROUP_PERCENTAGE_PER_RIDE = 0.0;
    public static final long SEED = 4321;

    public static SimulationMode MODE;
    public static final String VERIFICATION_ATTRACTION_FILE = "AttractionsDataVerify.csv";
    public static final String VERIFICATION_RESTAURANT_FILE = "RestaurantsDataVerify.csv";
    public static final String VERIFICATION_ENTRANCE_FILE = "EntranceDataVerify.csv";
    public static final String VERIFICATION_CONFIG_FILENAME = "ConfigVerify.json";
    public static int VERIFICATION_BATCH_SIZE = 2048; // DO NOT CHANGE THIS VALUE
    public static int VERIFICATION_BATCH_NUMBER = 200; // DO NOT CHANGE THIS VALUE

    public static final String VALIDATION_CONFIG_FILENAME = "ConfigValidation.json";
    public static final String VALIDATION_ATTRACTION_FILE = "AttractionsData_2.csv";
    public static final Integer VALDATION_REPLICATIONS_NUMBER = 100;

    public static String CONSISTENCY_CHECKS_CONFIG_FILENAME = null;
    public static final String PRE_CONSISTENCY_CHECKS_CONFIG_FILENAME = "ConfigConsistency_pre.json";
    public static final String POST_CONSISTENCY_CHECKS_CONFIG_FILENAME = "ConfigConsistency_post.json";
    public static final String CONSISTENCY_CHECKS_ATTRACTION_FILE = "AttractionsData_2.csv";
    public static final Integer CONSISTENCY_CHECKS_BATCH_NUMBER = 2048;
    public static final Integer CONSISTENCY_CHECKS_BATCH_SIZE = 200;

    public static final Integer REPLICATIONS_NUMBER = 75;
    public static final Integer MAX_NORMAL_QUEUE_EXTRACTION_TRY_TIMES = 3;
    public static int SMALL_GROUP_LIMIT_SIZE = 1;

    public static boolean IMPROVED_MODEL = false;

    public static double AVG_GROUP_SIZE_POISSON = 3;

}