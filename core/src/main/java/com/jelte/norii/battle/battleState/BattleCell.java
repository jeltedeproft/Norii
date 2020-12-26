package com.jelte.norii.battle.battleState;

public class BattleCell {
	private boolean occupied;
	private boolean walkable;
	private HypotheticalUnit unit;

	public BattleCell() {
		occupied = false;
		walkable = true;
		unit = null;
	}

	public int getScore() {
		if (occupied) {
			return unit.getScore();
		} else {
			return 0;
		}
	}

	public boolean isOccupied() {
		return occupied;
	}

	public void setOccupied(boolean occupied) {
		this.occupied = occupied;
	}

	public boolean isWalkable() {
		return walkable;
	}

	public void setWalkable(boolean walkable) {
		this.walkable = walkable;
	}

	public HypotheticalUnit getUnit() {
		return unit;
	}

	public void setUnit(HypotheticalUnit unit) {
		this.unit = unit;
	}

	public void removeUnit() {
		unit = null;
		occupied = false;
	}
}
