package com.jelte.norii.battle.battleState;

import java.awt.Point;

import com.badlogic.gdx.utils.Array;
import com.jelte.norii.magic.Ability;

public class SpellMove extends Move {
	Ability ability;
	Array<Point> affectedUnits;

	public SpellMove(MoveType moveType, Point location, Ability ability, Array<Point> affectedUnits) {
		super(moveType, location);
		this.ability = ability;
		this.affectedUnits = affectedUnits;
	}

	public Ability getAbility() {
		return ability;
	}

	public Array<Point> getAffectedUnits() {
		return affectedUnits;
	}

}
