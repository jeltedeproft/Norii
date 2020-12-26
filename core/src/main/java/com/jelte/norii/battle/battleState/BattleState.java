package com.jelte.norii.battle.battleState;

import java.awt.Point;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import com.jelte.norii.ai.UnitTurn;
import com.jelte.norii.magic.Modifier;

public class BattleState implements Comparable<BattleState> {
	private final BattleCell[][] stateOfField;
	private int score;
	private BattleState parentState = null;
	private UnitTurn turn;
	private final Array<HypotheticalUnit> outOfBoundsArray = new Array<>();

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

	public BattleState(BattleCell[][] field) {
		stateOfField = field;
		score = 0;
	}

	public BattleState(BattleCell[][] field, int score) {
		stateOfField = field;
		this.score = score;
	}

	public void addModifierToUnit(int width, int height, Modifier modifier) {
		if (stateOfField[width][height].isOccupied()) {
			stateOfField[width][height].getUnit().addModifier(modifier);
		}
	}

	public void setEntity(int width, int height, HypotheticalUnit unit) {
		if ((height > 0) && (width > 0)) {
			final int originalScore = stateOfField[width][height].getScore();
			final int newScore = unit.getScore();
			final int difference = newScore - originalScore;
			score += difference;
			unit.setX(width);
			unit.setY(height);
			stateOfField[width][height].setUnit(unit);
		} else {
			outOfBoundsArray.add(unit);
		}
	}

	public BattleCell get(int width, int height) {
		return stateOfField[width][height];
	}

	public void moveUnitFromTo(int entityID, Point from, Point to) {
		stateOfField[to.x][to.y].setOccupied(true);

		if (withinBounds(from)) {
			stateOfField[to.x][to.y].setUnit(stateOfField[from.x][from.y].getUnit());
			stateOfField[to.x][to.y].getUnit().setX(to.x);
			stateOfField[to.x][to.y].getUnit().setY(to.y);
			stateOfField[from.x][from.y].removeUnit();
		} else {
			for (final HypotheticalUnit unit : outOfBoundsArray) {
				if (unit.getEntityId() == entityID) {
					stateOfField[to.x][to.y].setUnit(unit);
				}
			}
		}
	}

	private boolean withinBounds(Point from) {
		return ((from.x >= 0) && (from.x <= getWidth()) && (from.y >= 0) && (from.y <= getHeight()));
	}

	public Point stepFromTowards(Point from, Point to) {
		final int random = MathUtils.random.nextInt(1);

		if (random == 0) {
			if (to.x > from.x) {
				return new Point(from.x + 1, from.y);
			} else if (to.x < from.x) {
				return new Point(from.x - 1, from.y);
			} else {
				return yUpOrYDown(from, to);
			}
		} else {
			return yUpOrYDown(from, to);
		}
	}

	private Point yUpOrYDown(Point from, Point to) {
		if (to.y > from.y) {
			return new Point(from.x, from.y + 1);
		} else if (to.y < from.y) {
			return new Point(from.x, from.y - 1);
		} else {
			return new Point(from.x, from.y);
		}
	}

	public BattleState makeCopy() {
		final BattleCell[][] copyField = stateOfField.clone();
		return new BattleState(copyField, score);

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
