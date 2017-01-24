package k2so;
import battlecode.common.*;

import java.util.function.BiFunction;


public class Lumberjack extends DefaultRobot{
    
    public Lumberjack(RobotController rc) throws GameActionException {
        super(rc);
    }
    
    @Override
    public void executeTurn() throws GameActionException {

        
        try {
            System.out.println("This is only printed once");
            // See if there are any enemy robots within striking range (distance 1 from lumberjack's radius)
            RobotInfo[] robots = rc.senseNearbyRobots();
            TreeInfo[] trees = rc.senseNearbyTrees();
            BulletInfo[] bullets = rc.senseNearbyBullets();

            PotentialMap potentialMap = new PotentialMap(trees, robots, bullets, rc, this.getPotentialFunction());
            PotentialMapPathFinder pathFinder = new PotentialMapPathFinder(potentialMap, rc);
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
        } catch (Exception e) {
            System.out.println("Lumberjack Exception");
            e.printStackTrace();
        }
    }

    private BiFunction<BodyInfo[], MapLocation, Double> getPotentialFunction() {
        return new BiFunction<BodyInfo[], MapLocation, Double>() {
            @Override
            public Double apply(BodyInfo[] bodyInfos, MapLocation mapLocation) {
                double potentialResult = 0.0;
                for(BodyInfo object: bodyInfos) {
                    double inverseDistance = 1.0/mapLocation.distanceTo(object.getLocation());
                    if(object.isBullet()) {
                        BulletInfo bulletObject = (BulletInfo) object;
                        potentialResult += RobotUtils.getBulletPotential(bulletObject, mapLocation);
                    }
                    if(object.isRobot()) {
                        RobotInfo robotObject = (RobotInfo) object;
                        if(robotObject.getTeam().equals(enemy)) {
                            potentialResult += (-10.0 - 50.0 * inverseDistance);
                        } else {
                            potentialResult += (1.0 + 1.0 * inverseDistance);
                        }
                    }
                    if(object.isTree()) {
                        TreeInfo treeObject = (TreeInfo) object;
                        if(treeObject.getTeam().equals(enemy)) {
                            potentialResult += (-2.0 - 5.0 * inverseDistance);
                        } else if(treeObject.getTeam().equals(Team.NEUTRAL)) {
                            potentialResult += (-10.0 - 10.0 * inverseDistance * treeObject.getContainedBullets());
                            potentialResult += (-1.0 * RobotUtils.getRobotValue(treeObject.getContainedRobot()) * inverseDistance);
                        } else {
                            potentialResult += (1.0 + 1.0 * inverseDistance);
                        }
                    }
                }
                return potentialResult;
            }
        };
    }

    private boolean shouldStrike() {
        RobotInfo[] enemyRobots = rc.senseNearbyRobots(GameConstants.LUMBERJACK_STRIKE_RADIUS, enemy);
        RobotInfo[] alleyRobots = rc.senseNearbyRobots(GameConstants.LUMBERJACK_STRIKE_RADIUS, rc.getTeam());
        return enemyRobots.length > alleyRobots.length;
    }


    private void shakeThenChop(TreeInfo[] neutralTrees) throws GameActionException{
        
        TreeInfo tree = neutralTrees[0];
        MapLocation treeLocation = tree.getLocation();
        
        if (rc.canShake() && tree.containedBullets > 0) {
            
            rc.shake(treeLocation);
            
        } else if(rc.canChop(treeLocation) && tree.containedRobot != null) {
        
            rc.chop(treeLocation);
        } else {
            tryMove(RobotUtils.randomDirection());
        }
        
    }
    
}
