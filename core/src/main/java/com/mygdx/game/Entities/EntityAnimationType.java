package com.mygdx.game.Entities;

public enum EntityAnimationType {

	SPELLCAST(0),
	THRUST(0),
	WALK(0),
	SLASH(0),
	SHOOT(0),
	HURT(0),
	IDLE(0);

	private int positionInSpriteSheet;

	public int getYPosition() {
		return positionInSpriteSheet;
	}

	EntityAnimationType(final int yPosition) {
		this.positionInSpriteSheet = yPosition;
	}

}
