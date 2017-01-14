package k2so;
import battlecode.common.*;


public class Gardener extends DefaultRobot{
		
	static int numScoutsLeft = Constants.NUM_SCOUTS;
	
	public Gardener(RobotController rc) throws GameActionException {
		super(rc);
	}
	
	@Override
	public void executeTurn() throws GameActionException {

		try {
			
			// Listen for home archon's location
			int xPos = rc.readBroadcast(0);
			int yPos = rc.readBroadcast(1);
			MapLocation archonLoc = new MapLocation(xPos,yPos);
			
			 // Generate a random direction
            Direction dir = randomDirection();

            // Build the set number of scouts
			while (numScoutsLeft > 0) {
				if (rc.canBuildRobot(RobotType.SCOUT, dir)) {
					rc.buildRobot(RobotType.SCOUT, dir);
					numScoutsLeft--;
				} else {
					tryMove(randomDirection());
				}
			}
            
            // Randomly attempt to build a soldier or lumberjack in this direction
            if (rc.canBuildRobot(RobotType.SOLDIER, dir) && Math.random() < .01 && rc.isBuildReady()) {
                rc.buildRobot(RobotType.SOLDIER, dir);
            } else if (rc.canBuildRobot(RobotType.LUMBERJACK, dir) && Math.random() < .01 && rc.isBuildReady()) {
                rc.buildRobot(RobotType.LUMBERJACK, dir);
            } else if (rc.canBuildRobot(RobotType.TANK, dir) && Math.random() < .001 && rc.isBuildReady()) {
            	rc.buildRobot(RobotType.TANK, dir);
            }

            // Move randomly
            tryMove(randomDirection());

			
		} catch (Exception e) {
			System.out.println("Gardener Exception");
			e.printStackTrace();
		}
	}

}