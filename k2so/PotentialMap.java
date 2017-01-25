package k2so;
import battlecode.common.*;


import java.util.ArrayList;
import java.util.List;
import java.util.function.*;

/**
 * Created by Andrey on 1/13/2017.
 */
public class PotentialMap {

    private final int NUM_CELLS = 3; //NUM_CELLS has to be and odd number
    private PotentialCell[][] map;
    //private PotentialCell lastComputedCell;
    private PotentialCell startingCell; //this represents center of the map(i.e. robots starting point)
    private float cellSize;

    private BodyInfo[] gameObjects;//array of all objects in the robot's vision

    private BiFunction<BodyInfo[], MapLocation, Double> objectsPotentialFunction;
    private BiFunction<MapLocation, MapLocation, Double> pointPotential;
    private MapLocation attractingPoint;
    /**
     * TODO
     * @param robots
     * @param bullets
     * @param trees
     * @param rc
     */

    public PotentialMap(TreeInfo[] trees, RobotInfo[] robots, BulletInfo[] bullets, RobotController rc,
                        BiFunction<BodyInfo[], MapLocation, Double> objectsPotentialFunction, BiFunction<MapLocation, MapLocation, Double> pointPotential, MapLocation attractingPoint) {

        this.gameObjects = Utils.concatenateAllObjects(trees, robots, bullets);
        this.objectsPotentialFunction = objectsPotentialFunction;
        this.pointPotential = pointPotential;
        this.attractingPoint = attractingPoint;

        cellSize = (rc.getType().strideRadius*2) / ((float) NUM_CELLS);
        map = new PotentialCell[NUM_CELLS][NUM_CELLS];
        startingCell = new PotentialCell(rc.getLocation(), (NUM_CELLS - 1)/2, (NUM_CELLS - 1)/2);
        initializeAllMapCells();
        updateCellPotential(startingCell);
        map[(NUM_CELLS - 1)/2][(NUM_CELLS - 1)/2] = startingCell;
    }

    private void initializeAllMapCells() {
        for(int x = 0; x < NUM_CELLS; x++) {
            for(int y = 0; y < NUM_CELLS; y++) {
                map[x][y] = new PotentialCell(getMapLocationByCellCoordinates(x, y), x, y);
            }
        }
    }


    public PotentialCell getStartingCell () {
        return startingCell;
    }

    /**
     * Returns all the cells surrounding a current location the the potential map
     * @param xOffset x-coordinate in the potential map
     * @param yOffset y-coordinate in the potential map
     * @return
     */
    public List<PotentialCell> getAllSurroundingCells(int xOffset, int yOffset){
        List<PotentialCell> surroundingCells = new ArrayList<>();
        for(int x = -1; x <= 1; x++) {
            for(int y = -1; y <= 1; y++) {
                if (x == 0 && y == 0 ) {
                    continue;
                }

                int newCellXOffset = xOffset + x;
                int newCellYOffset = yOffset + y;
                if (newCellXOffset < 0 || newCellXOffset >= NUM_CELLS || newCellYOffset < 0 || newCellYOffset >= NUM_CELLS) {
                    continue;
                }
                surroundingCells.add(map[newCellXOffset][newCellYOffset]);
            }
        }
        return surroundingCells;
    }

    public double updateCellPotential(PotentialCell cell) {
        if(cell == null) {
            //technically an exception should be thrown here but i'll leave it just for debugging purposes
            System.out.print("cannot update cell with unknown coordinates");
            return -1;
        }
        return updateCellPotential(cell.getX(), cell.getY());
    }

    /**
     * Updates potential of a cell given its coordinates in the map and/or returns the potential value
     * This method assumes that cell has already been initialized
     *
     * @param xOffset x coordinate in the map
     * @param yOffset y coordinate in the map
     */
    public double updateCellPotential(int xOffset, int yOffset) {
        PotentialCell cell = map[xOffset][yOffset];
        if(cell.isPotentialSet()) {
            System.out.println("Trying to update a potential cell that has already been computed");
            return cell.getPotentialValue();
        }
        System.out.println("Computing cell's potential");
        MapLocation cellLocation = cell.getLocation();
        double potentialValue = objectsPotentialFunction.apply(gameObjects, cellLocation);
        potentialValue += pointPotential.apply(attractingPoint, cellLocation);
        //potentialValue += Arrays.asList(gameObjects).stream().mapToDouble(t -> objectsPotentialFunction.apply(t, cellLocation)).sum();

        map[xOffset][yOffset].setPotentialValue(potentialValue);
        return potentialValue;
    }


    private MapLocation getMapLocationByCellCoordinates(int xOffset, int yOffset) {
        //offset from the starting location
        int xOffsetToStart = xOffset - startingCell.getX();
        int yOffsetToStart = yOffset - startingCell.getY();
        return new MapLocation(startingCell.getLocation().x + cellSize * xOffsetToStart, startingCell.getLocation().y + cellSize * yOffsetToStart);
    }


    public class PotentialCell {

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

        public float getDistanceToCell(PotentialCell cell){
            return location.distanceTo(cell.getLocation());
        }
    }
}
