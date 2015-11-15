package TUDarmstadtTeam2.utils;

public class Config {

	public static final boolean LOGGING = false;
	public static final boolean LOGTOCONSOLE = false;
	public static final boolean DEBUGGING = false;

	// create a balance between amount of samples and simulation depth
	public static final int INIT_ROLLOUT_DEPTH_STORACHSTIC_AGENT = 5;

	public static final double PROBABILITY_CUT_BLOCKING_OR_DEADLY = 0.9;
	
	

	public static final int ABORT_TIME = 2; // ms TODO set this automatically
	public static final int ABORT_TIME_START_OF_NEW_SAMPLE = 3;
	public static final int ABORT_TIME_SINGLE_SIMULATION = 2;
	public static final int MAX_ROLLOUT_DEPTH = 100; // TODO make this dynamic
	public static int NUMBEROFACTIONS;
	/* Constant in UCT search */
	public static final double C = Math.sqrt(2);

	// KnowledgeBase and KnowledgeItem constants
	public static double DiscScoreWeight = 0.5;
	public static final int MAX_NUMBER_OF_POSITIONS_TO_REMEMBER = 20;
	public static final int NUMBER_OF_REPEATED_POSITIONS_UNTIL_STUCK = 30;	
	
	public static final double BLOCK_PERCENTAGE_THRESHOLD = 0.2;
//	public static final double DEATH_PRECENTAGE_THRESHOLD = 0.2;
	public static final double EPSILON = 1e-6;
	public static final int VALUE_OF_LOST_GAME = -100;
	public static final int FORGET_TIME = 75;

	// Timing constants
	public static final int SAFETY_TIME_FOR_AGENT_INIT = 40;
	public static final int Agent_Restart_Threshold = 900;

	// Search Config
	public static final double MAX_MEMORY_UTILIZATION_FACTOR = 0.6;
	public static final int SIMULATION_DEPTH_FOR_BACKUPGAME = 30;
	public static final int SEARCH_DEPTH = 200;
	public static final int ITERATION_SIZE = 1800;
	public static final boolean TIE_BREAK = true;
	public static final boolean PRINT_OUTPUTS = false;
	
	public static final boolean USE_MAP = true;

	public static final long GOAL_SEARCH_ABORT_TIME = 20;

}
