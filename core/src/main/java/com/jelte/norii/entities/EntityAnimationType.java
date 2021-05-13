package com.jelte.norii.entities;

public enum EntityAnimationType {

	SPELLCAST("Spellcast"), THRUST("Thrust"), WALK("Walk"), RUN("Run"), SLASH("Slash"), SHOOT("Shoot"), HURT("Hurt"), DIE("Die"), IDLE("Idle");

	private String typeAsString;

	public String getTypeAsString() {
		return typeAsString;
	}

	EntityAnimationType(final String typeAsString) {
		this.typeAsString = typeAsString;
	}

}
