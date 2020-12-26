package com.jelte.norii.battle.battleState;

import java.awt.Point;

public class Move {

	private MoveType moveType;
	private Point location;

	public Move(MoveType moveType, Point location) {
		this.moveType = moveType;
		this.location = location;
	}

	public MoveType getMoveType() {
		return moveType;
	}

	public void setMoveType(MoveType moveType) {
		this.moveType = moveType;
	}

	public Point getLocation() {
		return location;
	}

	public void setLocation(Point location) {
		this.location = location;
	}
}
