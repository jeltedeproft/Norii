package com.jelte.norii.battle.battleState;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import com.jelte.norii.ai.UnitTurn;
import com.jelte.norii.entities.Entity;
import com.jelte.norii.magic.Ability.DamageType;
import com.jelte.norii.magic.Modifier;
import com.jelte.norii.magic.ModifiersEnum;
import com.jelte.norii.utility.MyPoint;
import com.jelte.norii.utility.TiledMapPosition;
import com.jelte.norii.utility.Utility;

public class BattleState implements Comparable<BattleState> {
	private final BattleCell[][] stateOfField;
	private int score;
	private BattleState parentState = null;
	private UnitTurn turn;
	private final Map<Integer, Entity> units = new HashMap<>();
	private final Map<Integer, List<Entity>> linkedUnitsIds = new HashMap<>();
	private final Random random = new Random();

	public static final int NO_UNIT = 0;
	private static final String TAG = BattleState.class.getSimpleName();

	public BattleState(int width, int height) {
		stateOfField = new BattleCell[width][height];
		score = 0;
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				stateOfField[i][j] = new BattleCell();
			}
		}
	}

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder();
		sb.append("\n");
		for (int i = stateOfField.length - 1; i >= 0; i--) {
			final BattleCell[] row = stateOfField[i];
			for (int j = 0; j < row.length; j++) {
				final BattleCell cell = stateOfField[j][i];
				if (cell.isOccupied() && cell.getUnit().isPlayerUnit()) {
					sb.append("|    " + cell.getUnit().getHp() + "     |");
				} else if (cell.isOccupied() && !cell.getUnit().isPlayerUnit()) {
					sb.append("|    " + cell.getUnit().getHp() + "     |");
				} else if (!cell.isWalkable()) {
					sb.append("| XXXXXXXX |");
				} else {
					sb.append("|          |");
				}
			}
			sb.append("\n");
		}
		return sb.toString();
	}

	public BattleState(BattleCell[][] field, Array<Entity> unitsToAdd) {
		stateOfField = field;
		score = 0;
		for (final Entity unit : unitsToAdd) {
			units.put(unit.getEntityID(), unit);
		}
	}

	public BattleState(BattleCell[][] field, int score, Array<Entity> unitsToAdd) {
		stateOfField = field;
		this.score = score;
		for (final Entity unit : unitsToAdd) {
			units.put(unit.getEntityID(), unit);
		}
	}

	public BattleState(BattleCell[][] field, int score, Array<Entity> unitsToAdd, UnitTurn turn) {
		stateOfField = field;
		this.score = score;
		for (final Entity unit : unitsToAdd) {
			units.put(unit.getEntityID(), unit);
		}
		this.turn = turn;
	}

	public void addModifierToUnit(int width, int height, Modifier modifier) {
		if (stateOfField[width][height].isOccupied()) {
			stateOfField[width][height].getUnit().addModifier(modifier);
		}
	}

	public void addEntity(Entity unit) {
		addEntityAt(unit, unit.getCurrentPosition().getTileX(), unit.getCurrentPosition().getTileY());
	}

	public BattleCell get(int width, int height) {
		return stateOfField[width][height];
	}

	public void randomlyAddUnitsToBattleState(Array<Entity> units) {
		for (Entity unit : units) {
			if (isSpotFree()) {
				placeUnitOnRandomFreeSpot(unit);
			}
		}
	}

	private void placeUnitOnRandomFreeSpot(Entity unit) {
		int i = random.nextInt(getWidth());
		int j = random.nextInt(getHeight());
		while (!get(i, j).canMove()) {
			i = random.nextInt(getWidth());
			j = random.nextInt(getHeight());
		}
		unit.setCurrentPosition(new TiledMapPosition().setPositionFromTiles(i, j));
		addEntity(unit);
	}

	private boolean isSpotFree() {
		for (int i = 0; i < getWidth(); i++) {
			for (int j = 0; j < getHeight(); j++) {
				if (get(i, j).canMove()) {
					return true;
				}
			}
		}
		return false;
	}

	public void moveUnitTo(Entity entity, MyPoint to) {
		// if we have the unit, move it, if not create it
		final MyPoint from = new MyPoint(entity.getCurrentPosition().getTileX(), entity.getCurrentPosition().getTileY());
		boolean entityFound = false;
		if (!from.equals(to)) {
			for (final Entity unit : units.values()) {
				if ((unit.getEntityID() == entity.getEntityID()) && (unit.getCurrentPosition().getTileX() >= 0) && (unit.getCurrentPosition().getTileY() >= 0)) {
					stateOfField[to.x][to.y].setUnit(stateOfField[unit.getCurrentPosition().getTileX()][unit.getCurrentPosition().getTileY()].getUnit());
					stateOfField[to.x][to.y].getUnit().setOnlyCurrentPosition(new TiledMapPosition().setPositionFromTiles(to.x, to.y));
					if (oldSpotHasUnit(from)) {
						stateOfField[from.x][from.y].removeUnit();
					}
					entityFound = true;
				}
			}

			if (!entityFound) {
				addEntityAt(entity, to.x, to.y);
			}
			stateOfField[to.x][to.y].setOccupied(true);
		}

	}

	private boolean oldSpotHasUnit(MyPoint from) {
		if ((from.x >= 0) && (from.y >= 0)) {
			return stateOfField[from.x][from.y].isOccupied();
		}
		return false;
	}

	private void addEntityAt(Entity unit, int x, int y) {
		if ((y >= 0) && (x >= 0) && (x <= getWidth()) && (y <= getHeight())) {
			final int originalScore = stateOfField[x][y].getScore();
			final int newScore = unit.getScore();
			final int difference = newScore - originalScore;
			score += difference;
			stateOfField[x][y].setUnit(unit);
			stateOfField[x][y].setOccupied(true);
			units.put(unit.getEntityID(), unit);
			unit.setOnlyCurrentPosition(new TiledMapPosition().setPositionFromTiles(x, y));
		}
	}

	public void removeUnit(Entity unit) {
		units.remove(unit.getEntityID());
		final BattleCell cell = stateOfField[unit.getCurrentPosition().getTileX()][unit.getCurrentPosition().getTileY()];
		cell.setOccupied(false);
		cell.setUnit(null);
		cell.setWalkable(true);
	}

	public MyPoint stepFromTowards(MyPoint from, MyPoint to) {
		final int random = MathUtils.random.nextInt(1);

		if (random == 0) {
			if (to.x > from.x) {
				return new MyPoint(from.x + 1, from.y);
			} else if (to.x < from.x) {
				return new MyPoint(from.x - 1, from.y);
			} else {
				return yUpOrYDown(from, to);
			}
		} else {
			return yUpOrYDown(from, to);
		}
	}

	private MyPoint yUpOrYDown(MyPoint from, MyPoint to) {
		if (to.y > from.y) {
			return new MyPoint(from.x, from.y + 1);
		} else if (to.y < from.y) {
			return new MyPoint(from.x, from.y - 1);
		} else {
			return new MyPoint(from.x, from.y);
		}
	}

	public BattleState makeCopy() {
		final BattleCell[][] copyField = new BattleCell[getWidth()][getHeight()];
		for (int i = 0; i < getWidth(); i++) {
			for (int j = 0; j < getHeight(); j++) {
				final BattleCell oldCell = get(i, j);
				copyField[i][j] = new BattleCell(oldCell.isOccupied(), oldCell.isWalkable());
			}
		}
		final Array<Entity> copyUnits = new Array<>();
		for (final Entity unit : units.values()) {
			final Entity copyUnit = unit.makeCopyWithoutVisual();
			copyUnits.add(copyUnit);
			copyField[copyUnit.getCurrentPosition().getTileX()][copyUnit.getCurrentPosition().getTileY()].setUnit(copyUnit);
		}
		return new BattleState(copyField, score, copyUnits);
	}

	public BattleState makeCopyWithTurn() {
		final BattleState copy = makeCopy();
		copy.setTurn(turn);
		return copy;
	}

	public int getScore() {
		return score;
	}

	public int calculateScore() {
		int sum = 0;
		for (final BattleCell[] row : stateOfField) {
			for (final BattleCell cell : row) {
				sum += cell.getScore();
				if (cell.getScore() != 0) {
					int j = 0;
				}
			}
		}

		if (getPlayerUnits().isEmpty()) {
			sum += 50;
		}

		if (getAiUnits().isEmpty()) {
			sum -= 50;
		}

		return sum;
	}

	public int getWidth() {
		return stateOfField[0].length;
	}

	public int getHeight() {
		return stateOfField.length;
	}

	public Array<Entity> getPlayerUnits() {
		final Array<Entity> playerUnits = new Array<>();
		for (Entity entity : units.values()) {
			if (entity.isPlayerUnit()) {
				playerUnits.add(entity);
			}
		}
		return playerUnits;
	}

	public Array<Entity> getVisiblePlayerUnits() {
		final Array<Entity> playerUnits = new Array<>();
		for (Entity entity : units.values()) {
			if (entity.isPlayerUnit() && !entity.isInvisible()) {
				playerUnits.add(entity);
			}
		}
		return playerUnits;
	}

	public Array<Entity> getAiUnits() {
		final Array<Entity> aiUnits = new Array<>();
		for (Entity entity : units.values()) {
			if (!entity.isPlayerUnit()) {
				aiUnits.add(entity);
			}
		}
		return aiUnits;
	}

	public Array<Entity> getVisibleAiUnits() {
		final Array<Entity> aiUnits = new Array<>();
		for (Entity entity : units.values()) {
			if (!entity.isPlayerUnit() && !entity.isInvisible()) {
				aiUnits.add(entity);
			}
		}
		return aiUnits;
	}

	public Array<Entity> getAllUnits() {
		final Array<Entity> allUnits = new Array<>();
		for (final BattleCell[] row : stateOfField) {
			for (final BattleCell cell : row) {
				if (cell.getUnit() != null) {
					allUnits.add(cell.getUnit());
				}
			}
		}
		return allUnits;
	}

	public Array<Entity> getAllVisibleUnits() {
		final Array<Entity> allUnits = new Array<>();
		for (final BattleCell[] row : stateOfField) {
			for (final BattleCell cell : row) {
				if ((cell.getUnit() != null) && !cell.getUnit().isInvisible()) {
					allUnits.add(cell.getUnit());
				}
			}
		}
		return allUnits;
	}

	public void reduceModifierCounts() {
		for (final Entity unit : getAllUnits()) {
			unit.applyModifiers();
		}
		score = calculateScore();
	}

	public BattleState getParentState() {
		return parentState;
	}

	public void setParentState(BattleState parentState) {
		this.parentState = parentState;
	}

	public UnitTurn getTurn() {
		return turn;
	}

	public void setTurn(UnitTurn turn) {
		this.turn = turn;
	}

	@Override
	public int compareTo(BattleState battleState) {
		final int thisScore = calculateScore();
		final int otherScore = battleState.calculateScore();
		if (thisScore == otherScore)
			return 0;
		else if (thisScore < otherScore)
			return 1;
		else
			return -1;
	}

	@Override
	public boolean equals(Object obj) {
		final BattleState battleState = (BattleState) obj;
		if ((getHeight() == battleState.getHeight()) && (getWidth() == battleState.getWidth())) {
			for (int i = 0; i < (getWidth() - 1); i++) {
				for (int j = 0; j < (getHeight() - 1); j++) {
					if (get(i, j) != battleState.get(i, j)) {
						return false;
					}
				}
			}
		}
		return true;
	}

	public void moveUnitTo(Entity currentUnit, TiledMapPosition newUnitPos) {
		final MyPoint target = new MyPoint(newUnitPos.getTileX(), newUnitPos.getTileY());
		moveUnitTo(currentUnit, target);
	}

	public void swapPositions(Entity caster, Entity target) {
		final TiledMapPosition casterPos = caster.getCurrentPosition();
		final TiledMapPosition targetPos = target.getCurrentPosition();
		caster.setCurrentPosition(targetPos);
		target.setCurrentPosition(casterPos);
		stateOfField[casterPos.getTileX()][casterPos.getTileY()].setUnit(target);
		stateOfField[targetPos.getTileX()][targetPos.getTileY()].setUnit(caster);
	}

	public void damageUnit(MyPoint attackLocation, int damage, DamageType type) {
		stateOfField[attackLocation.getX()][attackLocation.getY()].getUnit().damage(damage, type);
	}

	public void healUnit(MyPoint attackLocation, int damage) {
		stateOfField[attackLocation.getX()][attackLocation.getY()].getUnit().heal(damage);
	}

	public void pushOrPullUnit(MyPoint casterPos, MyPoint targetPos, int maxCellsToMove, boolean isPulling) {
		// decide direction to move
		final boolean casterIsRight = casterPos.x > targetPos.x;
		final boolean casterIsLeft = casterPos.x < targetPos.x;
		final boolean casterIsDown = casterPos.y < targetPos.y;
		final boolean casterIsUp = casterPos.y > targetPos.y;

		// keep moving in loop until next cell is not reachable or movecells runs out
		MyPoint nextPoint = calculateNextPoint(targetPos, casterIsRight, casterIsLeft, casterIsDown, casterIsUp, isPulling);
		final Entity unitToMove = stateOfField[targetPos.x][targetPos.y].getUnit();
		int cellsToMove = maxCellsToMove;

		while (canMoveTo(nextPoint, cellsToMove)) {
			unitToMove.pushTo(nextPoint);
			moveUnitTo(unitToMove, nextPoint);
			nextPoint = calculateNextPoint(nextPoint, casterIsRight, casterIsLeft, casterIsDown, casterIsUp, isPulling);
			cellsToMove--;
		}
	}

	private boolean canMoveTo(MyPoint nextPoint, int cellsToMove) {
		if (cellsToMove <= 0) {
			return false;
		}

		return canMoveTo(nextPoint);
	}

	private boolean canMoveTo(MyPoint nextPoint) {
		if ((nextPoint.x >= getWidth()) || (nextPoint.x < 0) || (nextPoint.y >= getHeight()) || (nextPoint.y < 0)) {
			return false;
		}

		return (stateOfField[nextPoint.x][nextPoint.y].isWalkable() && !stateOfField[nextPoint.x][nextPoint.y].isOccupied());
	}

	private boolean canMoveTo(int x, int y) {
		if ((x >= getWidth()) || (x < 0) || (y >= getHeight()) || (y < 0)) {
			return false;
		}

		return (stateOfField[x][y].isWalkable() && !stateOfField[x][y].isOccupied());
	}

	private MyPoint calculateNextPoint(MyPoint oldPos, boolean casterIsRight, boolean casterIsLeft, boolean casterIsDown, boolean casterIsUp, boolean isPulling) {
		if (casterIsRight) {
			if (isPulling) {
				return new MyPoint(oldPos.x + 1, oldPos.y);
			} else {
				return new MyPoint(oldPos.x - 1, oldPos.y);
			}

		}
		if (casterIsLeft) {
			if (isPulling) {
				return new MyPoint(oldPos.x - 1, oldPos.y);
			} else {
				return new MyPoint(oldPos.x + 1, oldPos.y);
			}
		}
		if (casterIsDown) {
			if (isPulling) {
				return new MyPoint(oldPos.x, oldPos.y - 1);
			} else {
				return new MyPoint(oldPos.x, oldPos.y + 1);
			}
		}
		if (casterIsUp) {
			if (isPulling) {
				return new MyPoint(oldPos.x, oldPos.y + 1);
			} else {
				return new MyPoint(oldPos.x, oldPos.y - 1);
			}
		}

		Gdx.app.debug(TAG, "next point in push calculation needs to be in one of 4 directions");
		return null;
	}

	public TiledMapPosition findFreeSpotNextTo(Entity otherPortal) {
		final int maxField = Math.min(getHeight(), getWidth());
		final int portalX = otherPortal.getCurrentPosition().getTileX();
		final int portalY = otherPortal.getCurrentPosition().getTileY();

		for (int max = 1; max <= maxField; max++) {
			for (int j = 0; j <= max; j++) {
				for (int k = 0; k <= max; k++) {
					if ((j + k) == max) {
						if (canMoveTo(portalX + j, portalY + k)) {
							return new TiledMapPosition().setPositionFromTiles(portalX + j, portalY + k);
						}

						if (canMoveTo(portalX - j, portalY + k)) {
							return new TiledMapPosition().setPositionFromTiles(portalX - j, portalY + k);
						}

						if (canMoveTo(portalX + j, portalY - k)) {
							return new TiledMapPosition().setPositionFromTiles(portalX + j, portalY - k);
						}

						if (canMoveTo(portalX - j, portalY - k)) {
							return new TiledMapPosition().setPositionFromTiles(portalX - j, portalY - k);
						}
					}
				}
			}
		}
		return null;
	}

	public Array<Entity> getNeighbours(MyPoint location) {
		final Array<Entity> neighbours = new Array<>();
		final Map<Integer, Array<Entity>> distances = Utility.getDistancesWithTarget(location, getAllUnits());
		if ((!distances.isEmpty()) && distances.containsKey(1)) {
			neighbours.addAll(distances.get(1));// explode in distance 1
		}
		return neighbours;
	}

	public Array<Entity> getNeighbours(MyPoint location, int range) {
		final Array<Entity> neighbours = new Array<>();
		final Map<Integer, Array<Entity>> distances = Utility.getDistancesWithTarget(location, getAllUnits());
		for (int i = 1; i <= range; i++) {
			if ((!distances.isEmpty()) && distances.containsKey(i)) {
				neighbours.addAll(distances.get(i));// explode in distance 1
			}
		}

		return neighbours;
	}

	public void linkUnits(MyPoint casterPos, MyPoint location) {
		final Entity target = stateOfField[location.getX()][location.getY()].getUnit();
		final int linkedIdCaster = stateOfField[location.getX()][location.getY()].getUnit().getEntityID();
		stateOfField[casterPos.getX()][casterPos.getY()].getUnit().addModifier(ModifiersEnum.LINKED, 3, target.getEntityID());
		linkedUnitsIds.computeIfAbsent(linkedIdCaster, k -> new ArrayList<>()).add(target);
	}

	public void unlinkUnits(MyPoint casterPos, MyPoint location) {
		final int linkedIdCaster = stateOfField[location.getX()][location.getY()].getUnit().getEntityID();
		linkedUnitsIds.remove(linkedIdCaster);
	}

	public void unitDamage(Entity entity, int damage) {
		if (entity.hasModifier(ModifiersEnum.LINKED)) {
			damage = (int) (damage * 0.5);
			final List<Entity> linkedEntities = linkedUnitsIds.get(entity.getEntityID());
			if (linkedEntities != null) {
				for (final Entity entityToDamage : linkedUnitsIds.get(entity.getEntityID())) {
					entityToDamage.damage(damage, DamageType.MAGICAL);
				}
			}
		}
	}

	public Entity getRandomUnit() {
		Optional<Entity> unit = Utility.getRandom(units.values());
		return unit.get();
	}

	public Entity getRandomAiUnit() {
		Entity unit = getRandomUnit();
		while (unit.isPlayerUnit()) {
			unit = getRandomUnit();
		}
		return unit;
	}

	public Entity getRandomPlayerUnit() {
		Entity unit = getRandomUnit();
		while (!unit.isPlayerUnit()) {
			unit = getRandomUnit();
		}
		return unit;
	}

	public MyPoint getRandomMoveSpotForUnit(Entity unit) {
		int counter = 0;
		int randomInt = random.nextInt(4);
		MyPoint currentPos = unit.getCurrentPosition().getTilePosAsPoint().makeCopy();

		while (!tryRandomDirection(currentPos, randomInt) && (counter < 4)) {
			randomInt = Utility.incrementModulo(randomInt, 4);
			counter++;
		}

		if (counter >= 4) {
			return null;
		} else {
			return updatePos(currentPos, randomInt);
		}
	}

	private MyPoint updatePos(MyPoint pos, int randomInt) {
		switch (randomInt) {
		case 0:
			return pos.makeCopy().incrementX();
		case 1:
			return pos.makeCopy().incrementY();
		case 2:
			return pos.makeCopy().decrementX();
		case 3:
			return pos.makeCopy().decrementY();
		default:
			Gdx.app.log(TAG, "cant possible return any other number in this function");
			return null;
		}
	}

	private boolean tryRandomDirection(MyPoint pos, int randomInt) {
		switch (randomInt) {
		case 0:
			return checkDirection(pos.makeCopy().incrementX());
		case 1:
			return checkDirection(pos.makeCopy().incrementY());
		case 2:
			return checkDirection(pos.makeCopy().decrementX());
		case 3:
			return checkDirection(pos.makeCopy().decrementY());
		default:
			return false;
		}
	}

	private boolean checkDirection(MyPoint changedPos) {
		if (canMoveTo(changedPos)) {
			return true;
		} else {
			return false;
		}
	}
}
