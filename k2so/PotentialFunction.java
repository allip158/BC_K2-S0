package k2so;

import battlecode.common.BulletInfo;
import battlecode.common.MapLocation;
import battlecode.common.RobotInfo;
import battlecode.common.TreeInfo;

/**
 * Created by Andrey on 1/25/2017.
 */
@FunctionalInterface
public interface PotentialFunction {
    /**
     *
     * @param robots robots to consider
     * @param trees trees to consider
     * @param bullets bullets to consider(likely all of them)
     * @param location location we want to calculate the potential of
     * @return potential at a certain location
     */
    float apply(RobotInfo[] robots, TreeInfo[] trees, BulletInfo[] bullets, MapLocation location);
}
