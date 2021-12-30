package com.jelte.norii.battle.battlestate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

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
	public static final int NO_UNIT = 0;
	private static final String TAG = BattleState.class.getSimpleName();

	private final Map<Integer, Entity> units = new HashMap<>();
	private final Map<Integer, List<Entity>> linkedUnitsIds = new HashMap<>();
	private final BattleCell[][] stateOfField;
	private BattleState parentState = null;
	private UnitTurn turn;
	private int score;

	// CONSTRUCTORS
	public BattleState(int width, int height) {
		stateOfField = new BattleCell[width][height];
		initField(width, height);
		score = 0;
	}

	private void initField(int width, int height) {
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				stateOfField[i][j] = new BattleCell();
			}
		}
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

	// MOVEMENT
	public void moveUnitAndCreateIfNecessary(Entity entity, MyPoint to) {
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

	public void moveUnitTo(Entity currentUnit, TiledMapPosition newUnitPos) {
		final MyPoint target = new MyPoint(newUnitPos.getTileX(), newUnitPos.getTileY());
		moveUnitAndCreateIfNecessary(currentUnit, target);
	}

	public void swapPositions(Entity caster, Entity target) {
		final TiledMapPosition casterPos = caster.getCurrentPosition();
		final TiledMapPosition targetPos = target.getCurrentPosition();
		caster.setCurrentPosition(targetPos);
		target.setCurrentPosition(casterPos);
		stateOfField[casterPos.getTileX()][casterPos.getTileY()].setUnit(target);
		stateOfField[targetPos.getTileX()][targetPos.getTileY()].setUnit(caster);
	}

	public void pushOrPullUnit(MyPoint casterPos, MyPoint targetPos, int maxCellsToMove, boolean isPulling) {
		final boolean casterIsRight = casterPos.x > targetPos.x;
		final boolean casterIsLeft = casterPos.x < targetPos.x;
		final boolean casterIsDown = casterPos.y < targetPos.y;
		final boolean casterIsUp = casterPos.y > targetPos.y;

		MyPoint nextPoint = calculateNextPoint(targetPos, casterIsRight, casterIsLeft, casterIsDown, casterIsUp, isPulling);
		final Entity unitToMove = stateOfField[targetPos.x][targetPos.y].getUnit();
		int cellsToMove = maxCellsToMove;

		while (canMoveTo(nextPoint, cellsToMove)) {
			unitToMove.pushTo(nextPoint);
			moveUnitAndCreateIfNecessary(unitToMove, nextPoint);
			nextPoint = calculateNextPoint(nextPoint, casterIsRight, casterIsLeft, casterIsDown, casterIsUp, isPulling);
			cellsToMove--;
		}
	}

	private MyPoint calculateNextPoint(MyPoint oldPos, boolean casterIsRight, boolean casterIsLeft, boolean casterIsDown, boolean casterIsUp, boolean isPulling) {
		if ((casterIsRight && isPulling) || (casterIsLeft && !isPulling))
			return new MyPoint(oldPos.x + 1, oldPos.y);
		if (casterIsRight || casterIsLeft)
			return new MyPoint(oldPos.x - 1, oldPos.y);
		if ((casterIsDown && isPulling) || (casterIsUp && !isPulling))
			return new MyPoint(oldPos.x, oldPos.y - 1);
		if (casterIsDown || casterIsUp)
			return new MyPoint(oldPos.x, oldPos.y + 1);
		Gdx.app.debug(TAG, "next point in push calculation needs to be in one of 4 directions, returning null");
		return null;
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

	public TiledMapPosition findFreeSpotNextTo(Entity otherPortal) {
		int distance = 1;
		int max = Math.max(getWidth(), getHeight());
		MyPoint startingPoint = otherPortal.getCurrentPosition().getTilePosAsPoint();
		TiledMapPosition pos = findFreeSpotInDistance(startingPoint, distance);

		while ((pos == null) && (distance <= max)) {
			distance++;
			pos = findFreeSpotInDistance(startingPoint, distance);
		}

		return pos;
	}

	private TiledMapPosition findFreeSpotInDistance(MyPoint startingPoint, int distance) {
		final int portalX = startingPoint.x;
		final int portalY = startingPoint.y;
		for (int i = 0; i <= distance; i++) {
			for (int j = 0; j <= distance; j++) {
				if ((i + j) == distance) {
					TiledMapPosition pos = findFreeSpotWithDeltas(portalX, portalY, i, j);
					if (pos != null) {
						return pos;
					}
				}
			}
		}
		return null;
	}

	private TiledMapPosition findFreeSpotWithDeltas(final int portalX, final int portalY, int deltaX, int deltaY) {
		if (canMoveTo(portalX + deltaX, portalY + deltaY)) {
			return new TiledMapPosition().setPositionFromTiles(portalX + deltaX, portalY + deltaY);
		}

		if (canMoveTo(portalX - deltaX, portalY + deltaY)) {
			return new TiledMapPosition().setPositionFromTiles(portalX - deltaX, portalY + deltaY);
		}

		if (canMoveTo(portalX + deltaX, portalY - deltaY)) {
			return new TiledMapPosition().setPositionFromTiles(portalX + deltaX, portalY - deltaY);
		}

		if (canMoveTo(portalX - deltaX, portalY - deltaY)) {
			return new TiledMapPosition().setPositionFromTiles(portalX - deltaX, portalY - deltaY);
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

	public MyPoint stepFromTowards(MyPoint from, MyPoint to) {
		final int random = MathUtils.random.nextInt(2);

		if (random == 0) {
			if (to.x > from.x) {
				return new MyPoint(from.x + 1, from.y);
			} else if (to.x < from.x) {
				return new MyPoint(from.x - 1, from.y);
			}
		}
		return yUpOrYDown(from, to);

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

	// ADD/REMOVE
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

	public void addEntity(Entity unit) {
		addEntityAt(unit, unit.getCurrentPosition().getTileX(), unit.getCurrentPosition().getTileY());
	}

	public void removeUnit(Entity unit) {
		units.remove(unit.getEntityID());
		final BattleCell cell = stateOfField[unit.getCurrentPosition().getTileX()][unit.getCurrentPosition().getTileY()];
		cell.setOccupied(false);
		cell.setUnit(null);
		cell.setWalkable(true);
	}

	public void randomlyAddUnitsToBattleState(Array<Entity> units) {
		for (Entity unit : units) {
			if (isSpotFree()) {
				placeUnitOnRandomFreeSpot(unit);
			}
		}
	}

	public void placeUnitOnSpecificSpot(Entity unit, int i, int j) {
		if (get(i, j).canMove()) {
			unit.setCurrentPosition(new TiledMapPosition().setPositionFromTiles(i, j));
			addEntity(unit);
		} else {
			Gdx.app.error(TAG, "unit " + unit.getEntityData().getName() + "cannot be placed on " + i + "," + j + "), it is not free");
		}
	}

	private void placeUnitOnRandomFreeSpot(Entity unit) {
		MyPoint point = new MyPoint(0, 0).randomize(getWidth(), getHeight());
		while (!get(point).canMove()) {
			point.randomize(getWidth(), getHeight());
		}
		unit.setCurrentPosition(new TiledMapPosition().setPositionFromTiles(point.x, point.y));
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

	// EFFECTS ON UNITS
	public void damageUnit(MyPoint attackLocation, int damage, DamageType type) {
		stateOfField[attackLocation.getX()][attackLocation.getY()].getUnit().damage(damage, type);
	}

	public void healUnit(MyPoint attackLocation, int damage) {
		stateOfField[attackLocation.getX()][attackLocation.getY()].getUnit().heal(damage);
	}

	public void linkUnits(MyPoint casterPos, MyPoint location) {
		final Entity target = stateOfField[location.getX()][location.getY()].getUnit();
		final int linkedIdCaster = stateOfField[location.getX()][location.getY()].getUnit().getEntityID();
		stateOfField[casterPos.getX()][casterPos.getY()].getUnit().addModifier(ModifiersEnum.LINKED, 3, target.getEntityID());
		linkedUnitsIds.computeIfAbsent(linkedIdCaster, k -> new ArrayList<>()).add(target);
	}

	public void unlinkUnits(MyPoint location) {
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

	public void addModifierToUnit(int width, int height, Modifier modifier) {
		if (stateOfField[width][height].isOccupied()) {
			stateOfField[width][height].getUnit().addModifier(modifier);
		}
	}

	// GETTERS SETTERS

	public BattleCell get(int width, int height) {
		return stateOfField[width][height];
	}

	public BattleCell get(MyPoint point) {
		return stateOfField[point.x][point.y];
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
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = (prime * result) + Arrays.deepHashCode(stateOfField);
		return result;
	}

	public Entity getRandomUnit() {
		Optional<Entity> unit = Utility.getRandom(units.values());
		if (unit.isPresent()) {
			return unit.get();
		} else {
			return null;
		}

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
		int randomInt = Utility.random.nextInt(4);

		MyPoint testPoint = unit.getCurrentPosition().getTilePosAsPoint().makeCopy();

		while (!canMoveTo(testPoint.randomChange(randomInt)) && (counter < 4)) {
			testPoint.set(unit.getCurrentPosition().getTileX(), unit.getCurrentPosition().getTileY());
			randomInt = Utility.incrementModulo(randomInt, 4);
			counter++;
		}

		if (counter < 4) {
			return testPoint;
		} else {
			return null;
		}
	}

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder();
		List<Entity> entities = new ArrayList<>(units.values());
		Collections.sort(entities);
		for (Entity unit : entities) {
			sb.append(printUnitAttributes(unit));
			sb.append("\n");
		}
		return sb.toString();
	}

	private String printUnitAttributes(Entity unit) {
		StringBuilder sb = new StringBuilder();
		sb.append(unit.getEntityData().getName() + "  hp: " + unit.getHp() + "/" + unit.getEntityData().getMaxHP());
		sb.append(" attackpower: " + unit.getEntityData().getAttackPower());
		sb.append(" attackRange: " + unit.getEntityData().getAttackRange());
		sb.append(" basic atk cost: " + unit.getEntityData().getBasicAttackCost());
		sb.append(" mag def: " + unit.getEntityData().getMagicalDefense());
		sb.append(" phy def: " + unit.getEntityData().getPhysicalDefense());
		sb.append(" ap: " + unit.getAp());
		sb.append(" pos: " + unit.getCurrentPosition());
		for (Modifier mod : unit.getModifiers()) {
			sb.append("modifier: " + mod.getType().toString() + " turns left: " + mod.getTurns() + " amount: " + mod.getAmount() + "  ");
		}

		return sb.toString();
	}
}
