package k2so;
import battlecode.common.*;


public class Lumberjack extends DefaultRobot{
	
	public Lumberjack(RobotController rc) throws GameActionException {
		super(rc);
	}
	
	@Override
	public void executeTurn() throws GameActionException {
		Team enemy = rc.getTeam().opponent();
		
		try {

            // See if there are any enemy robots within striking range (distance 1 from lumberjack's radius)
            RobotInfo[] robots = rc.senseNearbyRobots(RobotType.LUMBERJACK.bodyRadius+GameConstants.LUMBERJACK_STRIKE_RADIUS, enemy);
			TreeInfo[] neutralTrees = rc.senseNearbyTrees(RobotType.LUMBERJACK.bodyRadius+GameConstants.LUMBERJACK_STRIKE_RADIUS, Team.NEUTRAL);
            
            if(robots.length > 0 && !rc.hasAttacked()) {
                // Use strike() to hit all nearby robots!
                rc.strike();
            } else if(neutralTrees.length > 0) { 	
            	
            	shakeThenChop(neutralTrees);
            	
            } else {
                // No close robots, so search for robots within sight radius
                robots = rc.senseNearbyRobots(-1,enemy);

                // If there is a robot, move towards it
                if(robots.length > 0) {
                    MapLocation myLocation = rc.getLocation();
                    MapLocation enemyLocation = robots[0].getLocation();
                    Direction toEnemy = myLocation.directionTo(enemyLocation);

                    tryMove(toEnemy);
                } 
            }

			
		} catch (Exception e) {
			System.out.println("Lumberjack Exception");
			e.printStackTrace();
		}
	}

	private void shakeThenChop(TreeInfo[] neutralTrees) throws GameActionException{
		
		TreeInfo tree = neutralTrees[0];
		MapLocation treeLocation = tree.getLocation();
		
    	if (rc.canShake() && tree.containedBullets > 0) {
    		
    		rc.shake(treeLocation);
    		
    	} else if(rc.canChop(treeLocation) && tree.containedRobot != null) {
    	
    		rc.chop(treeLocation);
    	} else {
    		tryMove(randomDirection());
    	}
		
	}
	
}