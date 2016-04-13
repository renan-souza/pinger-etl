package edu.stanford.slac.pinger.general;

import edu.stanford.slac.pinger.general.utils.Utils;



public final class C {

	public static String SESAME_SERVER;
	public static String PROJECT_HOME = null;
	static {	
		SESAME_SERVER = "http://wanmon.slac.stanford.edu:8181/openrdf-sesame";
		setInitConstantsBasedOnTheOS();		
		setProjectHome();
	}
	

	/* *******************************************************************
	 * ************* Other General Public Constants *******************
	 ******************************************************************* */ 	
	public static final String STANDARD_SPARQLQUERY = "SELECT * WHERE { ?a ?b ?c } LIMIT 10";
	public static final int MAX_ATTEMPT_INSTANTIATOR = 10;
	public static final int NUM_THREADS_MONITORING_NODES_TSV = 20;
	public static final int KB = (int) Math.pow(2, 10);
	public static final int MB = (int) Math.pow(2, 20);

	public static final int NUM_FILES_PER_TIME = 10;
	
	public static final String DEFAULT_PACKET_SIZE = "100";
	
	public static boolean continue_town = true;
	public static boolean continue_country = true;
	
	public static final String NULL_CHARACTER = "\\N";
	
	
	public static int DEBUG_LEVEL=1;
	public static boolean IS_TO_LOAD_REMAINING=false;

	
	/* *******************************************************************
	 * ************* Initial Properties ************************************
	 ******************************************************************* */
	/*
	 * The following line should be uncommented when putting in production.
	 */
	//public static final String SESAME_SERVER = "http://wanmon.slac.stanford.edu:8181/openrdf-sesame";
	public static final String REPOSITORY_ID =  "pinger";

	
	public static String PERL_HOME;
	public static String TMP_DIR;
	public static String OS;
	
	public static final String DATA_TMP_DIR = TMP_DIR+"data_pid"+Utils.getCurrentPID()+"/";
	public static final int CUT_HEAP_COEF = 10;
	public static final int CUT_HD = 500 * MB;
	public static final String[] GEONAMES_USERNAME = {"pinger","renansouza","renan2","renan3","demo"};
	//http://www.geonames.org/manageaccount


	
	/* *******************************************************
	 * ***************** JSON Files **************************
	 ********************************************************* */
	public static final String JSON_DIR = PROJECT_HOME+"data/json/";
	
	public static final String NODEDETAILS_JSON_FILE = PROJECT_HOME+"data/json/NodeDetails.json";
	public static final String MONITORING_MONITORED_JSON_FILE = JSON_DIR+"MonitoringMonitoredNodes.json";
	public static final String MONITORING_MONITORED_GROUPED_JSON_FILE = JSON_DIR+"MonitoringMonitoredNodesGrouped.json";
	public static final String MONITORING_NODES_GROUPED = JSON_DIR+"MonitoringNodesGrouped.json"; //used for parallelizing the process of creating a NTriple file. It could create more than 1 file at time, 1 for each monitoring node.
	public static final String MONITORING_NODES_GROUPED_FOR_TSV = JSON_DIR+"MonitoringNodesGroupedForTSV.json";
	public static final String COUNTRIES_GEONAMES_JSON = JSON_DIR+"countries_geonames.json";
	public static final String COUNTRIES_JSON = JSON_DIR+"countries.json";
	public static final String MONITORING_MONITORED_COUNTRIES = JSON_DIR+"monitoring_monitored_countries.json";
	
	public static final String MONITORING_MONITORED_JSON_FILE_WWW = "/afs/slac.stanford.edu/g/www/www-iepm/pinger/lod/data/json/MonitoringMonitoredNodes.js";
	public static final String MONITORING_MONITORED_COUNTRIES_WWW = "/afs/slac.stanford.edu/g/www/www-iepm/pinger/lod/data/json/monitoring_monitored_countries.js";

	public static final String HOURS_JSON = JSON_DIR + "hoursIds.json";
	public static final String DAYS_JSON = JSON_DIR + "daysIds.json";
	public static final String MONTHS_JSON = JSON_DIR + "monthsIds.json";
	public static final String YEARS_JSON = JSON_DIR + "yearsIds.json";

	
	
	/* *******************************************************
	 * ***************** CSV Files **************************
	 ********************************************************* */ 
	
	public static final String CSV_DIR = PROJECT_HOME+"data/csv/";

	public static final String CONTINENT_CSV = CSV_DIR + "continent.csv";
	public static final String COUNTRY_CSV = CSV_DIR + "country.csv";
	public static final String DAY_CSV = CSV_DIR + "day.csv";
	public static final String YEAR_CSV = CSV_DIR + "year.csv";	
	public static final String HOUR_CSV = CSV_DIR + "time_hourly.csv";
	
	public static final String DESTINATION_NODE_CSV = CSV_DIR + "destination_node.csv";
	public static final String DESTINATION_SOURCE_NODE_CSV = CSV_DIR + "destination_source_node.csv";
	public static final String METRIC_CSV = CSV_DIR + "metric.csv";
	public static final String MONTH_CSV = CSV_DIR + "month.csv";	
	public static final String SOURCE_NODE_CSV = CSV_DIR + "source_node.csv";
	
	
	/* *******************************************************
	 * ***************** RDF Files **************************
	 ********************************************************* */ 
	public static final String PREFIXES_FILE = PROJECT_HOME+"data/rdf/prefixes.rdf";

	/* *******************************************************
	 * ***************** NTRIPLES Files **************************
	 ********************************************************* */ 
	public static final String NTRIPLES_DIR = DATA_TMP_DIR+"ntriples/";

	
	/* *******************************************************
	 * ***************** Other Paths **************************
	 ********************************************************* */ 
	public static final String PERL_DIR = PROJECT_HOME+"data/perl/";
	public static final String REMAINING_NTRIPLES =  PROJECT_HOME+"data/remaining_ntriples.txt";
	public static final String EXPORT_FILE = "C:/pinger-export/export.n3";
	
	
	
	/* *****************************************************************
	 * ***************** Web Sparql Endpoints **************************
	 ******************************************************************* */ 	
	public static final String DBPEDIA_ENDPOINT = "http://dbpedia.org/sparql/";
	public static final String FACTFORGE_ENDPOINT = "http://factforge.net/sparql";
	public static final String FACTFORGE_ENDPOINT_JSON = FACTFORGE_ENDPOINT+".json";
	
	
	
	/* *****************************************************************
	 * ***************** URLs **************************
	 ******************************************************************* */ 	
	
	public static final String NODE_DETAILS_CF = "http://www-iepm.slac.stanford.edu/pinger/pingerworld/nodes.cf";
	public static final String NODE_DETAILS_ALL_CF = "http://www-iepm.slac.stanford.edu/pinger/pingerworld/all-nodes.cf";

	/* *******************************************************************
	 * ************* General Functions ***********************************
	 ******************************************************************* */ 
	private static void setInitConstantsBasedOnTheOS() {
		String OSname = System.getProperty("os.name").toLowerCase();
		boolean windows = OSname.contains("windows");
		boolean linux = ( OSname.contains("linux") || OSname.contains("unix") );
		if (windows) {
			OS = "windows";
			TMP_DIR = "C:/tmp/pinger/";
			PERL_HOME = "C:/strawberry/perl/bin/";
		} else if (linux) {
			OS = "linux";
			TMP_DIR = "/scratch/pinger/lod/";
			PERL_HOME = "/usr/bin/";
		} else {
			Logger.error("Operating System not supported: " + OSname, "errors");
			System.exit(-1);
		}
	}	
	
	private static void setProjectHome() {
		ClassLoader classLoader = C.class.getClassLoader();
        String path = classLoader.getResource("").getPath().replace("/bin/", "/");
        
        if (OS.equals("windows")) {
        	path = path.replace("\\", "/");
			if (path.matches("^/.*$")) {
				path = path.replaceFirst("/", "");
			}
        }        
		PROJECT_HOME = path;
	}
	
	/**
	 * level > 0 prints in console.
	 * @param level
	 */
	

}




