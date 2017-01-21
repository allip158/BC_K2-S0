package k2so;
import battlecode.common.*;


public class Lumberjack extends DefaultRobot{
    
    public Lumberjack(RobotController rc) throws GameActionException {
        super(rc);
    }
    
    @Override
    public void executeTurn() throws GameActionException {
        Team enemy = rc.getTeam().opponent();
        
        try {

            // See if there are any enemy robots within striking range (distance 1 from lumberjack's radius)
            RobotInfo[] robots = rc.senseNearbyRobots();
            TreeInfo[] trees = rc.senseNearbyTrees();
            BulletInfo[] bullets = rc.senseNearbyBullets();
            
        } catch (Exception e) {
            System.out.println("Lumberjack Exception");
            e.printStackTrace();
        }
    }

    private void shakeThenChop(TreeInfo[] neutralTrees) throws GameActionException{
        
        TreeInfo tree = neutralTrees[0];
        MapLocation treeLocation = tree.getLocation();
        
        if (rc.canShake() && tree.containedBullets > 0) {
            
            rc.shake(treeLocation);
            
        } else if(rc.canChop(treeLocation) && tree.containedRobot != null) {
        
            rc.chop(treeLocation);
        } else {
            tryMove(randomDirection());
        }
        
    }
    
}
