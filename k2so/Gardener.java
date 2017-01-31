package k2so;

import battlecode.common.*;

import java.util.List;
import java.util.Arrays;


import battlecode.common.*;
import scala.collection.immutable.Stream;

public class Gardener extends DefaultRobot {
	
	Trees trees;
	Boolean isBaby = true;
    private PotentialFunction potentialFunction;

	public Gardener(RobotController rc) throws GameActionException {
		super(rc);
		this.trees = new Trees();
        potentialFunction = getPotentialFunction();
	}

	@Override
	public void executeTurn() throws GameActionException {

		try {

			/* The ratio of gardeners to builders */
//			if (rc.getID() % (Constants.RATIO_GARDENERS_TO_BUILDERS+1) < Constants.RATIO_GARDENERS_TO_BUILDERS) {
//				trees.garden();
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
//			} else {
//
//				// Generate a random direction
//				Direction dir = Utils.randomDirection();
//				buildRobot(dir);
//
//			}

            if(rc.senseNearbyTrees(rc.getType().sensorRadius, Team.NEUTRAL).length > 5){
                buildLumberjack();
            }

            if(turnsAtLastLocation > 5 && isBaby) {
                moveRandomly();
            }

            if(isBaby) {
                RobotInfo[] nearbyRobots = rc.senseNearbyRobots();
                TreeInfo[] nearbyTrees = rc.senseNearbyTrees();
                BulletInfo[] nearbyBullets = rc.senseNearbyBullets();

                PathFinder pathFinder = new PotentialPathFinder(nearbyTrees, nearbyRobots, nearbyBullets, rc, potentialFunction, null, null);
                MapLocation locationToMove = pathFinder.getDestinationLocation();

                if (rc.canMove(locationToMove)) {
                    rc.move(locationToMove);
                    System.out.println("Moving to " + locationToMove.x + " " + locationToMove.y);
                } else {
                    System.out.println("Can't move to " + locationToMove.x + " " + locationToMove.y);
                }
                if(rc.getTeamBullets() > Constants.MIN_BULLETS_TO_BUILD_LUMBERJACK) {
                    if(nearbyBullets.length + nearbyRobots.length + nearbyTrees.length < 10) {
                        buildLumberjack();
                    }
				}
                if(hasEnoughRoom() && !nearEdge()) {
                    isBaby = false;
                    trees.garden();
                }
            } else {
                trees.garden();
            }


		} catch (Exception e) {
			System.out.println("Gardener Exception");
			e.printStackTrace();
		}
	}

	private boolean hasEnoughRoom() {
	
		TreeInfo[] nearbyTrees = rc.senseNearbyTrees(Constants.MIN_PLANTING_RADIUS);
		RobotInfo[] nearbyRobots = rc.senseNearbyRobots(Constants.MIN_PLANTING_RADIUS);
		if (nearbyTrees.length == 0 && nearbyRobots.length == 0) {
			return true;
		} 
		
		return false;
	}

    public boolean nearEdge() throws GameActionException{
        Direction dir = Direction.NORTH;
        for(int i = 0; i < 4; i++) {
            dir = dir.rotateLeftDegrees(90);
            MapLocation loc = rc.getLocation().add(dir, Constants.MIN_PLANTING_RADIUS);
            if (!rc.onTheMap(loc)){
                return true;
            }
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
	 *
	 * @throws GameActionException
	 */
	private void buildLumberjack() throws GameActionException{
        Direction dir = Utils.randomDirection();
        for(int i=0; i < 36; i+=10) {
            float angleDegrees = i * 10;
            if (rc.canBuildRobot(RobotType.LUMBERJACK, dir.rotateLeftDegrees(angleDegrees))) {
                rc.buildRobot(RobotType.LUMBERJACK, dir.rotateLeftDegrees(angleDegrees));
                return;
            }
        }
	}
	
	private int getNumRobots(RobotType type) throws GameActionException {
		return rc.readBroadcastInt(Utils.getSignalFromRobotType(type));
	}
	
/*
 * 	Farming Methods
 */

public class Trees {

		public Tree[] treeArray;
		private Direction[] directions;
		private int NUM_TREES_AROUND_GARDENER = 6;
		
		public Trees() {
			
			this.treeArray = new Tree[NUM_TREES_AROUND_GARDENER];

			this.directions = new Direction[]{	Direction.getNorth().rotateLeftDegrees(30), 
												Direction.getNorth().rotateRightDegrees(30), 
												Direction.getEast(), 
												Direction.getSouth().rotateLeftDegrees(30), 
												Direction.getSouth().rotateRightDegrees(30), 
												Direction.getWest()};

            for(int i = 0; i < NUM_TREES_AROUND_GARDENER; i++) {
                treeArray[i] = new Tree(directions[i]);
            }
		}
		
		public void garden() throws GameActionException {

		    //Update tree states
            updateTreeArray();

			// Priority 1: plant
			int index = this.getAvailableDirectionIndex();
			if (index != -1) {
				this.tryPlant(index);
			
			// Priority 2: water lowest health tree
			}

            index = this.getLowestHealthTree();
            if (index != -1) {
                this.tryWater(index);
            }
		}

        public void updateTreeArray() throws GameActionException{
            for(Tree tree: treeArray) {
                TreeInfo sensedTree = rc.senseTreeAtLocation(getLocationFromDirection(tree.getDirection()));
                RobotInfo sensedRobot = rc.senseRobotAtLocation(getLocationFromDirection(tree.getDirection()));
                if((sensedTree != null && sensedTree.getTeam().equals(Team.NEUTRAL)) || sensedRobot != null) {
                    tree.setTreeState(TreeState.OCCUPIED);
                } else if(sensedTree != null && sensedTree.getTeam().equals(rc.getTeam())) {
                    tree.setTreeState(TreeState.PLANTED);
                    tree.setTreeInfo(sensedTree);
                } else if(sensedTree == null && sensedRobot == null) {
                    tree.setTreeState(TreeState.EMPTY);
                }
            }
        }

        public MapLocation getLocationFromDirection(Direction dir) {
            return rc.getLocation().add(dir, GameConstants.BULLET_TREE_RADIUS+rc.getType().bodyRadius);
        }

		/**
		 *  gets index in treeArray of lowest health tree or -1 if none
		 * 	@return int index
		 */
		private int getLowestHealthTree() throws GameActionException {
		    //initialize to the maximum possible health of a bullet tree
			float minimumHealth = GameConstants.BULLET_TREE_MAX_HEALTH;
			int indexOfMinHealthTree = -1;

			for (int i = 0; i < NUM_TREES_AROUND_GARDENER; i++) {
                if(treeArray[i].getTreeState() == TreeState.PLANTED) {
                    if(treeArray[i].getTreeInfo().getHealth() < minimumHealth) {
                        minimumHealth = treeArray[i].getTreeInfo().getHealth();
                        indexOfMinHealthTree = i;
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
				if (treeArray[i].getTreeState() == TreeState.EMPTY) {
					// tree does not exist and the location is empty
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
			
			Direction direction = treeArray[index].getDirection();
			
			if (rc.canPlantTree(direction)) {
				rc.plantTree(direction);
				return true;
			} 
			return false;
		}
		
		private Boolean tryWater(int index) throws GameActionException{
			
			TreeInfo tree = treeArray[index].getTreeInfo();
			
			if (!tree.equals(null) && rc.canWater(tree.getID())) {
				rc.water(tree.getID());
				return true;
			}
			
			return false;
		}

	}


    public enum TreeState {
        OCCUPIED, PLANTED, EMPTY, UNKNOWN
    }

    private class Tree {
        private TreeState treeState;
        private TreeInfo treeInfo;
        private Direction direction;

        public Tree(Direction dir) {
            this.treeState = TreeState.UNKNOWN;
            this.treeInfo = null;
            this.direction = dir;
        }

        public void setTreeState (TreeState state) {
            this.treeState = state;
        }

        public void setTreeInfo (TreeInfo treeInfo) {
            this.treeInfo = treeInfo;
        }

        public TreeState getTreeState() {
            return this.treeState;
        }

        public TreeInfo getTreeInfo() {
            return this.treeInfo;
        }

        public Direction getDirection() {
            return direction;
        }
    }

    private PotentialFunction getPotentialFunction() {
        return new PotentialFunction() {
            @Override
            public float apply(RobotInfo[] robots, TreeInfo[] trees, BulletInfo[] bullets, MapLocation location) {
                float potentialResult = 0.0f;
                float inverseDistance;
                for(RobotInfo robot: robots) {
                    if(robot.getTeam().equals(rc.getTeam()) &&
                            location.distanceTo(robot.getLocation()) > (rt.bodyRadius + robot.getType().bodyRadius) &&
                            robot.getType() != RobotType.GARDENER ) {
                        //do not consider a alli robots outside of stride radius
                        continue;
                    }
                    inverseDistance = 1.0f/location.distanceTo(robot.getLocation());
                    if (robot.getTeam().equals(enemy)) {
                        potentialResult += (500.0 * inverseDistance);
                    } else {
                        potentialResult += (50.0 * inverseDistance);
                    }
                }
                for(TreeInfo tree: trees) {
                    if(tree.getTeam().equals(Team.NEUTRAL) &&  location.distanceTo(tree.getLocation()) > rt.bodyRadius + tree.getRadius())
                        continue;
                    if(tree.getTeam().equals(Team.NEUTRAL) &&  location.distanceTo(tree.getLocation()) > rt.bodyRadius + tree.getRadius())
                        continue;
                    inverseDistance = 1.0f/location.distanceTo(tree.getLocation());
                    if(tree.getTeam().equals(enemy)) {
                        potentialResult += (500.0 * inverseDistance);
                    } else if(tree.getTeam().equals(Team.NEUTRAL)) {
                        potentialResult += (50.0 * inverseDistance);
                    } else {
                        potentialResult += (100.0 * inverseDistance);
                    }
                }
//                for(BulletInfo bullet: bullets) {
//                    potentialResult += Utils.getBulletPotential(bullet, location);
//                }
                return potentialResult;
            }
        };
    }
}