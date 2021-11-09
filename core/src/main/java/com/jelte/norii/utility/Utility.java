
package com.jelte.norii.utility;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.jelte.norii.battle.battleState.BattleState;
import com.jelte.norii.entities.Entity;

public final class Utility {
	private static final String TAG = Utility.class.getSimpleName();

	private static ShapeRenderer debugRenderer = new ShapeRenderer();
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

	public static boolean checkIfUnitsWithinDistance(final Entity unit1, final TiledMapPosition targetPos, final int distance) {
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

	public static float clamp(final float var, final float max, final float min) {
		if (var > min) {
			if (var < max) {
				return var;
			} else {
				return max;
			}
		} else {
			return min;
		}
	}

	private Utility() {

	}

	public static void drawDebugLine(Vector2 start, Vector2 end, int lineWidth, Color color, Matrix4 projectionMatrix) {
		Gdx.gl.glLineWidth(lineWidth);
		debugRenderer.setProjectionMatrix(projectionMatrix);
		debugRenderer.begin(ShapeRenderer.ShapeType.Line);
		debugRenderer.setColor(color);
		debugRenderer.line(start, end);
		debugRenderer.end();
		Gdx.gl.glLineWidth(1);
	}

	public static void drawDebugLine(Vector2 start, Vector2 end, Matrix4 projectionMatrix) {
		Gdx.gl.glLineWidth(1);
		debugRenderer.setProjectionMatrix(projectionMatrix);
		debugRenderer.begin(ShapeRenderer.ShapeType.Line);
		debugRenderer.setColor(Color.BLACK);
		final Color color = debugRenderer.getColor();
		color.a = 0.2f;
		debugRenderer.setColor(color);
		debugRenderer.line(start, end);
		debugRenderer.end();
		Gdx.gl.glLineWidth(1);
	}

	public static String arrayOfStringsToString(Array<String> strings) {
		StringBuilder builder = new StringBuilder();
		for (String string : strings) {
			builder.append(string);
		}
		return builder.toString();
	}
}
