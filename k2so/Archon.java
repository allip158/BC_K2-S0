package k2so;
import battlecode.common.*;


public class Archon extends DefaultRobot{
    private PotentialFunction potentialFunction;

	public Archon(RobotController rc) throws GameActionException {
		super(rc);

        potentialFunction = getPotentialFunction();
	}
	
	@Override
	public void executeTurn() throws GameActionException {

		try{
						
			// Initialize array at beginning
			if (!isInitialized()) {
				initializeSignalArray();
			}

			RobotInfo[] robots = rc.senseNearbyRobots();

			if(shouldHireGardener(robots)) {
                tryToHireGardner();
			}

//			if (rc.canHireGardener(dir) && Math.random() < .01) {
//				rc.hireGardener(dir);
//				//tryToHireGardner();
//			}

			// Move randomly
//			tryMove(Utils.randomDirection());
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


			// Broadcast archon's location for other robots on the team to know
			MapLocation myLocation = rc.getLocation();
			rc.broadcast(0,(int)myLocation.x);
			rc.broadcast(1,(int)myLocation.y);

		} catch (Exception e) {
			System.out.println("Archon Exception");
			e.printStackTrace();
		}

	}

	private boolean shouldHireGardener(RobotInfo[] neighbourRobots) {
		int numGardenersAround = 0;
		for(RobotInfo robot: neighbourRobots) {
            if(robot.getType() == RobotType.GARDENER && robot.getTeam() == rc.getTeam()) {
                numGardenersAround++;
            }
        }
        return numGardenersAround <= Constants.MIN_NUM_OF_GARDENERS_TO_STOP_BUILDING;
	}

	private void tryToHireGardner() throws GameActionException{
		Direction dir = Utils.randomDirection();
		for(int i=0; i < 36; i+=10) {
			float angleDegrees = i * 10;
			if (rc.canHireGardener(dir.rotateLeftDegrees(angleDegrees))) {
				rc.hireGardener(dir.rotateLeftDegrees(angleDegrees));
				return;
			}
		}
	}

	private boolean isInitialized() throws GameActionException {
		
		return rc.readBroadcastBoolean(Constants.INITIALIZATION_CHANNEL);
	}

	private void initializeSignalArray() throws GameActionException {
		
		rc.broadcastInt(Utils.getSignalFromRobotType(RobotType.SCOUT), Constants.NUM_SCOUT);
		rc.broadcastInt(Utils.getSignalFromRobotType(RobotType.SOLDIER), Constants.NUM_SOLDIER);
		rc.broadcastInt(Utils.getSignalFromRobotType(RobotType.LUMBERJACK), Constants.NUM_LUMBERJACK);
		rc.broadcastInt(Utils.getSignalFromRobotType(RobotType.TANK), Constants.NUM_TANK);
		rc.broadcastInt(Utils.getSignalFromRobotType(RobotType.GARDENER), Constants.NUM_GARDENER);
		
		rc.broadcastBoolean(Constants.INITIALIZATION_CHANNEL, true);
	}


    private PotentialFunction getPotentialFunction() {
        return new PotentialFunction() {
            @Override
            public float apply(RobotInfo[] robots, TreeInfo[] trees, BulletInfo[] bullets, MapLocation location) {
                float potentialResult = 0.0f;
                float inverseDistance;
                for(RobotInfo robot: robots) {
                    inverseDistance = 1.0f/location.distanceTo(robot.getLocation());
                    if (robot.getTeam().equals(enemy)) {
                        potentialResult += (1000.0 * inverseDistance);
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