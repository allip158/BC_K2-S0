package k2so;

import battlecode.common.*;

import java.util.function.BiFunction;

/**
 * This is a different implementation of the potential path finding designed to reduce bytecode usage
 * Created by Andrey on 1/26/2017.
 */
public class PotentialPathFinder implements PathFinder {
    private TreeInfo[] trees;
    private RobotInfo[] robots;
    private BulletInfo[] bullets;

    private MapLocation startingMapLocation;
    private float cellSize;

    private PotentialFunction objectsPotentialFunction;
    private BiFunction<MapLocation, MapLocation, Double> pointPotential;
    private MapLocation attractingPoint;

    private int NUM_CELLS = 3;



    public PotentialPathFinder(TreeInfo[] trees, RobotInfo[] robots, BulletInfo[] bullets, RobotController rc,
                               PotentialFunction objectsPotentialFunction, BiFunction<MapLocation, MapLocation, Double> pointPotential, MapLocation attractingPoint) {
        this.objectsPotentialFunction = objectsPotentialFunction;
        this.pointPotential = pointPotential;
        this.attractingPoint = attractingPoint;

        startingMapLocation = rc.getLocation();
        cellSize = (rc.getType().strideRadius*2) / ((float) NUM_CELLS);

        this.trees = trees;
        this.robots = robots;
        this.bullets = bullets;
    }

    @Override
    public MapLocation getDestinationLocation() {
        MapLocation locWithLowestPotential, nextMapLoc;
        float lowestPotential, nextPotential;
        locWithLowestPotential = null;
        lowestPotential = Float.MAX_VALUE;
        for(int i = 0; i < NUM_CELLS; i++) {
            for(int j = 0; j < NUM_CELLS; j++) {
                nextMapLoc = new MapLocation(startingMapLocation.x + cellSize * (i - (NUM_CELLS - 1)/2),
                        startingMapLocation.y + cellSize * (j - (NUM_CELLS - 1)/2));
                nextPotential = objectsPotentialFunction.apply(robots, trees, bullets, nextMapLoc);
                nextPotential += pointPotential.apply(attractingPoint, nextMapLoc);
                if(nextPotential < lowestPotential) {
                    lowestPotential = nextPotential;
                    locWithLowestPotential = nextMapLoc;
                }
            }
        }
        return locWithLowestPotential;
    }
}
