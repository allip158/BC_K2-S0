package k2so;
import battlecode.common.*;

public final class Utils {
	
	/**
	 * returns robot type from signal, or null if signal not between 0 and 5
	 * @param signal
	 * @return RobotType
	 */
	public static RobotType getRobotTypeFromSignal(int signal) {
		
		RobotType type;
		signal -= Constants.DEATH_ARRAY_START_INDEX;
		
		if (signal == Constants.ARCHON_DEATH_SIGNAL) {
			type = RobotType.ARCHON;
		} else if (signal == Constants.SCOUT_DEATH_SIGNAL) {
			type = RobotType.SCOUT;
		} else if (signal == Constants.SOLDIER_DEATH_SIGNAL) {
			type = RobotType.SOLDIER;
		} else if (signal == Constants.TANK_DEATH_SIGNAL) {
			type = RobotType.TANK;
		} else if (signal == Constants.GARDENER_DEATH_SIGNAL) {
			type = RobotType.GARDENER;
		} else if (signal == Constants.LUMBERJACK_DEATH_SIGNAL) {
			type = RobotType.LUMBERJACK;
		} else {
			type = null;
		}
		
		return type;
	}
	
	/**
	 * gets signal from robot type or -1 if invalid type
	 * @param type
	 * @return
	 */
	public static int getSignalFromRobotType(RobotType type) {
		int signal;
		
		if (type.equals(RobotType.ARCHON)) {
			signal = Constants.ARCHON_DEATH_SIGNAL;
		} else if (type.equals(RobotType.SCOUT)) {
			signal = Constants.SCOUT_DEATH_SIGNAL;
		} else if (type.equals(RobotType.SOLDIER)) {
			signal = Constants.SOLDIER_DEATH_SIGNAL;
		} else if (type.equals(RobotType.TANK)) {
			signal = Constants.TANK_DEATH_SIGNAL;
		} else if (type.equals(RobotType.GARDENER)) {
			signal = Constants.GARDENER_DEATH_SIGNAL;
		} else if (type.equals(RobotType.LUMBERJACK)) {
			signal = Constants.LUMBERJACK_DEATH_SIGNAL;
		} else {
			signal = -1;
			signal -= Constants.DEATH_ARRAY_START_INDEX;
		}
		
		/* offset signal by start index */
		signal += Constants.DEATH_ARRAY_START_INDEX;
		
		return signal;
	}
	
	
	
}