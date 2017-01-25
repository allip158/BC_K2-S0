package k2so;

public final class Constants {
	
	/* Number of rounds before scouts make their density calculation */
	public static final int ROUNDS_FOR_DENSITY_CALCS = 50;
	
	/* Constants related to death array */
	public static final int DEATH_ARRAY_START_INDEX = 10;
	public static final int DEATH_ARRAY_END_INDEX = 16;
	public static final float PERCENT_HEALTH_FOR_DEATH_SIGNAL = 10;
	
	public static final int GARDENER_DEATH_SIGNAL = 0;
	public static final int LUMBERJACK_DEATH_SIGNAL = 1;
	public static final int SCOUT_DEATH_SIGNAL = 2;
	public static final int SOLDIER_DEATH_SIGNAL = 3;
	public static final int TANK_DEATH_SIGNAL = 4;
	public static final int ARCHON_DEATH_SIGNAL = 5;
	
	/* Constants indicating what number each of the robots will be present */
	public static final int NUM_LUMBERJACK = 100;
	public static final int NUM_SCOUT = 0;
	public static final int NUM_SOLDIER = 0;
	public static final int NUM_TANK = 0;
	public static final int NUM_GARDENER = 10;	
	
	/* Ratio of Gardeners who garden to those who build robots, i.e. X to 1 */
	public static final int RATIO_GARDENERS_TO_BUILDERS = 6;
	
	/* Index in signal array to store boolean */
	public static final int INITIALIZATION_CHANNEL = 2;
	
	/* Necessary radius to be clear for gardeners to plant */
	public static final int MIN_PLANTING_RADIUS = 2;
	
	
}