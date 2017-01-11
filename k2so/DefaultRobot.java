package k2so;
import battlecode.common.*;

import java.util.Random;

public strictfp class DefaultRobot {
	static RobotController rc;
	static RobotType rt;
	static Random rand;
	
	public DefaultRobot(RobotController rc){
		this.rc = rc;
		rand = new Random(rc.getID());
		rt = rc.getType();
	}
	
	public void run() throws GameActionException{
		executeTurn();
	}
	
	public void executeTurn() throws GameActionException{
		tryMove(randomDirection());
		return;
	}
	
	public void donateToWin() throws GameActionException {
		float bullets = rc.getTeamBullets();
		int victoryPts = rc.getTeamVictoryPoints();
		
		int potentialPts = ((int) bullets)*GameConstants.BULLET_EXCHANGE_RATE;
		
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
