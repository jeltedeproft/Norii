package com.mygdx.game.Entities;

public enum EntityAnimationType {

	SPELLCAST("Spellcast"),
	THRUST("Thrust"),
	WALK("Walk"),
	SLASH("Slash"),
	SHOOT("Shoot"),
	HURT("Hurt"),
	IDLE("idle");

	private String typeAsString;

	public String getTypeAsString() {
		return typeAsString;
	}

	EntityAnimationType(final String typeAsString) {
		this.typeAsString = typeAsString;
	}

}
