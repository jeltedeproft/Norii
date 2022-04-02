package com.jelte.norii.battle.battlestate;

import java.util.Objects;

import com.jelte.norii.entities.Entity;
import com.jelte.norii.utility.MyPoint;

public class Move {

	protected MoveType moveType;
	protected MyPoint location;
	protected Entity unit;

	public Move(MoveType moveType, MyPoint location) {
		this.moveType = moveType;
		this.location = location;
		this.unit = null;
	}

	public Move(MoveType moveType, MyPoint location, Entity unit) {
		this.moveType = moveType;
		this.location = location;
		this.unit = unit;
	}

	public MoveType getMoveType() {
		return moveType;
	}

	public void setMoveType(MoveType moveType) {
		this.moveType = moveType;
	}

	public MyPoint getLocation() {
		return location;
	}

	public void setLocation(MyPoint location) {
		this.location = location;
	}

	public Entity getUnit() {
		return unit;
	}

	public void setUnit(Entity unit) {
		this.unit = unit;
	}

	@Override
	public String toString() {
		if (this instanceof SpellMove) {
			SpellMove spell = (SpellMove) this;
			return ("casting " + spell.getAbility() + " \non : " + location + " \nfor unit : " + unit);
		}

		return "moving " + unit + " \nto : " + location;
	}

	public Move makeCopy() {
		return new Move(moveType, new MyPoint(location.x, location.y), unit);
	}

	@Override
	public int hashCode() {
		return Objects.hash(location, moveType, unit);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Move other = (Move) obj;
		return Objects.equals(location, other.location) && (moveType == other.moveType) && Objects.equals(unit, other.unit);
	}

}
