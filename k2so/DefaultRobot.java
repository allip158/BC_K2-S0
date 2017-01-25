package k2so;
import battlecode.common.*; 

import java.util.Random;

// TODO: clear out death array when full
// TODO: same robot should only write in one array location

public strictfp class DefaultRobot {
	static RobotController rc;
	static RobotType rt;
	static Random rand;
		
	public DefaultRobot(RobotController rc) throws GameActionException{
		this.rc = rc;
		rand = new Random(rc.getID());
		rt = rc.getType();
	}

	public void run() throws GameActionException{
		donateToWin();
		executeTurn();
//		sendDeathSignal();
	}
	
	public void executeTurn() throws GameActionException{
		tryMove(randomDirection());
		return;
	}
	
	/**
	 * 
	 * @return false if dying but no room in deathArray, true otherwise
	 * @throws GameActionException
	 */
	public Boolean sendDeathSignal() throws GameActionException {
		
		float health = rc.getHealth();
		int maxHealth = rc.getType().maxHealth;
		
		float percentHealth = (health / (float)maxHealth) * (float)100;
		
		if (percentHealth < Constants.PERCENT_HEALTH_FOR_DEATH_SIGNAL) {
			
			/* Send Death Signal */
			int i = Constants.DEATH_ARRAY_START_INDEX;
			while (i < Constants.DEATH_ARRAY_END_INDEX) {
				
				System.out.println("I am a " + rc.getType() + " and I am dying!");
				
				int existingSignal = rc.readBroadcastInt(i);
				int signal = Utils.getSignalFromRobotType(rc.getType());
				
				if (existingSignal != 0) {
					rc.broadcastInt(i, signal);
					return true;
				}
				i++;
			}
			return false;
		}
		return true;
	}
	
	
	public void donateToWin() throws GameActionException {
		float bullets = rc.getTeamBullets();
		int victoryPts = rc.getTeamVictoryPoints();
		
		int potentialPts = (int)(bullets / rc.getVictoryPointCost());
		
		if ((GameConstants.VICTORY_POINTS_TO_WIN - victoryPts) <= potentialPts) {
			rc.donate(bullets);
		}
	}
	
    static Direction randomDirection() {
        return new Direction((float)Math.random() * 2 * (float)Math.PI);
    }

    static boolean tryMove(Direction dir) throws GameActionException {
        return tryMove(dir,20,3);
    }

    static boolean tryMove(Direction dir, float degreeOffset, int checksPerSide) throws GameActionException {

        if (rc.canMove(dir)) {
            rc.move(dir);
            return true;
        }

        boolean moved = false;
        int currentCheck = 1;

        while(currentCheck<=checksPerSide) {

        	if(rc.canMove(dir.rotateLeftDegrees(degreeOffset*currentCheck))) {
                rc.move(dir.rotateLeftDegrees(degreeOffset*currentCheck));
                return true;
            }

            if(rc.canMove(dir.rotateRightDegrees(degreeOffset*currentCheck))) {
                rc.move(dir.rotateRightDegrees(degreeOffset*currentCheck));
                return true;
            }
            currentCheck++;
        }

        return false;
    }

    static boolean willCollideWithMe(BulletInfo bullet) {
        MapLocation myLocation = rc.getLocation();

        Direction propagationDirection = bullet.dir;
        MapLocation bulletLocation = bullet.location;

        Direction directionToRobot = bulletLocation.directionTo(myLocation);
        float distToRobot = bulletLocation.distanceTo(myLocation);
        float theta = propagationDirection.radiansBetween(directionToRobot);

        if (Math.abs(theta) > Math.PI/2) {
            return false;
        }

        float perpendicularDist = (float)Math.abs(distToRobot * Math.sin(theta)); // soh cah toa :)

        return (perpendicularDist <= rt.bodyRadius);
    }

	
}
