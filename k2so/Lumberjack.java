package k2so;
import battlecode.common.*;

import java.util.function.BiFunction;


public class Lumberjack extends DefaultRobot{
    private MapLocation attractionPoint;
    private PotentialFunction potentialFunction;

    public Lumberjack(RobotController rc) throws GameActionException {
        super(rc);
        MapLocation[] archonLocations = rc.getInitialArchonLocations(enemy);
        System.out.println("There are " + archonLocations.length + " enemy archons");
        attractionPoint = archonLocations[rand.nextInt(archonLocations.length)];
        potentialFunction = getPotentialFunction();
    }
    
    @Override
    public void executeTurn() throws GameActionException {

        
        try {
            System.out.println("This is only printed once");
            // See if there are any enemy robots within striking range (distance 1 from lumberjack's radius)
            RobotInfo[] robots = rc.senseNearbyRobots();
            TreeInfo[] trees = rc.senseNearbyTrees();
            BulletInfo[] bullets = rc.senseNearbyBullets();

            //PotentialMap potentialMap = new PotentialMap(trees, robots, bullets, rc, potentialFunction, (attractLoc, cellLoc) -> -20*(1.0/cellLoc.distanceSquaredTo(attractLoc)), attractionPoint);
            //PathFinder pathFinder = new PotentialMapPathFinder(potentialMap, rc);
            if(turnsAtLastLocation > 5 && rc.senseNearbyTrees(GameConstants.INTERACTION_DIST_FROM_EDGE + rt.bodyRadius, Team.NEUTRAL).length == 0) {
                moveRandomly();
            }

            PathFinder pathFinder = new PotentialPathFinder(trees, robots, bullets, rc, potentialFunction,
                    (attractLoc, cellLoc) -> -20.0*(1.0/cellLoc.distanceSquaredTo(attractLoc)), attractionPoint);
            MapLocation locationToMove = pathFinder.getDestinationLocation();
            if (rc.canMove(locationToMove)) {
                rc.move(locationToMove);
                System.out.println("Moving to " + locationToMove.x + " " + locationToMove.y);
            } else {
                System.out.println("Can't move to " + locationToMove.x + " " + locationToMove.y);
            }
            if(rc.canStrike() && shouldStrike()) {
                rc.strike();
            }
            TreeInfo[] neutralTrees = rc.senseNearbyTrees(rt.sensorRadius, Team.NEUTRAL);
            if(neutralTrees.length > 0) {
                shakeThenChop(neutralTrees);
            }
        } catch (Exception e) {
            System.out.println("Lumberjack Exception");
            e.printStackTrace();
        }
    }

    private PotentialFunction getPotentialFunction() {
        return new PotentialFunction() {
            @Override
            public float apply(RobotInfo[] robots, TreeInfo[] trees, BulletInfo[] bullets, MapLocation location) {
                float potentialResult = 0.0f;
                float inverseDistance;
                for(RobotInfo robot: robots) {
                    if(robot.getTeam().equals(rc.getTeam()) && location.distanceTo(robot.getLocation()) > (rt.bodyRadius + robot.getType().bodyRadius)) {
                        //do not consider a alli robots outside of stride radius
                        continue;
                    }
                    if(robot.getTeam() == rc.getTeam() && robot.getType() == RobotType.LUMBERJACK) {
                        inverseDistance = 1.0f/location.distanceTo(robot.getLocation());
                        potentialResult += (50.0 * inverseDistance);
                        continue;
                    }
                    inverseDistance = 1.0f/location.distanceTo(robot.getLocation());
                    if (robot.getTeam().equals(enemy)) {
                        potentialResult += (-1000.0 * inverseDistance);
                    } else {
                        potentialResult += (100.0 * inverseDistance);
                    }
                }
                for(TreeInfo tree: trees) {
                    if(tree.getTeam().equals(Team.NEUTRAL) && tree.getContainedRobot() == null && tree.getContainedBullets() == 0)
                        continue;
                    if(tree.getTeam().equals(rc.getTeam()) &&  location.distanceTo(tree.getLocation()) > (rt.bodyRadius + tree.getRadius()))
                        continue;
                    inverseDistance = 1.0f/location.distanceTo(tree.getLocation());
                    if(tree.getTeam().equals(enemy)) {
                        potentialResult += (-5.0 * inverseDistance);
                    } else if(tree.getTeam().equals(Team.NEUTRAL)) {
                        potentialResult += (-10.0 * inverseDistance * tree.getContainedBullets());
                        potentialResult += (-10.0 * Utils.getRobotValue(tree.getContainedRobot()) * inverseDistance);
                    } else {
                        potentialResult += (50.0 * inverseDistance);
                    }
                }
                for(BulletInfo bullet: bullets) {
                    potentialResult += Utils.getBulletPotential(bullet, location);
                }
                return potentialResult;
            }
        };
    }

    private boolean shouldStrike() {
        RobotInfo[] enemyRobots = rc.senseNearbyRobots(GameConstants.LUMBERJACK_STRIKE_RADIUS, enemy);
//        RobotInfo[] alleyRobots = rc.senseNearbyRobots(GameConstants.LUMBERJACK_STRIKE_RADIUS, rc.getTeam());
//        TreeInfo[] enemyTrees = rc.senseNearbyTrees(GameConstants.LUMBERJACK_STRIKE_RADIUS, enemy);
        return enemyRobots.length > 0;
    }


    private void shakeThenChop(TreeInfo[] neutralTrees) throws GameActionException {
        //TODO make shaking a priority!
        TreeInfo closestTree = (TreeInfo) Utils.findClosestBody(neutralTrees, rc.getLocation());
        MapLocation treeLocation = closestTree.getLocation();
        if (rc.canShake(treeLocation) && closestTree.containedBullets > 0) {
            rc.shake(treeLocation);
        } else if (rc.canChop(treeLocation)) {
            rc.chop(treeLocation);
        }
    }
}
