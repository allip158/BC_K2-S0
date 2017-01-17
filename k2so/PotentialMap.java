package k2so;
import battlecode.common.*;


import java.util.Arrays;
import java.util.List;
import java.util.function.*;

/**
 * Created by Andrey on 1/13/2017.
 */
public class PotentialMap {

    private final int NUM_CELLS = 9; //NUM_CELLS has to be and odd number
    private PotentialCell[][] map;
    private PotentialCell lastComputedCell;
    private PotentialCell startingCell; //this represents center of the map(i.e. robots starting point)
    private float cellSize;

    private BodyInfo[] gameObjects;//array of all objects in the robot's vision

    private BiFunction<BodyInfo[], MapLocation, Double> objectsPotentialFunction;


    /**
     * TODO
     * @param mapSize
     * @param robots
     * @param bullets
     * @param trees
     * @param rc
     */

    public PotentialMap(int mapSize, TreeInfo[] trees, RobotInfo[] robots, BulletInfo[] bullets, RobotController rc,
                        BiFunction<BodyInfo[], MapLocation, Double> objectsPotentialFunction) {


        this.gameObjects = Utils.concatenateAllObjects(trees, robots, bullets);

        this.objectsPotentialFunction = objectsPotentialFunction;

        cellSize = (rc.getType().strideRadius*2) / ((float) NUM_CELLS);
        map = new PotentialCell[NUM_CELLS][NUM_CELLS];
        startingCell = new PotentialCell(rc.getLocation(), (NUM_CELLS + 1)/2, (NUM_CELLS + 1)/2);
        updateCellPotential(startingCell);
    }

    public PotentialCell getStartingCell () {
        return startingCell;
    }


    public List<PotentialCell> getAllSurroundingCells(int xOffset, int yOffset){
        // TODO
        return null;
    }



    public void updateCellPotential(PotentialCell cell) {
        if(cell == null) {
            //technically and exception should be thrown here but i'll leave it just for debugging purposes
            System.out.print("cannot update cell with unknown coordinates");
            return;
        }
        updateCellPotential(cell.getX(), cell.getY());
    }

    /**
     * Updates potential of a cell given its coordinates in the map.
     * This method assumes that cell has already been initialized
     *
     * @param xOffset x coordinate in the map
     * @param yOffset y coordinate in the map
     */
    public void updateCellPotential(int xOffset, int yOffset) {
        PotentialCell cell = map[xOffset][yOffset];
        if(cell.isPotentialSet()) {
            System.out.println("Trying to update an potential cell that has already been computed");
            return;
        }

        MapLocation cellLocation = cell.getLocation();
        double potentialValue = objectsPotentialFunction.apply(gameObjects, cellLocation);
        //potentialValue += Arrays.asList(gameObjects).stream().mapToDouble(t -> objectsPotentialFunction.apply(t, cellLocation)).sum();


        cell.setPotentialValue(potentialValue);
    }


    private MapLocation getCellLocationByMapCoordinates(int xOffset, int yOffset) {
        //offset from the starting location
        int xOffsetToStart = xOffset - startingCell.getX();
        int yOffsetToStart = yOffset - startingCell.getY();
        return new MapLocation()
    }


    private class PotentialCell {

        private MapLocation location;
        private final int x;
        private final int y;
        private double potentialValue;
        private boolean isPotentialSet;

        public PotentialCell(MapLocation location, int x, int y) {
            this.location = location;
            this.x = x;
            this.y = y;
            this.isPotentialSet = false;

        }

        public int getX() {
            return x;
        }

        public int getY() {
            return y;
        }

        public MapLocation getLocation() {
            return location;
        }

        public double getPotentialValue() {
            return potentialValue;
        }

        public boolean isPotentialSet() { return isPotentialSet; }

        public void setPotentialValue(double value) {
            potentialValue = value;
            isPotentialSet = true;
        }
    }
}
