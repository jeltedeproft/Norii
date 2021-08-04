package com.jelte.norii.battle.battleState;

import com.jelte.norii.entities.Entity;

public class BattleCell {
	private boolean occupied;
	private boolean walkable;
	private Entity unit;

	public BattleCell() {
		occupied = false;
		walkable = true;
		unit = null;
	}

	public BattleCell(boolean occupied, boolean walkable, Entity unit) {
		super();
		this.occupied = occupied;
		this.walkable = walkable;
		this.unit = unit;
	}

	public BattleCell(boolean occupied, boolean walkable) {
		this.occupied = occupied;
		this.walkable = walkable;
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

	public Entity getUnit() {
		return unit;
	}

	public void setUnit(Entity unit) {
		this.unit = unit;
	}

	public void removeUnit() {
		unit = null;
		occupied = false;
	}

	@Override
	public String toString() {
		return occupied + "unit:" + unit.getEntityID() + " with position : " + unit.getX() + ", " + unit.getY();
	}
}
