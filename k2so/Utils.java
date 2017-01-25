package k2so;
import battlecode.common.*;

public final class Utils {

	//private constructor so that class can't be instantiated
	private Utils() {}

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

	static Direction randomDirection() {
		return new Direction((float)Math.random() * 2 * (float)Math.PI);
	}

	public static double getRobotValue(RobotType type) {
		if(type == null) {
			return 0.0;
		}
		switch (type) {
			case ARCHON:
				return 50.0;
			case GARDENER:
				return 3.0;
			case LUMBERJACK:
				return 3.0;
			case SCOUT:
				return 2.0;
			case SOLDIER:
				return 3.0;
			case TANK:
				return 10.0;
			default:
				return 0.0;
		}
	}

	public static double getBulletTreeValue() {
		return 2.0;
	}

	public static double getRobotsValues(RobotInfo[] robots) {
		double value = 0.0;
		for(RobotInfo rbt: robots) {
			value += Utils.getRobotValue(rbt.getType());
		}
		return value;
	}


	public static double getBulletPotential(BulletInfo bullet, MapLocation location) {
		MapLocation bulletLocation = bullet.getLocation();
		float distance = location.distanceTo(bulletLocation);
		float angle = Math.abs(bulletLocation.directionTo(location).degreesBetween(bullet.getDir()));
		double length= Math.sin(angle) * distance;
		double height = Math.cos(angle) * distance;
		double speed = bullet.getSpeed();
		double damage = bullet.getDamage();
		return (speed * damage) / (length * height);

	}
}