package com.jelte.norii.battle.battleState;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import com.jelte.norii.ai.UnitTurn;
import com.jelte.norii.entities.Entity;
import com.jelte.norii.entities.UnitOwner;
import com.jelte.norii.magic.Modifier;
import com.jelte.norii.utility.MyPoint;
import com.jelte.norii.utility.TiledMapPosition;

public class BattleState implements Comparable<BattleState> {
	private static BattleState currentState;
	private final BattleCell[][] stateOfField;
	private int score;
	private BattleState parentState = null;
	private UnitTurn turn;
	private final List<UnitOwner> owners = new ArrayList<UnitOwner>();

	public static final int NO_UNIT = 0;

	public BattleState(int width, int height) {
		stateOfField = new BattleCell[width][height];
		score = 0;
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				stateOfField[i][j] = new BattleCell();
			}
		}
	}

	public static BattleState getCurrentBattleState() {
		return currentState;
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
					sb.append("| " + cell.getUnit().getEntityID() + " -- (" + cell.getUnit().getX() + "," + cell.getUnit().getY() + ") |");
				} else if (cell.isOccupied() && !cell.getUnit().isPlayerUnit()) {
					sb.append("| " + cell.getUnit().getEntityID() + " -- (" + cell.getUnit().getX() + "," + cell.getUnit().getY() + ") |");
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
		for (Entity unit : unitsToAdd) {
			units.put(unit.getEntityID(), unit);
		}
	}

	public BattleState(BattleCell[][] field, int score, Array<Entity> unitsToAdd) {
		stateOfField = field;
		this.score = score;
		for (Entity unit : unitsToAdd) {
			units.put(unit.getEntityID(), unit);
		}
	}

	public BattleState(BattleCell[][] field, int score, Array<Entity> unitsToAdd, UnitTurn turn) {
		stateOfField = field;
		this.score = score;
		for (Entity unit : unitsToAdd) {
			units.put(unit.getEntityID(), unit);
		}
		this.turn = turn;
	}

	public void addModifierToUnit(int width, int height, Modifier modifier) {
		if (stateOfField[width][height].isOccupied()) {
			stateOfField[width][height].getUnit().addModifier(modifier);
		}
	}

	public void addModifiersToUnit(int width, int height, Collection<Modifier> modifiers) {
		if (stateOfField[width][height].isOccupied()) {
			stateOfField[width][height].getUnit().setModifiers(modifiers);
		}
	}

	public void addEntity(Entity unit) {
		int height = unit.getCurrentPosition().getTileY();
		int width = unit.getCurrentPosition().getTileX();
		if ((height > 0) && (width > 0) && (width <= getWidth()) && (height <= getHeight())) {
			final int originalScore = stateOfField[width][height].getScore();
			final int newScore = unit.getScore();
			final int difference = newScore - originalScore;
			score += difference;
			unit.setX(width);
			unit.setY(height);
			stateOfField[width][height].setUnit(unit);
			stateOfField[width][height].setOccupied(true);
			units.put(unit.getEntityID(), unit);
		}
	}

	public BattleCell get(int width, int height) {
		return stateOfField[width][height];
	}

	public void moveUnitTo(Entity entity, MyPoint to) {
		// if we have the unit, move it, if not create it
		final MyPoint from = new MyPoint(entity.getCurrentPosition().getTileX(), entity.getCurrentPosition().getTileY());
		boolean entityFound = false;
		if (!from.equals(to)) {
		for (final Entity unit : units.values()) {
			if (unit.getEntityID() == entity.getEntityID()) {
				stateOfField[to.x][to.y].setUnit(stateOfField[unit.getCurrentPosition().getTileX()][unit.getCurrentPosition().getTileY()].getUnit());
				stateOfField[to.x][to.y].getUnit().setX(to.x);
				stateOfField[to.x][to.y].getUnit().setY(to.y);
				stateOfField[from.x][from.y].removeUnit();
				entityFound = true;
				unit.setX(to.x);
				unit.setY(to.y);
			}
		}

		if (!entityFound) {
			addEntity(entity);
		}
		stateOfField[to.x][to.y].setOccupied(true);

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
			final Entity copyUnit = unit.makeCopy();
			copyUnits.add(copyUnit);
			copyField[copyUnit.getX()][copyUnit.getY()].setUnit(copyUnit);
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

	public void updateEntity(int tileX, int tileY, int hp) {
		if (hp == 0) {
			removeEntityFromStateAndListOfUnits(tileX, tileY);
		} else {
			if (get(tileX, tileY).getUnit() == null) {
				int j = 5;
			}
			get(tileX, tileY).getUnit().setHp(hp);
		}
	}

	private void removeEntityFromStateAndListOfUnits(int tileX, int tileY) {
		stateOfField[tileX][tileY].removeUnit();
		int index = 0;
		for (int i = 0; i < units.size(); i++) {
			if ((units.get(i).getX() == tileX) && (units.get(i).getY() == tileY)) {
				units.remove(units.get(i));
			}
		}
	}

	public Array<Entity> getPlayerUnits() {
		final Array<Entity> playerUnits = new Array<>();
		for (final BattleCell[] row : stateOfField) {
			for (final BattleCell cell : row) {
				if ((cell.getUnit() != null) && cell.getUnit().isPlayerUnit()) {
					playerUnits.add(cell.getUnit());
				}
			}
		}
		return playerUnits;
	}

	public Array<Entity> getAiUnits() {
		final Array<Entity> aiUnits = new Array<>();
		for (final BattleCell[] row : stateOfField) {
			for (final BattleCell cell : row) {
				if ((cell.getUnit() != null) && !cell.getUnit().isPlayerUnit()) {
					aiUnits.add(cell.getUnit());
				}
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

	public

	public void reduceModifierCounts() {
		for (final Entity unit : getAllUnits()) {
			unit.applyModifiers();
		}
		score = calculateScore();
	}

	private void checkdead() {
		for (final Entity unit : units.values()) {
			if (unit.getHp() == 0) {
				removeEntityFromStateAndListOfUnits(unit);
			}
		}
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
}
