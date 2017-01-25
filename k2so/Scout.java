package k2so;
import battlecode.common.*;


public class Scout extends DefaultRobot{
		
	public MapLocation currentDestination = rc.getInitialArchonLocations(rc.getTeam().opponent())[0];
	public int numberOfTrees = 0;
	
	public Scout(RobotController rc) throws GameActionException {
		super(rc);
	}
	
	@Override
	public void executeTurn() throws GameActionException {

		try {
						
			Direction dir = getDirection();
            tryMove(dir);
            
            calculateForestDensity();
			
		} catch (Exception e) {
			System.out.println("Scout Exception");
			e.printStackTrace();
		}
	}
	
	/**
	 * Gets the next direction to ensure Scouts depth-first movement
	 * @return suggested direction
	 */
	public Direction getDirection() {
		Direction dir = rc.getLocation().directionTo(currentDestination);

		if (!rc.hasMoved()) {
			dir = dir.rotateLeftDegrees(90);
		}
		
		return dir;
	}
	
	/**
	 * Keeps track of number of trees and after a constant number of rounds,
	 * calculates and broadcasts the tree density
	 * @throws GameActionException
	 */
	public void calculateForestDensity() throws GameActionException{
		float density = 0;
		
		updateNumberOfTrees();

		if(rc.getRoundNum() > Constants.ROUNDS_FOR_DENSITY_CALCS) {
			density = (float)numberOfTrees / (float)rc.getRoundNum();
			broadcastDensity(density);
		}
	}

	private void broadcastDensity(float density) {
		System.out.println("Density for Scout " + String.valueOf(rc.getID()) + " : " + density);
		
	}

	private void updateNumberOfTrees() throws GameActionException {
		
		// if current location has tree
		if (rc.isLocationOccupiedByTree(rc.getLocation())) {
			numberOfTrees++;
		}
		
	}
	
}