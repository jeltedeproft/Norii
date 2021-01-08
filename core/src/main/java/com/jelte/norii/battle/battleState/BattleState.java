package com.jelte.norii.battle.battleState;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import com.jelte.norii.ai.UnitTurn;
import com.jelte.norii.entities.Entity;
import com.jelte.norii.magic.Modifier;
import com.jelte.norii.utility.MyPoint;

public class BattleState implements Comparable<BattleState> {
	private final BattleCell[][] stateOfField;
	private int score;
	private BattleState parentState = null;
	private UnitTurn turn;
	private final Array<HypotheticalUnit> units = new Array<>();

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

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("\n");
		for (int i = stateOfField.length - 1; i >= 0; i--) {
			BattleCell[] row = stateOfField[i];
			for (int j = 0; j < row.length; j++) {
				BattleCell cell = stateOfField[j][i];
				if (cell.isOccupied() && cell.getUnit().isPlayerUnit()) {
					sb.append("| " + cell.getUnit().getEntityId() + " -- (" + cell.getUnit().getX() + "," + cell.getUnit().getY() + ") |");
				} else if (cell.isOccupied() && !cell.getUnit().isPlayerUnit()) {
					sb.append("| " + cell.getUnit().getEntityId() + " -- (" + cell.getUnit().getX() + "," + cell.getUnit().getY() + ") |");
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

	public BattleState(BattleCell[][] field, Array<HypotheticalUnit> unitsToAdd) {
		stateOfField = field;
		score = 0;
		units.addAll(unitsToAdd);
	}

	public BattleState(BattleCell[][] field, int score, Array<HypotheticalUnit> unitsToAdd) {
		stateOfField = field;
		this.score = score;
		units.addAll(unitsToAdd);
	}

	public BattleState(BattleCell[][] field, int score, Array<HypotheticalUnit> unitsToAdd, UnitTurn turn) {
		stateOfField = field;
		this.score = score;
		units.addAll(unitsToAdd);
		this.turn = turn;
	}

	public void addModifierToUnit(int width, int height, Modifier modifier) {
		if (stateOfField[width][height].isOccupied()) {
			stateOfField[width][height].getUnit().addModifier(modifier);
		}
	}

	public void addEntity(int width, int height, HypotheticalUnit unit) {
		if ((height > 0) && (width > 0) && (width <= getWidth()) && (height <= getHeight())) {
			final int originalScore = stateOfField[width][height].getScore();
			final int newScore = unit.getScore();
			final int difference = newScore - originalScore;
			score += difference;
			unit.setX(width);
			unit.setY(height);
			stateOfField[width][height].setUnit(unit);
			stateOfField[width][height].setOccupied(true);
			units.add(unit);
		}
	}

	public BattleCell get(int width, int height) {
		return stateOfField[width][height];
	}

	public void moveUnitTo(HypotheticalUnit aiUnit, MyPoint to) {
		// if we have the unit, move it, if not create it
		MyPoint from = new MyPoint(aiUnit.getX(), aiUnit.getY());
		boolean entityFound = false;
		if (!from.equals(to)) {
			for (HypotheticalUnit unit : units) {
				if (unit.getEntityId() == aiUnit.getEntityId()) {
					stateOfField[to.x][to.y].setUnit(stateOfField[unit.getX()][unit.getY()].getUnit());
					stateOfField[to.x][to.y].getUnit().setX(to.x);
					stateOfField[to.x][to.y].getUnit().setY(to.y);
					stateOfField[from.x][from.y].removeUnit();
					entityFound = true;
				}
			}

			if (!entityFound) {
				addEntity(to.x, to.y, aiUnit);
			}
			stateOfField[to.x][to.y].setOccupied(true);
		}
	}

	public void moveUnitTo(Entity entity, MyPoint to) {
		// if we have the unit, move it, if not create it
		MyPoint from = new MyPoint(entity.getCurrentPosition().getTileX(), entity.getCurrentPosition().getTileY());
		boolean entityFound = false;
		for (HypotheticalUnit unit : units) {
			if (unit.getEntityId() == entity.getEntityID()) {
				stateOfField[to.x][to.y].setUnit(stateOfField[unit.getX()][unit.getY()].getUnit());
				stateOfField[to.x][to.y].getUnit().setX(to.x);
				stateOfField[to.x][to.y].getUnit().setY(to.y);
				stateOfField[from.x][from.y].removeUnit();
				entityFound = true;
			}
		}

		if (!entityFound) {
			addEntity(to.x, to.y, new HypotheticalUnit(entity.getEntityID(), entity.isPlayerUnit(), entity.getHp(), entity.getEntityData().getMaxHP(), entity.getAttackRange(), entity.getEntityData().getAttackPower(), entity.getAp(),
					entity.getModifiers(), entity.getAbilities()));
		}
		stateOfField[to.x][to.y].setOccupied(true);

	}

	private boolean withinBounds(MyPoint from) {
		return ((from.x >= 0) && (from.x <= getWidth()) && (from.y >= 0) && (from.y <= getHeight()));
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
				BattleCell oldCell = get(i, j);
				copyField[i][j] = new BattleCell(oldCell.isOccupied(), oldCell.isWalkable());
			}
		}
		Array<HypotheticalUnit> copyUnits = new Array<>();
		for (HypotheticalUnit unit : units) {
			HypotheticalUnit copyUnit = unit.makeCopy();
			copyUnits.add(copyUnit);
			copyField[copyUnit.getX()][copyUnit.getY()].setUnit(copyUnit);
		}
		return new BattleState(copyField, score, copyUnits);
	}

	public BattleState makeCopyWithTurn() {
		BattleState copy = makeCopy();
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
			stateOfField[tileX][tileY].removeUnit();
			int index = 0;
			for (int i = 0; i < units.size; i++) {
				if ((units.get(i).getX() == tileX) && (units.get(i).getY() == tileY)) {
					index = i;
					units.removeIndex(index);
				}
			}
		} else {
			get(tileX, tileY).getUnit().setHp(hp);
		}
	}

	public Array<HypotheticalUnit> getPlayerUnits() {
		final Array<HypotheticalUnit> units = new Array<>();
		for (final BattleCell[] row : stateOfField) {
			for (final BattleCell cell : row) {
				if ((cell.getUnit() != null) && cell.getUnit().isPlayerUnit()) {
					units.add(cell.getUnit());
				}
			}
		}
		return units;
	}

	public Array<HypotheticalUnit> getAiUnits() {
		final Array<HypotheticalUnit> units = new Array<>();
		for (final BattleCell[] row : stateOfField) {
			for (final BattleCell cell : row) {
				if ((cell.getUnit() != null) && !cell.getUnit().isPlayerUnit()) {
					units.add(cell.getUnit());
				}
			}
		}
		return units;
	}

	public Array<HypotheticalUnit> getAllUnits() {
		final Array<HypotheticalUnit> units = new Array<>();
		for (final BattleCell[] row : stateOfField) {
			for (final BattleCell cell : row) {
				if (cell.getUnit() != null) {
					units.add(cell.getUnit());
				}
			}
		}
		return units;
	}

	public void reduceModifierCounts() {
		for (final HypotheticalUnit unit : getAllUnits()) {
			for (final Modifier mod : unit.getModifiers()) {
				mod.decrementTurns();
			}
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
}
