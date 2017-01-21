package k2so;

import battlecode.common.MapLocation;
import battlecode.common.RobotController;

/**
 * Created by Andrey on 1/20/2017.
 */
public class PotentialMapPathFinder {
    private PotentialMap map;
    private RobotController rc;
    private PotentialMap.PotentialCell currentPotentialMapLocation;
    private double pathTraveledSoFar;
    private final double MAX_TRAVEL_DISTANCE; //equal to the stride length of the robot 

    public PotentialMapPathFinder(PotentialMap map, RobotController rc) {
        this.map = map;
        this.rc = rc;
        currentPotentialMapLocation = map.getStartingCell();
        pathTraveledSoFar = 0.0;
        MAX_TRAVEL_DISTANCE = rc.getType().strideRadius;
    }

    private PotentialMap.PotentialCell getNextBestCell() {
        PotentialMap.PotentialCell nextCell = currentMapLocation;
        for(PotentialMap.PotentialCell cell: map.getAllSurroundingCells(currentPotentialMapLocation.getX(), currentPotentialMapLocation.getY())) {
            if(map.updateCellPotential(cell) > nextCell.getPotentialValue()){
                nextCell = cell;
            }
        }
        return nextCell;
    }


    public MapLocation getDestinationLocation() {
        PotentialMap.PotentialCell destination = currentPotentialMapLocation;
        PotentialMap.PotentialCell nextCell;
        while(true) {
            nextCell = getNextBestCell();
            if(destination == nextCell) {
                break; 
            }
            float distanceToNextCell = destination.getDistanceToCell(nextCell);
            if(distanceToNextCell + pathTraveledSoFar > MAX_TRAVEL_DISTANCE) {
                break; 
            } else {
                destination = nextCell;
                this.pathTraveledSoFar += distanceToNextCell; 
            }
        }
        return destination.getLocation();
    }

}
