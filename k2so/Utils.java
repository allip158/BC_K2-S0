package k2so;
import battlecode.common.*;

public final class Utils {

	//private constructor so that class can't be instantiated
	private Utils () {}

	/**
	 * Find a closest body to a give location among any array of bodies
	 * @param objects array of bodies(can be either BulletInfo, RobotInfo or TreeInfo)
	 * @param location location on the map
	 * @return closest body
	 */
	public static BodyInfo findClosestBody(BodyInfo[] objects, MapLocation location) {
		float closestObjectDistanceSquared = Float.MAX_VALUE;
		BodyInfo closestObject = null;
		for(BodyInfo obj: objects) {
			if(location.distanceSquaredTo(obj.getLocation()) <= closestObjectDistanceSquared) {
				closestObject = obj;
				closestObjectDistanceSquared = location.distanceSquaredTo(obj.getLocation());
			}
		}
		return closestObject;
	}

	public static BodyInfo[] concatenateAllObjects(TreeInfo[] trees, RobotInfo[] robots, BulletInfo[] bullets) {
		int treesLength = trees.length;
		int robotsLength = robots.length;
		int bulletsLengths = bullets.length;

		BodyInfo[] gameObjects = new BodyInfo[treesLength + robotsLength + bulletsLengths];
		System.arraycopy(trees, 0, gameObjects, 0, treesLength);
		System.arraycopy(robots, 0, gameObjects, treesLength, robotsLength);
		System.arraycopy(bullets, 0, gameObjects, treesLength + robotsLength, bulletsLengths);
		return gameObjects;
	}
}