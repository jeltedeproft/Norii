package com.mygdx.game.Entities;

public enum EntityAnimationType {

	SPELLCAST(0),
	THRUST(4),
	WALK(8),
	SLASH(12),
	SHOOT(16),
	HURT(20),
	IDLE(24);

	private int positionInSpriteSheet;

	public int getYPosition() {
		return positionInSpriteSheet;
	}

	EntityAnimationType(final int yPosition) {
		this.positionInSpriteSheet = yPosition;
	}

}
