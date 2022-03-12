
package com.jelte.norii.utility;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.TreeMap;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.badlogic.gdx.utils.Array;
import com.jelte.norii.battle.battlestate.BattleState;
import com.jelte.norii.entities.Entity;

public final class Utility {
	public static final Random random = new Random();

	public static int getRandomIntFrom1to(final int to) {
		final int result = random.nextInt(to);
		return result + 1;
	}

	public static List<TiledMapPosition> collectPositionsUnits(final List<Entity> allUnits) {
		final ArrayList<TiledMapPosition> positions = new ArrayList<>();

		for (final Entity unit : allUnits) {
			positions.add(unit.getCurrentPosition());
		}

		return positions;
	}

	public static List<TiledMapPosition> collectPositionsTeamUnits(final List<Entity> units, final boolean isPlayer) {
		final ArrayList<TiledMapPosition> enemyPositions = new ArrayList<>();

		for (final Entity unit : units) {
			if (unit.isPlayerUnit() != isPlayer) {
				enemyPositions.add(unit.getCurrentPosition());
			}
		}

		return enemyPositions;
	}

	public static Map<Integer, Array<Entity>> getDistancesWithTarget(MyPoint location, Array<Entity> otherUnits) {
		final Map<Integer, Array<Entity>> distancesWithTarget = new TreeMap<>();
		for (final Entity target : otherUnits) {
			if (!target.getCurrentPosition().isTileEqualTo(location)) {
				final Integer distance = getDistance(location, target);
				if (distancesWithTarget.containsKey(distance)) {
					distancesWithTarget.get(distance).add(target);
				} else {
					final Array<Entity> entitiesAtThisDistance = new Array<>();
					entitiesAtThisDistance.add(target);
					distancesWithTarget.put(distance, entitiesAtThisDistance);
				}
			}
		}
		return distancesWithTarget;
	}

	public static boolean checkIfUnitWithinDistance(final Entity unit1, final TiledMapPosition targetPos, final int distance) {
		final TiledMapPosition pos1 = unit1.getCurrentPosition();
		return checkIfWithinDistance(pos1, targetPos, distance);
	}

	public static boolean checkIfUnitsWithinDistance(final Entity unit1, final Entity unit2, final int distance) {
		final TiledMapPosition pos1 = unit1.getCurrentPosition();
		final TiledMapPosition pos2 = unit2.getCurrentPosition();
		return checkIfWithinDistance(pos1, pos2, distance);
	}

	private static boolean checkIfWithinDistance(final TiledMapPosition pos1, final TiledMapPosition pos2, final int distance) {
		return (Math.abs(pos1.getTileX() - pos2.getTileX()) + Math.abs(pos1.getTileY() - pos2.getTileY())) <= distance;
	}

	public static int getDistanceBetweenUnits(final Entity unit1, final Entity unit2) {
		final TiledMapPosition pos1 = unit1.getCurrentPosition();
		final TiledMapPosition pos2 = unit2.getCurrentPosition();
		return (Math.abs(pos1.getTileX() - pos2.getTileX()) + Math.abs(pos1.getTileY() - pos2.getTileY()));
	}

	public static int getDistance(final TiledMapPosition pos1, final Entity unit2) {
		final TiledMapPosition pos2 = unit2.getCurrentPosition();
		return (Math.abs(pos1.getTileX() - pos2.getTileX()) + Math.abs(pos1.getTileY() - pos2.getTileY()));
	}

	public static int getDistance(final MyPoint pos1, final Entity unit2) {
		final TiledMapPosition pos2 = unit2.getCurrentPosition();
		return (Math.abs(pos1.x - pos2.getTileX()) + Math.abs(pos1.y - pos2.getTileY()));
	}

	public static Array<Entity> getUnitsWithinDistanceFromTarget(final Entity unit, final Array<Entity> units, int distance) {
		Array<Entity> closeUnits = new Array<>();
		for (Entity entity : units) {
			if (getDistanceBetweenUnits(unit, entity) <= distance) {
				closeUnits.add(entity);
			}
		}
		return closeUnits;
	}

	public static Entity getClosestUnit(Entity unit, Array<Entity> units) {
		int closest = getDistanceBetweenUnits(units.get(0), unit);
		Entity closestEntity = units.get(0);
		for (Entity entity : units) {
			int distance = getDistanceBetweenUnits(unit, entity);
			if (distance < closest) {
				closest = distance;
				closestEntity = entity;
			}
		}
		return closestEntity;
	}

	public static MyPoint getCenterOfGravityAi(BattleState battleState) {
		final Array<Entity> aiUnits = battleState.getAiUnits();
		final int numberOfElements = aiUnits.size;
		int sumX = 0;
		int sumY = 0;

		for (int i = 0; i < numberOfElements; i++) {
			sumX += aiUnits.get(i).getCurrentPosition().getTileX();
			sumY += aiUnits.get(i).getCurrentPosition().getTileY();
		}

		return new MyPoint(sumX / numberOfElements, sumY / numberOfElements);
	}

	public static MyPoint getCenterOfGravityPlayers(BattleState battleState) {
		final Array<Entity> playerUnits = battleState.getPlayerUnits();
		final int numberOfElements = playerUnits.size;
		int sumX = 0;
		int sumY = 0;

		for (int i = 0; i < numberOfElements; i++) {
			sumX += playerUnits.get(i).getCurrentPosition().getTileX();
			sumY += playerUnits.get(i).getCurrentPosition().getTileY();
		}

		return new MyPoint(sumX / numberOfElements, sumY / numberOfElements);
	}

	public static MyPoint getCenterOfGravityAllUnits(BattleState battleState) {
		final Array<Entity> allUnits = battleState.getAllUnits();
		final int numberOfElements = allUnits.size;
		int sumX = 0;
		int sumY = 0;

		for (int i = 0; i < numberOfElements; i++) {
			sumX += allUnits.get(i).getCurrentPosition().getTileX();
			sumY += allUnits.get(i).getCurrentPosition().getTileY();
		}

		return new MyPoint(sumX / numberOfElements, sumY / numberOfElements);
	}

	public static float clamp(final float variable, final float max, final float min) {
		if (variable <= min) {
			return min;
		}
		if (variable < max) {
			return variable;
		}
		return max;
	}

	private Utility() {

	}

	public static String arrayOfStringsToString(Array<String> strings) {
		StringBuilder builder = new StringBuilder();
		for (String string : strings) {
			builder.append(string);
		}
		return builder.toString();
	}

	public static boolean isBetween(float number, float min, float max) {
		return (min <= number) && (number <= max);
	}

	public static <E> Optional<E> getRandom(Collection<E> e) {
		return e.stream().skip((long) (e.size() * random.nextDouble())).findFirst();
	}

	public static <E> E getRandom(Array<E> e) {
		return e.get(random.nextInt(e.size));
	}

	public static int incrementModulo(int x, int modulo) {
		return ((x + 1) == modulo ? 0 : x + 1);
	}

	public static int getTotalHpTeam(Array<Entity> team) {
		int totalHP = 0;
		for (Entity unit : team) {
			totalHP += unit.getHp();
		}
		return totalHP;
	}

	public static boolean stringContainsItemFromList(String inputStr, String[] items) {
		return Arrays.stream(items).anyMatch(inputStr::contains);
	}

	public static boolean checkIfArrayOfStringsContainsElementFromOtherArray(Array<String> teamHeroesNames, String[] heroNames) {
		List<String> heroNamesList = Arrays.asList(heroNames);
		for (String str : teamHeroesNames) {
			if (heroNamesList.contains(str)) {
				return true;
			}
		}
		return false;
	}

	public static <T, U> List<U> convertStringListToIntList(List<T> listOfString, Function<T, U> function) {
		return listOfString.stream().map(function).collect(Collectors.toList());
	}
}
