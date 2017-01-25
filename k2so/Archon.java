package k2so;
import battlecode.common.*;


public class Archon extends DefaultRobot{
		
	public Archon(RobotController rc) throws GameActionException {
		super(rc);
	}
	
	@Override
	public void executeTurn() throws GameActionException {

		try{
						
			// Initialize array at beginning
			if (!isInitialized()) {
				initializeSignalArray();
			}
			
			// Randomly attempt to build a gardener in this direction
			Direction dir = Utils.randomDirection();

			if (rc.canHireGardener(dir) && Math.random() < .01) {
				rc.hireGardener(dir);
			} 

			// Move randomly
			tryMove(Utils.randomDirection());

			// Broadcast archon's location for other robots on the team to know
			MapLocation myLocation = rc.getLocation();
			rc.broadcast(0,(int)myLocation.x);
			rc.broadcast(1,(int)myLocation.y);

		} catch (Exception e) {
			System.out.println("Archon Exception");
			e.printStackTrace();
		}

	}
	
	private boolean isInitialized() throws GameActionException {
		
		return rc.readBroadcastBoolean(Constants.INITIALIZATION_CHANNEL);
	}

	private void initializeSignalArray() throws GameActionException {
		
		rc.broadcastInt(Utils.getSignalFromRobotType(RobotType.SCOUT), Constants.NUM_SCOUT);
		rc.broadcastInt(Utils.getSignalFromRobotType(RobotType.SOLDIER), Constants.NUM_SOLDIER);
		rc.broadcastInt(Utils.getSignalFromRobotType(RobotType.LUMBERJACK), Constants.NUM_LUMBERJACK);
		rc.broadcastInt(Utils.getSignalFromRobotType(RobotType.TANK), Constants.NUM_TANK);
		rc.broadcastInt(Utils.getSignalFromRobotType(RobotType.GARDENER), Constants.NUM_GARDENER);
		
		rc.broadcastBoolean(Constants.INITIALIZATION_CHANNEL, true);

	}
	
	
}