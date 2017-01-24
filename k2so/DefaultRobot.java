package k2so;
import battlecode.common.*; 

import java.util.Random;

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
		executeTurn();
        //donateToWin();
        donateAtTheLastTurn();
	}
	
	public void executeTurn() throws GameActionException{
		tryMove(RobotUtils.randomDirection());
		return;
	}

	public void donateToWin() throws GameActionException {
		float bullets = rc.getTeamBullets();
		int victoryPts = rc.getTeamVictoryPoints();

		int potentialPts = (int)((bullets) / rc.getVictoryPointCost());

		if ((GameConstants.VICTORY_POINTS_TO_WIN - victoryPts) <= potentialPts) {
			rc.donate(bullets);
		}
	}

	public void donateAtTheLastTurn() throws GameActionException {
        float bullets = rc.getTeamBullets();
	    if(rc.getRoundNum() == (GameConstants.GAME_DEFAULT_ROUNDS - 1))
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
