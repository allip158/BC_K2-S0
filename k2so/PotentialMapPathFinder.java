package k2so;

import battlecode.common.MapLocation;
import battlecode.common.RobotController;

/**
 * Created by Andrey on 1/20/2017.
 */
public class PotentialMapPathFinder {
    private PotentialMap map;
    private RobotController rc;
    private PotentialMap.PotentialCell currentMapLocation;
    private double

    public PotentialMapPathFinder(PotentialMap map, RobotController rc) {
        this.map = map;
        this.rc = rc;
        currentMapLocation = map.getStartingCell();

    }

    private PotentialMap.PotentialCell getNextBestCell() {
        PotentialMap.PotentialCell nextCell = currentMapLocation;
        for (PotentialMap.PotentialCell cell: map.getAllSurroundingCells(currentMapLocation.getX(), currentMapLocation.getY())) {
            if(map.updateCellPotential(cell) > nextCell.getPotentialValue()){
                nextCell = cell;
            }
        }
        return nextCell;
    }


    public MapLocation getDestinationLocation() {

    }

}
