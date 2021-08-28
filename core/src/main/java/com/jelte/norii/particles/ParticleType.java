package com.jelte.norii.particles;

public enum ParticleType {
	SPAWN("particles/spawn.p"),
	MOVE("particles/move.p"),
	SPELL("particles/spell.p"),
	FIREBALL("particles/fireball.p"),
	HEAL("particles/heal.p"),
	SWAP("particles/swap.p"),
	RED_CIRCLE("particles/red_ring.p"),
	BLUE_CIRCLE("particles/blue_ring.p"),
	WHITE_SQUARE("particles/white.p"),
	PURPLE_SQUARE("particles/purple.p"),
	ICE("particles/ice.p"),
	FROST("particles/frost.p"),
	CRACKLE("particles/crackle.p"),
	WIND("particles/wind.p"),
	ELECTRICITY("particles/electricity.p"),
	ATTACK("particles/attack.p"),
	LOVE("particles/love.p");

	private String particleFileLocation;

	public String getParticleFileLocation() {
		return particleFileLocation;
	}

	ParticleType(final String particleLocation) {
		particleFileLocation = particleLocation;
	}
}
