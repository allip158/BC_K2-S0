package k2so;
import battlecode.common.*;

import java.util.Arrays;
import java.util.List;
import java.util.function.*;

/**
 * Created by Andrey on 1/13/2017.
 */
public class PotentialMap {

    private final int NUM_CELLS = 9;
    private PotentialCell[][] map;
    private PotentialCell lastComputedCell;
    private PotentialCell startingCell;
    private float cellSize;


    private RobotInfo[] robots;
    private BulletInfo bullets;
    private TreeInfo[] trees;

    private BiFunction<RobotInfo, MapLocation, Double> robotPotentialFunction;
    private BiFunction<BulletInfo, MapLocation, Double> bulletPotentialFunction;
    private BiFunction<TreeInfo, MapLocation, Double> treePotentialFunction;



    /**
     *
     * @param mapSize
     * @param robots
     * @param bullets
     * @param trees
     * @param rc
     */

    public PotentialMap(int mapSize, RobotInfo[] robots, BulletInfo bullets, TreeInfo[] trees, RobotController rc,
                        BiFunction<RobotInfo, MapLocation, Double> robotPotentialFunction,
                        BiFunction<BulletInfo, MapLocation, Double> bulletPotentialFunction,
                        BiFunction<TreeInfo, MapLocation, Double> treePotentialFunction) {
        cellSize = (rc.getType().strideRadius*2) / ((float) NUM_CELLS);
        map = new PotentialCell[NUM_CELLS][NUM_CELLS];
        startingCell = PotentialCell
    }

    public PotentialCell getStartingCell () {
        return startingCell;
    }


    public List<PotentialCell> getAllSurroundingCells(int xOffset, int yOffset){
        return null;
    }

    public void updateCellPotential(PotentialCell cell, int xOffset, int yOffset) {
        double currentPotential = 0.0;
        float x =
        MapLocation cellLocation = new MapLocation(cell.getX(), cell.getY());
        currentPotential += Arrays.asList(robots).stream().mapToDouble(t -> robotPotentialFunction.apply(t, cellLocation)).sum();
        currentPotential += Arrays.asList(bullets).stream().mapToDouble(t -> bulletPotentialFunction.apply(t, cellLocation)).sum();
        currentPotential += Arrays.asList(trees).stream().mapToDouble(t -> treePotentialFunction.apply(t, cellLocation)).sum();

        cell = new PotentialCell(x, y, currentPotential);
    }


    private class PotentialCell {
        private final float x;
        private final float y;
        private double potentialValue;

        public PotentialCell(float x, float y, double val) {
            this.x = x;
            this.y = y;
            this.potentialValue = val;
        }

        public float getX() {
            return x;
        }

        public float getY() {
            return y;
        }

        public double getValue() {
            return potentialValue;
        }
    }
}
