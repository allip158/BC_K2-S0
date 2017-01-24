package k2so;

import battlecode.common.MapLocation;
import battlecode.common.RobotController;

/**
 * Created by Andrey on 1/20/2017.
 */
public class PotentialMapPathFinder {
    private PotentialMap map;
    private PotentialMap.PotentialCell currentPotentialMapLocation;
    private double pathTraveledSoFar;
    private final double MAX_TRAVEL_DISTANCE; //equal to the stride length of the robot 

    public PotentialMapPathFinder(PotentialMap map, RobotController rc) {
        this.map = map;
        currentPotentialMapLocation = map.getStartingCell();
        pathTraveledSoFar = 0.0;
        MAX_TRAVEL_DISTANCE = rc.getType().strideRadius;
    }

    private PotentialMap.PotentialCell getNextBestCell() {
        System.out.println("I'm stuck here!");
        System.out.println(map.updateCellPotential(currentPotentialMapLocation));
        PotentialMap.PotentialCell nextCell = currentPotentialMapLocation;
        for(PotentialMap.PotentialCell cell: map.getAllSurroundingCells(currentPotentialMapLocation.getX(), currentPotentialMapLocation.getY())) {
            //go to the cell with lower potential
            System.out.println(map.updateCellPotential(cell));
            System.out.println("Coordinates: " + cell.getLocation().x + " " + cell.getLocation().y);
            if(cell.getPotentialValue() < nextCell.getPotentialValue()){
                nextCell = cell;
            }
        }

        return nextCell;
    }


    public MapLocation getDestinationLocation() {
        PotentialMap.PotentialCell destination = currentPotentialMapLocation;
        PotentialMap.PotentialCell nextCell;
        while(true) {
            System.out.println("let's see how many times we go through here");
            nextCell = getNextBestCell();
            if(destination == nextCell) {
                System.out.println("we exit cuz equal");
                break; 
            }
            float distanceToNextCell = destination.getDistanceToCell(nextCell);
            System.out.println("distance is  " + distanceToNextCell);
            if(distanceToNextCell + pathTraveledSoFar > MAX_TRAVEL_DISTANCE) {
                System.out.println("we exit here");
                break; 
            } else {
                destination = nextCell;
                currentPotentialMapLocation = nextCell;
                this.pathTraveledSoFar += distanceToNextCell; 
            }
        }
        return destination.getLocation();
    }

}
