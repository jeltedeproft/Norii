package com.jelte.norii.headless;

import com.jelte.norii.magic.AbilitiesEnum;
import com.jelte.norii.utility.MyPoint;

public class Move {
	private MoveType type;
	private MyPoint location;
	private AbilitiesEnum ability; 
	
	private enum MoveType{
		WALK, SPELL, ATTACK, SKIP, SURRENDER
	}

	public Move(MoveType type, MyPoint location, AbilitiesEnum ability) {
		this.type = type;
		this.location = location;
		this.ability = ability;
	}

	public MoveType getType() {
		return type;
	}

	public void setType(MoveType type) {
		this.type = type;
	}

	public MyPoint getLocation() {
		return location;
	}

	public void setLocation(MyPoint location) {
		this.location = location;
	}

	public AbilitiesEnum getAbility() {
		return ability;
	}

	public void setAbility(AbilitiesEnum ability) {
		this.ability = ability;
	}
	
	
}
