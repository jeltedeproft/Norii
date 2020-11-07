
package utility;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.game.entities.Entity;

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

	public static List<TiledMapPosition> collectPositionsEnemyUnits(final List<Entity> units, final boolean isPlayer) {
		final ArrayList<TiledMapPosition> enemyPositions = new ArrayList<>();

		for (final Entity unit : units) {
			if (unit.isPlayerUnit() != isPlayer) {
				enemyPositions.add(unit.getCurrentPosition());
			}
		}

		return enemyPositions;
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
		Gdx.gl.glLineWidth(2);
		debugRenderer.setProjectionMatrix(projectionMatrix);
		debugRenderer.begin(ShapeRenderer.ShapeType.Line);
		debugRenderer.setColor(Color.DARK_GRAY);
		debugRenderer.line(start, end);
		debugRenderer.end();
		Gdx.gl.glLineWidth(1);
	}
}
