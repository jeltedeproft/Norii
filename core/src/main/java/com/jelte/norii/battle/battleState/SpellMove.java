package com.jelte.norii.battle.battleState;

import com.badlogic.gdx.utils.Array;
import com.jelte.norii.entities.Entity;
import com.jelte.norii.magic.Ability;
import com.jelte.norii.utility.MyPoint;

public class SpellMove extends Move {
	Ability ability;
	Array<MyPoint> affectedUnits;

	public SpellMove(MoveType moveType, MyPoint location, Ability ability, Array<MyPoint> affectedUnits) {
		super(moveType, location);
		this.ability = ability;
		this.affectedUnits = affectedUnits;
	}
	
	public SpellMove(MoveType moveType, MyPoint location, Ability ability, Array<MyPoint> affectedUnits, Entity unit) {
		super(moveType, location,unit);
		this.ability = ability;
		this.affectedUnits = affectedUnits;
	}

	public Ability getAbility() {
		return ability;
	}

	public Array<MyPoint> getAffectedUnits() {
		return affectedUnits;
	}

}
