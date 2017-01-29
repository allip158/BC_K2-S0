package k2so;

import battlecode.common.MapLocation;

/**
 * Any class that tries to computes the next destination location on the map should implement this interface
 * Created by Andrey on 1/26/2017.
 */
public interface PathFinder {

    /**
     *
     * @return the location of the next move
     */
    MapLocation getDestinationLocation();
}
