package k2so;
import java.util.List;
import java.util.Arrays;

import battlecode.common.*;

public class Gardener extends DefaultRobot {
	
	Trees trees;
	Boolean isBaby = true;

	public Gardener(RobotController rc) throws GameActionException {
		super(rc);
		this.trees = new Trees();
	}

	@Override
	public void executeTurn() throws GameActionException {

		try {

			/* The ratio of gardeners to builders */
			if (rc.getID() % (Constants.RATIO_GARDENERS_TO_BUILDERS+1) < Constants.RATIO_GARDENERS_TO_BUILDERS) {
				trees.garden();
//				if(hasEnoughRoom() && !isBaby) {
//					trees.garden();
//				} else {
//					MapLocation nearbyTree = getNearbyTreeLocation();
//					if (nearbyTree != null) {
//						// Direction away from trees
//						Direction directionAway = rc.getLocation().directionTo(nearbyTree).opposite(); 
//						tryMove(directionAway);
//					} else {
//						tryMove(randomDirection());
//					}
//				}
			} else {

				// Generate a random direction
				Direction dir = randomDirection();
				buildRobot(dir);

			}

			isBaby = false;

		} catch (Exception e) {
			System.out.println("Gardener Exception");
			e.printStackTrace();
		}
	}

	private boolean hasEnoughRoom() {
	
		TreeInfo[] nearbyTrees = rc.senseNearbyTrees(Constants.MIN_PLANTING_RADIUS);
		if (nearbyTrees.length == 0) {
			return true;
		} 
		
		return false;
	}

	private MapLocation getNearbyTreeLocation() {
		
		TreeInfo[] nearbyTrees = rc.senseNearbyTrees(Constants.MIN_PLANTING_RADIUS);
		if (nearbyTrees.length > 0) {
			return nearbyTrees[0].getLocation();
		} else {
			return null;
		}
		
	}
	
	/**
	 * builds any robot that is under-represented according to Constants, or moves randomly
	 * @param Direction
	 * @throws GameActionException
	 */
	private void buildRobot(Direction dir) throws GameActionException{
		
		if (getNumRobots(RobotType.SCOUT) > 0 && rc.canBuildRobot(RobotType.SCOUT, dir)) {
			rc.buildRobot(RobotType.SCOUT, dir);
			int currentNumRobots = getNumRobots(RobotType.SCOUT);
			rc.broadcastInt(Utils.getSignalFromRobotType(RobotType.SCOUT), currentNumRobots-1);
						
		} else if (getNumRobots(RobotType.SOLDIER) > 0 && rc.canBuildRobot(RobotType.SOLDIER, dir)) {
			rc.buildRobot(RobotType.SOLDIER, dir);
			rc.broadcastInt(Utils.getSignalFromRobotType(RobotType.SOLDIER), getNumRobots(RobotType.SOLDIER)-1);

		} else if (getNumRobots(RobotType.LUMBERJACK) > 0 && rc.canBuildRobot(RobotType.LUMBERJACK, dir)) {
			rc.buildRobot(RobotType.LUMBERJACK, dir);
			rc.broadcastInt(Utils.getSignalFromRobotType(RobotType.LUMBERJACK), getNumRobots(RobotType.LUMBERJACK)-1);

		} else if (getNumRobots(RobotType.TANK) > 0 && rc.canBuildRobot(RobotType.TANK, dir)) {
			rc.buildRobot(RobotType.TANK, dir);
			rc.broadcastInt(Utils.getSignalFromRobotType(RobotType.TANK), getNumRobots(RobotType.TANK)-1);

		} else{
			tryMove(dir);
		}
	}
	
	private int getNumRobots(RobotType type) throws GameActionException {
		return rc.readBroadcastInt(Utils.getSignalFromRobotType(type));
	}
	
/*
 * 	Farming Methods
 */

public class Trees {

		public List<TreeInfo> treeArray;
		private Direction[] directions;
		private int NUM_TREES_AROUND_GARDENER = 6;
		
		public Trees() {
			
			this.treeArray = Arrays.asList(new TreeInfo[NUM_TREES_AROUND_GARDENER]);
			
			this.directions = new Direction[]{	Direction.getNorth().rotateLeftDegrees(30), 
												Direction.getNorth().rotateRightDegrees(30), 
												Direction.getEast(), 
												Direction.getSouth().rotateLeftDegrees(30), 
												Direction.getSouth().rotateRightDegrees(30), 
												Direction.getWest()};
		}
		
		public void garden() throws GameActionException {
			
			// Priority 1: plant
			int index = this.getAvailableDirectionIndex();
			if (index != -1) {
				this.tryPlant(index);
			
			// Priority 2: water lowest health tree
			} else {
				index = this.getLowestHealthTree();
				if (index != -1) {
					this.tryWater(index);
				}
			}
			
		}
		
		/**
		 *  gets index in treeArray of lowest health tree or -1 if none
		 * 	@return int index
		 */
		private int getLowestHealthTree() throws GameActionException {
			float minimumHealth = 50;
			int indexOfMinHealthTree = -1;
			
			for (int i = 0; i < NUM_TREES_AROUND_GARDENER; i++) {
				
				TreeInfo tree = treeArray.get(i);
				if (tree != null && rc.senseTreeAtLocation(tree.location) != null) {
					
					/* NOTE: only use location in tree info, health changes */
					TreeInfo updatedTreeInfo = rc.senseTreeAtLocation(tree.location);
					
					float health = updatedTreeInfo.getHealth();
					if (health < minimumHealth) {
						indexOfMinHealthTree = i;
						minimumHealth = health;
					}
				} 
				
			}
			return indexOfMinHealthTree;
		}
		
		/**
		 * finds an empty direction or -1 if there is none
		 * @return int index
		 * @throws GameActionException
		 */
		private int getAvailableDirectionIndex() throws GameActionException{
			
			for (int i = 0; i < NUM_TREES_AROUND_GARDENER; i++) {
				Direction direction = directions[i];
				MapLocation location = rc.getLocation().add(direction, GameConstants.BULLET_TREE_RADIUS+rc.getType().bodyRadius);

				TreeInfo tree = rc.senseTreeAtLocation(location);
				if (tree == null) {
					// tree does not exist
					return i;
				}
			}
			
			return -1;
		}
		
		/**
		 * 	Attempts to plant a tree at the given direction index
		 *  if successful, adds tree info at given index
		 */
		private Boolean tryPlant(int index) throws GameActionException{
			
			Direction direction = directions[index];
			
			if (rc.canPlantTree(direction)) {
				rc.plantTree(direction);
				
				MapLocation location = rc.getLocation().add(direction, GameConstants.BULLET_TREE_RADIUS+rc.getType().bodyRadius);
				
				TreeInfo info = rc.senseTreeAtLocation(location);

				/* Replace element currently there with info */
				treeArray.set(index, info);
				
				return true;
			} 
			return false;
		}
		
		private Boolean tryWater(int index) throws GameActionException{
			
			TreeInfo tree = treeArray.get(index);
			
			if (!tree.equals(null) && rc.canWater(tree.getID())) {
				rc.water(tree.getID());
				return true;
			}
			
			return false;
		}

	}	





}