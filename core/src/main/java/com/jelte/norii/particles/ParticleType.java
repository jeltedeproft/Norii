package com.jelte.norii.particles;

public enum ParticleType {
	SPAWN("particles/spawn.p"),
	MOVE("particles/move.p"),
	SPELL("particles/spell.p"),
	FIREBALL("particles/fireball.p"),
	SWAP("particles/swap.p"),
	RED_CIRCLE("particles/red_ring.p"),
	BLUE_CIRCLE("particles/blue_ring.p"),
	ATTACK("particles/attack.p");

	private String particleFileLocation;

	public String getParticleFileLocation() {
		return particleFileLocation;
	}

	ParticleType(final String particleLocation) {
		particleFileLocation = particleLocation;
	}
}
