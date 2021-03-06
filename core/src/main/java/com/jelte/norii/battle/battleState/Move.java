package com.jelte.norii.battle.battleState;

import com.jelte.norii.utility.MyPoint;

public class Move {

	private MoveType moveType;
	private MyPoint location;

	public Move(MoveType moveType, MyPoint location) {
		this.moveType = moveType;
		this.location = location;
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

	@Override
	public String toString() {
		return moveType.toString() + " : " + location;
	}

	public Move makeCopy() {
		return new Move(moveType, new MyPoint(location.x, location.y));
	}
}
