package k2so;
import battlecode.common.*; 

import java.util.Random;

// TODO: clear out death array when full
// TODO: same robot should only write in one array location

public strictfp class DefaultRobot {

	protected RobotController rc;
    protected RobotType rt;
    protected Random rand;
    protected Team enemy;
	
	public DefaultRobot(RobotController rc){

		this.rc = rc;
		rand = new Random(rc.getID());
		rt = rc.getType();
        enemy = rc.getTeam().opponent();
	}

	public void run() throws GameActionException{
		donateToWin();
		executeTurn();
     //donateToWin();
    donateAtTheLastTurn();

//		sendDeathSignal();

	}
	
	public void executeTurn() throws GameActionException{
		tryMove(Utils.randomDirection());
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

	public void donateAtTheLastTurn() throws GameActionException {
        float bullets = rc.getTeamBullets();
	    if(rc.getRoundNum() == (rc.getRoundLimit() - 1))
            rc.donate(bullets);
    }


    boolean tryMove(Direction dir) throws GameActionException {
        return tryMove(dir,20,3);
    }

    boolean tryMove(Direction dir, float degreeOffset, int checksPerSide) throws GameActionException {

        if (rc.canMove(dir)) {
            rc.move(dir);
            return true;
        }

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

    boolean willCollideWithMe(BulletInfo bullet) {
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
