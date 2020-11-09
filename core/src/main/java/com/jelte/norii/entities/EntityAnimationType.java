package com.jelte.norii.entities;

public enum EntityAnimationType {

	SPELLCAST("Spellcast"),
	THRUST("Thrust"),
	WALK("Walk"),
	SLASH("Slash"),
	SHOOT("Shoot"),
	HURT("Hurt"),
	IDLE("Idle");

	private String typeAsString;

	public String getTypeAsString() {
		return typeAsString;
	}

	EntityAnimationType(final String typeAsString) {
		this.typeAsString = typeAsString;
	}

}
