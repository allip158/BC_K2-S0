package k2so;
import battlecode.common.*;


public class Archon extends DefaultRobot{
	
	public Archon(RobotController rc) throws GameActionException {
		super(rc);
	}
	
	@Override
	public void executeTurn() throws GameActionException {

		try{
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
	
}