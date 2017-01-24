package k2so;
import java.util.List;
import java.util.Arrays;

import battlecode.common.*;
// TODO: Update tree status

public class Gardener extends DefaultRobot{

	static int numScoutsLeft = Constants.NUM_SCOUTS;
	Trees trees;

	public Gardener(RobotController rc) throws GameActionException {
		super(rc);
		this.trees = new Trees();
	}

	@Override
	public void executeTurn() throws GameActionException {

		try {

			trees.garden();

			//			// Listen for home archon's location
			//			int xPos = rc.readBroadcast(0);
			//			int yPos = rc.readBroadcast(1);
			//			MapLocation archonLoc = new MapLocation(xPos,yPos);
			//			
			//			 // Generate a random direction
			//            Direction dir = randomDirection();
			//
			//            // Build the set number of scouts
			//			while (numScoutsLeft > 0) {
			//				if (rc.canBuildRobot(RobotType.SCOUT, dir)) {
			//					rc.buildRobot(RobotType.SCOUT, dir);
			//					numScoutsLeft--;
			//				} else {
			//					tryMove(randomDirection());
			//				}
			//			}
			//            
			//            // Randomly attempt to build a soldier or lumberjack in this direction
			//            if (rc.canBuildRobot(RobotType.SOLDIER, dir) && Math.random() < .01 && rc.isBuildReady()) {
			//                rc.buildRobot(RobotType.SOLDIER, dir);
			//            } else if (rc.canBuildRobot(RobotType.LUMBERJACK, dir) && Math.random() < .01 && rc.isBuildReady()) {
			//                rc.buildRobot(RobotType.LUMBERJACK, dir);
			//            } else if (rc.canBuildRobot(RobotType.TANK, dir) && Math.random() < .001 && rc.isBuildReady()) {
			//            	rc.buildRobot(RobotType.TANK, dir);
			//            }
			//
			//            // Move randomly
			//            tryMove(randomDirection());


		} catch (Exception e) {
			System.out.println("Gardener Exception");
			e.printStackTrace();
		}
	}

	/*
	 * 	Farming Methods
	 */

	public class Trees {

		public List<TreeInfo> treeArray;
		private Direction[] directions;
		private int NUM_TREES_AROUND_GARDENER = 6;
		
		public Trees() {
//			this.treeArray = new ArrayList<TreeInfo>(NUM_TREES_AROUND_GARDENER);
			
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
				System.out.println("Watering at " + index);
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
			
			System.out.println(indexOfMinHealthTree);
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
//				System.err.println(location.x + " " + location.y);
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